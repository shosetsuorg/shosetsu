package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Update;
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdateHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import java.util.ArrayList;
import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInBrowser;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInWebview;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleBookmarkChapter;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter;

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class UpdatersAdapter extends RecyclerView.Adapter<UpdateHolder> {
    public static int DefaultTextColor;
    private static boolean set = false;

    public final ArrayList<Update> updates;
    public final Activity activity;

    public UpdatersAdapter(ArrayList<Update> updates, Activity activity) {
        this.updates = updates;
        this.activity = activity;
    }


    @NonNull
    @Override
    public UpdateHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.update_card, viewGroup, false);
        UpdateHolder updateHolder = new UpdateHolder(view);
        if (!set) {
            DefaultTextColor = updateHolder.title.getCurrentTextColor();
            Log.i("TextDefaultColor", String.valueOf(DefaultTextColor));
            set = !set;
        }
        return updateHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateHolder updateHolder, int i) {
        Log.d("Binding", updates.get(i).CHAPTER_URL);
        updateHolder.setNovelChapter(Objects.requireNonNull(DatabaseChapter.getChapter(updates.get(i).CHAPTER_URL)));
        updateHolder.popupMenu.setOnMenuItemClickListener(menuItem -> {
            NovelPage novelPage = new NovelPage();
            String nURL = DatabaseChapter.getChapterNovelURL(updateHolder.novelChapter.link);

            if (nURL != null)
                novelPage = Database.DatabaseLibrary.getNovelPage(nURL);

            if (novelPage == null) {
                Log.e("DatabaseError", "No such novel in DB");
                System.exit(-1);
            }

            Formatter formatter = Database.DatabaseLibrary.getFormat(nURL);

            if (novelPage != null)
                switch (menuItem.getItemId()) {
                    case R.id.popup_chapter_menu_bookmark:
                        if (toggleBookmarkChapter(updateHolder.novelChapter.link))
                            updateHolder.title.setTextColor(updateHolder.itemView.getResources().getColor(R.color.bookmarked));
                        else {
                            Log.i("SetDefault", String.valueOf(DefaultTextColor));
                            updateHolder.title.setTextColor(DefaultTextColor);
                        }
                        notifyDataSetChanged();
                        return true;
                    case R.id.popup_chapter_menu_download:
                        if (!Database.DatabaseChapter.isSaved(updateHolder.novelChapter.link)) {
                            DownloadItem downloadItem = new DownloadItem(formatter, novelPage.title, updateHolder.novelChapter.chapterNum, nURL, updateHolder.novelChapter.link);
                            Download_Manager.addToDownload(downloadItem);
                        } else {
                            if (Download_Manager.delete(updateHolder.itemView.getContext(), new DownloadItem(formatter, novelPage.title, updateHolder.novelChapter.chapterNum, nURL, updateHolder.novelChapter.link))) {
                                updateHolder.downloadTag.setVisibility(View.INVISIBLE);
                            }
                        }
                        notifyDataSetChanged();
                        return true;

                    case R.id.popup_chapter_menu_mark_read:
                        Database.DatabaseChapter.setChapterStatus(updateHolder.novelChapter.link, Status.READ);
                        notifyDataSetChanged();

                        return true;
                    case R.id.popup_chapter_menu_mark_unread:
                        Database.DatabaseChapter.setChapterStatus(updateHolder.novelChapter.link, Status.UNREAD);
                        notifyDataSetChanged();

                        return true;
                    case R.id.popup_chapter_menu_mark_reading:
                        Database.DatabaseChapter.setChapterStatus(updateHolder.novelChapter.link, Status.READING);
                        notifyDataSetChanged();

                        return true;
                    case R.id.browser:
                        if (activity != null)
                            openInBrowser(activity, updateHolder.novelChapter.link);
                        return true;
                    case R.id.webview:
                        if (activity != null)
                            openInWebview(activity, updateHolder.novelChapter.link);
                        return true;
                    default:
                        return false;
                }
            return false;
        });
        updateHolder.moreOptions.setOnClickListener(view -> updateHolder.popupMenu.show());
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }

}
