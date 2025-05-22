package logger.model

enum class LogLevel(val priority: Int) {
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    FATAL(5);

    companion object {
        fun fromString(level: String): LogLevel? = try {
            valueOf(level.uppercase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
