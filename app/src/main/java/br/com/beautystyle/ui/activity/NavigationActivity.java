package br.com.beautystyle.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystyle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.com.beautystyle.ui.fragment.ExpenseListFragment;
import br.com.beautystyle.ui.fragment.ListEventFragment;
import br.com.beautystyle.ui.fragment.ReportFragment;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        startHomeFragment(savedInstanceState);
        setShadowBottomNavigation();
        setBottomNavigationListener();
        setFabNavigationListener();
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

    private void setBottomNavigationListener() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_schedule_bottomview);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("home")
                        .replace(R.id.frame_container, new ListEventFragment())
                        .commit();
                return true;
            }else if(item.getItemId() == R.id.report){
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("report")
                        .replace(R.id.frame_container, new ReportFragment())
                        .commit();
                return true;
            }else if(item.getItemId() == R.id.spending){
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("expense")
                        .replace(R.id.frame_container, new ExpenseListFragment())
                        .commit();
                return true;
            }
            return false;
        });
    }

    private void setFabNavigationListener() {
        FloatingActionButton fabNavigation = findViewById(R.id.fab_navigation);
        fabNavigation.setOnClickListener(V -> {
            startActivity(new Intent(NavigationActivity.this, NewEventActivity.class));
        });
    }
}