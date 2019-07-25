
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FoundDrinksResponse {

    @Expose
    private List<DrinkDetails> drinks;

    @Nullable
    public List<DrinkDetails> getDrinkDetails() {
        return drinks;
    }

    public void setDrinkDetails(List<DrinkDetails> drinks) {
        this.drinks = drinks;
    }

}
