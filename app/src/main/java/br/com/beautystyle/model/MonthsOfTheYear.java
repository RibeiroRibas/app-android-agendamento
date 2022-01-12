package br.com.beautystyle.model;

public enum MonthsOfTheYear {
    JANEIRO(1,"Janeiro"),FEVEREIRO(2,"Fevereiro"),MARCO(3,"Março"),ABRIL(4,"Abril"),
    MAIO(5,"Maio"),JUNHO(6,"Junho"),JULHO(7,"Julho"), AGOSTO(8,"Agosto"),SETEMBRO(9,"Setembro"),
    OUTUBRO(10,"Outubro"),NOVEMBRO(11,"Novembro"),DEZEMBRO(12,"Dezembro");

    private final int position;
    private final String description;

    MonthsOfTheYear(int position, String description) {
        this.position = position;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getPosition() {
        return position;
    }
}
