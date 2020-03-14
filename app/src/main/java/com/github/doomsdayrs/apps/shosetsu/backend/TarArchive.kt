package com.github.doomsdayrs.apps.shosetsu.backend


import org.kamranzafar.jtar.*
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

class TarArchive(file:String):File(file) {


    fun read(name: String): InputStream {

        TarInputStream(this.inputStream()).use {

            generateSequence { it.nextEntry }.find {entry-> entry.name.contains(name, true) }
            return it.readBytes().inputStream()

        }

    }



    fun write(name: String, buffer: ByteArray) {
        TarOutputStream(this, true).use {
            val header = TarHeader()

            header.name = StringBuffer(name)
            header.size = buffer.size.toLong()
            it.putNextEntry(TarEntry(header))
            it.write(buffer)
        }
    }



    fun delete(filename: String) {
        var offset: Long = 0
        var nextOffset: Long = 0
        var entry:TarEntry? = null

        TarInputStream(this.inputStream()).use {

            entry = generateSequence { it.nextEntry }.find { TE-> TE.name.contains(filename, true) }
            offset = it.currentOffset - TarConstants.HEADER_BLOCK
            it.nextEntry
            nextOffset = it.currentOffset - TarConstants.HEADER_BLOCK

        }

       if (entry!=null){
            val buffer = ByteArray((this.length() - nextOffset).toInt())
           RandomAccessFile(this, "rw").use {

               it.seek(nextOffset)
               it.read(buffer)
               it.seek(offset)
               it.write(buffer)
               it.setLength(this.length() - (nextOffset - offset))
           }

        }

        if (this.length()<1024+512)this.delete()
    }



}