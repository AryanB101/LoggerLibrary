package app

import app.logger.provider.LoggerProvider
import logger.model.Namespace

fun main() {
    // 1. Hardcoded demo values
    val userId: String     = "demoUser"
    val hostName: String   = "host-01"
    val trackingId: String = "txn-000"

    // 2. Logger configuration
    val configMap = mapOf(
        "ts_format"      to "yyyy-MM-dd HH:mm:ss.SSS",
        "log_level"      to "DEBUG",

        // FileSink options
        "file_location"  to "logs/application.log",
        "max_file_size"  to "10485",   // 1 mb
        "backup_count"   to "3",

        // DatabaseSink options
        "dbhost"         to "127.0.0.1",
        "dbport"         to "5432",
        "dbuser"         to "logwriter",
        "dbpass"         to "s3cr3t"
    )

    // 3. Create the logger
    val logger = LoggerProvider.getLogger(configMap)


    // 4. Emit some log entries
    repeat(10){
        logger.info(
            namespace  = Namespace.AUTH,
            message    = "User $userId logged in",
            trackingId = trackingId,
            hostName   = hostName
        )
    }
    logger.debug(
        namespace  = Namespace.AUTH,
        message    = "Starting authentication flow for user $userId",
        trackingId = trackingId,
        hostName   = hostName
    )

    logger.info(
        namespace  = Namespace.AUTH,
        message    = "User $userId logged in",
        trackingId = trackingId,
        hostName   = hostName
    )

    logger.warn(
        namespace  = Namespace.CACHE,
        message    = "Cache miss for user $userId",
        trackingId = trackingId,
        hostName   = hostName
    )

    logger.error(
        namespace  = Namespace.DB,
        message    = "Failed to load profile for $userId",
        trackingId = trackingId,
        hostName   = hostName
    )

    logger.fatal(
        namespace  = Namespace.SYSTEM,
        message    = "OutOfMemoryError encountered!",
        trackingId = trackingId,
        hostName   = hostName
    )
}
