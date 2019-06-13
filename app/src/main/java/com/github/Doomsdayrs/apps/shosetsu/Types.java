package com.github.Doomsdayrs.apps.shosetsu;

public enum Types {
    DOWNLOAD("download"),
    VIEW("view"),
    ADVANCED("advanced"),
    CREDITS("credits");

    final String name;

    Types(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}