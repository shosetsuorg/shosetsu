package com.github.doomsdayrs.apps.shosetsu.ui.updates.async;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

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
 * shosetsu
 * 13 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ChapterUpdater extends AsyncTask<Void, Void, Void> {
    private static final int ID = 1917;
    private static final String channel_ID = "shosetsu_updater";

    private ArrayList<NovelCard> novelCards;
    private boolean continueProcesss = true;
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private ArrayList<NovelCard> updatedNovels = new ArrayList<>();

    public ChapterUpdater(@NotNull ArrayList<NovelCard> novelCards, Context context) {
        this.novelCards = novelCards;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_ID, "Shosetsu Update", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(context, channel_ID);
        } else builder = new Notification.Builder(context);
    }

    public void setContinueProcesss(boolean continueProcesses) {
        this.continueProcesss = continueProcesses;
    }

    @Override
    protected void onPreExecute() {
        builder = builder
                .setSmallIcon(R.drawable.ic_system_update_alt_black_24dp)
                .setContentTitle("Update")
                .setContentText("Update in progress")
                .setProgress(novelCards.size(), 0, false)
                .setOngoing(true)
                .setOnlyAlertOnce(true);
        notificationManager.notify(ID, builder.build());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            for (int x = 0; x < novelCards.size(); x++) {
                NovelCard novelCard = novelCards.get(x);
                builder.setContentText(novelCard.title);
                builder.setProgress(novelCards.size(), x + 1, false);
                notificationManager.notify(ID, builder.build());

                Formatter formatter = DefaultScrapers.getByID(novelCard.formatterID);
                if (formatter != null) {
                    int page = 1;
                    NovelPage tempPage;
                    if (formatter.isIncrementingChapterList()) {
                        tempPage = formatter.parseNovel(novelCard.novelURL, page);
                        int mangaCount = 0;
                        while (page <= tempPage.maxChapterPage && continueProcesss) {
                            tempPage = formatter.parseNovel(novelCard.novelURL, page);
                            for (NovelChapter novelChapter : tempPage.novelChapters)
                                add(mangaCount, novelChapter, novelCard);
                            page++;
                            Utilities.wait(300);
                        }
                    } else {
                        tempPage = formatter.parseNovel(novelCard.novelURL, page);
                        int mangaCount = 0;
                        for (NovelChapter novelChapter : tempPage.novelChapters)
                            add(mangaCount, novelChapter, novelCard);
                    }
                }
                Utilities.wait(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        StringBuilder stringBuilder = new StringBuilder();
        if (updatedNovels.size() > 0) {
            builder.setContentTitle("Completed Update");
            for (NovelCard novelCard : updatedNovels)
                stringBuilder.append(novelCard.title).append("\n");
            builder.setStyle(new Notification.BigTextStyle());
        } else stringBuilder.append("No updates found");

        builder.setContentText(stringBuilder.toString());
        builder.setProgress(0, 0, false);
        builder.setOngoing(false);
        notificationManager.notify(ID, builder.build());
    }


    private void add(int mangaCount, NovelChapter novelChapter, NovelCard novelCard) {
        if (continueProcesss && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
            mangaCount++;
            System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);
            Database.DatabaseChapter.addToChapters(novelCard.novelURL, novelChapter);
            Database.DatabaseUpdates.addToUpdates(novelCard.novelURL, novelChapter.link, System.currentTimeMillis());

            if (!updatedNovels.contains(novelCard))
                updatedNovels.add(novelCard);
        }
    }


}
