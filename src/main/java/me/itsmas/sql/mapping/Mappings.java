package me.itsmas.sql.mapping;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Class holding all available {@link Mapping} instances
 */
public class Mappings
{
    /**
     * The available mappings
     */
    private static final Map<Class<?>, Mapping<?>> MAPPINGS = new HashMap<>();

    /**
     * Registers a new {@link Mapping}
     *
     * @param clazz The class to register the mapping for
     * @param mapping The mapping to register
     */
    public static void registerMapping(Class<?> clazz, Mapping<?> mapping)
    {
        MAPPINGS.put(clazz, mapping);
    }

    /**
     * Fetches an {@link Optional<Mapping>} for a class
     *
     * @param clazz The class
     * @return The optional mapping for the class
     */
    public static Optional<Mapping> getMapping(Class<?> clazz)
    {
        return Optional.ofNullable(MAPPINGS.get(clazz));
    }

    static
    {
        registerMapping(String.class, new Mapping<String>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, String data, int index) throws SQLException
            {
                statement.setString(index, data);
            }

            @Override
            public String fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return results.getString(fieldName);
            }
        });

        registerMapping(boolean.class, new Mapping<Boolean>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, Boolean data, int index) throws SQLException
            {
                statement.setBoolean(index, data);
            }

            @Override
            public Boolean fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return results.getBoolean(fieldName);
            }
        });

        registerMapping(int.class, new Mapping<Integer>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, Integer data, int index) throws SQLException
            {
                statement.setInt(index, data);
            }

            @Override
            public Integer fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return results.getInt(fieldName);
            }
        });

        registerMapping(double.class, new Mapping<Double>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, Double data, int index) throws SQLException
            {
                statement.setDouble(index, data);
            }

            @Override
            public Double fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return results.getDouble(fieldName);
            }
        });

        registerMapping(float.class, new Mapping<Float>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, Float data, int index) throws SQLException
            {
                statement.setFloat(index, data);
            }

            @Override
            public Float fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return results.getFloat(fieldName);
            }
        });

        registerMapping(BigDecimal.class, new Mapping<BigDecimal>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, BigDecimal data, int index) throws SQLException
            {
                statement.setBigDecimal(index, data);
            }

            @Override
            public BigDecimal fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return results.getBigDecimal(fieldName);
            }
        });

        registerMapping(UUID.class, new Mapping<UUID>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, UUID data, int index) throws SQLException
            {
                statement.setString(index, data.toString());
            }

            @Override
            public UUID fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return UUID.fromString(results.getString(fieldName));
            }
        });

        registerMapping(Date.class, new Mapping<Date>()
        {
            @Override
            public void updateStatement(PreparedStatement statement, Date data, int index) throws SQLException
            {
                statement.setDate(index, new java.sql.Date(data.getTime()));
            }

            @Override
            public Date fromResults(ResultSet results, String fieldName) throws SQLException
            {
                return results.getDate(fieldName);
            }
        });
    }
}
