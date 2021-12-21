package com.example.beautystyle.util;

import static android.content.ContentValues.TAG;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.example.beautystyle.ui.activity.NewEventActivity;
import com.example.beautystyle.ui.fragment.ListEventFragment;
import com.example.beautystyle.ui.fragment.ListServiceFragment;
import com.example.beautystyle.ui.fragment.NewServiceFragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CoinUtil implements TextWatcher {

    public static final String PORTUGUES = "pt";
    public static final String BRASIL = "br";
    public static final String FORMAT_PADRAO = "R$";
    public static final String FORMAT_DESEJADO = "";

    private String current = "";
    private final String Currency = "";
    private final String Separator = ",";
    private final Boolean Spacing=false;
    boolean Delimiter= false;
    boolean dialogNewServiceContext = false;
    private final Boolean Decimals = true;
    private NewEventActivity newEventActivity;
    private NewServiceFragment newServiceFragment ;

    public CoinUtil(NewEventActivity newEventActivity) {
        this.newEventActivity = newEventActivity;
    }

    public CoinUtil(NewServiceFragment newServiceFragment,boolean checkService){
        this.newServiceFragment = newServiceFragment;
        this.dialogNewServiceContext = checkService;
    }

    public static String formatBr(BigDecimal valor) {
        NumberFormat formatoBr = DecimalFormat.getCurrencyInstance(
                new Locale(PORTUGUES, BRASIL));
        return formatoBr.format(valor)
                .replace(FORMAT_PADRAO, FORMAT_DESEJADO);
    }

    public static String formatBrBigDecimal(String valor) {
        valor = valor.replace(".","").replace(",",".").substring(1);
        return valor;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (!s.toString().equals(current)) {
            if(!dialogNewServiceContext){
                newEventActivity.valueOfTheServices.removeTextChangedListener(this);
            }else{
                newServiceFragment.valueService.removeTextChangedListener(this);
            }
            String cleanString = s.toString().replaceAll("[$,.]", "").replaceAll(Currency, "").replaceAll("\\s+", "");

            if (cleanString.length() != 0) {
                try {
                    String currencyFormat = "";
                    if (Spacing) {
                        if (Delimiter) {
                            currencyFormat = Currency + ". ";
                        } else {
                            currencyFormat = Currency + " ";
                        }
                    } else {
                        if (Delimiter) {
                            currencyFormat = Currency + ".";
                        } else {
                            currencyFormat = Currency;
                        }
                    }

                    double parsed;
                    int parsedInt;
                    String formatted;

                    if (Decimals) {
                        parsed = Double.parseDouble(cleanString);
                        formatted = NumberFormat.getCurrencyInstance().format((parsed / 100)).replace(NumberFormat.getCurrencyInstance().getCurrency().getSymbol(), currencyFormat);
                    } else {
                        parsedInt = Integer.parseInt(cleanString);
                        formatted = currencyFormat + NumberFormat.getNumberInstance(Locale.US).format(parsedInt);
                    }

                    current = formatted;

                    //if decimals are turned off and Separator is set as anything other than commas..
                    if (!Separator.equals(",") && !Decimals) {
                        //..replace the commas with the new separator
                        if(!dialogNewServiceContext) {
                            newEventActivity.valueOfTheServices.setText(formatted.replaceAll(",", Separator));
                        }else{
                            newServiceFragment.valueService.setText(formatted.replaceAll(",", Separator));
                            }

                    } else {
                        //since no custom separators were set, proceed with comma separation
                        if(!dialogNewServiceContext){
                            newEventActivity.valueOfTheServices.setText(formatted);
                        }else{
                            newServiceFragment.valueService.setText(formatted);
                        }

                    }
                    if(!dialogNewServiceContext){
                        newEventActivity.valueOfTheServices.setSelection(formatted.length());
                    }else{
                        newServiceFragment.valueService.setSelection(formatted.length());
                    }

                } catch (NumberFormatException e) {

                }
            }
            if(!dialogNewServiceContext){
                newEventActivity.valueOfTheServices.addTextChangedListener(this);
            }else{
                newServiceFragment.valueService.addTextChangedListener(this);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}