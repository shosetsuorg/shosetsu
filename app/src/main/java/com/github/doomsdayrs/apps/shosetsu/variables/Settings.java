package com.github.doomsdayrs.apps.shosetsu.variables;

import android.graphics.Color;
import android.net.ConnectivityManager;

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
 * 14 / June / 2019
 *
 * @author github.com/doomsdayrs
 */

/**
 * Setting variables to work with
 */
public class Settings {
    /**
     * Reader text size
     */
    @SuppressWarnings("unused")
    public static float ReaderTextSize = 14;

    /**
     * Reader text color
     */
    public static int ReaderTextColor = Color.BLACK;

    /**
     * Reader background color
     */
    public static int ReaderTextBackgroundColor = Color.WHITE;

    /**
     * global connectivity manager variable
     */
    public static ConnectivityManager connectivityManager;

    /**
     * If download manager is paused
     */
    public static boolean downloadPaused;

    /**
     * Current theme to use
     * <p>
     * 0: Light mode
     * 1: Night mode
     * 2: Dark mode
     */
    public static int themeMode;

    public static int paragraphSpacing;
    public static int indentSize;
}
