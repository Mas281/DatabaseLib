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
     * The class of the object being updated
     */
    private final Class<?> clazz;

    /**
     * The field holding the being updated
     */
    private final Field valueField;

    /**
     * The unique field for the object
     */
    private final Field uniqueField;

    /**
     * {@link UpdateOperation} constructor
     *
     * @param object The object with the field being updated
     * @param fieldName The field in the object to update
     */
    public UpdateOperation(Object object, String fieldName)
    {
        this.object = object;
        this.clazz = object.getClass();

        this.valueField = ClassTools.getField(clazz, fieldName);
        this.uniqueField = ClassTools.getUniqueField(clazz);
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
            ClassTools.getColumnName(valueField) + "=?",
            getConditionString()
        );
    }

    @Override
    String getConditionString()
    {
        String condition = super.getConditionString();

        if (!condition.isEmpty())
        {
            condition += " AND ";
        }

        return condition + getUniqueKeyCondition();
    }

    /**
     * Creates the condition string
     * for the unique object field
     *
     * @return The unique field condition
     */
    private String getUniqueKeyCondition()
    {
        return ClassTools.getColumnName(uniqueField) + "=?";
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
            Object[] objects = new Object[conditions.size() + 2];

            objects[0] = valueField.get(object);

            Object[] conditionValues = conditions.values().toArray();
            System.arraycopy(conditionValues, 0, objects, 1, conditionValues.length);

            objects[objects.length - 1] = uniqueField.get(object);

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
