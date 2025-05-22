package logger.util

import logger.model.LogLevel

object Defaults {
    const val DEFAULT_MAX_FILE_SIZE: Long = 2 * 1024 * 1024  // 2 MB
    const val DEFAULT_BACKUP_COUNT: Int = 3
    const val DEFAULT_TIMESTAMP_FORMAT: String = "yyyy-MM-dd_HH-mm-ss"
    val DEFAULT_LOG_LEVEL: LogLevel = LogLevel.INFO
}
