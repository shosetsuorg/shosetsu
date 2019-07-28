package com.github.doomsdayrs.apps.shosetsu.backend.database.objects;

import android.graphics.Color;

import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import java.io.Serializable;

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
 * shosetsu
 * 27 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class SettingsSerialized implements Serializable {
    public int reader_text_color = Settings.ReaderTextColor;
    public int reader_text_background_color = Settings.ReaderTextBackgroundColor;
    public String shoDir = Download_Manager.shoDir;
    public boolean paused = Settings.downloadPaused;
    public float textSize = Settings.ReaderTextSize;
    public int themeMode = Settings.themeMode;
    public int paraSpace = Settings.paragraphSpacing;
    public int indent = Settings.indentSize;

    @Override
    public String toString() {
        return "SettingsSerialized{" +
                "reader_text_color=" + reader_text_color +
                ", reader_text_background_color=" + reader_text_background_color +
                ", shoDir='" + shoDir + '\'' +
                ", paused=" + paused +
                ", textSize=" + textSize +
                ", themeMode=" + themeMode +
                ", paraSpace=" + paraSpace +
                ", indent=" + indent +
                '}';
    }
}
