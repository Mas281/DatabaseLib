package me.itsmas.sql.mapping;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstraction of an object mapping
 *
 * @param <T> The object type being mapped
 */
public interface Mapping<T>
{
    /**
     * Adds the given data to a {@link PreparedStatement}
     *
     * @param statement The statement to add the data to
     * @param data The data to set
     * @param index The index of the data in the statement
     *
     * @throws SQLException If an SQL error is encountered
     */
    void updateStatement(PreparedStatement statement, T data, int index) throws SQLException;

    /**
     * Fetches data from a {@link ResultSet}
     *
     * @param results The {@link ResultSet} to parse the object from
     * @param fieldName The name of the field in the statement
     *
     * @return The mapped data
     *
     * @throws SQLException If an SQL error is encountered
     */
    T fromResults(ResultSet results, String fieldName) throws SQLException;
}
