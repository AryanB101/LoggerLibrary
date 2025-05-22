package logger.model

/**
 * Identifies the part of the application sending the log.
 */
enum class Namespace {
    AUTH,
    CACHE,
    DB,
    SYSTEM,
    // add more as needed
}
