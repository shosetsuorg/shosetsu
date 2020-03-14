package com.github.doomsdayrs.apps.shosetsu.backend

import org.kamranzafar.jtar.*
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

class TarArchive(file:String):File(file) {


    fun read(name: String): InputStream {
        val tar = TarInputStream(this.inputStream())

        generateSequence { tar.nextEntry }.find {  it.name.contains(name,true) }

        val buffer = tar.readBytes()
        tar.close()
        return buffer.inputStream()
    }



    fun write(name: String, buffer: ByteArray) {
        val t = TarOutputStream(this, true)
        val header = TarHeader()

        header.name = StringBuffer(name)
        header.size = buffer.size.toLong()
        t.putNextEntry(TarEntry(header))
        t.write(buffer)
        t.close()
    }



    fun delete(filename: String) {
        val tar = TarInputStream(this.inputStream())
        val offset: Long
        val nextoffset:Long
        val entry =  generateSequence { tar.nextEntry }.find { it.name.contains(filename,true) }

        if (entry!=null){
            offset = tar.currentOffset - TarConstants.HEADER_BLOCK
            tar.nextEntry
            nextoffset = tar.currentOffset - TarConstants.HEADER_BLOCK

            tar.close()

            val buffer = ByteArray((this@TarArchive.length() - nextoffset).toInt())
            val raf = RandomAccessFile(this@TarArchive, "rw")

            raf.seek(nextoffset)
            raf.read(buffer)
            raf.seek(offset)
            raf.write(buffer)
            raf.setLength(this@TarArchive.length() - (nextoffset - offset))
            raf.close()
        }
        tar.close()

        if (this.length()<1024+512)this.delete()
    }



}