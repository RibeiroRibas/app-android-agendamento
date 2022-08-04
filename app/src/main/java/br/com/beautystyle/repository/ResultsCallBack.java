package br.com.beautystyle.repository;

public interface ResultsCallBack<T> {

    void onSuccess(T result);
    void onError(String error);
}
