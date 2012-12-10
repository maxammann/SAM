package com.p000ison.dev.sqlapi;

import com.p000ison.dev.sqlapi.annotation.DatabaseTable;
import com.p000ison.dev.sqlapi.exception.QueryException;
import com.p000ison.dev.sqlapi.exception.TableBuildingException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Represents a Database
 */
public abstract class Database {

    protected DataSource dataSource;
    protected Connection connection;
    protected DatabaseConfiguration configuration;
    /**
     * Whether old columns should be dropped
     *
     */
    private boolean dropOldColumns = false;
    /**
     * Prepared statements
     *
     */
    private TreeMap<Integer, PreparedStatement> preparedStatements = new TreeMap<Integer, PreparedStatement>();
    /**
     * A map of registered tables (classes) and a list of columns
     *
     */
    private Map<Class<? extends TableObject>, List<Column>> registeredTables = new HashMap<Class<? extends TableObject>, List<Column>>();

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
    public static String getTableName(Class<? extends TableObject> clazz)
    {
        DatabaseTable annotation = clazz.getAnnotation(DatabaseTable.class);
        return annotation == null ? null : annotation.name();
    }

    public PreparedStatement createPreparedStatement(int id, String query) throws SQLException
    {
        PreparedStatement statement = getConnection().prepareStatement(query);
        preparedStatements.put(id, statement);
        return statement;
    }

    public int createPreparedStatement(String query) throws SQLException
    {
        PreparedStatement statement = getConnection().prepareStatement(query);
        int statementId;
        if (preparedStatements.isEmpty()) {
            statementId = 0;
        } else {
            statementId = preparedStatements.lastKey() + 1;
        }
        preparedStatements.put(statementId, statement);
        return statementId;
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

        registeredTables.put(table, builder.getColumns());

        String tableQuery = builder.createTable().getQuery();
        System.out.println(tableQuery);

        String modifyQuery = builder.createModifyQuery().getQuery();
        System.out.println(modifyQuery);

        executeDirectQuery(tableQuery);
        executeDirectQuery(modifyQuery);
        return this;
    }

    public boolean existsDatabaseTable(String table)
    {
        ResultSet set;
        try {
            set = getMetadata().getTables(null, null, table, null);
            return set.next();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }


    public final Database registerTable(TableObject table)
    {
        return registerTable(table.getClass());
    }

    public final List<String> getDatabaseColumns(Class<? extends TableObject> table)
    {
        return getDatabaseColumns(getTableName(table));
    }

    public final List<String> getDatabaseColumns(TableObject table)
    {
        return getDatabaseColumns(getTableName(table.getClass()));
    }

    protected final List<String> getDatabaseColumns(String table)
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

    public final Set<String> getDatabaseTables() throws SQLException
    {
        Set<String> columns = new HashSet<String>();

        ResultSet columnResult = this.getConnection().getMetaData().getTables(null, null, null, null);

        while (columnResult.next()) {
            columns.add(columnResult.getString("TABLE_NAME"));
        }

        return columns;
    }

    void executeDirectQuery(String query)
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

    public Column getColumn(Class<? extends TableObject> table, String columnName) {
        List<Column> columns = registeredTables.get(table);

        if (columns == null) {
            throw new TableBuildingException("The table %s is not registered!", table.getName());
        }

        for (Column column : columns) {
            String name = column.getColumnName();
            if (name.hashCode() == columnName.hashCode() && name.equals(columnName)) {
                return column;
            }
        }

        return null;
    }

    public final Connection getConnection()
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

    public List<Column> getColumns(Class<? extends TableObject> clazz)
    {
        return registeredTables.get(clazz);
    }
}
