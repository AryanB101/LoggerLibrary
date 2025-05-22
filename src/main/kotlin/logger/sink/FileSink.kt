package logger.sink

import logger.model.LogMessage
import logger.util.Defaults
import logger.util.TimeUtil
import java.io.*
import java.time.format.DateTimeFormatter

/**
 * A file-based sink with size-based rotation and GZIP compression.
 *
 * @param filePath       Path to the active log file.
 * @param maxFileSize    Max size in bytes before rotating.
 * @param backupCount    How many compressed archives to keep.
 * @param timestampFormat Format for timestamps in file names.
 */
class FileSink(
    private val filePath: String,
    private val maxFileSize: Long = Defaults.DEFAULT_MAX_FILE_SIZE,
    private val backupCount: Int = Defaults.DEFAULT_BACKUP_COUNT,
    private val timestampFormat: String = Defaults.DEFAULT_TIMESTAMP_FORMAT
) : Sink {

    private var writer: BufferedWriter = createWriter()
    private val fmt = DateTimeFormatter.ofPattern(timestampFormat)

    @Synchronized
    override fun write(message: LogMessage) {
        try {
            val line = formatMessage(message)
            writer.appendLine(line)
            writer.flush()

            if (currentFileSize() >= maxFileSize) {
                rotateFiles()
            }
        } catch (e: IOException) {
            System.err.println("Error writing log message: ${e.message}")
            // Optionally recover or rethrow depending on requirements
        } catch (e: Exception) {
            System.err.println("Unexpected error in write(): ${e.message}")
        }
    }

    @Synchronized
    override fun close() {
        try {
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            System.err.println("Error closing writer: ${e.message}")
        } catch (e: Exception) {
            System.err.println("Unexpected error in close(): ${e.message}")
        }
    }

    private fun createWriter(): BufferedWriter {
        return try {
            val file = File(filePath)
            file.parentFile?.mkdirs()
            val fos = FileOutputStream(file, true) // append mode
            BufferedWriter(OutputStreamWriter(fos))
        } catch (e: IOException) {
            System.err.println("Failed to create BufferedWriter for $filePath: ${e.message}")
            throw RuntimeException("Unable to create writer for $filePath", e)
        }
    }

    private fun currentFileSize(): Long =
        try {
            File(filePath).length()
        } catch (e: Exception) {
            System.err.println("Failed to get file size for $filePath: ${e.message}")
            0L
        }

    private fun rotateFiles() {
        try {
            writer.close()

            // Step 1: Delete oldest backup if it exists
            val oldest = File("$filePath-$backupCount.gz")
            if (oldest.exists()) {
                if (!oldest.delete()) {
                    System.err.println("Failed to delete oldest backup file: ${oldest.absolutePath}")
                }
            }

            // Step 2: Shift backups: -2.gz → -3.gz, -1.gz → -2.gz, etc.
            for (i in (backupCount - 1) downTo 1) {
                val src = File("$filePath-$i.gz")
                val dst = File("$filePath-${i + 1}.gz")
                if (src.exists()) {
                    if (!src.renameTo(dst)) {
                        System.err.println("Failed to rename backup $src to $dst")
                    }
                }
            }

            // Step 3: Compress current file to -1.gz
            val rotatedFile = File(filePath)
            val compressed = File("$filePath-1.gz")
            try {
                logger.util.CompressionUtil.compressFile(rotatedFile, compressed)
            } catch (e: Exception) {
                System.err.println("Compression failed: ${e.message}")
            }

            // Step 4: Truncate original file
            try {
                FileOutputStream(rotatedFile, false).close()
            } catch (e: IOException) {
                System.err.println("Failed to truncate log file: ${e.message}")
            }

            // Step 5: Reopen writer for new log file
            writer = createWriter()

        } catch (e: Exception) {
            System.err.println("Error during log rotation: ${e.message}")
        }
    }

    private fun formatMessage(msg: LogMessage): String {
        val ts = TimeUtil.format(msg.timestamp, "yyyy-MM-dd HH:mm:ss.SSS")
        val base = "$ts [${msg.level.name}] [${msg.namespace}] ${msg.content}"
        val withTracking = msg.trackingId?.let { "$base (tid=$it)" } ?: base
        return msg.hostName?.let { "$withTracking (host=$it)" } ?: withTracking
    }
}
