package br.com.beautystyle.ui.fragment.client;

import static br.com.beautystyle.ui.fragment.ConstantFragment.INVALID_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_CLIENT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.beautystyle.R;

import java.util.Objects;

import br.com.beautystyle.model.entity.Client;

public class NewClientFragment extends DialogFragment {

    private EditText nameClient, phoneClient;
    private int adapterPosition;
    private  Client client = new Client();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_new_client, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets(view);
        setResultClientListener(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        loadClient();
        setLayoutParamsDialog();
    }

    private void setLayoutParamsDialog() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    private void initWidgets(View view) {
        nameClient = view.findViewById(R.id.fragment_new_client_name);
        phoneClient = view.findViewById(R.id.fragment_new_client_phone);
    }

    private void loadClient() {
        Bundle bundle = getArguments();
        if (isKeyUpdateClient(bundle)) {
            client = (Client) bundle.getSerializable(KEY_UPDATE_CLIENT);
            adapterPosition = bundle.getInt(KEY_POSITION, INVALID_POSITION);
            fillAllForm(client);
        }
    }

    private boolean isKeyUpdateClient(Bundle bundle) {
        return bundle != null && getTag() != null && getTag().equals(TAG_UPDATE_CLIENT);
    }

    private void fillAllForm(Client client) {
        nameClient.setText(client.getName());
        phoneClient.setText(client.getPhone());
    }

    private void setResultClientListener(View inflateNewEventView) {
        ImageView saveClient = inflateNewEventView.findViewById(R.id.fragment_new_client_save);
        saveClient.setOnClickListener(v -> {
            if (checkInputNameClient()) {
                setResult();
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

    private void setResult() {
        setClient();
        if (getTag() != null) {
            if (getTag().equals(TAG_UPDATE_CLIENT)) {
                setResult(KEY_UPDATE_CLIENT, adapterPosition, client);
            } else {
                setResult(KEY_INSERT_CLIENT, INVALID_POSITION, client);
            }
        }
        Objects.requireNonNull(getDialog()).dismiss();
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    private void setResult(String key, int position, Client client) {
        Bundle result = new Bundle();
        result.putSerializable(key, client);
        result.putInt(KEY_POSITION, position);
        getParentFragmentManager().setFragmentResult(KEY_CLIENT, result);
    }

    private void setClient() {
        String name = nameClient.getText().toString();
        client.setName(name);
        String phone = phoneClient.getText().toString();
        client.setPhone(phone);
    }

}
