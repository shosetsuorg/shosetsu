package com.github.Doomsdayrs.apps.shosetsu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.github.Doomsdayrs.apps.shosetsu.fragment.CatalogueFragment;
import com.github.Doomsdayrs.apps.shosetsu.fragment.SettingsFragment;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.RecycleCard;
import com.github.Doomsdayrs.apps.shosetsu.fragment.LibraryFragement;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    private ArrayList<RecycleCard> settingsCards = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LibraryFragement()).commit();
            navigationView.setCheckedItem(R.id.nav_library);
        }


        settingsCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "dummy"));


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_library: {
                Log.e("Nav", "Library selected");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LibraryFragement()).commit();


            }
            break;
            case R.id.nav_catalogue: {
                Log.e("Nav", "Catalogue selected");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CatalogueFragment()).commit();
            }
            break;
            case R.id.nav_settings: {
                Log.e("Nav", "Settings selected");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
            }
            break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
