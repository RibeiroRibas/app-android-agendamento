package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_CALENDAR_VIEW;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.database.room.references.EventWithClientAndJobs;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.repository.RoomRepository;
import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.ui.fragment.event.EventListFragment;
import br.com.beautystyle.ui.fragment.expense.ExpenseListFragment;
import br.com.beautystyle.ui.fragment.report.ReportFragment;
import br.com.beautystyle.util.CalendarUtil;

public class NavigationActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private BottomNavigationView bottomNavigationView;
    private final CalendarViewFragment calendarViewFragment = new CalendarViewFragment();
    @Inject
    RoomRepository roomRepository;
    @Inject
    EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        injectActivity();

        initWidgets();

        removeShadowBottomNavigation();
        startHomeFragment(savedInstanceState);

        setBottomNavigationListener();
        setFabNavigationListener();
        onCalendarClickListener();

        registerActivityResult();

    }

    private void injectActivity() {
        ((BeautyStyleApplication) getApplicationContext())
                .applicationComponent.injectNavigationAct(this);
    }

    private void initWidgets() {
        bottomNavigationView = findViewById(R.id.activity_navigation_bottom);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void removeShadowBottomNavigation() {
        bottomNavigationView.setBackground(null);
    }

    private void startHomeFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_navigation_container,
                            new EventListFragment(), null)
                    .commit();
        }
    }

    private void setBottomNavigationListener() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.home):
                    CalendarUtil.selectedDate = LocalDate.now();
                    replaceContainer(new EventListFragment());
                    return true;
                case (R.id.report):
                    replaceContainer(new ReportFragment());
                    return true;
                case (R.id.expense):
                    replaceContainer(new ExpenseListFragment());
                    return true;
                case (R.id.calendar):
                    calendarViewFragment.show(getSupportFragmentManager(), TAG_CALENDAR_VIEW);
                    return true;
            }
            return false;
        });
    }

    private void replaceContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_navigation_container, fragment, null)
                .commit();
    }

    private void setFabNavigationListener() {
        FloatingActionButton fabNavigation = findViewById(R.id.activity_navigation_fab_new_event);
        fabNavigation.setOnClickListener(V -> {
            Intent intent = new Intent(this, NewEventActivity.class);
            activityResultLauncher.launch(intent);
        });
    }

    private void onCalendarClickListener() {
        calendarViewFragment.setOnCalendarClickListener((view, year, month, dayOfMonth) -> {
            CalendarUtil.selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            changeEventDate();
            calendarViewFragment.dismiss();
        });
    }

    private void changeEventDate() {
        replaceContainer(new EventListFragment());
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == REQUEST_CODE_INSERT_EVENT) {
                Intent intent = result.getData();
                if (intent != null) {
                    EventWithClientAndJobs event =
                            (EventWithClientAndJobs) intent.getSerializableExtra(KEY_INSERT_EVENT);
                    insertEventOnApi(event);
                }
            }
        });
    }

    private void insertEventOnApi(EventWithClientAndJobs event) {
        eventRepository.insertOnApi(event, new ResultsCallBack<EventWithClientAndJobs>() {
            @Override
            public void onSuccess(EventWithClientAndJobs eventFromApi) {
                event.getEvent().setApiId(eventFromApi.getEvent().getApiId());
                insertEventOnRoom(event);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void insertEventOnRoom(EventWithClientAndJobs eventFromApi) {
        roomRepository.insertEvent(eventFromApi,
                new ResultsCallBack<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        changeEventDate();
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

}