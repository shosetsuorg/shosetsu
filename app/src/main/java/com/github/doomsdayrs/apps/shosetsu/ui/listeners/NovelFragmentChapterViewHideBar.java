package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentChapterViewHideBar implements View.OnClickListener {
    private final Toolbar toolbar;
    private boolean visible = true;

    public NovelFragmentChapterViewHideBar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    public void onClick(View v) {
        if (visible) {
            toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            visible = !visible;
        } else {
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            visible = !visible;
        }
    }
}