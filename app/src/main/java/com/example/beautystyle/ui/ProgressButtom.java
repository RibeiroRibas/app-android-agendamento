package com.example.beautystyle.ui;

import android.view.View;
import android.widget.TextView;

import com.example.beautystyle.R;

public class ProgressButtom {

    private View progressbar;
    private TextView textView;

    public ProgressButtom(View view) {
        progressbar = view.findViewById(R.id.custom_loading_progress_bar);
        textView = view.findViewById(R.id.custom_loading_text_view);
    }

    public void buttonActivated(){
        progressbar.setVisibility(View.VISIBLE);
        textView.setText("Importando...");
    }

    public void buttonFinished(){
        progressbar.setVisibility(View.GONE);
        textView.setText("Importar");
    }
}
