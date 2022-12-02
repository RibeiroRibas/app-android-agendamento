package br.com.beautystyle.ui.fragment.job;

import static br.com.beautystyle.ui.activity.ContantsActivity.TAG_EVENT_DURATION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_JOB;
import static br.com.beautystyle.util.ConstantsUtil.REMOVE_SYMBOL;

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

import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.ui.fragment.TimePickerFragment;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.MoneyTextWatcher;
import br.com.beautystyle.util.TimeUtil;

public class NewJobFragment extends DialogFragment {

    private EditText durationOfJob, nameJob, valueOfJob;
    private final TimePickerDialog.OnTimeSetListener timeListener = timePickerDialogListener();
    private Job job = new Job();
    private int adapterPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_new_job, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        restoreTimePickerListener(savedInstanceState);

        initWidgets(view);
        loadJob();

        //LISTENERS
        durationOfJobListener();
        setResultJobListener(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        setLayoutParamsDialog();
    }

    private void restoreTimePickerListener(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            TimePickerFragment tpf = (TimePickerFragment) getParentFragmentManager()
                    .findFragmentByTag(TAG_EVENT_DURATION);
            if (tpf != null) {
                tpf.setOnTimeSetListener(timeListener);
            }
        }
    }

    private void setLayoutParamsDialog() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog())
                .getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    private void initWidgets(View view) {
        nameJob = view.findViewById(R.id.fragment_new_job_description);
        valueOfJob = view.findViewById(R.id.fragment_new_job_value);
        valueOfJob.addTextChangedListener(new MoneyTextWatcher(valueOfJob));
        durationOfJob = view.findViewById(R.id.fragment_new_job_duration);
    }

    private void loadJob() {
        Bundle bundle = getArguments();
        if (bundle != null && getTag() != null && getTag().equals(TAG_UPDATE_JOB)) {
            job = (Job) bundle.getSerializable(KEY_UPDATE_JOB);
            adapterPosition = bundle.getInt(KEY_POSITION);
            fillAllForm();
        }
    }

    private void fillAllForm() {
        nameJob.setText(job.getName());
        String duration = TimeUtil.formatLocalTime(job.getDurationTime());
        durationOfJob.setText(duration);
        String value = CoinUtil.format(job.getPrice(), REMOVE_SYMBOL);
        valueOfJob.setText(value);
    }

    private void durationOfJobListener() {
        durationOfJob.setOnClickListener(v2 ->
                getParentFragmentManager()
                        .beginTransaction()
                        .add(TimePickerFragment.newInstance(timeListener), TAG_EVENT_DURATION
                        )
                        .commit()
        );
    }

    private void setResultJobListener(View inflateNewServiceView) {
        ImageView insertJob = inflateNewServiceView.findViewById(R.id.fragment_new_job_save);
        insertJob.setOnClickListener(v -> {
            if (checkInputJob()) {
                setResult();
            }
        });
    }

    private boolean checkInputJob() {
        if (nameJob.getText().toString().isEmpty()
                || durationOfJob.getText().toString().isEmpty()
                || Objects.requireNonNull(valueOfJob.getText()).toString().isEmpty()) {
            new AlertDialog.Builder(Objects.requireNonNull(getDialog()).getContext())
                    .setTitle("Todos Os Campos São Obrigatórios!")
                    .setPositiveButton("ok", null)
                    .show();
            return false;
        }
        return true;
    }

    private void onBindJob(Job job) {
        String name = nameJob.getText().toString();
        LocalTime duration = TimeUtil.formatDurationService(durationOfJob.getText().toString());
        BigDecimal value = new BigDecimal(CoinUtil.formatPriceSave((valueOfJob.getText()).toString()));

        job.setName(name);
        job.setDurationTime(duration);
        job.setPrice(value);
    }

    private void setResult() {
        String key = (getTag() != null && getTag().equals(TAG_UPDATE_JOB))
                ? KEY_UPDATE_JOB : KEY_INSERT_JOB;
        setResult(key);
        Objects.requireNonNull(getDialog()).dismiss();
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    private void setResult(String key) {
        Bundle result = new Bundle();
        onBindJob(job);
        result.putSerializable(key, job);
        result.putInt(KEY_POSITION, adapterPosition);
        getParentFragmentManager().setFragmentResult(KEY_JOB, result);
    }

    private TimePickerDialog.OnTimeSetListener timePickerDialogListener() {
        return (view, hour, minute) -> {
            LocalTime timeWatch = LocalTime.of(hour, minute);
            String time = TimeUtil.formatLocalTime(timeWatch);
            durationOfJob.setText(time);
        };
    }

}
