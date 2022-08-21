package br.com.beautystyle.ui.activity;


import static br.com.beautystyle.repository.ConstantsRepository.EMAIL_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.IS_LOGGED_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_SHARED_PREFERENCES;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import br.com.beautystyle.ui.ProgressBottom;


public class LoginActivity extends AppCompatActivity {

    @Inject
    UserRepository userRepository;
    private UserViewModel viewModel;
    private SharedPreferences preferences;
    private EditText email, password;
    private ProgressBottom progressBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        injectActivity();

        UserFactory factory = new UserFactory(userRepository);
        viewModel = new ViewModelProvider(this,factory).get(UserViewModel.class);
        preferences = getSharedPreferences(USER_SHARED_PREFERENCES, MODE_PRIVATE);

        initWidgets();
        btnLoginAuthenticationListener();

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

    private void initWidgets() {
        email = findViewById(R.id.activity_login_email);
        password = findViewById(R.id.activity_login_password);
    }

    private void btnLoginAuthenticationListener() {
        Button btnAuthentication = findViewById(R.id.activity_login_btn);
        btnAuthentication.setOnClickListener(v -> {
            password.clearFocus();
            email.clearFocus();
            if (checkRequiredFields()) {
                ProgressBar progressBar = findViewById(R.id.activity_login_progress_bar);
                progressBottom = new ProgressBottom(progressBar);
                progressBottom.buttonActivated();
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
        viewModel.authUser(userLogin).observe(this,resource->{
            if(resource.isDataNotNull()){
                setPreferences(resource.getData(), userLogin.getEmail());
                User user = new User(userLogin, resource.getData().getProfiles());
                insertUserOnRoom(user);
            }else{
                progressBottom.buttonFinished();
                showErrorMessage(resource.getError());
            }
        });
    }

    private void insertUserOnRoom(User user) {
        userRepository.insertOnRoom(user)
                .doOnComplete(this::startNavigationActivity).subscribe();
    }

    private void startNavigationActivity() {
        progressBottom.buttonFinished();
        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    private void setPreferences(UserToken userToken, String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL_SHARED_PREFERENCES, email);
        editor.putString(TOKEN_SHARED_PREFERENCES, userToken.getTypeToken());
        editor.putLong(TENANT_SHARED_PREFERENCES, userToken.getCompanyId());
        editor.putString(PROFILE_SHARED_PREFERENCES, userToken.getProfile());
        editor.putBoolean(IS_LOGGED_SHARED_PREFERENCES, true);
        editor.apply();
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}