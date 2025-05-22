package logger.factory

import logger.core.Logger
import logger.core.MessageDispatcher
import logger.model.LogLevel
import logger.sink.ConsoleSink
import logger.sink.Sink
import logger.util.Defaults
import logger.util.Defaults.DEFAULT_TIMESTAMP_FORMAT

class ConsoleLoggerFactory(
    private val configMap: Map<String, String>
) : LoggerFactory {

    override fun createSink(): Sink = ConsoleSink(DEFAULT_TIMESTAMP_FORMAT)

    override fun createLogger(): Logger {
        val logLevelStr = configMap["log_level"] ?: Defaults.DEFAULT_LOG_LEVEL.name
        val minLogLevel = LogLevel.fromString(logLevelStr) ?: Defaults.DEFAULT_LOG_LEVEL

        val consoleSink = ConsoleSink()
        println("Parsed minLogLevel = $minLogLevel")

        val sinksByLevel = LogLevel.entries.associateWith { level ->
            if (level.priority >= minLogLevel.priority) listOf(consoleSink) else emptyList()
        }

        val dispatcher = MessageDispatcher(sinksByLevel)
        return Logger(minLogLevel, dispatcher)
    }
}

