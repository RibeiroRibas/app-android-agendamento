package br.com.beautystyle.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.domain.model.Report;

public class ReportViewModel extends ViewModel {

    private MutableLiveData<List<Report>> mReportList;

    public MutableLiveData<List<Report>> getReportList() {
        if (mReportList == null) {
            mReportList = new MutableLiveData<>();
        }
        return mReportList;
    }

    public void add(List<Report> reportList) {
        mReportList.setValue(reportList);
    }

}
