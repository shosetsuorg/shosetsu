package com.github.Doomsdayrs.apps.shosetsu.recycleObjects;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.apps.shosetsu.R;

public class CatalogueCard extends RecycleCard {
   public Formatter formatter;

    public CatalogueCard(Formatter formatter) {
        super(formatter.getName());
        this.formatter = formatter;
    }


}
