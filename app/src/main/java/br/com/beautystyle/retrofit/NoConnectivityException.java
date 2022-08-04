package br.com.beautystyle.retrofit;

import static br.com.beautystyle.retrofit.callback.CallbackMessages.NO_INTERNET_CONNECTION;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return NO_INTERNET_CONNECTION;
    }
}