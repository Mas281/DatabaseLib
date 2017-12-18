package me.itsmas.sql.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.logging.Level;

/**
 * Logging utilities
 */
public final class Logs
{
    private Logs() {}

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
        print(Level.INFO, String.format(msg, params));
    }

    /**
     * Logs a message on the {@link Level#SEVERE} level
     *
     * @param msg The log message
     * @param params Optional formatting arguments
     */
    public static void severe(String msg, Object... params)
    {
        print(Level.SEVERE, String.format(msg, params));
    }

    /**
     * Logs a message to the console
     *
     * @param level The log level
     * @param msg The log message
     */
    private static void print(Level level, String msg)
    {
        System.out.println(FORMATTER.apply(level, msg));
    }

    /**
     * The default logging format
     */
    private static final String DEFAULT_LOGGING_FORMAT = "[Database : %s : %s] %s";

    /**
     * The time format for logging
     */
    private static final SimpleDateFormat LOGGING_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    /**
     * The function for formatting log messages
     */
    private static BiFunction<Level, String, String> FORMATTER = (level, msg) ->
        String.format(DEFAULT_LOGGING_FORMAT,
            LOGGING_TIME_FORMAT.format(new Date()),
            level,
            msg
        );
}
