package br.com.beautystyle.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.ui.PieChartData;
import br.com.beautystyle.ui.ReportInterface;

public class MonthReportFragment extends Fragment {

    private String[] monthOfTheYears = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItens;
    private ReportInterface onReportInterface;
    private EventDao dao = new EventDao();

    public MonthReportFragment(ReportInterface onReportInterface) {
        this.onReportInterface = onReportInterface;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_month, container, false);
        adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1,monthOfTheYears);
        autoCompleteTextView = inflatedView.findViewById(R.id.fragment_report_month_auto_complete);
        autoCompleteTextView.setAdapter(adapterItens);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) ->{
            String s = parent.getItemAtPosition(position).toString();
            List<Event> eventByMonth = dao.findEventByMonth(position+1);
            BigDecimal sumValueOfEvents = eventByMonth.stream()
                    .map(Event::getValueEvent)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            List<PieChartData> listData = new ArrayList<>();
            for (Event ev : eventByMonth) {
                BigDecimal percent = (ev.getValueEvent().multiply(new BigDecimal(100))).divide(sumValueOfEvents,2, RoundingMode.HALF_UP);
                 listData.add(new PieChartData(percent.floatValue(),ev.getClient().getName()));
            }
            onReportInterface.loadPieChartData(listData);
        });
        return inflatedView;
    }
}