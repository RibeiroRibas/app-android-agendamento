package br.com.beautystyle.util;

import static br.com.beautystyle.util.MoneyTextWatcher.getCurrencySymbol;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CoinUtil {

    private static final String PORTUGUESE = "pt";
    private static final String BRASIL = "br";
    private static final String DEFAULT_FORMAT = "R$";

    public static String format(BigDecimal value, String desiredFormat){
        NumberFormat formatoBr = DecimalFormat.getCurrencyInstance(
                new Locale(PORTUGUESE, BRASIL));
        return formatoBr.format(value)
                .replace(DEFAULT_FORMAT, desiredFormat);
    }

    public static String formatPriceSave(String price) {

        String replaceable = String.format("[%s,.\\s]", getCurrencySymbol());
        String cleanString = price.replaceAll(replaceable, "");
        StringBuilder stringBuilder = new StringBuilder(cleanString.replaceAll(" ", ""));

        return String.valueOf(stringBuilder.insert(cleanString.length() - 2, '.'));
    }
}