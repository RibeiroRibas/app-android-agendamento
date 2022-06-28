package br.com.beautystyle.ui.activity;


import static br.com.beautystyle.repository.ConstantsRepository.EMAIL_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.ISLOGGED_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_SHARED_PREFERENCES;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystyle.R;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.model.UserLogin;
import br.com.beautystyle.model.UserToken;
import br.com.beautystyle.model.entity.User;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.repository.UserRepository;


public class LoginActivity extends AppCompatActivity {

    @Inject
    UserRepository userRepository;
    private SharedPreferences preferences;
    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences(USER_SHARED_PREFERENCES, MODE_PRIVATE);

        initWidgets();
        injectActivity();
        btnLoginAuthenticationListener();

    }

    private void initWidgets() {
        email = findViewById(R.id.activity_login_email);
        password = findViewById(R.id.activity_login_password);
    }

    private void btnLoginAuthenticationListener() {
        Button btnAuthentication = findViewById(R.id.activity_login_btn);
        btnAuthentication.setOnClickListener(v -> {
            if (checkRequiredFields()) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                UserLogin userLogin = new UserLogin(userEmail, userPassword);
                authUserOnApi(userLogin);
            } else {
                requiredFieldsAlertDialog();
            }
        });
    }

    public void requiredFieldsAlertDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Todos os campos são obrigatórios")
                .setPositiveButton("Ok", null)
                .show();
    }

    private boolean checkRequiredFields() {
        return !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty();
    }

    private void injectActivity() {
        ((BeautyStyleApplication) getApplicationContext())
                .applicationComponent.injectLoginAct(this);
    }

    private void authUserOnApi(UserLogin userLogin) {
        userRepository.authUser(userLogin, new ResultsCallBack<UserToken>() {
            @Override
            public void onSuccess(UserToken userToken) {
                setPreferences(userToken, userLogin.getEmail());
                User user = new User(userLogin, userToken.getProfiles());
                insertUserOnRoom(user);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void insertUserOnRoom(User user) {
        userRepository.insert(user)
                .doOnComplete(this::startNavigationActivity).subscribe();
    }

    private void startNavigationActivity() {
        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    private void setPreferences(UserToken userToken, String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL_SHARED_PREFERENCES, email);
        editor.putString(TOKEN_SHARED_PREFERENCES, userToken.getTypeToken());
        editor.putLong(TENANT_SHARED_PREFERENCES, userToken.getCompanyId());
        editor.putBoolean(ISLOGGED_SHARED_PREFERENCES, true);
        editor.apply();
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}