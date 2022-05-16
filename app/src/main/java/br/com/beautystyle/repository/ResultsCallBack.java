package br.com.beautystyle.repository;

public interface ResultsCallBack<T> {

    void onSuccess(T resultado);
    void onError(String erro);
}
