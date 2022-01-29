package br.com.beautystyle.model;

public enum MonthsOfTheYear {
    JANEIRO(1,"Jan"),FEVEREIRO(2,"Fev"),MARCO(3,"Mar"),ABRIL(4,"Abr"),
    MAIO(5,"Mai"),JUNHO(6,"Jun"),JULHO(7,"Jul"), AGOSTO(8,"Ago"),SETEMBRO(9,"Set"),
    OUTUBRO(10,"Out"),NOVEMBRO(11,"Nov"),DEZEMBRO(12,"Dez");

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
