package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.INVALID_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EDIT_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NEW_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_EDIT_CLIENT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.beautystyle.R;

import java.util.Objects;

import br.com.beautystyle.model.Client;

public class NewClientFragment extends DialogFragment {

    private EditText nameClient, phoneClient;
    private Client client = new Client();
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_new_client, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initWidgets(view);
        loadClient();
        saveClientListener(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        setLayoutParamsDialog();
    }

    private void setLayoutParamsDialog() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    private void initWidgets(View view) {
        nameClient = view.findViewById(R.id.fragment_new_client_name);
        phoneClient = view.findViewById(R.id.fragment_new_client_phone);
    }

    private void loadClient() {
        Bundle bundle = getArguments();
        if (bundle != null && getTag() != null && getTag().equals(TAG_EDIT_CLIENT)) {
            client = (Client) bundle.getSerializable(KEY_EDIT_CLIENT);
            position = bundle.getInt(KEY_POSITION, INVALID_POSITION);
            fillAllForm();
        }
    }

    private void fillAllForm() {
        nameClient.setText(client.getName());
        phoneClient.setText(client.getPhone());
    }

    private void saveClientListener(View inflateNewEventView) {
        ImageView saveClient = inflateNewEventView.findViewById(R.id.fragment_new_client_save);
        saveClient.setOnClickListener(v -> {
            if (checkInputNameClient()) {
                saveClient();
            }
        });
    }

    private boolean checkInputNameClient() {
        if (nameClient.getText().toString().isEmpty()) {
            new AlertDialog.Builder(Objects.requireNonNull(getDialog()).getContext())
                    .setTitle("Campo nome obrigatório!")
                    .setPositiveButton("ok", null).show();
            return false;
        }
        return true;
    }

    private void saveClient() {
        getClient();
        if (getTag() != null) {
            if (getTag().equals(TAG_EDIT_CLIENT)) {
                setResult(KEY_EDIT_CLIENT, position);
            } else {
                setResult(KEY_NEW_CLIENT, INVALID_POSITION);
            }
        }
        Objects.requireNonNull(getDialog()).dismiss();
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    private void setResult(String key, int position) {
        Bundle result = new Bundle();
        result.putSerializable(key, client);
        result.putInt(KEY_POSITION, position);
        getParentFragmentManager().setFragmentResult(KEY_CLIENT, result);
    }


    private void getClient() {
        String name = nameClient.getText().toString();
        String phone = phoneClient.getText().toString();

        client.setName(name);
        client.setPhone(phone);
    }

}
