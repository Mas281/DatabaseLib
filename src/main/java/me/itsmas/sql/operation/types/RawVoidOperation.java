package me.itsmas.sql.operation.types;

import me.itsmas.sql.Database;
import me.itsmas.sql.operation.DatabaseOperation;
import me.itsmas.sql.util.Logs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * {@link DatabaseOperation} type for executing updates with no returned data
 */
public class RawVoidOperation extends StatementOperation<Void>
{
    public RawVoidOperation(String statement, Object... data)
    {
        super(statement, data);
    }

    @Override
    public Void execute(Database database)
    {
        try
        (
            Connection connection = database.fetchConnection();
            PreparedStatement statement = prepareStatement(connection)
        )
        {
            statement.execute();
        }
        catch (SQLException ex)
        {
            Logs.severe("Error executing insert statement");
            throw new RuntimeException(ex);
        }

        return null;
    }
}
