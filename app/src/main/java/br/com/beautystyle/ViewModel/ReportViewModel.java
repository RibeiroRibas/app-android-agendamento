package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.util.Report;
import br.com.beautystyle.model.entity.TypeOfReport;
import br.com.beautystyle.repository.ReportRepository;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.util.CreateListsUtil;

public class ReportViewModel extends ViewModel {

    private final ReportRepository repository;

    public ReportViewModel(ReportRepository repository) {
        this.repository = repository;
    }

    private final MutableLiveData<Resource<List<Report>>> mutableReport = new MutableLiveData<>();

    public LiveData<List<String>> getTypeOfReport() {
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();
        List<String> typeOfReportList = TypeOfReport.getTypeOfReportList();
        liveData.setValue(typeOfReportList);
        return liveData;
    }

    public void addMonthlyReport() {
        repository.getMonthlyReport(new ResultsCallBack<List<Report>>() {
            @Override
            public void onSuccess(List<Report> result) {
                mutableReport.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                mutableReport.setValue(new Resource<>(null, error));
            }
        });
    }

    public LiveData<Resource<List<Report>>> getReport() {
        return mutableReport;
    }

    public LiveData<Resource<List<String>>> getYearsListLiveData() {
        return repository.getYearsList();
    }

    public LiveData<List<String>> getMonthsOfTheYearLiveData() {
        return CreateListsUtil.createMonthsList();
    }

    public void addReportByDate() {
        repository.getReportByDate(new ResultsCallBack<List<Report>>() {
            @Override
            public void onSuccess(List<Report> result) {
                mutableReport.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                mutableReport.setValue(new Resource<>(null, error));
            }
        });
    }

    public void clearReport() {
        mutableReport.setValue(new Resource<>(new ArrayList<>(), null));
    }

    public void addReportByPeriod(LocalDate startDate, LocalDate endDate) {
        repository.getReportByPeriod(startDate, endDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> result) {
                        mutableReport.setValue(new Resource<>(result, null));
                    }

                    @Override
                    public void onError(String error) {
                        mutableReport.setValue(new Resource<>(null, error));
                    }
                });
    }
}
