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

    lateinit var fragment_novel_add: FloatingActionButton
    lateinit var fragment_novel_main_refresh: SwipeRefreshLayout
    lateinit var fragment_novel_title: TextView
    private lateinit var fragment_novel_author: TextView
    private lateinit var fragment_novel_status: TextView
    private lateinit var fragment_novel_description: TextView
    private lateinit var fragment_novel_publish: TextView
    private lateinit var fragment_novel_artists: TextView
    private lateinit var fragment_novel_genres: ChipGroup
    private lateinit var fragment_novel_formatter: TextView
    private lateinit var fragment_novel_image: ImageView
    private lateinit var fragment_novel_image_background: ImageView

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.source_migrate -> {
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
            R.id.webview -> {
                if (activity != null) Utilities.openInWebview(activity!!, novelFragment!!.novelURL)
                return true
            }
            R.id.browser -> {
                if (activity != null) Utilities.openInBrowser(activity!!, novelFragment!!.novelURL)
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_novel, menu)
        menu.findItem(R.id.source_migrate).isVisible = novelFragment != null && Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)
    }

    override val idRes: Int = R.layout.fragment_novel_main


    override fun onViewCreated(view: View) {
        Log.d("OnCreateView", "NovelFragmentMain")
        novelFragment = parentController as NovelController
        if (novelFragment != null) {
            novelFragment!!.novelFragmentInfo = this
        }
        fragment_novel_add = view.findViewById(R.id.fragment_novel_add)
        fragment_novel_title = view.findViewById(R.id.fragment_novel_title)
        fragment_novel_main_refresh = view.findViewById(R.id.fragment_novel_main_refresh)
        fragment_novel_author = view.findViewById(R.id.fragment_novel_author)
        fragment_novel_status = view.findViewById(R.id.fragment_novel_status)
        fragment_novel_description = view.findViewById(R.id.fragment_novel_description)
        fragment_novel_publish = view.findViewById(R.id.fragment_novel_publish)
        fragment_novel_artists = view.findViewById(R.id.fragment_novel_artists)
        fragment_novel_genres = view.findViewById(R.id.fragment_novel_genres)
        fragment_novel_formatter = view.findViewById(R.id.fragment_novel_formatter)
        fragment_novel_image = view.findViewById(R.id.fragment_novel_image)
        fragment_novel_image_background = view.findViewById(R.id.fragment_novel_image_background)
        novelFragment!!.fragmentNovelMainRefresh = view.findViewById(R.id.fragment_novel_main_refresh)


        fragment_novel_add.hide()
        if (novelFragment != null && Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)) fragment_novel_add.setImageResource(R.drawable.ic_baseline_check_circle_24)
        setData()
        fragment_novel_add.setOnClickListener {
            if (novelFragment != null)
                if (!Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)) {
                    Database.DatabaseNovels.bookMark(novelFragment!!.novelID)
                    fragment_novel_add.setImageResource(R.drawable.ic_baseline_check_circle_24)
                } else {
                    Database.DatabaseNovels.unBookmark(novelFragment!!.novelID)
                    fragment_novel_add.setImageResource(R.drawable.ic_add_circle_outline_black_24dp)
                }
        }
        fragment_novel_main_refresh.setOnRefreshListener(NovelFragmentUpdate(this))
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
                fragment_novel_title.text = novelFragment!!.novelPage.title
                if (novelFragment!!.novelPage.authors.isNotEmpty()) fragment_novel_author.text = novelFragment!!.novelPage.authors.contentToString()
                fragment_novel_description.text = novelFragment!!.novelPage.description
                if (novelFragment!!.novelPage.artists.isNotEmpty()) fragment_novel_artists.text = novelFragment!!.novelPage.artists.contentToString()
                fragment_novel_status.text = novelFragment!!.status.status
                when (novelFragment!!.novelPage.status) {
                    Novel.Status.PAUSED -> {
                        fragment_novel_publish.setText(R.string.paused)
                    }
                    Novel.Status.COMPLETED -> {
                        fragment_novel_publish.setText(R.string.completed)
                    }
                    Novel.Status.PUBLISHING -> {
                        fragment_novel_publish.setText(R.string.publishing)
                    }
                    else -> fragment_novel_publish.setText(R.string.unknown)
                }
                if (context != null) {
                    val layoutInflater = LayoutInflater.from(context)
                    for (string in novelFragment!!.novelPage.genres) {
            //            val chip: Chip = layoutInflater.inflate(R.layout.genre_chip, null, false) as Chip
             //           chip.text = string
          //              fragment_novel_genres!!.addView(chip)
                    }
                } else fragment_novel_genres!!.visibility = View.GONE

                if (novelFragment!!.novelPage.imageURL.isNotEmpty()) {
                    Picasso.get().load(novelFragment!!.novelPage.imageURL).into(fragment_novel_image)
                    Picasso.get().load(novelFragment!!.novelPage.imageURL).into(fragment_novel_image_background)
                }
                fragment_novel_add!!.show()
                fragment_novel_formatter!!.text = novelFragment!!.formatter.name
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