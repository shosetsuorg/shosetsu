package com.github.doomsdayrs.apps.shosetsu.variables;
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
 * 15 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import org.joda.time.DateTime;

public class Update {
    public final String novelURL;
    public final String chapterURL;
    public final long timeMS;
    public final DateTime dateTime;

    public Update(String novelURL, String chapterURL, long timeMS) {
        this.novelURL = novelURL;
        this.chapterURL = chapterURL;
        this.timeMS = timeMS;
        dateTime = new DateTime(timeMS);
    }
}
