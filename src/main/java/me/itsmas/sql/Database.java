package me.itsmas.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import me.itsmas.sql.credential.DatabaseCredentials;
import me.itsmas.sql.operation.DatabaseOperation;
import me.itsmas.sql.util.Logs;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The class holding the database connection and methods
 */
public class Database
{
    /**
     * The database credentials
     */
    private final DatabaseCredentials credentials;

    /**
     * Database initialisation
     *
     * @param credentials The database credential
     */
    public Database(@Nonnull DatabaseCredentials credentials)
    {
        this.credentials = credentials;
    }

    /**
     * Attempts to open a connection to the database
     */
    public void openConnection()
    {
        try
        {
            openConnection(credentials);

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        }
        catch (Exception ex)
        {
            Logs.severe("Error connecting to database");
            ex.printStackTrace();
        }
    }

    /**
     * Closes the database connection
     */
    public void closeConnection()
    {
        checkArgument(isConnected(), "Database connection is not open");

        shutdown();
    }

    /**
     * Executes a {@link DatabaseOperation} synchronously
     *
     * @param operation The operation
     *
     * @return The operation result
     */
    public <T> T executeSync(@Nonnull DatabaseOperation<T> operation)
    {
        return operation.execute(this);
    }

    /**
     * Executes a {@link DatabaseOperation} asynchronously
     *
     * @param operation The operation
     * @return A {@link CompletableFuture} holding the operation result
     */
    public <T> CompletableFuture<T> executeAsync(@Nonnull DatabaseOperation<T> operation)
    {
        return CompletableFuture.supplyAsync(() -> operation.execute(this), executor);
    }

    /**
     * The connection pool
     */
    private HikariDataSource connectionPool;

    /**
     * Fetches a new {@link ProxyConnection} from the pool
     *
     * @return A new connection
     *
     * @throws SQLException If an SQL error is encountered
     */
    public Connection fetchConnection() throws SQLException
    {
        checkArgument(isConnected(), "Database connection is not open");

        return connectionPool.getConnection();
    }

    /**
     * The executor for async operations
     */
    private ExecutorService executor;

    /**
     * Attempts to open a connection to the database
     *
     * @param credentials The database credentials
     */
    private void openConnection(DatabaseCredentials credentials)
    {
        checkArgument(!isConnected(), "Already connected to database");

        HikariConfig config = new HikariConfig();

        config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");

        config.setJdbcUrl(String.format("jdbc:mysql//%s:%s/%s", credentials.host, credentials.port, credentials.database));
        config.addDataSourceProperty("databaseName", credentials.database);

        config.setUsername(credentials.username);
        config.setPassword(credentials.password);

        config.setConnectionTimeout(30_000L);

        connectionPool = new HikariDataSource(config);
        executor = Executors.newCachedThreadPool();

        Logs.info("Connected to database successfully");
    }

    /**
     * Fetches whether the database connection is open
     *
     * @see #connectionPool
     *
     * @return Whether the connection pool is open
     */
    public boolean isConnected()
    {
        return connectionPool != null && !connectionPool.isClosed();
    }

    /**
     * Shuts down the database connection and the executor
     *
     * @see #connectionPool
     * @see #executor
     */
    private void shutdown()
    {
        if (isConnected())
        {
            connectionPool.close();
            executor.shutdown();
        }
    }
}
