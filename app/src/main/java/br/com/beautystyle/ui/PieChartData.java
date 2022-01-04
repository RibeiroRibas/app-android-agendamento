package br.com.beautystyle.ui;

public class PieChartData {
    private float value;
    private String description;

    public PieChartData(float value, String description) {
        this.value = value;
        this.description = description;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
