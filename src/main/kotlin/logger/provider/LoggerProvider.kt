package app.logger.provider

import logger.core.Logger
import logger.core.MessageDispatcher
import logger.factory.ConsoleLoggerFactory
import logger.factory.FileLoggerFactory
import logger.model.LogLevel
import logger.model.SinkType
import logger.sink.Sink
import logger.util.Defaults

/**
 * Singleton provider for Logger instances supporting multiple sink types
 * and honoring level-to-sink mappings.
 */
object LoggerProvider {

    private var logger: Logger? = null
    private val defaultLevelToSinkTypes = DefaultLevelMapping.levelToSinkTypes

    /**
     * Creates or returns existing Logger singleton combining sinks as per DefaultLevelMapping.
     */
    fun getLogger(configMap: Map<String, String>): Logger {
        if (logger != null) return logger!!

        return try {
            // Parse min log level or use default
            val minLevelStr = configMap["log_level"] ?: Defaults.DEFAULT_LOG_LEVEL.name
            val minLogLevel = LogLevel.fromString(minLevelStr)
                ?: throw IllegalArgumentException("Invalid log_level: '$minLevelStr'")

            // Determine all sink types needed for levels >= minLogLevel
            val neededSinkTypes = defaultLevelToSinkTypes
                .filterKeys { it.priority >= minLogLevel.priority }
                .values
                .flatten()
                .toSet()

            if (neededSinkTypes.isEmpty()) {
                throw IllegalStateException("No sink types required for log level: $minLogLevel")
            }

            // Create sink instances (each only once)
            val sinkInstances: Map<SinkType, Sink> = neededSinkTypes.associateWith { sinkType ->
                try {
                    when (sinkType) {
                        SinkType.CONSOLE -> ConsoleLoggerFactory(configMap).createLogger().dispatcher
                            .sinksByLevel.values.flatten().firstOrNull()
                            ?: throw IllegalStateException("No ConsoleSink created")
                        SinkType.FILE -> FileLoggerFactory(configMap).createLogger().dispatcher
                            .sinksByLevel.values.flatten().firstOrNull()
                            ?: throw IllegalStateException("No FileSink created")
                        else -> throw IllegalArgumentException("Unsupported sink type: $sinkType")
                    }
                } catch (e: Exception) {
                    throw RuntimeException("Failed to create sink for type: $sinkType", e)
                }
            }

            // Build sink mapping per level based on DefaultLevelMapping
            val sinksByLevel = defaultLevelToSinkTypes
                .filterKeys { it.priority >= minLogLevel.priority }
                .mapValues { (_, sinkTypes) ->
                    val sinks = sinkTypes.mapNotNull { sinkInstances[it] }
                    if (sinks.isEmpty()) {
                        throw IllegalStateException("No sinks available for sink types: $sinkTypes")
                    }
                    sinks
                }

            val dispatcher = MessageDispatcher(sinksByLevel)
            logger = Logger(minLogLevel, dispatcher)

            logger!!
        } catch (ex: Exception) {
            println("LoggerProvider failed: ${ex.message}")
            ex.printStackTrace()
            throw RuntimeException("Logger initialization failed", ex)
        }
    }
}
