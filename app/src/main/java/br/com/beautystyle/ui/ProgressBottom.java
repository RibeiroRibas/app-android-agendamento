package br.com.beautystyle.ui;

import android.view.View;
import android.widget.ProgressBar;

public class ProgressBottom {

    private final View progressbar;

    public ProgressBottom(ProgressBar progressBar) {
        progressbar = progressBar;
    }

    public void buttonActivated() {
        progressbar.setVisibility(View.VISIBLE);
    }

    public void buttonFinished() {
        progressbar.setVisibility(View.GONE);
    }

}
