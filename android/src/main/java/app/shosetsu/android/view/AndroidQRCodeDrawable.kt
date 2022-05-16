package app.shosetsu.android.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import app.shosetsu.android.common.CompressionException
import io.github.g0dkar.qrcode.render.QRCodeCanvas
import java.io.OutputStream

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
 */

/**
 * Shosetsu
 *
 * @since 05 / 03 / 2022
 * @author Doomsdayrs
 */
class AndroidQRCodeDrawable(
	width: Int,
	height: Int
) : QRCodeCanvas<Bitmap>(width, height) {

	override val image: Bitmap =
		createBitmap(width, height)

	override fun drawImage(img: QRCodeCanvas<*>, x: Int, y: Int) {
		image.applyCanvas {
			val bytes = img.toByteArray()
			val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

			drawBitmap(
				bitmap,
				x.toFloat(),
				y.toFloat(),
				null
			)
		}
	}

	override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
		image.applyCanvas {
			this.drawLine(
				x1.toFloat(),
				y1.toFloat(),
				x2.toFloat(),
				y2.toFloat(),
				Paint().apply {
					style = Paint.Style.STROKE
					this.color = color
				})
		}
	}

	override fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
		image.applyCanvas {
			this.drawRect(
				x.toFloat(),
				y.toFloat(),
				width.toFloat(),
				height.toFloat(),
				Paint().apply {
					style = Paint.Style.STROKE
					this.color = color
				})
		}
	}

	override fun drawRoundRect(
		x: Int,
		y: Int,
		width: Int,
		height: Int,
		borderRadius: Int,
		color: Int
	) {
		image.applyCanvas {
			this.drawRoundRect(
				x.toFloat(),
				y.toFloat(),
				width.toFloat(),
				height.toFloat(),
				borderRadius.toFloat(),
				borderRadius.toFloat(),
				Paint().apply {
					style = Paint.Style.STROKE
					this.color = color
				})
		}
	}

	override fun fillRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
		image.applyCanvas {
			this.drawRect(
				x.toFloat(),
				y.toFloat(),
				width.toFloat(),
				height.toFloat(),
				Paint().apply {
					style = Paint.Style.FILL
					this.color = color
				})
		}
	}

	override fun fillRoundRect(
		x: Int,
		y: Int,
		width: Int,
		height: Int,
		borderRadius: Int,
		color: Int
	) {
		image.applyCanvas {
			this.drawRoundRect(
				x.toFloat(),
				y.toFloat(),
				width.toFloat(),
				height.toFloat(),
				borderRadius.toFloat(),
				borderRadius.toFloat(),
				Paint().apply {
					style = Paint.Style.FILL
					this.color = color
				}
			)
		}
	}

	@Throws(CompressionException::class)
	override fun writeImage(outputStream: OutputStream) {
		val result = image.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
		if (!result)
			throw CompressionException()
	}

}