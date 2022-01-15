package br.com.beautystyle.ui.adapter.recyclerview;

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

import br.com.beautystyle.util.CalendarUtil;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListDaysAdaper extends RecyclerView.Adapter<ListDaysAdaper.ListDaysHolder> {

    private final List<LocalDate> listDays = new ArrayList<>();
    private final OnDayListener mOnDayListener;
    int selectedPosition;

    public ListDaysAdaper(OnDayListener onDayListener) {
        this.mOnDayListener = onDayListener;
    }

    @NonNull
    @Override
    public ListDaysAdaper.ListDaysHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View createView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_days_week_button, parent, false);
        return new ListDaysHolder(createView, mOnDayListener, listDays);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDaysAdaper.ListDaysHolder holder, int position) {

        holder.setTextView(listDays.get(position), position);

    }

    @Override
    public int getItemCount() {
        return listDays.size();
    }

    public void publishAllDays() {
        List<LocalDate> createdList = CalendarUtil.createDaysList();
        this.listDays.addAll(createdList);
        int size2 = createdList.size();
        notifyItemRangeInserted(0, size2);
    }

    public class ListDaysHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Button buttonDay;
        private final TextView dayWeek;
        private final OnDayListener onDayListener;
        private final List<LocalDate> listDays;


        public ListDaysHolder(@NonNull View itemView, OnDayListener onDayListener, List<LocalDate> listDays) {
            super(itemView);
            this.buttonDay = itemView.findViewById(R.id.button);
            this.dayWeek = itemView.findViewById(R.id.day_of_week_textview);
            this.onDayListener = onDayListener;
            this.listDays = listDays;

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

        public void setTextView(LocalDate date, int position) {
            String checkIsToday = LocalDate.now().equals(date) ? "TODAY" : CalendarUtil.formatDayWeek(date);
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
            buttonDay.setText(CalendarUtil.formatDay(date));
            buttonDay.setTextColor(textColor);
            dayWeek.setTypeface(null, typeFace);
            dayWeek.setText(formatedDay);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(selectedPosition);
            selectedPosition = getLayoutPosition();
            notifyItemChanged(selectedPosition);
            onDayListener.onDayClick(listDays.get(getAdapterPosition()), selectedPosition);
        }
    }

    public interface OnDayListener {
        void onDayClick(LocalDate date, int position);
        void onDayBinding(LocalDate date);
    }
}

