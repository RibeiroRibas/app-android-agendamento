package br.com.beautystyle.repository;

import android.content.Context;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomExpenseDao;
import br.com.beautystyle.model.entities.Expense;
import br.com.beautystyle.database.retrofit.BeautyStyleRetrofit;
import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.retrofit.service.ExpenseService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class ExpenseRepository {

    private final RoomExpenseDao dao;
    private final ExpenseService service;

    public ExpenseRepository(Context context) {
        dao = BeautyStyleDatabase.getInstance(context).getRoomExpenseDao();
        service = new BeautyStyleRetrofit().getExpenseService();
    }

    public void insert(Expense expense, ResultsCallBack<Expense> callBack) {
        insertOnApi(expense, callBack);
    }

    private void insertOnApi(Expense expense, ResultsCallBack<Expense> callBack) {
        Call<Expense> callNewExpense = service.insert(expense);
        callNewExpense.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Expense>() {
            @Override
            public void onSuccess(Expense response) {
                callBack.onSuccess(response);
                insertOnRoom(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void insertOnRoom(Expense expense) {
        dao.insert(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void update(Expense expense, ResultsCallBack<Expense> callBack) {
        updateOnApi(expense, callBack);
    }

    private void updateOnApi(Expense expense, ResultsCallBack<Expense> callBack) {
        Call<Expense> callUpdate = service.update(expense);
        callUpdate.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Expense>() {
            @Override
            public void onSuccess(Expense response) {
                callBack.onSuccess(response);
                updateOnRoom(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void updateOnRoom(Expense expense) {
        dao.update(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void delete(Expense expense, ResultsCallBack<Void> callack) {
        deleteOnApi(expense, callack);
    }

    private void deleteOnApi(Expense expense, ResultsCallBack<Void> callack) {
        Call<Void> callDelete = service.delete(expense.getId());
        callDelete.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callack.onSuccess(null);
                deleteOnRoom(expense);
            }

            @Override
            public void onError(String erro) {
                callack.onError(erro);
            }
        }));
    }

    private void deleteOnRoom(Expense expense) {
        dao.delete(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public Observable<List<Expense>> getAllFromRoom() {
        return dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void getByDateFromApi(LocalDate date, ResultsCallBack<List<Expense>> callBack) {
        Call<List<Expense>> callByDate = service.findByDate(date);
        callByDate.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Expense>>() {
            @Override
            public void onSuccess(List<Expense> response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void getAllFromApi(ResultsCallBack<List<Expense>> callBack) {
        Call<List<Expense>> callExpenses = service.getAll();
        callExpenses.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Expense>>() {
            @Override
            public void onSuccess(List<Expense> response) {
                callBack.onSuccess(response);
                insertAllOnRoom(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void insertAllOnRoom(List<Expense> response) {
        dao.insertAll(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
