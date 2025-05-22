package logger.core

import logger.model.LogLevel
import logger.model.LogMessage
import logger.model.Namespace
import java.time.LocalDateTime

/**
 * Main entry point for client code.
 *
 * Usage:
 *   val logger = Logger(minLogLevel, dispatcher)
 *   logger.info("auth", "User logged in")
 *   logger.error("db", "Failed to connect", trackingId = "abc123")
 */
class Logger internal constructor(
    val minLogLevel: LogLevel,
    val dispatcher: MessageDispatcher
) {

    fun log(
        level: LogLevel,
        namespace: Namespace,
        message: String,
        trackingId: String? = null,
        hostName: String? = null
    ) {
        try {
            if (!LogLevel.entries.toTypedArray().contains(level)) {
                throw IllegalArgumentException("Invalid log level: $level")
            }
            if (!Namespace.entries.toTypedArray().contains(namespace)) {
                throw IllegalArgumentException("Invalid namespace: $namespace")
            }
            if (level.priority < minLogLevel.priority) return

            val now = LocalDateTime.now()
            val logMsg = LogMessage(
                content = message,
                level = level,
                namespace = namespace,
                timestamp = now,
                trackingId = trackingId,
                hostName = hostName
            )

            dispatcher.dispatch(logMsg)

        } catch (e: Exception) {
            System.err.println("Logger failed to dispatch message: ${e.message}")
            e.printStackTrace()
        }
    }

    // Convenience methods
    fun debug(
        namespace: Namespace,
        message: String,
        trackingId: String? = null,
        hostName: String? = null
    ) = log(LogLevel.DEBUG, namespace, message, trackingId, hostName)

    fun info(
        namespace: Namespace,
        message: String,
        trackingId: String? = null,
        hostName: String? = null
    ) = log(LogLevel.INFO, namespace, message, trackingId, hostName)

    fun warn(
        namespace: Namespace,
        message: String,
        trackingId: String? = null,
        hostName: String? = null
    ) = log(LogLevel.WARN, namespace, message, trackingId, hostName)

    fun error(
        namespace: Namespace,
        message: String,
        trackingId: String? = null,
        hostName: String? = null
    ) = log(LogLevel.ERROR, namespace, message, trackingId, hostName)

    fun fatal(
        namespace: Namespace,
        message: String,
        trackingId: String? = null,
        hostName: String? = null
    ) = log(LogLevel.FATAL, namespace, message, trackingId, hostName)

}
