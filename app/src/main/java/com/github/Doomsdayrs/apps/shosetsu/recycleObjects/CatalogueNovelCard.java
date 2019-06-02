package com.github.Doomsdayrs.apps.shosetsu.recycleObjects;

public class CatalogueNovelCard {
    public String libraryImageResource;
    public String title;

    public CatalogueNovelCard(String libraryImageResource, String title){
        this.libraryImageResource = libraryImageResource;
        this.title = title;
    }

    public CatalogueNovelCard(String title){
        this.libraryImageResource = null;
        this.title = title;
    }


}
