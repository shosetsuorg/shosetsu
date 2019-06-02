package com.github.Doomsdayrs.apps.shosetsu.recycleObjects;

import com.github.Doomsdayrs.apps.shosetsu.R;

public class RecycleCard {
    public int libraryImageResource;
    public String libraryText;

    public RecycleCard(int libraryImageResource, String libraryText){
        this.libraryImageResource = libraryImageResource;
        this.libraryText = libraryText;
    }

    public RecycleCard(String libraryText){
        this.libraryImageResource = R.drawable.ic_close_black_24dp;
        this.libraryText = libraryText;
    }


}
