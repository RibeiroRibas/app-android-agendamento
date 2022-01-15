package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsEventActivity.REQUEST_CODE_NEW_EVENT;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystyle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.com.beautystyle.model.Event;
import br.com.beautystyle.ui.ListEventView;
import br.com.beautystyle.ui.fragment.ExpenseListFragment;
import br.com.beautystyle.ui.fragment.EventListFragment;
import br.com.beautystyle.ui.fragment.ReportFragment;

public class NavigationActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        startHomeFragment(savedInstanceState);
        setShadowBottomNavigation();
        setBottomNavigationListener();
        setFabNavigationListener();

        registerActivityResult();
    }

    private void startHomeFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_container, new EventListFragment())
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
            switch (item.getItemId()) {
                case (R.id.home):
                    replaceContainerToEventList();
                    return true;
                case (R.id.report):
                    replaceContainerToReport();
                    return true;
                case (R.id.expense):
                    replaceContainerToExpenseList();
                    return true;
            }
            return false;
        });

        }

        private void replaceContainerToExpenseList () {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("expense")
                    .replace(R.id.frame_container, new ExpenseListFragment())
                    .commit();
        }

        private void replaceContainerToReport () {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("report")
                    .replace(R.id.frame_container, new ReportFragment())
                    .commit();
        }

        private void replaceContainerToEventList () {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("home")
                    .replace(R.id.frame_container, new EventListFragment())
                    .commit();
        }

        private void setFabNavigationListener () {
            FloatingActionButton fabNavigation = findViewById(R.id.fab_navigation);
            fabNavigation.setOnClickListener(V -> {
                Intent intent = new Intent(this, NewEventActivity.class);
                activityResultLauncher.launch(intent);
            });
        }

    private void registerActivityResult() {
        ListEventView listEventView = new ListEventView((this));
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == REQUEST_CODE_NEW_EVENT) {
                Intent intent = result.getData();
                if (intent != null) {
                    Event event = (Event) intent.getSerializableExtra("newEvent");
                    listEventView.save(event);
                }
            }
        });
    }

}