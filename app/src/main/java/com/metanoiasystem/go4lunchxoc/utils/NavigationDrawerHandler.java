package com.metanoiasystem.go4lunchxoc.utils;

import android.app.Activity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.metanoiasystem.go4lunchxoc.R;
import com.metanoiasystem.go4lunchxoc.domain.usecase.UpdateUserViewDrawerUseCase;
import com.metanoiasystem.go4lunchxoc.view.activities.MainActivity;

import java.util.Objects;

public class NavigationDrawerHandler {
    private final MainActivity mainActivity;
    private final DrawerLayout drawerLayout;
    private final NavigationView navigationView;
    private final UpdateUserViewDrawerUseCase updateUserViewDrawerUseCase;

    public NavigationDrawerHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.drawerLayout = mainActivity.findViewById(R.id.my_drawer_layout);
        this.navigationView = mainActivity.findViewById(R.id.drawer_navigation);
        updateUserViewDrawerUseCase = Injector.provideUpdateUserViewDrawerUseCase();
    }

    public void setupNavigationDrawer() {
        // Configuration de l'icône du tiroir de navigation
        Objects.requireNonNull(mainActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mainActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        updateUserViewDrawerUseCase.updateUserView(navigationView);

        // Configuration du NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            // Gestion des clics sur les éléments du menu (à compléter selon vos besoins)
            switch (item.getItemId()) {
                case R.id.nav_account:
                    // ...
                    break;
                case R.id.nav_settings:
                    // ...
                    break;
                case R.id.nav_logout:
                    // ...
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}

