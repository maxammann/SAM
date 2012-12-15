package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseTable;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.query.SelectQuery;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Represents a Database
 */
public abstract class Database {

    protected DataSource dataSource;
    private Connection connection;
    private DatabaseConfiguration configuration;
    /**
     * Whether old columns should be dropped
     */
    private boolean dropOldColumns = false;
    /**
     * Prepared statements
     */
    private TreeMap<Integer, PreparedStatement> preparedStatements = new TreeMap<Integer, PreparedStatement>();
    /**
     * A map of registered tables (classes) and a list of columns
     */
    private Set<RegisteredTable> registeredTables = new HashSet<RegisteredTable>();

    /**
     * Creates a new database connection based on the configuration
     *
     * @param configuration The database configuration
     * @throws SQLException
     */
    protected Database(DatabaseConfiguration configuration) throws SQLException
    {
        this.configuration = configuration;
        String driver = configuration.getDriverName();
        try {
            Class.forName(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load driver " + driver + "!");
        }
        init(configuration);

        connection = dataSource.getConnection();
    }

    /**
     * Gets the name of a table
     *
     * @param clazz The class of the {@link TableObject}.
     * @return The name
     */
    static String getTableName(Class<? extends TableObject> clazz)
    {
        DatabaseTable annotation = clazz.getAnnotation(DatabaseTable.class);
        return annotation == null ? null : annotation.name();
    }

    private int prepareStatement(String query)
    {
        PreparedStatement statement;
        try {
            statement = getConnection().prepareStatement(query);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
        int statementId;
        if (preparedStatements.isEmpty()) {
            statementId = 0;
        } else {
            statementId = preparedStatements.lastKey() + 1;
        }
        preparedStatements.put(statementId, statement);
        return statementId;
    }

    public int prepareStatement(SelectQuery query)
    {
        return prepareStatement(query.getQuery());
    }

    public <T extends TableObject> List<T> executeStatement(int id, RegisteredTable table)
    {
        try {
            ResultSet result = preparedStatements.get(id).executeQuery();
            List<Column> columns = table.getRegisteredColumns();
            List<T> objects = new ArrayList<T>();

            try {
                while (result.next()) {
                    T object = table.createNewInstance();
                    for (int i = 0; i < columns.size(); i++) {
                        Column column = columns.get(i);

                        column.setValue(object, result.getObject(i + 1));
                    }
                    objects.add(object);
                }
            } catch (SQLException e) {
                throw new QueryException(e);
            }
            return objects;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    protected abstract void init(DatabaseConfiguration configuration);

    /**
     * Closes the connection to the database
     *
     * @throws SQLException
     */
    public abstract void close() throws SQLException;

    protected TableBuilder createTableBuilder(TableObject table)
    {
        return createTableBuilder(table.getClass());
    }

    protected abstract TableBuilder createTableBuilder(Class<? extends TableObject> table);

    public final Database registerTable(Class<? extends TableObject> table)
    {
        TableBuilder builder = createTableBuilder(table);

        registeredTables.add(new RegisteredTable(builder.getTableName(), table, builder.getColumns(), builder.getDefaultConstructor()));

        String tableQuery = builder.createTable().getQuery();
        System.out.println("Create Query:" + tableQuery);

        String modifyQuery = builder.createModifyQuery().getQuery();
        System.out.println("Modify Query:" + modifyQuery);

        executeDirectUpdate(tableQuery);
        executeDirectUpdate(modifyQuery);
        return this;
    }

    boolean existsDatabaseTable(String table)
    {
        return getDatabaseTables().contains(table);
    }

    public final Database registerTable(TableObject table)
    {
        return registerTable(table.getClass());
    }

    private List<String> getDatabaseColumns(Class<? extends TableObject> table)
    {
        return getDatabaseColumns(getTableName(table));
    }

    private List<String> getDatabaseColumns(TableObject table)
    {
        return getDatabaseColumns(getTableName(table.getClass()));
    }

    List<String> getDatabaseColumns(String table)
    {
        List<String> columns = new ArrayList<String>();

        try {
            ResultSet columnResult = getMetadata().getColumns(null, null, table, null);


            while (columnResult.next()) {
                columns.add(columnResult.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return columns;
    }

    private DatabaseMetaData getMetadata()
    {
        try {
            return getConnection().getMetaData();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public final Set<String> getDatabaseTables()
    {
        Set<String> columns = new HashSet<String>();

        ResultSet columnResult = null;
        try {
            columnResult = this.getConnection().getMetaData().getTables(null, null, null, null);

            while (columnResult.next()) {
                columns.add(columnResult.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return columns;
    }

    void executeDirectUpdate(String query)
    {
        if (query == null) {
            return;
        }
        try {
            getConnection().createStatement().executeUpdate(query);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    ResultSet executeDirectQuery(String query)
    {
        if (query == null) {
            return null;
        }
        try {
            return getConnection().createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    public RegisteredTable getRegisteredTable(Class<? extends TableObject> table)
    {
        for (RegisteredTable registeredTable : registeredTables) {
            if (registeredTable.isRegisteredClass(table)) {
                return registeredTable;
            }
        }

        return null;
    }

    public void save(TableObject tableObject)
    {
        save(tableObject, new String[]{});
    }

    public void save(TableObject tableObject, String... columns)
    {
        RegisteredTable registration = getRegisteredTable(tableObject.getClass());


        StringBuilder query = new StringBuilder("INSERT INTO ").append(registration.getName()).append(" (");
        StringBuilder values = new StringBuilder(" VALUES (");

        List<Column> registeredColumns = registration.getRegisteredColumns();

        for (Column column : registeredColumns) {
            Object value = column.getValue(tableObject);

            if (value == null) {
                continue;
            }

            value = DatabaseUtil.validateColumnValue(value, column);

            query.append(column.getColumnName());
            values.append(value);
            query.append(',');
            values.append(',');
        }

        query.deleteCharAt(query.length() - 1);
        values.deleteCharAt(values.length() - 1);

        query.append(')');
        values.append(')');

        query.append(values).append(';');
        System.out.println(query);
        executeDirectUpdate(query.toString());
    }

//    public abstract <T extends TableObject> SelectQuery<T> createQuery();

    protected final Connection getConnection()
    {
        return connection;
    }

    public DatabaseConfiguration getConfiguration()
    {
        return configuration;
    }

    public final boolean isDropOldColumns()
    {
        return dropOldColumns;
    }

    public final void setDropOldColumns(boolean dropOldColumns)
    {
        this.dropOldColumns = dropOldColumns;
    }
}
