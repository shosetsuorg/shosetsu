package app.shosetsu.android.common.ext

import android.widget.ImageView
import com.github.doomsdayrs.apps.shosetsu.R
import com.squareup.picasso.Picasso

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 13 / 09 / 2020
 */


fun picasso(source: String, into: ImageView) = Picasso.get()
		.load(source)
		.placeholder(R.drawable.animated_refresh)
		.error(R.drawable.ic_broken_image_24dp)
		.into(into)