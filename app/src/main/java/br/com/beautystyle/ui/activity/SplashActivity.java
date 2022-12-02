package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.repository.ConstantsRepository.EMAIL_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.IS_LOGGED_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_SHARED_PREFERENCES;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.UserViewModel;
import br.com.beautystyle.ViewModel.factory.UserFactory;
import br.com.beautystyle.model.entity.OpeningHours;
import br.com.beautystyle.repository.OpeningHoursRepository;
import br.com.beautystyle.retrofit.model.form.UserLoginForm;
import br.com.beautystyle.retrofit.model.dto.UserDto;
import br.com.beautystyle.model.entity.User;
import br.com.beautystyle.repository.UserRepository;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    @Inject
    UserRepository userRepository;
    private UserViewModel viewModel;
    @Inject
    OpeningHoursRepository openingHoursRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        injectActivity();
        UserFactory factory = new UserFactory(userRepository);
        viewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        preferences = getSharedPreferences(USER_SHARED_PREFERENCES, MODE_PRIVATE);
        checkIsUserLoggedIn();
    }

    private void checkIsUserLoggedIn() {
        if (isUserLoggedIn()) {
            String email = preferences.getString(EMAIL_SHARED_PREFERENCES, "");
            getUserByEmail(email);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(this::startLoginActivity, 2000);
        }
    }

    private void getUserByEmail(String email) {
        viewModel.getByEmail(email).observe(this, resource -> {
            if (resource.isDataNotNull()) {
                User user = resource.getData();
                checkPremiumAccount(user);
            } else {
                startLoginActivity();
            }
        });
    }

    private void checkPremiumAccount(User user) {
        UserLoginForm userLoginForm = new UserLoginForm(user);
        if (user.isPremiumAccount()) {
            authUserOnApi(userLoginForm);
        } else {
            startNavigationActivity();
        }
    }

    private boolean isUserLoggedIn() {
        return preferences.getBoolean(IS_LOGGED_SHARED_PREFERENCES, false);
    }

    private void injectActivity() {
        ((BeautyStyleApplication) getApplicationContext())
                .applicationComponent.injectSplashAct(this);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void authUserOnApi(UserLoginForm userLoginForm) {
        viewModel.authUser(userLoginForm).observe(this, resource -> {
            if (resource.isDataNotNull()) {
                setPreferences(resource.getData());
                openingHoursRepository.getAll().doOnSuccess(openingHours -> {
                    openingHours.forEach(fromRoom -> {
                        resource.getData().getOpeningHours().forEach(fromApi -> {
                            if (fromRoom.isApiIdEquals(fromApi))
                                fromApi.setId(fromRoom.getId());
                        });
                    });
                    insertOpeningHoursOnRoom(resource.getData());
                }).subscribe();
            } else {
                showErrorMessage(resource.getError());
                startLoginActivity();
            }
        });
    }

    private void insertOpeningHoursOnRoom(UserDto response) {
        List<OpeningHours> openingHours = response.getOpeningHours();
        openingHours.forEach(openingHour -> {
            openingHour.setTenant(response.getTenant());
        });
        openingHoursRepository.insertAll(response.getOpeningHours())
                .doOnComplete(this::startNavigationActivity).subscribe();
    }

    private void startNavigationActivity() {
        Intent intent = new Intent(SplashActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    private void setPreferences(UserDto userDto) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_SHARED_PREFERENCES, userDto.getTypeToken());
        editor.putLong(TENANT_SHARED_PREFERENCES, userDto.getTenant());
        editor.apply();
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

}