package me.itsmas.sql.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.itsmas.sql.annotation.Column;
import me.itsmas.sql.annotation.DatabaseObject;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Field utilities
 */
public final class ClassTools
{
    private ClassTools() {}

    /**
     * Cache of classes to a map linking {@link Field} names to objects
     */
    private static final LoadingCache<Class<?>, Map<String, Field>> FIELD_CACHE = CacheBuilder.newBuilder()
        .weakKeys()
        .build(new CacheLoader<Class<?>, Map<String, Field>>()
        {
            @Override
            public Map<String, Field> load(Class<?> clazz) throws Exception
            {
                return Stream.of(clazz.getDeclaredFields())
                    .filter(ClassTools::isDatabaseColumn)
                    .peek(field -> field.setAccessible(true))
                    .collect(Collectors.toMap(
                        Field::getName,
                        Function.identity()
                    )
                );
            }
        });

    /**
     * Cache of classes to their empty constructors
     */
    private static final LoadingCache<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = CacheBuilder.newBuilder()
        .weakKeys()
        .build(new CacheLoader<Class<?>, Constructor<?>>()
        {
            @Override
            public Constructor<?> load(Class<?> clazz) throws Exception
            {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);

                return constructor;
            }
        });

    /**
     * Determines whether a {@link Field} is a database column
     *
     * @param field The field
     *
     * @return If the field is a database column
     */
    private static boolean isDatabaseColumn(@Nonnull Field field)
    {
        return field.isAnnotationPresent(Column.class);
    }

    /**
     * Fetches a {@link Field} from a class by name
     *
     * @param clazz The class
     * @param name The field name
     *
     * @return The field
     */
    public static Field getField(@Nonnull Class<?> clazz, @Nonnull String name)
    {
        try
        {
            return FIELD_CACHE.get(clazz).get(name);
        }
        catch (ExecutionException ex)
        {
            Logs.severe("Error fetching field from cache");
            throw new RuntimeException(ex);
        }
    }

    /**
     * Fetches all {@link Field} objects from
     * a clazz pertaining to database columns
     *
     * @param clazz The class
     *
     * @return A collection of the fields
     */
    public static Collection<Field> getDatabaseFields(@Nonnull Class<?> clazz)
    {
        try
        {
            return FIELD_CACHE.get(clazz).values();
        }
        catch (ExecutionException ex)
        {
            Logs.severe("Error fetching fields from cache");
            throw new RuntimeException(ex);
        }
    }

    /**
     * Fetches the name of a database column from a {@link Field}
     *
     * @param field The field
     *
     * @return The column name
     */
    public static String getColumnName(@Nonnull Field field)
    {
        checkArgument(field.isAnnotationPresent(Column.class), "Field is not a column");

        String value = field.getAnnotation(Column.class).value();
        return value.equals("") ? field.getName() : value;
    }

    /**
     * Fetches the table a class should
     * be inserted to or queried from
     *
     * @param clazz The class
     *
     * @return The table name
     */
    public static String getTable(@Nonnull Class<?> clazz)
    {
        checkArgument(clazz.isAnnotationPresent(DatabaseObject.class), "Object class is not @DatabaseObject");

        return clazz.getAnnotation(DatabaseObject.class).table();
    }

    /**
     * Fetches the fields an object should insert by default
     *
     * @param object The object
     *
     * @return The fields
     */
    public static List<Field> getInsertFields(@Nonnull Object object)
    {
        Class<?> clazz = object.getClass();

        checkArgument(clazz.isAnnotationPresent(DatabaseObject.class), "Object class is not @DatabaseObject");

        return Stream.of(object.getClass().getAnnotation(DatabaseObject.class).insertFields())
            .map(field -> getField(clazz, field))
            .collect(Collectors.toList());
    }

    /**
     * Fetches the unique field of a class
     *
     * @param clazz The class
     * @return The field
     */
    public static Field getUniqueField(@Nonnull Class<?> clazz)
    {
        checkArgument(clazz.isAnnotationPresent(DatabaseObject.class), "Object class is not @DatabaseObject");

        String fieldName = clazz.getAnnotation(DatabaseObject.class).uniqueKeyField();

        checkArgument(!fieldName.isEmpty(), "Class does not contain a unique field");

        return getField(clazz, fieldName);
    }

    /**
     * Creates and returns a new instance
     * of a class from its empty constructor
     *
     * @see #CONSTRUCTOR_CACHE
     *
     * @param clazz The class
     *
     * @return The new instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(@Nonnull Class<T> clazz)
    {
        try
        {
            return (T) CONSTRUCTOR_CACHE.get(clazz).newInstance();
        }
        catch (ReflectiveOperationException ex)
        {
            Logs.severe("Error creating new instance of class from constructor");
            throw new RuntimeException(ex);
        }
        catch (ExecutionException ex)
        {
            Logs.severe("Error fetching constructor from cache");
            throw new RuntimeException(ex);
        }
    }
}
