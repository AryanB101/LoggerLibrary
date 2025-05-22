package logger.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TimeUtil {
    /**
     * Format the given LocalDateTime according to the provided pattern.
     *
     * @param timestamp the time to format
     * @param pattern   a DateTimeFormatter pattern, e.g. "yyyy-MM-dd HH:mm:ss.SSS"
     * @return formatted timestamp string
     */
    fun format(timestamp: LocalDateTime, pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return timestamp.format(formatter)
    }
}
