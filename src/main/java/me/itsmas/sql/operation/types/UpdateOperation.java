package me.itsmas.sql.operation.types;

import me.itsmas.sql.Database;
import me.itsmas.sql.operation.DatabaseOperation;
import me.itsmas.sql.util.ClassTools;

import java.lang.reflect.Field;

/**
 * {@link DatabaseOperation} type for updating existing data
 */
public class UpdateOperation extends ConditionOperation<Void>
{
    /**
     * The object the update is referring to
     */
    private final Object object;

    /**
     * The field being updated
     */
    private final Field field;

    /**
     * {@link UpdateOperation} constructor
     *
     * @param object The object with the field being updated
     * @param fieldName The field in the object to update
     */
    public UpdateOperation(Object object, String fieldName)
    {
        this.object = object;
        this.field = ClassTools.getField(object.getClass(), fieldName);
    }

    @Override
    public Void execute(Database database)
    {
        RawVoidOperation rawOperation = new RawVoidOperation(constructStatement(), getValues());
        rawOperation.execute(database);

        return null;
    }

    /**
     * Constructs an SQL statement
     * for updating the object
     *
     * @return The statement
     */
    private String constructStatement()
    {
        return String.format("UPDATE %s SET %s WHERE %s;",
            ClassTools.getTable(object.getClass()),
            ClassTools.getColumnName(field) + "=?",
            getConditionString()
        );
    }

    /**
     * Fetches the values to
     * insert into the statement
     *
     * @return The object to update
     */
    private Object[] getValues()
    {
        try
        {
            Object[] objects = new Object[conditions.size() + 1];

            objects[0] = field.get(object);

            Object[] conditionValues = conditions.values().toArray();
            System.arraycopy(conditionValues, 0, objects, 1, conditionValues.length);

            return objects;
        }
        catch (IllegalAccessException ignored) { return null; }
    }

    @Override
    public UpdateOperation where(String column, Object value)
    {
        super.where(column, value);

        return this;
    }
}
