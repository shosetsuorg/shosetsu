package com.github.doomsdayrs.apps.shosetsu.backend.database.objects.base;

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
 */public class Base implements Serializable {
    public final String NOVEL_URL;

    public Base(String novel_url) {
        NOVEL_URL = novel_url;
    }

    @Override
    public String toString() {
        return "Base{" +
                "NOVEL_URL='" + NOVEL_URL + '\'' +
                '}';
    }
}
