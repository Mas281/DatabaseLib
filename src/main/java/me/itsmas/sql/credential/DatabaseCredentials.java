package me.itsmas.sql.credential;

import javax.annotation.Nonnull;

/**
 * Database credentials info
 */
public class DatabaseCredentials
{
    public final String host;
    public final int port;
    public final String database;
    public final String username;
    public final String password;

    public DatabaseCredentials(@Nonnull String host, @Nonnull int port, @Nonnull String database, @Nonnull String username, @Nonnull String password)
    {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }
}
