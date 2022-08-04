package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.KEY_CLICK_FAB_NAVIGATION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_CALENDAR_VIEW;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.ui.fragment.ProfileFragment;
import br.com.beautystyle.ui.fragment.event.EventListFragment;
import br.com.beautystyle.ui.fragment.expense.ExpenseListFragment;
import br.com.beautystyle.ui.fragment.report.ReportFragment;
import br.com.beautystyle.util.CalendarUtil;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final CalendarViewFragment calendarViewFragment = new CalendarViewFragment();
    private boolean clicked = false;
    private FloatingActionButton costumerFab, profileFab, newEventFab, navigationFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        injectActivity();
        initFabWidgets();
        removeShadowBottomNavigation();
        startHomeFragment(savedInstanceState);

        setBottomNavigationListener();
        fabNavigationListener();
        fabCostumerListener();
        fabProfileListener();
        fabNewEventListener();
        onCalendarClickListener();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Deseja sair do app?")
                .setPositiveButton("SIM", (dialog, whichButton) -> {
                    finish();
                    dialog.dismiss();
                }).setNegativeButton("NÃO", (dialog, whichButton) -> {
                    dialog.dismiss();
                }).show();
    }

    private void fabNewEventListener() {
        newEventFab.setOnClickListener(v -> {
            navigationFab.callOnClick();
            EventListFragment eventListFragment = new EventListFragment();
            eventListFragment.setArguments(createBundle());
            replaceContainer(eventListFragment);
        });
    }

    private void fabProfileListener() {
        profileFab.setOnClickListener(v -> {
            navigationFab.callOnClick();
            replaceContainer(new ProfileFragment());
        });
    }


    private void fabCostumerListener() {
        costumerFab.setOnClickListener(v -> {
            navigationFab.callOnClick();
        });
    }

    private void initFabWidgets() {
        navigationFab = findViewById(R.id.activity_navigation_fab_plus);
        costumerFab = findViewById(R.id.activity_navigation_fab_costumer);
        profileFab = findViewById(R.id.activity_navigation_fab_profile);
        newEventFab = findViewById(R.id.activity_navigation_fab_new_event);
    }

    private void injectActivity() {
        ((BeautyStyleApplication) getApplicationContext())
                .applicationComponent.injectNavigationAct(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void removeShadowBottomNavigation() {
        bottomNavigationView = findViewById(R.id.activity_navigation_bottom);
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
            CalendarUtil.selectedDate = LocalDate.now();
            switch (item.getItemId()) {
                case (R.id.home):
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

    private void fabNavigationListener() {
        navigationFab.setOnClickListener(V -> {
            setVisibility(clicked);
            setAnimation(clicked);
            clicked = !clicked;
        });
    }

    private void setAnimation(boolean clicked) {
        if (!clicked) {
            Animation fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_left_anim);
            costumerFab.setAnimation(fromBottom);

            fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_right_anim);
            profileFab.setAnimation(fromBottom);

            fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_center_anim);
            newEventFab.setAnimation(fromBottom);

            Animation rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
            navigationFab.setAnimation(rotateOpen);
        } else {
            Animation toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_left_anim);
            costumerFab.setAnimation(toBottom);

            toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_right_anim);
            profileFab.setAnimation(toBottom);

            toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_center_anim);
            newEventFab.setAnimation(toBottom);

            Animation rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
            navigationFab.setAnimation(rotateClose);
        }
    }

    private void setVisibility(boolean clicked) {
        if (!clicked) {
            costumerFab.setVisibility(View.VISIBLE);
            profileFab.setVisibility(View.VISIBLE);
            newEventFab.setVisibility(View.VISIBLE);
        } else {
            costumerFab.setVisibility(View.INVISIBLE);
            profileFab.setVisibility(View.INVISIBLE);
            newEventFab.setVisibility(View.INVISIBLE);
        }
    }

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_CLICK_FAB_NAVIGATION, 1);
        return bundle;
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

}