package br.com.beautystyle.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ReportViewModel extends ViewModel {

    private MutableLiveData<List<Object>> mReportList;

    public MutableLiveData<List<Object>> getReportList() {
        if (mReportList == null) {
            mReportList = new MutableLiveData<>();
        }
        return mReportList;
    }

    public void add(List<Object> reportList) {
        mReportList.setValue(reportList);
    }

}
