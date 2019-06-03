package com.github.Doomsdayrs.apps.shosetsu.recycleObjects;

import java.net.URI;

public class NovelCard {
    public String libraryImageResource;
    public String title;
    public String URL;

    public NovelCard(String libraryImageResource, String title,URI URL){
        this.libraryImageResource = libraryImageResource;
        this.title = title;
    }

    public NovelCard(String title, URI URL){
        this.libraryImageResource = null;
        this.title = title;
    }


}
