package com.example.beautystyle.ui.fragment;

import android.app.TimePickerDialog;
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

import com.example.beautystyle.model.Services;
import com.example.beautystyle.util.TimeUtil;
import com.example.beautystyle.R;
import com.example.beautystyle.ui.ListServiceView;
import com.example.beautystyle.util.CoinUtil;

import java.math.BigDecimal;
import java.time.LocalTime;

public class NewServiceFragment extends DialogFragment {

    private EditText durationService, nameService;
    public EditText valueService;
    private final ListServiceView listServiceView;
    private Services service = new Services();

    public NewServiceFragment(ListServiceView listServiceView) {
        this.listServiceView = listServiceView;
    }

    public NewServiceFragment(ListServiceView listServiceView, Services editService) {
        this.listServiceView = listServiceView;
        this.service = editService;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.dialog_new_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initiWidgets(view);

        if (getTag().equals("EditServiceFragment"))
            fillFormServiceEditMode();

        setDurationServiceListener();
        setValueServiceListener();
        saveServiceListener(view);

    }

    private void fillFormServiceEditMode() {
        nameService.setText(service.getName());
        String formatedDurationService = TimeUtil.formatLocalTime(service.getTimeOfDuration());
        durationService.setText(formatedDurationService);
        String formatedValueService = CoinUtil.formatBr(service.getValueOfService());
        valueService.setText(formatedValueService);
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

    private void initiWidgets(View view) {
        nameService = view.findViewById(R.id.edTxt_name_service);
        valueService = view.findViewById(R.id.edTxt_value_service);
        durationService = view.findViewById(R.id.edTxt_duration_service);
    }

    private void setDurationServiceListener() {
        durationService.setOnClickListener(v2 -> {
            TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hour, minute) -> {
                String durationServiceFormated = TimeUtil.formatLocalTime(LocalTime.of(hour, minute));
                durationService.setText(durationServiceFormated);
            };
            int hour = 7;
            int minute = 0;
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute, true);
            timePickerDialog.show();
        });
    }

    private void setValueServiceListener() {
        valueService.addTextChangedListener(new CoinUtil(this,true));
    }

    private void saveServiceListener(View inflateNewServiceView) {
        ImageView saveService = inflateNewServiceView.findViewById(R.id.img_save_service);
        saveService.setOnClickListener(v -> {
            if (checkInputService()) {
                getService();
                saveService(service);
            }
        });
    }

    private boolean checkInputService() {
        if (nameService.getText().toString().isEmpty()
                || durationService.getText().toString().isEmpty()
                || valueService.getText().toString().isEmpty()) {
            new AlertDialog.Builder(getDialog().getContext())
                    .setTitle("Todos Os Campos São Obrigatórios!")
                    .setPositiveButton("ok", null)
                    .show();
            return false;
        }
        return true;
    }

    private void getService() {
        String name = nameService.getText().toString();
        LocalTime duration = TimeUtil.formatDurationService(durationService.getText().toString());
        BigDecimal value = new BigDecimal(CoinUtil.formatBrBigDecimal(valueService.getText().toString()));

        service.setName(name);
        service.setTimeOfDuration(duration);
        service.setValueOfService(value);
    }

    private void saveService(Services service) {
        if (getTag().equals("EditServiceFragment")){
            listServiceView.edit(service);
        }else{
            listServiceView.save(service);
        }
        getDialog().dismiss();
    }

}
