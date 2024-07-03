package avelios.starwarsreferenceapp.data.local

import androidx.room.TypeConverter

/**
 * Provides type converters to convert complex data types to and from database-supported types.
 */
class Converters {
    /**
     * Converts a comma-separated string to a list of strings.
     *
     * @param value The comma-separated string.
     * @return The list of strings.
     */
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    /**
     * Converts a list of strings to a comma-separated string.
     *
     * @param list The list of strings.
     * @return The comma-separated string.
     */
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}