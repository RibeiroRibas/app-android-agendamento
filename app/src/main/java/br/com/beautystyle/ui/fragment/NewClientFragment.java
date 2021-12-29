package br.com.beautystyle.ui.fragment;

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

import br.com.beautystyle.model.Client;
import com.example.beautystyle.R;
import br.com.beautystyle.ui.ListClientView;

public class NewClientFragment extends DialogFragment {

    private final ListClientView listClientView;
    private EditText nameClient,phoneClient;
    private Client client =new Client();

    public NewClientFragment(ListClientView listClientView) {
        this.listClientView = listClientView;
    }

    public NewClientFragment(ListClientView listClientView, Client editClient) {
        this.listClientView = listClientView;
        this.client = editClient;
    }

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
        if(getTag().equals("EditClientFragment"))
            fillFormService();
        saveClientListener(view);
    }

    private void initWidgets(View view) {
        nameClient = view.findViewById(R.id.edTxt_name_service);
        phoneClient = view.findViewById(R.id.edTxt_duration_service);
    }

    private void fillFormService() {
        nameClient.setText(client.getName());
        phoneClient.setText(client.getPhone());
    }

    @Override
    public void onResume() {
        super.onResume();
        setLayoutParamsDialog();
    }

    private void setLayoutParamsDialog() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    private void saveClientListener(View inflateNewEventView) {
        ImageView saveClient = inflateNewEventView.findViewById(R.id.img_save_service);
        saveClient.setOnClickListener(v -> {
            if (checkInputNameClient()) {
                saveClient();
            }
        });
    }

    private boolean checkInputNameClient() {
        if (nameClient.getText().toString().isEmpty()) {
            new AlertDialog.Builder(getDialog().getContext())
                    .setTitle("Campo nome obrigatório!")
                    .setPositiveButton("ok", null).show();
            return false;
        }
        return true;
    }

    private void saveClient() {
        getClient();
        if(getTag().equals("EditClientFragment")){
            listClientView.edit(client);
        }else{
            listClientView.save(client);
        }
        getDialog().dismiss();
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void getClient() {
        String name = nameClient.getText().toString();
        String phone = phoneClient.getText().toString();

        client.setName(name);
        client.setPhone(phone);
    }

}
