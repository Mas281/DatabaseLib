package me.itsmas.sql.operation.types;

import me.itsmas.sql.Database;
import me.itsmas.sql.operation.DatabaseOperation;
import me.itsmas.sql.util.ClassTools;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link DatabaseOperation} type for inserting objects into the database
 */
public class InsertOperation implements DatabaseOperation<Void>
{
    /**
     * The object to insert
     */
    private final Object object;

    /**
     * The fields being inserted
     */
    private final List<Field> fields;

    /**
     * {@link InsertOperation} constructor
     *
     * @param object The object to insert
     */
    public InsertOperation(@Nonnull Object object)
    {
        this.object = object;

        this.fields = ClassTools.getInsertFields(object);
    }

    @Override
    public Void execute(Database database)
    {
        RawVoidOperation rawOperation = new RawVoidOperation(
            constructStatement(),
            getValues()
        );

        rawOperation.execute(database);
        return null;
    }

    /**
     * Constructs a statement for inserting the object
     *
     * @see #object
     *
     * @return The statement
     */
    private String constructStatement()
    {
        return String.format("INSERT INTO %s (%s) VALUES (%s);",
            ClassTools.getTable(object.getClass()),
            getJoinedColumns(),
            String.join(",", Collections.nCopies(fields.size(), "?"))
        );
    }

    /**
     * Fetches the names of the object
     * columns delimited by a comma (",")
     *
     * @return The joined column names
     */
    private String getJoinedColumns()
    {
        return String.join(",", getStream().map(ClassTools::getColumnName).collect(Collectors.toList()));
    }

    /**
     * Fetches the values to
     * insert into the statement
     *
     * @return The values
     */
    private Object[] getValues()
    {
        return getStream().map(field ->
        {
            try
            {
                return field.get(object);
            }
            catch (IllegalAccessException ignored) { return null; }
        }).toArray();
    }

    /**
     * Fetches a stream of the object fields
     *
     * @see #object
     * @see #fields
     *
     * @return The field
     */
    private Stream<Field> getStream()
    {
        return fields.stream();
    }
}
