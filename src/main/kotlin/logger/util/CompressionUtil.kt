package logger.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream

object CompressionUtil {
    /**
     * Compresses the input file into the output file using GZIP format.
     *
     * @param inputFile The file to compress.
     * @param outputFile The destination .gz file.
     */
    fun compressFile(inputFile: File, outputFile: File) {
        FileInputStream(inputFile).use { fis ->
            GZIPOutputStream(FileOutputStream(outputFile)).use { gos ->
                fis.copyTo(gos)
            }
        }
    }
}
