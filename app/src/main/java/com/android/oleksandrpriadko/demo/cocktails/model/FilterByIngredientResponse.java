
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class FilterByIngredientResponse {

    @Expose
    private List<DrinkDetails> drinks;

    public List<DrinkDetails> getDrinkDetails() {
        return drinks;
    }

    public void setDrinkDetails(List<DrinkDetails> drinkDetails) {
        this.drinks = drinkDetails;
    }

}