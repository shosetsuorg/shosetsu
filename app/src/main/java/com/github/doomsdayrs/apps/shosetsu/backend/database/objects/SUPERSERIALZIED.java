package com.github.doomsdayrs.apps.shosetsu.backend.database.objects;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

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
public class SUPERSERIALZIED implements Serializable {
    public final ArrayList<Library> libraries = new ArrayList<>();
    public final ArrayList<Chapter> chapters = new ArrayList<>();
    public final ArrayList<Download> downloads = new ArrayList<>();
    public final ArrayList<Update> updates = new ArrayList<>();
    public final SettingsSerialized settingsSerialized = new SettingsSerialized();

    @Override
    public String toString() {
        return "SUPERSERIALZIED{" +
                "libraries=" + libraries +
                ", chapters=" + chapters +
                ", downloads=" + downloads +
                ", updates=" + updates +
                ", settings=" + settingsSerialized +
                '}';
    }

    public String serialize() {
        try {
            return Database.serialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "FUCK_UP";
    }
}
