package logger.factory

import logger.core.Logger
import logger.sink.Sink

interface LoggerFactory {
    fun createLogger(): Logger
    fun createSink(): Sink
}
