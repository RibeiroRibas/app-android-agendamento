package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_BLOCK_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_BLOCK_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_START_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_BLOCK_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_END_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_START_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_BLOCK_TIME;

import android.app.TimePickerDialog;
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
import androidx.fragment.app.DialogFragment;

import com.example.beautystyle.R;

import java.time.LocalTime;
import java.util.Objects;

import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.TimeUtil;

public class BlockTimeFragment extends DialogFragment {

    private EditText startTime, endTime, reason;
    private final TimePickerDialog.OnTimeSetListener timePickerListener = timePickerDialogListener();
    private BlockTime blockTime = new BlockTime();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_block_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initWidgets(view);
        loadBlockTime();
        restoreTimePickerListener(savedInstanceState);
        startTimeListener();
        endTimeListener();
        setResultListener(view);
    }

    private void loadBlockTime() {
        Bundle bundle = getArguments();
        if (bundle != null)
            if (isTagUpdate(bundle)) {
                this.blockTime = (BlockTime) bundle.getSerializable(KEY_UPDATE_BLOCK_TIME);
                startTime.setText(TimeUtil.formatLocalTime(blockTime.getStartTime()));
                endTime.setText(TimeUtil.formatLocalTime(blockTime.getEndTime()));
                reason.setText(blockTime.getReason());
            } else {
                LocalTime startTime = (LocalTime) bundle.getSerializable(KEY_START_TIME);
                this.blockTime.setStartTime(startTime);
                this.startTime.setText(TimeUtil.formatLocalTime(startTime));
            }
    }

    private boolean isTagUpdate(Bundle bundle) {
        return bundle != null && getTag() != null && getTag().equals(TAG_UPDATE_BLOCK_TIME);
    }

    private void setResultListener(View view) {
        ImageView insert = view.findViewById(R.id.fragment_block_time_insert);
        insert.setOnClickListener(v -> {
            if (checkRequiredFields()) {
                setResult();
            }
        });
    }

    private void setResult() {
        blockTime.setReason(reason.getText().toString());
        blockTime.setDate(CalendarUtil.selectedDate);
        if (getTag() != null) {
            if (getTag().equals(TAG_UPDATE_BLOCK_TIME)) {
                setResult(KEY_UPDATE_BLOCK_TIME);
            } else {
                setResult(KEY_INSERT_BLOCK_TIME);
            }
        }
        Objects.requireNonNull(getDialog()).dismiss();
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    private void setResult(String key) {
        Bundle result = new Bundle();
        result.putSerializable(key, blockTime);
        getParentFragmentManager().setFragmentResult(KEY_BLOCK_TIME, result);
    }

    private boolean checkRequiredFields() {
        return true;
    }

    private void endTimeListener() {
        endTime.setOnClickListener(v -> {
            timePickerFragment(TimePickerFragment.newInstance(
                    timePickerListener, blockTime.getStartTime()), TAG_END_TIME
            );
        });
    }

    private void restoreTimePickerListener(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            TimePickerFragment startTimeFragment = getFragmentByTag(TAG_START_TIME);
            TimePickerFragment endTimeFragment = getFragmentByTag(TAG_END_TIME);
            checkListener(startTimeFragment, endTimeFragment);
        }
    }

    private void checkListener(TimePickerFragment startTimeFrag,
                               TimePickerFragment endTimeFrag) {
        if (startTimeFrag != null) {
            startTimeFrag.setOnTimeSetListener(timePickerListener);
        } else if (endTimeFrag != null) {
            endTimeFrag.setOnTimeSetListener(timePickerListener);
        }
    }

    private void startTimeListener() {
        startTime.setOnClickListener(v -> {
            timePickerFragment(TimePickerFragment.newInstance(
                    timePickerListener, blockTime.getStartTime()), TAG_START_TIME
            );
        });
    }

    private void timePickerFragment(TimePickerFragment timePickerFragment, String tag) {
        getParentFragmentManager()
                .beginTransaction()
                .add(timePickerFragment, tag)
                .commit();
    }

    private void initWidgets(View view) {
        startTime = view.findViewById(R.id.fragment_block_time_start);
        endTime = view.findViewById(R.id.fragment_block_time_end);
        reason = view.findViewById(R.id.fragment_block_time_reason);
    }

    @Override
    public void onResume() {
        super.onResume();
        setLayoutParamsDialog();
    }

    private void setLayoutParamsDialog() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    private TimePickerDialog.OnTimeSetListener timePickerDialogListener() {
        return (view, hour, minute) -> {
            LocalTime timeWatch = LocalTime.of(hour, minute);
            String timeFormatted = TimeUtil.formatLocalTime(timeWatch);
            TimePickerFragment startTimeFragment = getFragmentByTag(TAG_START_TIME);
            if (startTimeFragment != null) {
                setStartTime(timeWatch, timeFormatted);
            } else {
                setEndTime(timeWatch, timeFormatted);
            }
        };
    }

    private TimePickerFragment getFragmentByTag(String tag) {
        return (TimePickerFragment) getParentFragmentManager().findFragmentByTag(tag);
    }

    private void setStartTime(LocalTime timeWatch, String timeFormatted) {
        startTime.setText(timeFormatted);
        blockTime.setStartTime(timeWatch);
    }

    private void setEndTime(LocalTime timeWatch, String timeFormatted) {
        endTime.setText(timeFormatted);
        blockTime.setEndTime(timeWatch);
    }

}