package app.shosetsu.android.common.ext

import org.joda.time.DateTime
import java.util.*

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
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
/**
 * @return this [DateTime] without any value lower then a day
 */
fun DateTime.trimDate(): DateTime {
	val cal = Calendar.getInstance()
	cal.clear() // as per BalusC comment.
	cal.time = toDate()
	cal[Calendar.HOUR_OF_DAY] = 0
	cal[Calendar.MINUTE] = 0
	cal[Calendar.SECOND] = 0
	cal[Calendar.MILLISECOND] = 0
	return DateTime(cal.timeInMillis)
}