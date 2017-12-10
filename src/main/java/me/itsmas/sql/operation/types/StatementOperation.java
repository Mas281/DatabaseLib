package me.itsmas.sql.operation.types;

import me.itsmas.sql.mapping.Mapping;
import me.itsmas.sql.mapping.Mappings;
import me.itsmas.sql.operation.DatabaseOperation;
import me.itsmas.sql.util.Logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

/**
 * {@link DatabaseOperation} type containing an SQL statement
 */
abstract class StatementOperation<T> implements DatabaseOperation<T>
{
    /**
     * The SQL statement
     */
    private final String statement;

    /**
     * The statement data
     */
    private final Object[] data;

    /**
     * {@link StatementOperation} constructor
     *
     * @param statement The SQL statement
     * @param data The statement data
     */
    StatementOperation(String statement, Object... data)
    {
        this.statement = statement;
        this.data = data;
    }

    /**
     * Prepares an SQL statement for execution
     *
     * @see #statement
     * @see #data
     *
     * @param connection A database connection
     *
     * @return The {@link PreparedStatement}
     *
     * @throws SQLException If an SQL error is encountered
     */
    @SuppressWarnings("unchecked")
    PreparedStatement prepareStatement(Connection connection) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement(statement);

        for (int i = 1; i <= data.length; i++)
        {
            Object object = data[i - 1];

            Optional<Mapping> mapping = Mappings.getMapping(object.getClass());

            if (mapping.isPresent())
            {
                mapping.get().updateStatement(preparedStatement, object, i);
            }
            else
            {
                Logs.severe("No mapping found for class %s", object.getClass().getCanonicalName());
            }
        }

        return preparedStatement;
    }
}
