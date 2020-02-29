package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.regret
import com.github.doomsdayrs.apps.shosetsu.backend.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.listeners.NovelFragmentUpdate
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.*

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
 * ====================================================================
 */
/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 *
 *
 * The page you see when you select a novel
 *
 */
class NovelFragmentInfo : ViewedController() {
    var novelFragment: NovelController? = null

    @Attach(id.fragment_novel_add)
    var fragmentNovelAdd: FloatingActionButton? = null

    @Attach(id.fragment_novel_main_refresh)
    var fragmentNovelMainRefresh: SwipeRefreshLayout? = null

    @Attach(id.fragment_novel_title)
    var fragmentNovelTitle: TextView? = null

    @Attach(id.fragment_novel_author)
    var fragmentNovelAuthor: TextView? = null

    @Attach(id.fragment_novel_status)
    var fragmentNovelStatus: TextView? = null

    @Attach(id.fragment_novel_description)
    var fragmentNovelDescription: TextView? = null

    @Attach(id.fragment_novel_publish)
    var fragmentNovelPublish: TextView? = null

    @Attach(id.fragment_novel_artists)
    var fragmentNovelArtists: TextView? = null

    @Attach(id.fragment_novel_genres)
    var fragmentNovelGenres: ChipGroup? = null

    @Attach(id.fragment_novel_formatter)
    var fragmentNovelFormatter: TextView? = null

    @Attach(id.fragment_novel_image)
    var fragmentNovelImage: ImageView? = null

    @Attach(id.fragment_novel_image_background)
    var fragmentNovelImageBackground: ImageView? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            id.source_migrate -> {
                val intent = Intent(activity, MigrationView::class.java)
                try {
                    val novelCards = ArrayList<NovelCard>()
                    novelCards.add(NovelCard(novelFragment!!.novelPage.title, novelFragment!!.novelID, novelFragment!!.novelURL, novelFragment!!.novelPage.imageURL, novelFragment!!.formatter.formatterID))
                    intent.putExtra("selected", Utilities.serializeToString(novelCards))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                intent.putExtra("target", 1)
                regret(context!!)
                // startActivity(intent)
                return true
            }
            id.webview -> {
                if (activity != null) Utilities.openInWebview(activity!!, novelFragment!!.novelURL)
                return true
            }
            id.browser -> {
                if (activity != null) Utilities.openInBrowser(activity!!, novelFragment!!.novelURL)
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_novel, menu)
        menu.findItem(id.source_migrate).isVisible = novelFragment != null && Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)
    }

    override val idRes: Int = R.layout.fragment_novel_main


    override fun onViewCreated(view: View) {
        Log.d("OnCreateView", "NovelFragmentMain")
        novelFragment = parentController as NovelController
        if (novelFragment != null) {
            novelFragment!!.novelFragmentInfo = this
        }
        novelFragment!!.fragmentNovelMainRefresh = view.findViewById(id.fragment_novel_main_refresh)


        fragmentNovelAdd?.hide()
        if (novelFragment != null && Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)) fragmentNovelAdd?.setImageResource(R.drawable.ic_baseline_check_circle_24)
        setData()
        fragmentNovelAdd?.setOnClickListener {
            if (novelFragment != null)
                if (!Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)) {
                    Database.DatabaseNovels.bookMark(novelFragment!!.novelID)
                    fragmentNovelAdd?.setImageResource(R.drawable.ic_baseline_check_circle_24)
                } else {
                    Database.DatabaseNovels.unBookmark(novelFragment!!.novelID)
                    fragmentNovelAdd?.setImageResource(R.drawable.ic_add_circle_outline_black_24dp)
                }
        }
        fragmentNovelMainRefresh?.setOnRefreshListener(NovelFragmentUpdate(this))
    }


    /**
     * Save data of view before destroyed
     *
     * @param outState output save
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("Saving Instance State", "NovelFragmentMain")
    }

    /**
     * Sets the data of this page
     */
    fun setData() {
        Utilities.setActivityTitle(activity, novelFragment!!.novelPage.title)
        if (view != null) {
            view!!.post {
                fragmentNovelTitle?.text = novelFragment!!.novelPage.title
                if (novelFragment!!.novelPage.authors.isNotEmpty()) fragmentNovelAuthor?.text = novelFragment!!.novelPage.authors.contentToString()
                fragmentNovelDescription?.text = novelFragment!!.novelPage.description
                if (novelFragment!!.novelPage.artists.isNotEmpty()) fragmentNovelArtists?.text = novelFragment!!.novelPage.artists.contentToString()
                fragmentNovelStatus?.text = novelFragment!!.status.status
                when (novelFragment!!.novelPage.status) {
                    Novel.Status.PAUSED -> {
                        fragmentNovelPublish?.setText(R.string.paused)
                    }
                    Novel.Status.COMPLETED -> {
                        fragmentNovelPublish?.setText(R.string.completed)
                    }
                    Novel.Status.PUBLISHING -> {
                        fragmentNovelPublish?.setText(R.string.publishing)
                    }
                    else -> fragmentNovelPublish?.setText(R.string.unknown)
                }
                if (context != null) {
                    val layoutInflater = LayoutInflater.from(context)
                    for (string in novelFragment!!.novelPage.genres) {
                        //            val chip: Chip = layoutInflater.inflate(R.layout.genre_chip, null, false) as Chip
                        //           chip.text = string
                        //              fragment_novel_genres!!.addView(chip)
                    }
                } else fragmentNovelGenres!!.visibility = View.GONE

                if (novelFragment!!.novelPage.imageURL.isNotEmpty()) {
                    Picasso.get().load(novelFragment!!.novelPage.imageURL).into(fragmentNovelImage)
                    Picasso.get().load(novelFragment!!.novelPage.imageURL).into(fragmentNovelImageBackground)
                }
                fragmentNovelAdd!!.show()
                fragmentNovelFormatter!!.text = novelFragment!!.formatter.name
            }
        } else Log.e("NovelFragmentInfo", "NovelFragmentInfo view is null")
    }

    /**
     * Constructor
     */
    init {
        setHasOptionsMenu(true)
    }
}