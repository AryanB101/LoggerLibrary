package logger.sink

import logger.model.LogMessage
import java.io.File
import java.io.FileWriter

class HashMap(
    private val flushThreshold: Int = 5,
    private val logFile: File = File("logs/hashmap_flush.log")
) : Sink {
    private val buffer = mutableMapOf<Int, String>()
    private var currentIndex = 0

    override fun write(message: LogMessage) {
        buffer[currentIndex++] = "[${message.level}] [${message.timestamp}] - ${message.content}"

        if(buffer.size >= flushThreshold){
            flushToFile()
        }
    }

    private fun flushToFile(){
        logFile.parentFile?.mkdirs()
        FileWriter(logFile, true).use { writer ->
            buffer.forEach { (key, value) ->
                writer.write("$key = $value\n")
            }
            writer.write("--Batch flushed--\n")
        }
        buffer.clear()
        currentIndex = 0
    }
}