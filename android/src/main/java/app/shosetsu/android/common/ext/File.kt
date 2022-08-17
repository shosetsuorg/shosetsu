package app.shosetsu.android.common.ext

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import app.shosetsu.android.BuildConfig.APPLICATION_ID
import java.io.File

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
 * ====================================================================
 */

/**
 * shosetsu
 * 20 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */


fun File.getUriCompat(context: Context): Uri {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		FileProvider.getUriForFile(context, "$APPLICATION_ID.provider", this)
	} else {
		this.toUri()
	}
}
