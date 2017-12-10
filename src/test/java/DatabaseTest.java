import me.itsmas.sql.Database;
import me.itsmas.sql.credential.DatabaseCredentials;
import me.itsmas.sql.mapping.Mapping;
import me.itsmas.sql.mapping.Mappings;
import me.itsmas.sql.operation.types.SingleFetchOperation;
import me.itsmas.sql.operation.types.InsertOperation;
import me.itsmas.sql.operation.types.UpdateOperation;
import me.itsmas.sql.util.Logs;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@FixMethodOrder
public class DatabaseTest
{
    private Database database;

    private User user;

    @Before
    public void databaseTest()
    {
        database = new Database(new DatabaseCredentials("localhost", 3306, "test", "root", "PASSWORD"));
        database.openConnection();

        user = new User(UUID.fromString("91874054-b5d0-468f-87a7-7093062278ef"), "Sam");
        registerMapping();
    }

    @Test @Ignore
    public void testInsertUser()
    {
        database.executeSync(new InsertOperation(user));
        Logs.info("Inserted user");
    }

    @Test @Ignore
    public void testUpdateUser()
    {
        user.setRank(Rank.VIP);
        database.executeSync(new UpdateOperation(user, "rank").where("name", "Sam"));

        Logs.info("Updated user");
    }

    @Test @Ignore
    public void testFetchUser()
    {
        SingleFetchOperation<User> operation = new SingleFetchOperation<>(User.class).where("name", "Sam");
        Optional<User> fetched = database.executeSync(operation);

        fetched.ifPresentOrElse(user ->
        {
            Logs.info("User fetched:");

            Logs.info("ID: %s", user.getId());
            Logs.info("Name: %s", user.getName());
            Logs.info("Rank: %s", user.getRank());
            Logs.info("First Join: %s", user.getFirstJoin());
        }, () -> Logs.info("User not fetched"));
    }

    private void registerMapping()
    {
        Mappings.registerMapping(Rank.class, new Mapping<Rank>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, Rank data, int index) throws SQLException
            {
                statement.setString(index, data.name());
            }

            @Override
            public Rank fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return Rank.valueOf(results.getString(fieldName));
            }
        });
    }
}
