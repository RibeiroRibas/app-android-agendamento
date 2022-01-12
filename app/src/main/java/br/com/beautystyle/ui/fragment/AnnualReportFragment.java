package br.com.beautystyle.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CreateListsUtil;

public class AnnualReportFragment extends Fragment {
    private EventDao dao = new EventDao();
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItens;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_year, container, false);
        List<String> listYears = CreateListsUtil.CreateListYears(dao.listAll());
        adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1,listYears);
        autoCompleteTextView = inflatedView.findViewById(R.id.fragment_report_month_auto_complete);
        String year = CalendarUtil.formatYear(LocalDate.now());
        autoCompleteTextView.setText(year, false);
        autoCompleteTextView.setAdapter(adapterItens);
//        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) ->{
//            String s = parent.getItemAtPosition(position).toString();
//            List<Event> eventByMonth = dao.findEventByMonth(position+1);
//            BigDecimal sumValueOfEvents = eventByMonth.stream()
//                    .map(Event::getValueEvent)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//            List<PieChartData> listData = new ArrayList<>();
//            for (Event ev : eventByMonth) {
//                BigDecimal percent = (ev.getValueEvent().multiply(new BigDecimal(100))).divide(sumValueOfEvents,2, RoundingMode.HALF_UP);
//                listData.add(new PieChartData(percent.floatValue(),ev.getClient().getName()));
//            }
//            onReportInterface.loadPieChartData(listData);
//        });
        return inflatedView;


    }
}