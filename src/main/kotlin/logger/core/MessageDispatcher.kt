package logger.core

import logger.model.LogLevel
import logger.model.LogMessage
import logger.sink.Sink

/**
 * Routes each LogMessage to the configured sinks for its level.
 *
 * @param sinksByLevel map of LogLevel → list of Sink instances to write to
 */
class MessageDispatcher(
    val sinksByLevel: Map<LogLevel, List<Sink>>
) {
    fun dispatch(message: LogMessage) {
        sinksByLevel[message.level]?.forEach { sink ->
            sink.write(message)
        }
    }
}
