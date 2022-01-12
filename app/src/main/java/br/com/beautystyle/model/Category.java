package br.com.beautystyle.model;

public enum Category {

    OUTROS("Outros"),FIXO("Gasto Fixo"), ESMALTE("Esmalte"), LUZ("Luz"), AMOLACAO("Amolação");

    private String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Category getCategoryByDescription(String description){
        for (Category category : Category.values()) {
            if(category.description.equals(description))
                return category;
        }
        return null;
    }
}
