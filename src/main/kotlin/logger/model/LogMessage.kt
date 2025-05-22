package logger.model

import java.time.LocalDateTime

data class LogMessage(
    val content: String,
    val level: LogLevel,
    val namespace: Namespace,
    val timestamp: LocalDateTime,
    val trackingId: String? = null,
    val hostName: String? = null
)
