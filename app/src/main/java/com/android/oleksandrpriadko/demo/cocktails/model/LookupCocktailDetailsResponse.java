
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class LookupCocktailDetailsResponse {

    @Expose
    private List<DrinkDetails> drinks = new ArrayList<>();

    public List<DrinkDetails> getDrinkDetails() {
        return drinks;
    }

    public void setDrinkDetails(List<DrinkDetails> drinkDetails) {
        this.drinks = drinkDetails;
    }

}
