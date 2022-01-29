package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EDIT_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NEW_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_EDIT_SERVICE;

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

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Objects;

import br.com.beautystyle.model.Services;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.TimeUtil;
import me.abhinay.input.CurrencyEditText;
import me.abhinay.input.CurrencySymbols;

public class NewServiceFragment extends DialogFragment {

    private EditText durationService, nameService;
    public CurrencyEditText valueService;
    private Services service = new Services();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_new_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initiWidgets(view);
        loadService();
        setDurationServiceListener();
        setValueServiceListener();
        saveServiceListener(view);

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

    private void initiWidgets(View view) {
        nameService = view.findViewById(R.id.fragment_new_service_description);
        valueService = view.findViewById(R.id.fragment_new_service_value);
        durationService = view.findViewById(R.id.fragment_new_service_duration);
    }

    private void loadService() {
        Bundle bundle = getArguments();
        if (bundle != null && getTag() != null && getTag().equals(TAG_EDIT_SERVICE)) {
            service = (Services) bundle.getSerializable(KEY_EDIT_SERVICE);
            fillAllForm();
        }
    }

    private void fillAllForm() {
        nameService.setText(service.getName());
        String formatedDurationService = TimeUtil.formatLocalTime(service.getTimeOfDuration());
        durationService.setText(formatedDurationService);
        String formatedValueService = CoinUtil.formatBrWithoutSymbol(service.getValueOfService());
        valueService.setText(formatedValueService);
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
        valueService.setCurrency(CurrencySymbols.NONE);
        valueService.setDelimiter(false);
        valueService.setSpacing(true);
        valueService.setDecimals(true);
        valueService.setSeparator(".");
    }

    private void saveServiceListener(View inflateNewServiceView) {
        ImageView saveService = inflateNewServiceView.findViewById(R.id.fragment_new_service_save);
        saveService.setOnClickListener(v -> {
            if (checkInputService()) {
                getService();
                saveService();
            }
        });
    }

    private boolean checkInputService() {
        if (nameService.getText().toString().isEmpty()
                || durationService.getText().toString().isEmpty()
                || Objects.requireNonNull(valueService.getText()).toString().isEmpty()) {
            new AlertDialog.Builder(Objects.requireNonNull(getDialog()).getContext())
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
        BigDecimal value = new BigDecimal(CoinUtil.formatBrBigDecimal(Objects.requireNonNull(valueService.getText()).toString()));

        service.setName(name);
        service.setTimeOfDuration(duration);
        service.setValueOfService(value);
    }

    private void saveService() {
        if (getTag() != null && getTag().equals(TAG_EDIT_SERVICE)) {
            setResult(KEY_EDIT_SERVICE);
        } else {
            setResult(KEY_NEW_SERVICE);
        }
        Objects.requireNonNull(getDialog()).dismiss();
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    private void setResult(String key) {
        Bundle result = new Bundle();
        result.putSerializable(key, service);
        getParentFragmentManager().setFragmentResult(KEY_SERVICE, result);
    }

}
