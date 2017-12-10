package me.itsmas.sql.operation.types;

import me.itsmas.sql.operation.DatabaseOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link DatabaseOperation} type where a "WHERE" clause is required
 */
abstract class ConditionOperation<T> implements DatabaseOperation<T>
{
    /**
     * The operation conditions
     */
    final Map<String, Object> conditions = new HashMap<>();

    ConditionOperation() {}

    /**
     * Adds a condition to the query
     *
     * @param column The column to check
     * @param value The value to test for
     *
     * @return The operation instance
     */
    public ConditionOperation<T> where(String column, Object value)
    {
        conditions.put(column, value);

        return this;
    }

    /**
     * Creates the string of conditions
     * based on the conditions map
     *
     * @see #conditions
     *
     * @return The condition string
     */
    String getConditionString()
    {
        return conditions.entrySet().stream()
            .map(entry -> entry.getKey() + "=?")
            .collect(Collectors.joining(" AND ")
        );
    }
}
