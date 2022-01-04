package br.com.beautystyle.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.beautystyle.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.ui.PieChartData;
import br.com.beautystyle.ui.ReportInterface;

public class ReportFragment extends Fragment implements ReportInterface {

    private PieChart pieChart;

    private String[] typeOfReport = {"Mensal", "Diário", "Por período", "Anual"};
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapteritens;

    public ReportFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_report, container, false);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_report_container, new MonthReportFragment(this))
                .commit();

        pieChart = inflateView.findViewById(R.id.activity_relatorios_piechart);
        adapteritens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, typeOfReport);
        autoCompleteTextView = inflateView.findViewById(R.id.auto_complete_tv);
        autoCompleteTextView.setAdapter(adapteritens);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {//mensal
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_report_container, new MonthReportFragment(this))
                        .commit();
            } else if (position == 1) {//diário
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_report_container, new DailyReportFragment())
                        .commit();
            } else if (position == 2) {//semanal
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_report_container, new WeekReportFragment())
                        .commit();
            } else {//anual
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_report_container, new YearReportFragment())
                        .commit();
            }
        });
        setupPieChart();
        loadPieChartData();
        return inflateView;
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
//        pieChart.setCenterText("Spending by Category");
//        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(true);
        l.setEnabled(false);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        BigDecimal valor = new BigDecimal("0.2");
        entries.add(new PieEntry(valor.floatValue(), "Food & Dining"));
        entries.add(new PieEntry(0.15f, "Medical"));
        entries.add(new PieEntry(0.10f, "Entertainment"));
        entries.add(new PieEntry(0.25f, "Electricity and Gas"));
        entries.add(new PieEntry(0.3f, "Housing"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    @Override
    public void publishReportData(BigDecimal gain, BigDecimal spending) {

    }

    @Override
    public void loadPieChartData(List<PieChartData> chartData) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (PieChartData d : chartData) {
            entries.add(new PieEntry(d.getValue(), d.getDescription()));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }
}