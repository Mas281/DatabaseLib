package me.itsmas.sql.util;

import me.itsmas.sql.Database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * Logging utilities
 */
public final class Logs
{
    private Logs() {}

    /**
     * The logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    /**
     * Logs a message on the {@link Level#INFO} level
     *
     * @see #DEFAULT_LOGGING_FORMAT
     *
     * @param msg The log message
     * @param params Optional formatting arguments
     */
    public static void info(String msg, Object... params)
    {
        LOGGER.info(String.format(msg, params));
    }

    /**
     * Logs a debug message on the {@link Level#FINE} level
     * Log messages printed at this level use a separate
     * logging format in order to show extra information
     * such as class name, method name, and line number
     *
     * @see #DEBUG_LOGGING_FORMAT
     *
     * @param msg The log message
     * @param params Optional formatting arguments
     */
    public static void debug(String msg, Object... params)
    {
        LOGGER.fine(String.format(msg, params));
    }

    /**
     * Logs a message on the {@link Level#SEVERE} level
     *
     * @param msg The log message
     * @param params Optional formatting arguments
     */
    public static void severe(String msg, Object... params)
    {
        LOGGER.severe(String.format(msg, params));
    }

    /**
     * The default logging format
     */
    private static final String DEFAULT_LOGGING_FORMAT = "[%s : %s] %s\n";

    /**
     * The logging format for debug messages
     */
    private static final String DEBUG_LOGGING_FORMAT = "[%s : DEBUG @ %s#%s (L%s)] %s\n";

    /**
     * The time format for logging
     */
    private static final SimpleDateFormat LOGGING_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    static
    {
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.ALL);

        StreamHandler handler = new StreamHandler(System.out, new Formatter()
        {
            @Override
            public String format(LogRecord record)
            {
                if (record.getLevel() == Level.FINE)
                {
                    StackTraceElement element = Thread.currentThread().getStackTrace()[8];

                    return String.format(DEBUG_LOGGING_FORMAT,
                        LOGGING_TIME_FORMAT.format(new Date(record.getMillis())),
                        element.getClassName(),
                        element.getMethodName(),
                        element.getLineNumber(),
                        record.getMessage()
                    );
                }

                return String.format(DEFAULT_LOGGING_FORMAT,
                    LOGGING_TIME_FORMAT.format(new Date(record.getMillis())),
                    record.getLevel(),
                    record.getMessage()
                );
            }
        });

        handler.setLevel(Level.ALL);

        LOGGER.addHandler(handler);
    }
}
