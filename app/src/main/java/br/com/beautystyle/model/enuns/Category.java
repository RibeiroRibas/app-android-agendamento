package br.com.beautystyle.model.enuns;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Category {

    OUTROS("Outros"),ALUGUEL("Aluguel"), ESMALTE("Esmalte"), LUZ("Luz"), AMOLACAO("Amolação"), AGUA("Água"), ALIMENTACAO("Alimentção");

    private final String description;

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

    public static List<String> getCategoriesList() {
        return Stream.of(values())
                .map(Category::getDescription)
                .collect(Collectors.toList());
    }
}
