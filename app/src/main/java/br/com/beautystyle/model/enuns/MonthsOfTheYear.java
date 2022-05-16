package br.com.beautystyle.model.enuns;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MonthsOfTheYear {
    JANEIRO(1, "Janeiro"), FEVEREIRO(2, "Fevereiro"), MARCO(3, "Março"), ABRIL(4, "Abril"),
    MAIO(5, "Maio"), JUNHO(6, "Junho"), JULHO(7, "Julho"), AGOSTO(8, "Agosto"), SETEMBRO(9, "Setembro"),
    OUTUBRO(10, "Outubro"), NOVEMBRO(11, "Novembro"), DEZEMBRO(12, "Dezembro");

    private final int position;
    private final String description;

    MonthsOfTheYear(int position, String description) {
        this.position = position;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static List<String> getMonthList() {
        return Stream.of(values())
                .map(MonthsOfTheYear::getDescription)
                .collect(Collectors.toList());
    }
}
