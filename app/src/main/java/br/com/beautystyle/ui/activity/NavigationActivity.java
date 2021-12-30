package br.com.beautystyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystyle.R;
import br.com.beautystyle.ui.fragment.ListEventFragment;
import br.com.beautystyle.ui.fragment.RelatorioFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        startHomeFragment(savedInstanceState);
        setShadowBottomNavigation();
        setBottomNavigation();
        setFabNavigation();
    }

    private void startHomeFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_container, new ListEventFragment())
                    .commit();
        }
    }

    private void setShadowBottomNavigation() {
        BottomNavigationView bottomIconView = findViewById(R.id.activity_main_schedule_bottomview);
        bottomIconView.setBackground(null);
    }

    private void setBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_schedule_bottomview);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new ListEventFragment())
                        .commit();
                return true;
            }else if(item.getItemId() == R.id.financas){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new RelatorioFragment())
                        .commit();
            }
            return false;
        });
    }

    private void setFabNavigation() {
        FloatingActionButton fabNavigation = findViewById(R.id.fab_navigation);
        fabNavigation.setOnClickListener(V -> {
            startActivity(new Intent(NavigationActivity.this, NewEventActivity.class));
        });
    }
}