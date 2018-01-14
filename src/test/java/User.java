import me.itsmas.sql.annotation.Column;
import me.itsmas.sql.annotation.DatabaseObject;

import java.util.Date;
import java.util.UUID;

@DatabaseObject(
    table = "users",
    insertFields = {"id", "name", "rank", "firstJoin"},
    uniqueKeyField = "id"
)
class User
{
    @Column
    private UUID id;

    @Column
    private String name;

    @Column
    private Rank rank = Rank.PLAYER;

    @Column("first_join")
    private Date firstJoin = new Date();

    private User() {}

    User(UUID id, String name)
    {
        this.id = id;
        this.name = name;
    }

    UUID getId()
    {
        return id;
    }

    String getName()
    {
        return name;
    }

    void setRank(Rank rank)
    {
        this.rank = rank;
    }

    Rank getRank()
    {
        return rank;
    }

    Date getFirstJoin()
    {
        return firstJoin;
    }
}
