package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.KEY_NEW_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_NEW_EVENT;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;

import br.com.beautystyle.ViewModel.CalendarViewModel;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.ui.ListDaysView;
import br.com.beautystyle.ui.ListEventView;
import br.com.beautystyle.ui.fragment.EventListFragment;
import br.com.beautystyle.ui.fragment.ExpenseListFragment;
import br.com.beautystyle.ui.fragment.ReportFragment;

public class NavigationActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private final ListDaysView listDaysView = new ListDaysView();
    private final ListEventView listEventView = new ListEventView((this));
    private BottomNavigationView bottomNavigationView;
    private CalendarViewModel calendarViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        startHomeFragment(savedInstanceState);
        setShadowBottomNavigation();
        setBottomNavigationListener();
        setFabNavigationListener();

        registerActivityResult();
        calendarObserve();

    }



    private void startHomeFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_navigation_container, new EventListFragment(listDaysView,listEventView))
                    .commit();
        }
    }

    private void setShadowBottomNavigation() {
        bottomNavigationView = findViewById(R.id.activity_navigation_bottom);
        bottomNavigationView.setBackground(null);
    }

    private void setBottomNavigationListener() {
        bottomNavigationView = findViewById(R.id.activity_navigation_bottom);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.home):
                    replaceContainer("home",new EventListFragment(listDaysView,listEventView));
                    return true;
                case (R.id.report):
                    replaceContainer("report",new ReportFragment());
                    return true;
                case (R.id.expense):
                    replaceContainer("expense",new ExpenseListFragment());
                    return true;
                case (R.id.calendar):
                    calendarViewModel.inflateCalendar(this);
                    return true;
            }
            return false;
        });

        }

    private void replaceContainer(String id, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(id)
                .replace(R.id.activity_navigation_container, fragment)
                .commit();
    }

        private void setFabNavigationListener () {
            FloatingActionButton fabNavigation = findViewById(R.id.activity_navigation_fab_new_event);
            fabNavigation.setOnClickListener(V -> {
                Intent intent = new Intent(this, NewEventActivity.class);
                activityResultLauncher.launch(intent);
            });
        }

    private void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == REQUEST_CODE_NEW_EVENT) {
                Intent intent = result.getData();
                if (intent != null) {
                    Event event = (Event) intent.getSerializableExtra(KEY_NEW_EVENT);
                    listEventView.save(event);
                    listDaysView.getScrollPosition(event.getEventDate());
                }
            }
        });
    }
    private void calendarObserve() {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getDate().observe(this,this::setDate);
    }
    private void setDate(LocalDate date) {
        replaceContainer("home",new EventListFragment(listDaysView,listEventView,date));
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }
}