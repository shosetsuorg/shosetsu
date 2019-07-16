package com.github.doomsdayrs.apps.shosetsu.variables.enums;

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
 * 20 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */

/**
 * Status of novel/Chapter
 */
public enum Status {
    // Novels and chapters
    UNREAD(0, "Unread"),
    READING(1, "Reading"),
    READ(2, "Read"),
    // These two are for novels only
    ONHOLD(3, "OnHold"),
    DROPPED(4, "Dropped");

    private final int a;

    private final String status;

    Status(int a, String status) {
        this.a = a;
        this.status = status;
    }

    public int getA() {
        return a;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "" + a;
    }}
