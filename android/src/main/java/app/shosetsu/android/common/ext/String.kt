package app.shosetsu.android.common.ext

import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * 04 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */


/**
 * Deserialize a string to the object
 *
 * @return Object from string
 * @throws IOException            exception
 * @throws ClassNotFoundException exception
 */
@Throws(IOException::class, ClassNotFoundException::class)
inline fun <reified R> String.deserializeString(): R? {
	var editString = this
	if (editString != "serial-null") {
		editString = editString.substring(7)
		//Log.d("Deserialize", string);
		val bytes = Base64.decode(editString, Base64.NO_WRAP)
		val byteArrayInputStream = ByteArrayInputStream(bytes)
		val objectInputStream = ObjectInputStream(byteArrayInputStream)
		return objectInputStream.readObject() as R
	}
	return null
}

/**
 * Converts a String Array back into an Array of Strings
 *
 * @return Array of Strings
 */
fun String.convertStringToArray(): Array<String> {
	val a = substring(1, length - 1).split(", ".toRegex()).toTypedArray()
	for (x in a.indices) {
		a[x] = a[x].replace(">,<", ",")
	}
	return a
}

/**
 * Makes an MD5 of the string
 */
fun String.md5(): String {
	try {
		// Create MD5 Hash
		val digest = MessageDigest.getInstance("MD5")
		digest.update(toByteArray())
		val messageDigest = digest.digest()
		// Create Hex String
		val hexString = StringBuffer()
		for (i in messageDigest.indices)
			hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
		return hexString.toString()
	} catch (e: NoSuchAlgorithmException) {
		Log.wtf(logID(), "How could an MD5 alg be missing", e)
	}
	return ""
}