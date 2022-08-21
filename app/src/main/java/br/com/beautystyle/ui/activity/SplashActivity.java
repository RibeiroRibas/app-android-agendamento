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

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.UserViewModel;
import br.com.beautystyle.ViewModel.factory.UserFactory;
import br.com.beautystyle.model.UserLogin;
import br.com.beautystyle.model.UserToken;
import br.com.beautystyle.model.entity.User;
import br.com.beautystyle.repository.UserRepository;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    @Inject
    UserRepository userRepository;
    private UserViewModel viewModel;

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
        UserLogin userLogin = new UserLogin(user);
        if (user.isPremiumAccount()) {
            authUserOnApi(userLogin);
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

    private void authUserOnApi(UserLogin userLogin) {
        viewModel.authUser(userLogin).observe(this, resource -> {
            if (resource.isDataNotNull()) {
                setPreferences(resource.getData());
                startNavigationActivity();
            } else {
                showErrorMessage(resource.getError());
                startLoginActivity();
            }
        });
    }

    private void startNavigationActivity() {
        Intent intent = new Intent(SplashActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    private void setPreferences(UserToken userToken) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_SHARED_PREFERENCES, userToken.getTypeToken());
        editor.putLong(TENANT_SHARED_PREFERENCES, userToken.getCompanyId());
        editor.apply();
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

}