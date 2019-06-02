package com.github.Doomsdayrs.apps.shosetsu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.github.Doomsdayrs.apps.shosetsu.fragment.CataloguesFragment;
import com.github.Doomsdayrs.apps.shosetsu.fragment.LibraryFragement;
import com.github.Doomsdayrs.apps.shosetsu.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private LibraryFragement libraryFragement = new LibraryFragement();
    private CataloguesFragment cataloguesFragment = new CataloguesFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the content view
        setContentView(R.layout.activity_main);
        //Sets the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Sets up the sidebar
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Prevent the frag from changing on rotation
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                   libraryFragement).commit();
            navigationView.setCheckedItem(R.id.nav_library);
        }
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
                        libraryFragement).commit();
            }
            break;
            case R.id.nav_catalogue: {
                Log.e("Nav", "Catalogue selected");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        cataloguesFragment).commit();
            }
            break;
            case R.id.nav_settings: {
                Log.e("Nav", "Settings selected");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        settingsFragment).commit();
            }
            break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
