package br.com.beautystyle.ui;

import android.view.View;
import android.widget.TextView;

import com.example.beautystyle.R;

public class ProgressButtom {

    private final View progressbar;
    private final TextView textView;
    private static final String TXT_IMPORT = "Importar";
    private static final String TXT_IMPORTING = "Importando...";

    public ProgressButtom(View view) {
        progressbar = view.findViewById(R.id.custom_loading_progress_bar);
        textView = view.findViewById(R.id.custom_loading_text_view);
    }

    public void buttonActivated() {
        progressbar.setVisibility(View.VISIBLE);
        textView.setText(TXT_IMPORTING);
    }

    public void buttonFinished() {
        progressbar.setVisibility(View.GONE);
        textView.setText(TXT_IMPORT);
    }
}
