package app.logger.factory

import logger.core.Logger
import logger.core.MessageDispatcher
import logger.factory.LoggerFactory
import logger.model.LogLevel
import logger.sink.ConsoleSink
import logger.sink.HashMap
import logger.sink.Sink
import logger.util.Defaults

class HashMapLoggerFactory (
    private val configMap: Map<String, String>
) : LoggerFactory{
    override fun createSink(): Sink {
        TODO("Not yet implemented")
    }

    override fun createLogger(): Logger {
        val logLevelStr = configMap["log_level"] ?: Defaults.DEFAULT_LOG_LEVEL.name
        val minLogLevel = LogLevel.fromString(logLevelStr) ?: Defaults.DEFAULT_LOG_LEVEL

        val hashMapSink = HashMap()
        val sinksByLevel = LogLevel.entries.associateWith { level ->
            if (level.priority >= minLogLevel.priority) listOf(hashMapSink) else emptyList()
        }

        val dispatcher = MessageDispatcher(sinksByLevel)
        return Logger(minLogLevel, dispatcher)
    }

}