package app.shosetsu.android.view.compose

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.PagingDataDiffer
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.flow.Flow

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
 * @since 14 / 05 / 2022
 * @author Doomsdayrs
 */

/**
 * Adds the [LazyPagingItems] and their content to the scope where the content of an item is
 * aware of its local index. The range from 0 (inclusive) to [LazyPagingItems.itemCount] (exclusive)
 * always represents the full range of presentable items, because every event from
 * [PagingDataDiffer] will trigger a recomposition.
 *
 *
 * @param items the items received from a [Flow] of [PagingData].
 * @param key a factory of stable and unique keys representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. If null is passed the position in the list will represent the key.
 * When you specify the key the scroll position will be maintained based on the key, which
 * means if you add/remove items before the current visible item the item with the given key
 * will be kept as the first visible one.
 * @param itemContent the content displayed by a single item. In case the item is `null`, the
 * [itemContent] method should handle the logic of displaying a placeholder instead of the main
 * content displayed by an item which is not `null`.
 */
fun <T : Any> LazyGridScope.itemsIndexed(
	items: LazyPagingItems<T>,
	key: ((index: Int, item: T) -> Any)? = null,
	itemContent: @Composable LazyGridItemScope.(index: Int, value: T?) -> Unit
) {
	items(
		count = items.itemCount,
		key = if (key == null) null else { index ->
			val item = items.peek(index)
			if (item == null) {
				PagingPlaceholderKey(index)
			} else {
				key(index, item)
			}
		}
	) { index ->
		itemContent(index, items[index])
	}
}

data class PagingPlaceholderKey(private val index: Int) : Parcelable {
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(index)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object {
		@Suppress("unused")
		@JvmField
		val CREATOR: Parcelable.Creator<PagingPlaceholderKey> =
			object : Parcelable.Creator<PagingPlaceholderKey> {
				override fun createFromParcel(parcel: Parcel) =
					PagingPlaceholderKey(parcel.readInt())

				override fun newArray(size: Int) = arrayOfNulls<PagingPlaceholderKey?>(size)
			}
	}
}