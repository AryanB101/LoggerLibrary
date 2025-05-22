package logger.sink

import logger.model.LogMessage
import logger.util.Defaults
import java.time.format.DateTimeFormatter

/**
 * A sink that writes log messages to the console (stdout).
 *
 * @param timestampFormat pattern for formatting the timestamp, e.g. "yyyy-MM-dd HH:mm:ss.SSS"
 */
class ConsoleSink(
    private val timestampFormat: String = Defaults.DEFAULT_TIMESTAMP_FORMAT
) : Sink {

    private val tsFormatter = DateTimeFormatter.ofPattern(timestampFormat)

    override fun write(message: LogMessage) {
        // Format timestamp
        val ts = message.timestamp.format(tsFormatter)

        // Build the log line
        val base = "$ts [${message.level.name}] [${message.namespace}] ${message.content}"
        val withTracking = message.trackingId?.let { "$base (tid=$it)" } ?: base
        val withHost = message.hostName?.let { "$withTracking (host=$it)" } ?: withTracking

        // Print to console
        println(withHost)
    }
}
