package app.shosetsu.android.view.uimodels.settings

import android.view.Gravity
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setPadding
import app.shosetsu.android.view.uimodels.settings.dsl.onButtonClicked
import app.shosetsu.android.view.uimodels.settings.dsl.text
import com.github.doomsdayrs.apps.shosetsu.databinding.SettingsItemBinding
import kotlin.math.absoluteValue


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
 * 21 / 02 / 2021
 */
class DoubleNumberSettingData(id: Int) : ButtonSettingData(id) {
	var maxWhole: Int = 10
	var minWhole: Int = -10

	var step: Double = 0.1

	val wholeSteps: List<Int> by lazy {
		ArrayList<Int>().apply {
			var position: Int = minWhole
			do {
				add(position)
				position += 1
			} while (position <= maxWhole)
		}
	}

	val decimalSteps: List<Int> by lazy {
		ArrayList<Int>().apply {
			val wholeStep = (step * 100).toInt()
			var position = 0
			do {
				add(position)
				position += wholeStep
			} while (position < 100)
		}
	}

	var initialWhole: Int = 0

	var initialDecimal: Int = 0

	var onValueSelected: (Double) -> Unit = {}

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		text { "${wholeSteps[initialWhole]}.${decimalSteps[initialDecimal]}" }

		onButtonClicked {
			val context = holder.root.context
			Builder(context).apply {
				if (titleRes != -1)
					setTitle(titleRes)
				else
					setTitle(titleText)

				setView(LinearLayoutCompat(context).apply {
					orientation = LinearLayoutCompat.HORIZONTAL
					gravity = Gravity.CENTER
					setPadding(16)
					var selectedWhole = initialWhole
					var selectedDecimal = initialDecimal

					addView(NumberPicker(context).apply {
						minValue = 0
						maxValue = wholeSteps.size - 1
						value = initialWhole
						wrapSelectorWheel = false

						setFormatter {
							"${wholeSteps[it]}"
						}
						setOnValueChangedListener { _, _, newVal ->
							selectedWhole = wholeSteps[newVal]
							initialWhole = newVal
						}
					})
					addView(AppCompatTextView(context).apply {
						text = "."
					})
					addView(NumberPicker(context).apply {
						minValue = 0
						maxValue = decimalSteps.size - 1
						value = initialDecimal
						wrapSelectorWheel = false

						setFormatter {
							"0${decimalSteps[it]}"
						}
						setOnValueChangedListener { _, _, newVal ->
							selectedDecimal = decimalSteps[newVal]
							initialDecimal = newVal
						}
					})

					setPositiveButton(android.R.string.ok) { d, w ->
						var value = 0.0
						value += selectedDecimal.toDouble() / 100.0
						value += selectedWhole.toDouble().absoluteValue
						if (selectedWhole < 0)
							value *= -1

						onValueSelected(value)
						holder.button.text = "$value"
						d.dismiss()
					}
				})


			}.show()
		}
		super.bindBinding(holder, payloads)
	}
}