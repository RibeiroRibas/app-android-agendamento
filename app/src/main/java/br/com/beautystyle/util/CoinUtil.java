package br.com.beautystyle.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CoinUtil {

    public static final String PORTUGUES = "pt";
    public static final String BRASIL = "br";
    public static final String FORMAT_PADRAO = "R$";
    public static final String FORMAT_DESEJADO = "R$ ";
    public static final String FORMAT_DESEJADO_ = "";

    public static String formatBrWithoutSymbol(BigDecimal valor) {
        NumberFormat formatoBr = DecimalFormat.getCurrencyInstance(
                new Locale(PORTUGUES, BRASIL));
        return formatoBr.format(valor)
                .replace(FORMAT_PADRAO, FORMAT_DESEJADO_);
    }

    public static String formatBr(BigDecimal valor) {
        NumberFormat formatoBr = DecimalFormat.getCurrencyInstance(
                new Locale(PORTUGUES, BRASIL));
        return formatoBr.format(valor)
                .replace(FORMAT_PADRAO, FORMAT_DESEJADO);
    }

    public static String formatBrBigDecimal(String valor) {
        valor = valor.replace(".", "")
                .replace(",", ".")
                .trim()
                .substring(1);
        return valor;
    }
}