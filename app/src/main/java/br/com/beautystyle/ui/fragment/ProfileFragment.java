package br.com.beautystyle.ui.fragment;

import static android.content.Context.MODE_PRIVATE;
import static br.com.beautystyle.repository.ConstantsRepository.IS_LOGGED_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_SHARED_PREFERENCES;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;

import br.com.beautystyle.ui.activity.LoginActivity;

public class ProfileFragment extends Fragment {

    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = requireActivity().getSharedPreferences(USER_SHARED_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_profile, container, false);

        Button logoutBtn = inflate.findViewById(R.id.fragment_profile_btn_logout);
        logoutBtn.setOnClickListener(v->{
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IS_LOGGED_SHARED_PREFERENCES, false);
            editor.apply();
        });

        return inflate;
    }
}