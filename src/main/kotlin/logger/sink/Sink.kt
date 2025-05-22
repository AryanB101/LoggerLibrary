package logger.sink

import logger.model.LogMessage

/**
 * A generic log destination.
 */
interface Sink {

    fun write(message: LogMessage)

    /**
     * Flush and release any resources. Default no-op.
     */
    fun close() {}
}
