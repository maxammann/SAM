![alt text](https://dl.dropboxusercontent.com/u/97517326/SAM_final_500.png)

This SQL library allows you to save your java objects in a sql database.

Supported:
- int, Integer, AtomicInteger
- short, Short
- byte, Byte
- long, Long, AtomicLong
- float, Float
- double, Double
- boolean, Boolean, AtomicBoolean
- char, Character
- String
- Serializable objects


Currently there is support for mysql, sqlite and h2, but it should be easily possible to add support for other engines.
To create a object you want to store just create a new class and let it implement "TableObject". the next step is to
define which fields and methods are used to push data to the database and get. Assign the "DatabaseColumn" annotations
to your fields. If you want to use methods, getters are used to get data from the object and setters are used to set
data in your object. Setters must have the return type "void" and only one parameter. The getters have no parameters and
a return type.

Example:

```java
@DatabaseTable(name = "table")
public class Person implements TableObject {
 
    @DatabaseColumn(position = 1, databaseName = "id", id = true)
    private int id;

    @DatabaseColumn(position = 2, databaseName = "name")
    private String name;
 
    @DatabaseColumn(position = 3, databaseName = "age")
    private int age;
 
    public Person()
    {
    }

    public String getFormattedName()
    {
        return name;
    }
}
```


As you can see you need to define the table name in the "DatabaseTable" annotation above the class. Every table needs to
have one id, which increments automatically if you insert the object into the database.



