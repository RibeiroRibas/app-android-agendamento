package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.util.ConstantsUtil.DD;
import static br.com.beautystyle.util.ConstantsUtil.E;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.beautystyle.util.CalendarUtil;

public class ListDaysAdapter extends RecyclerView.Adapter<ListDaysAdapter.ListDaysHolder> {

    private final List<LocalDate> daysList;
    private final OnDayListener mOnDayListener;
    int selectedPosition;
    private ListDaysHolder listDaysHolder;
    private final static String IS_TODAY = "HOJE";

    public ListDaysAdapter(OnDayListener onDayListener) {
        this.mOnDayListener = onDayListener;
        this.daysList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListDaysAdapter.ListDaysHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = inflateView(parent);
        listDaysHolder = new ListDaysHolder(inflatedView, mOnDayListener, daysList);
        return listDaysHolder;
    }

    public View inflateView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_days_week_button, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDaysAdapter.ListDaysHolder holder, int position) {
        holder.onBindDays(daysList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public void update() {
            int size = this.daysList.size();
            this.daysList.clear();
            notifyItemRangeRemoved(0, size);
            List<LocalDate> daysList = createDaysList(CalendarUtil.selectedDate);
            this.daysList.addAll(daysList);
            notifyItemRangeInserted(0, daysList.size());
    }

    private List<LocalDate> createDaysList(LocalDate selectedDate) {
        List<LocalDate> listDays = new ArrayList<>();
        listDays.add(selectedDate);
        for (int i = 1; i <= 364; i++) {
            listDays.add(selectedDate.minusDays(i));
            listDays.add(selectedDate.plusDays(i));
        }
        Collections.sort(listDays);
        return listDays;
    }

    public void onClickViewHolder(LocalDate date) {
        listDaysHolder.onClickCalendar(date, getPosition(date));
    }

    public int getPosition(LocalDate date) {
        return daysList.indexOf(date);
    }

    public class ListDaysHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Button buttonDay;
        private final TextView dayWeek;
        private final OnDayListener onDayListener;
        private final List<LocalDate> daysList;

        public ListDaysHolder(@NonNull View itemView, OnDayListener onDayListener, List<LocalDate> listDays) {
            super(itemView);
            this.buttonDay = itemView.findViewById(R.id.button);
            this.dayWeek = itemView.findViewById(R.id.day_of_week_textview);
            this.onDayListener = onDayListener;
            this.daysList = listDays;

            buttonDay.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void setDrawable(Drawable drawable) {
            buttonDay.setBackground(drawable);
        }

        private void setLayoutParams(int params) {
            ViewGroup.LayoutParams layoutParams = buttonDay.getLayoutParams();
            layoutParams.height = params;
            layoutParams.width = params;
            buttonDay.setLayoutParams(layoutParams);
        }

        public void onBindDays(LocalDate date, int position) {
            String checkIsToday = LocalDate.now().equals(date) ? IS_TODAY : CalendarUtil.formatLocalDate(date, E);
            if (CalendarUtil.selectedDate.equals(date)) {
                Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.shape_button_color2);
                setDrawable(drawable);
                setLayoutParams(150);
                selectedPosition = position;
                setText(checkIsToday, Typeface.BOLD, date, Color.WHITE);
            } else {
                Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.shape_button_color1);
                setLayoutParams(130);
                setDrawable(drawable);
                setText(checkIsToday, Typeface.NORMAL, date, Color.BLACK);
            }
            onDayListener.onDayBinding(date);
        }

        private void setText(String formatedDay, int typeFace, LocalDate date, int textColor) {
            buttonDay.setText(CalendarUtil.formatLocalDate(date, DD));
            buttonDay.setTextColor(textColor);
            dayWeek.setTypeface(null, typeFace);
            dayWeek.setText(formatedDay);
        }

        @Override
        public void onClick(View v) {
            itemChanged(getLayoutPosition());
            onDayListener.onDayClick(daysList.get(getAdapterPosition()), selectedPosition);
            onDayListener.onDayBinding(daysList.get(getAdapterPosition()));
        }

        private void itemChanged(int layoutPosition) {
            notifyItemChanged(selectedPosition);
            selectedPosition = layoutPosition;
            notifyItemChanged(selectedPosition);
        }

        public void onClickCalendar(LocalDate date, int position) {
            itemChanged(position);
            onDayListener.onDayClick(date, selectedPosition);
            onDayListener.onDayBinding(date);
        }
    }

    public interface OnDayListener {
        void onDayClick(LocalDate date, int position);

        void onDayBinding(LocalDate date);
    }
}

