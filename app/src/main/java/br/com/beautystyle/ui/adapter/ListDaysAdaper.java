package br.com.beautystyle.ui.adapter;

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

    public ListDaysAdaper(OnDayListener onDayListener) {
        this.mOnDayListener = onDayListener;
    }

    @NonNull
        @Override
        public ListDaysAdaper.ListDaysHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View createView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_days_week_button, parent, false);
            return new ListDaysHolder(createView, mOnDayListener,listDays);
        }

        @Override
        public void onBindViewHolder(@NonNull ListDaysAdaper.ListDaysHolder holder, int position) {
            holder.setLayoutEventHolder(listDays.get(position));
            holder.setTextView(listDays.get(position));
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        public void atualizaAdapter(){
            this.listDays.clear();
            this.listDays.addAll(CalendarUtil.fiveDays());
            notifyDataSetChanged();
        }

    public static class ListDaysHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final Button buttonDay;
        private final TextView dayWeek;
        private final OnDayListener onDayListener;
        private final List<LocalDate> listDays;

        public ListDaysHolder(@NonNull View itemView, OnDayListener onDayListener,List<LocalDate> listDays) {
            super(itemView);
            this.buttonDay = itemView.findViewById(R.id.button);
            this.dayWeek = itemView.findViewById(R.id.day_of_week_textview);
            this.onDayListener = onDayListener;
            this.listDays = listDays;

            buttonDay.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void setLayoutEventHolder(LocalDate date) {
            if(date.equals(CalendarUtil.selectedDate)){
                setDrawable();
                setLayoutParams();
                buttonDay.setTextColor(Color.WHITE);
            }
        }

        private void setDrawable() {
            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.shape_button_color2);
            buttonDay.setBackground(drawable);
        }

        private void setLayoutParams() {
            ViewGroup.LayoutParams layoutParams = buttonDay.getLayoutParams();
            layoutParams.height = 150;
            layoutParams.width = 150;
            buttonDay.setLayoutParams(layoutParams);
        }

        public void setTextView(LocalDate date){
            buttonDay.setText(CalendarUtil.formatDay(date));
            dayWeek.setTypeface(null, Typeface.NORMAL);
            dayWeek.setText(CalendarUtil.formatDayWeek(date));
            if(LocalDate.now().equals(date)){
                dayWeek.setText("HOJE");
                dayWeek.setTypeface(null, Typeface.BOLD);
            }
        }

        @Override
        public void onClick(View v) {
            onDayListener.onDayClick(listDays.get(getAdapterPosition()));
        }
    }
    public interface OnDayListener {
        void onDayClick(LocalDate date);
    }
    }

