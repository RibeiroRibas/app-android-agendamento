package br.com.beautystyle.repository;

import static br.com.beautystyle.retrofit.callback.CallbackMessages.DURATION_TIME_IS_NOT_AVAILABLE;

public class Resource<T> {
    private T data;
    private String error;

    public Resource(T data, String error) {
        this.data = data;
        this.error = error;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isDataNotNull() {
        return this.data!=null;
    }

    public boolean isErrorNotNull() {
        return this.error!=null;
    }

    public boolean isDurationTimeNotAvailable() {
        return error.contains(DURATION_TIME_IS_NOT_AVAILABLE);
    }
}
