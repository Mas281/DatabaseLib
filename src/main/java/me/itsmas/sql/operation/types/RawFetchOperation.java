package me.itsmas.sql.operation.types;

import me.itsmas.sql.Database;
import me.itsmas.sql.operation.DatabaseOperation;
import me.itsmas.sql.util.Logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link DatabaseOperation} type for querying data in the form of {@link ResultSet} instances
 *
 * After gathering all needed data from the returned {@link ResultSet}
 * the {@link #closeResources()} method should be called to ensure
 * that the resources are properly closed and returned to the pool
 */
public class RawFetchOperation extends StatementOperation<ResultSet>
{
    public RawFetchOperation(String statement, Object... data)
    {
        super(statement, data);
    }

    /**
     * The database connection used
     */
    private Connection connection;

    /**
     * The statement used
     */
    private PreparedStatement statement;

    @Override
    public ResultSet execute(Database database)
    {
        try
        {
            connection = database.fetchConnection();
            statement = prepareStatement(connection);

            return statement.executeQuery();
        }
        catch (SQLException ex)
        {
            Logs.severe("Error executing database query");
            throw new RuntimeException(ex);
        }
    }

    /**
     * Closes the query's resources
     */
    public void closeResources()
    {
        try
        {
            connection.close();
            statement.close();
        }
        catch (SQLException ex)
        {
            Logs.severe("Error closing resources");
            ex.printStackTrace();
        }
    }
}
