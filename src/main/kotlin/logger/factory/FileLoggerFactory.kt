package logger.factory

import logger.core.Logger
import logger.core.MessageDispatcher
import logger.model.LogLevel
import logger.sink.FileSink
import logger.sink.Sink
import logger.util.Defaults

class FileLoggerFactory(
    private val configMap: Map<String, String>
) : LoggerFactory {

    override fun createSink(): Sink = createFileSink()

    override fun createLogger(): Logger {
        val minLogLevel = parseLogLevel()

        val fileSink = createFileSink()

        val sinksByLevel = LogLevel.entries.associateWith { level ->
            if (level.priority >= minLogLevel.priority) listOf(fileSink) else emptyList()
        }

        val dispatcher = MessageDispatcher(sinksByLevel)
        return Logger(minLogLevel, dispatcher)
    }

    /**
     * Parses the log level from the config map or defaults.
     */
    private fun parseLogLevel(): LogLevel {
        return try {
            val levelStr = configMap["log_level"] ?: Defaults.DEFAULT_LOG_LEVEL.name
            LogLevel.fromString(levelStr) ?: Defaults.DEFAULT_LOG_LEVEL
        } catch (e: Exception) {
            System.err.println("Failed to parse log_level: ${e.message}")
            Defaults.DEFAULT_LOG_LEVEL
        }
    }

    /**
     * Parses the sink-specific configuration and returns a FileSink instance.
     */
    private fun createFileSink(): Sink {
        return try {
            val filePath = configMap["file_location"]
                ?: throw IllegalArgumentException("file_location missing in config")
            val maxFileSize = configMap["max_file_size"]?.toLongOrNull()
                ?: Defaults.DEFAULT_MAX_FILE_SIZE
            val backupCount = configMap["backup_count"]?.toIntOrNull()
                ?: Defaults.DEFAULT_BACKUP_COUNT
            val timestampFormat = configMap["ts_format"]
                ?: Defaults.DEFAULT_TIMESTAMP_FORMAT

            FileSink(
                filePath = filePath,
                maxFileSize = maxFileSize,
                backupCount = backupCount,
                timestampFormat = timestampFormat
            )
        } catch (e: Exception) {
            System.err.println("Failed to create FileSink: ${e.message}")
            throw RuntimeException("Unable to initialize FileSink", e)
        }
    }
}
