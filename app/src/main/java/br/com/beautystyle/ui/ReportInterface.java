package br.com.beautystyle.ui;

import java.math.BigDecimal;
import java.util.List;

import br.com.beautystyle.ui.PieChartData;

public interface ReportInterface {

    void publishReportData(BigDecimal gain, BigDecimal spending);
    void loadPieChartData(List<PieChartData> data);

}
