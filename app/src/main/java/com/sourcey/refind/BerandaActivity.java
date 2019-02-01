package com.sourcey.refind;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BerandaActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        BottomNavigationView navigasiView = findViewById(R.id.nav_bottom);

        final HomeFragment homeFragment = new HomeFragment();
        final NearbyFragment nearbyFragment= new NearbyFragment();
        final ChatFragment chatFragment = new ChatFragment();

        navigasiView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.home) {
                    setFragment(homeFragment);
                } else if (id == R.id.nearby) {
                    setFragment(nearbyFragment);
                    return true;
                } else if (id == R.id.chat) {
                    setFragment(chatFragment);
                    return true;
                }

                return false;
            }
        });

        navigasiView.setSelectedItemId(R.id.home);
    }

        private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }
}
