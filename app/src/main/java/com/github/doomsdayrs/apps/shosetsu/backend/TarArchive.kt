package com.github.doomsdayrs.apps.shosetsu.backend

import org.kamranzafar.jtar.*
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

class TarArchive(val file: File)

{


fun readTar(name: String):InputStream{
    val tar =TarInputStream(file.inputStream())
    var entry = tar.nextEntry
    var buffer = ByteArray(0)
     while (entry!=null){
         if (entry.name.contains(name,true)){
             buffer = ByteArray(entry.size.toInt())
             tar.read(buffer)
             break
         }
         entry = tar.nextEntry
     }
    tar.close()
 return buffer.inputStream()
}

fun writeTar(name:String,buffer:ByteArray) {
    val t = TarOutputStream(file,true)
    val header = TarHeader()
    header.name = StringBuffer(name)
    header.size = buffer.size.toLong()
    t.putNextEntry(TarEntry(header))
    t.write(buffer)
    t.close()
}

fun deleteTar(name:String){
    val tar =TarInputStream(file.inputStream())
    var entry = tar.nextEntry
    var offset :Long= 0
    var delete = false
    var entries = 0
    var nextoffset:Long = file.length()
    while (entry!=null){
        entries += 1
        if (entry.name!!.contentEquals(name)){
            entries -= 1
            delete = true
            offset = tar.currentOffset - TarConstants.HEADER_BLOCK
            if (tar.nextEntry!=null) {
                entries += 1
                nextoffset = tar.currentOffset - TarConstants.HEADER_BLOCK
            }
            break
        }
        entry = tar.nextEntry
    }
    tar.close()
    if (delete){
    val buffer = ByteArray((file.length()-nextoffset).toInt())
    val raf = RandomAccessFile(file, "rw")
        raf.seek(nextoffset)
        raf.read(buffer)
    raf.seek(offset)
    raf.write(buffer)
        raf.setLength(file.length()-(nextoffset-offset))
raf.close()

}
if (entries<1)file.delete()

}








}