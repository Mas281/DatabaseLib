package me.itsmas.sql.operation;

import me.itsmas.sql.Database;

import javax.annotation.Nonnull;

/**
 * Abstraction of a database operation
 *
 * @param <T> The object type the operation will return
 */
public interface DatabaseOperation<T>
{
    /**
     * Executes the operation
     *
     * @param database The database instance
     *
     * @return The result of the operation
     */
    T execute(@Nonnull Database database);
}
