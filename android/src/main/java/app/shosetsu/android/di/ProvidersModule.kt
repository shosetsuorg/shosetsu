package app.shosetsu.android.di

import app.shosetsu.android.providers.file.base.IFileSystemProvider
import app.shosetsu.android.providers.file.impl.AndroidFileSystemProvider
import app.shosetsu.android.providers.prefrences.SharedPreferenceProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

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
 * 23 / 10 / 2020
 */

val providersModule = DI.Module("providers_module") {
	bind<SharedPreferenceProvider>() with singleton { SharedPreferenceProvider((instance())) }
	bind<IFileSystemProvider>() with singleton { AndroidFileSystemProvider(instance()) }
}