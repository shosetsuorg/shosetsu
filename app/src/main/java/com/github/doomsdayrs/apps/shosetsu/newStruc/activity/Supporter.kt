package com.github.doomsdayrs.apps.shosetsu.newStruc.activity

import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar

interface Supporter {
	fun setTitle(name: String?)

	fun getSupportActionBar(): ActionBar?
	fun setSupportActionBar(toolbar: Toolbar?)
}