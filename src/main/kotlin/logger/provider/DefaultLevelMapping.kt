package app.logger.provider

import logger.model.LogLevel
import logger.model.SinkType

/**
 * Default, internal mapping of LogLevels → SinkTypes.
 * Can be replaced or extended by custom configurations if needed.
 */
object DefaultLevelMapping {
    val levelToSinkTypes: Map<LogLevel, List<SinkType>> = mapOf(
        LogLevel.DEBUG to listOf(SinkType.FILE),
        LogLevel.INFO  to listOf(SinkType.FILE),
        LogLevel.WARN  to listOf(SinkType.FILE),
        LogLevel.ERROR to listOf(SinkType.FILE, SinkType.CONSOLE),
        LogLevel.FATAL to listOf(SinkType.FILE, SinkType.CONSOLE)
    )
}