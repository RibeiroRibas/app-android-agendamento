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

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.UserViewModel;
import br.com.beautystyle.ViewModel.factory.UserFactory;
import br.com.beautystyle.model.entity.OpeningHours;
import br.com.beautystyle.model.entity.User;
import br.com.beautystyle.repository.OpeningHoursRepository;
import br.com.beautystyle.repository.UserRepository;
import br.com.beautystyle.retrofit.model.dto.UserDto;
import br.com.beautystyle.retrofit.model.form.UserLoginForm;
import br.com.beautystyle.ui.ProgressBottom;
import io.reactivex.rxjava3.core.Completable;


public class LoginActivity extends AppCompatActivity {

    @Inject
    UserRepository userRepository;
    @Inject
    OpeningHoursRepository openingHoursRepository;
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
        viewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
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
                UserLoginForm userLoginForm = new UserLoginForm(userEmail, userPassword);
                authUserOnApi(userLoginForm);
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

    private void authUserOnApi(UserLoginForm userLoginForm) {
        viewModel.authUser(userLoginForm).observe(this, resource -> {
            if (resource.isDataNotNull()) {
                UserDto response = resource.getData();
                setPreferences(response, userLoginForm.getEmail());

                insertUserOnRoom(userLoginForm, response)
                        .doOnComplete(() ->
                                openingHoursRepository.getAll().doOnSuccess(openingHours -> {
                                    openingHours.forEach(fromRoom -> {
                                        response.getOpeningHours().forEach(fromApi -> {
                                            if (fromRoom.isApiIdEquals(fromApi))
                                                fromApi.setId(fromRoom.getId());
                                        });
                                    });
                                    insertOpeningHoursOnRoom(response);
                                }).subscribe()
                        ).subscribe();


            } else {
                progressBottom.buttonFinished();
                showErrorMessage(resource.getError());
            }
        });
    }

    private Completable insertUserOnRoom(UserLoginForm userLoginForm, UserDto response) {
        User user = new User(userLoginForm, response.getProfiles());
        return userRepository.insertOnRoom(user);
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
        progressBottom.buttonFinished();
        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    private void setPreferences(UserDto userDto, String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL_SHARED_PREFERENCES, email);
        editor.putString(TOKEN_SHARED_PREFERENCES, userDto.getTypeToken());
        editor.putLong(TENANT_SHARED_PREFERENCES, userDto.getTenant());
        editor.putString(PROFILE_SHARED_PREFERENCES, userDto.getProfile());
        editor.putBoolean(IS_LOGGED_SHARED_PREFERENCES, true);
        editor.apply();
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}