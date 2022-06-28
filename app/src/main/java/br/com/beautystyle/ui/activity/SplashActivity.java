package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.repository.ConstantsRepository.EMAIL_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.ISLOGGED_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystyle.R;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.model.UserLogin;
import br.com.beautystyle.model.UserToken;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.repository.UserRepository;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    @Inject
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        injectActivity();
        preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        if (isUserLoggedIn()) {
            String email = preferences.getString(EMAIL_SHARED_PREFERENCES, "");
            getUserByEmail(email);
        } else {
            startLoginActivity();
        }
    }

    private void getUserByEmail(String email) {
        userRepository.getByEmail(email)
                .doOnSuccess(user -> {
                    if (user != null) {
                        UserLogin userLogin = new UserLogin(user);
                        authUserOnApi(userLogin);
                    } else {
                        startLoginActivity();
                    }
                }).subscribe();
    }

    private boolean isUserLoggedIn() {
        return preferences.getBoolean(ISLOGGED_SHARED_PREFERENCES, false);
    }

    private void injectActivity() {
        ((BeautyStyleApplication) getApplicationContext())
                .applicationComponent.injectSplashAct(this);
    }

    private void startLoginActivity() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }, 2000);
    }

    private void authUserOnApi(UserLogin userLogin) {
        userRepository.authUser(userLogin, new ResultsCallBack<UserToken>() {
            @Override
            public void onSuccess(UserToken userToken) {
                setPreferences(userToken);
                Intent intent = new Intent(SplashActivity.this, NavigationActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(String erro) {

            }
        });
    }

    private void setPreferences(UserToken userToken) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_SHARED_PREFERENCES, userToken.getTypeToken());
        editor.putLong(TENANT_SHARED_PREFERENCES, userToken.getCompanyId());
        editor.apply();

    }
}