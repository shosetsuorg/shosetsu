package com.github.doomsdayrs.apps.shosetsu.ui.webView;

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
 * 05 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public enum Actions {
    VIEW(0),
    CLOUD_FLARE(1);
    public static final ArrayList<Actions> actions = new ArrayList<>();

    static {
        actions.add(Actions.VIEW);
        actions.add(Actions.CLOUD_FLARE);
    }

    public final int action;

    Actions(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}