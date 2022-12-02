package br.com.beautystyle.model.entity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TypeOfReport {

    MONTHLY("Mensal"), DAILY("Diário"), BYPERIOD("Por período");

    private final String description;

    TypeOfReport(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static List<String> getTypeOfReportList() {
        return Stream.of(values())
                .map(TypeOfReport::getDescription)
                .collect(Collectors.toList());
    }
}
