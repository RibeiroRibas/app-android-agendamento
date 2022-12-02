package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.model.util.Report;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.retrofit.service.ExpenseService;
import retrofit2.Call;

public class ExpenseWebClient {

    @Inject
    ExpenseService service;
    private final String token;
    private final Long tenant;

    @Inject
    public ExpenseWebClient(SharedPreferences preferences) {
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void insert(Expense expense, ResultsCallBack<Expense> callBack) {
        Call<Expense> callNewExpense = service.insert(expense, token);
        callNewExpense.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Expense>() {
            @Override
            public void onSuccess(Expense response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void update(Expense expense, ResultsCallBack<Expense> callBack) {
        Call<Expense> callUpdate = service.update(expense, token);
        callUpdate.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Expense>() {
            @Override
            public void onSuccess(Expense response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));

    }

    public void delete(Expense expense, ResultsCallBack<Void> callBack) {
        Call<Void> callDelete = service.delete(expense.getApiId(), token);
        callDelete.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void getByPeriod(LocalDate startDate, LocalDate endDate,
                            ResultsCallBack<List<Expense>> callBack) {
        Call<List<Expense>> callByPeriod = service.getByPeriod(startDate, endDate, tenant, token);
        callByPeriod.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Expense>>() {
            @Override
            public void onSuccess(List<Expense> expensesFromApi) {
                callBack.onSuccess(expensesFromApi);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void getYearsList(ResultsCallBack<List<String>> callBack) {
        Call<List<String>> callYears = service.getYearsList(tenant, token);
        callYears.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<String>>() {
            @Override
            public void onSuccess(List<String> response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));

    }

    public void getExpenseReportByDateFromApi(LocalDate selectedDate,
                                              ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReport = service.getReportByDate(selectedDate, tenant, token);
        callReport.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> response) {
                        callBack.onSuccess(response);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }));
    }

    public void getReportByPeriod(LocalDate startDate, LocalDate endDate,
                                  ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReportByPeriod =
                service.getReportByPeriod(startDate, endDate, tenant, token);
        callReportByPeriod.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> response) {
                        callBack.onSuccess(response);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }));
    }
}
