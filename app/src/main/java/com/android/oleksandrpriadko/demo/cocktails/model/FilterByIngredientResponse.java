
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class FilterByIngredientResponse {

    @Expose
    private List<Drink> drinks;

    public List<Drink> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }

}
