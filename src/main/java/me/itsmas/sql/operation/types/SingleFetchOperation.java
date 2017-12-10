package me.itsmas.sql.operation.types;

import me.itsmas.sql.Database;
import me.itsmas.sql.mapping.Mapping;
import me.itsmas.sql.mapping.Mappings;
import me.itsmas.sql.operation.DatabaseOperation;
import me.itsmas.sql.util.ClassTools;
import me.itsmas.sql.util.Logs;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * {@link DatabaseOperation} type for fetching objects from the database
 *
 * The query will only fetch the first operation matching the conditions
 */
public class SingleFetchOperation<T> extends ConditionOperation<Optional<T>>
{
    /**
     * The class to map the resulting object to
     */
    private final Class<T> clazz;

    /**
     * {@link SingleFetchOperation} constructor
     *
     * @param clazz The class to map the resulting object to
     */
    public SingleFetchOperation(@Nonnull Class<T> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public Optional<T> execute(Database database)
    {
        RawFetchOperation rawOperation = new RawFetchOperation(constructStatement(), conditions.values().toArray());
        ResultSet results = rawOperation.execute(database);

        try
        {
            if (results.first())
            {
                T mapped = getMappedObject(results);
                rawOperation.closeResources();

                return Optional.ofNullable(mapped);
            }
            while (results.next())
            {
                // map and return

                rawOperation.closeResources();
            }
        }
        catch (SQLException ex)
        {
            Logs.severe("Error fetching results from database query");
            throw new RuntimeException(ex);
        }

        return Optional.empty();
    }

    /**
     * Creates the mapped objects
     * from a {@link ResultSet} object
     *
     * @param results The query results
     *
     * @return The mapped object
     */
    private T getMappedObject(ResultSet results)
    {
        T object = ClassTools.newInstance(clazz);

        try
        {
            for (Field field : ClassTools.getDatabaseFields(clazz))
            {
                Optional<Mapping> mapping = Mappings.getMapping(field.getType());

                if (mapping.isPresent())
                {
                    Object value = mapping.get().fromResults(results, ClassTools.getColumnName(field));

                    field.set(object, value);
                }
            }

            return object;
        }
        catch (SQLException ex)
        {
            Logs.severe("Error creating mapped object from ResultSet");
            throw new RuntimeException(ex);
        }
        catch (IllegalAccessException ignored) { return null; }
    }

    /**
     * Constructs an SQL statement for fetching the object
     *
     * @see #conditions
     *
     * @return The statement
     */
    private String constructStatement()
    {
        return String.format("SELECT * FROM %s WHERE %s LIMIT 1;",
            ClassTools.getTable(clazz),
            getConditionString()
        );
    }

    @Override
    public SingleFetchOperation<T> where(String column, Object value)
    {
        super.where(column, value);

        return this;
    }
}
