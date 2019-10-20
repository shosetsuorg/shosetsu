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
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedChapterHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import java.util.ArrayList;
import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInBrowser;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInWebview;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleBookmarkChapter;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getFormatterIDFromNovelURL;

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
public class UpdatedChaptersAdapter extends RecyclerView.Adapter<UpdatedChapterHolder> {
    public static int DefaultTextColor;
    private static boolean set = false;

    public final ArrayList<Update> updates;
    public final Activity activity;

    public UpdatedChaptersAdapter(ArrayList<Update> updates, Activity activity) {
        this.updates = updates;
        this.activity = activity;
    }


    @NonNull
    @Override
    public UpdatedChapterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.update_card, viewGroup, false);
        UpdatedChapterHolder updatedChapterHolder = new UpdatedChapterHolder(view);
        if (!set) {
            DefaultTextColor = updatedChapterHolder.title.getCurrentTextColor();
            Log.i("TextDefaultColor", String.valueOf(DefaultTextColor));
            set = !set;
        }
        return updatedChapterHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UpdatedChapterHolder updatedChapterHolder, int i) {
        Log.d("Binding", updates.get(i).CHAPTER_URL);
        updatedChapterHolder.setNovelChapter(Objects.requireNonNull(DatabaseChapter.getChapter(updates.get(i).CHAPTER_URL)));
        updatedChapterHolder.popupMenu.setOnMenuItemClickListener(menuItem -> {
            NovelPage novelPage = new NovelPage();
            String nURL = Database.DatabaseIdentification.getNovelURLFromChapterURL(updatedChapterHolder.novelChapter.link);

            if (nURL != null)
                novelPage = Database.DatabaseNovels.getNovelPage(nURL);

            if (novelPage == null) {
                Log.e("DatabaseError", "No such novel in DB");
                System.exit(-1);
            }

            Formatter formatter = DefaultScrapers.getByID(getFormatterIDFromNovelURL(nURL));

            if (novelPage != null)
                switch (menuItem.getItemId()) {
                    case R.id.popup_chapter_menu_bookmark:
                        if (toggleBookmarkChapter(updatedChapterHolder.novelChapter.link))
                            updatedChapterHolder.title.setTextColor(updatedChapterHolder.itemView.getResources().getColor(R.color.bookmarked));
                        else {
                            Log.i("SetDefault", String.valueOf(DefaultTextColor));
                            updatedChapterHolder.title.setTextColor(DefaultTextColor);
                        }
                        notifyDataSetChanged();
                        return true;
                    case R.id.popup_chapter_menu_download:
                        if (!Database.DatabaseChapter.isSaved(updatedChapterHolder.novelChapter.link)) {
                            DownloadItem downloadItem = new DownloadItem(formatter, novelPage.title, updatedChapterHolder.novelChapter.title, nURL, updatedChapterHolder.novelChapter.link);
                            Download_Manager.addToDownload(downloadItem);
                        } else {
                            if (Download_Manager.delete(updatedChapterHolder.itemView.getContext(), new DownloadItem(formatter, novelPage.title, updatedChapterHolder.novelChapter.title, nURL, updatedChapterHolder.novelChapter.link))) {
                                updatedChapterHolder.downloadTag.setVisibility(View.INVISIBLE);
                            }
                        }
                        notifyDataSetChanged();
                        return true;

                    case R.id.popup_chapter_menu_mark_read:
                        Database.DatabaseChapter.setChapterStatus(updatedChapterHolder.novelChapter.link, Status.READ);
                        notifyDataSetChanged();

                        return true;
                    case R.id.popup_chapter_menu_mark_unread:
                        Database.DatabaseChapter.setChapterStatus(updatedChapterHolder.novelChapter.link, Status.UNREAD);
                        notifyDataSetChanged();

                        return true;
                    case R.id.popup_chapter_menu_mark_reading:
                        Database.DatabaseChapter.setChapterStatus(updatedChapterHolder.novelChapter.link, Status.READING);
                        notifyDataSetChanged();

                        return true;
                    case R.id.browser:
                        if (activity != null)
                            openInBrowser(activity, updatedChapterHolder.novelChapter.link);
                        return true;
                    case R.id.webview:
                        if (activity != null)
                            openInWebview(activity, updatedChapterHolder.novelChapter.link);
                        return true;
                    default:
                        return false;
                }
            return false;
        });
        updatedChapterHolder.moreOptions.setOnClickListener(view -> updatedChapterHolder.popupMenu.show());
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }

}
