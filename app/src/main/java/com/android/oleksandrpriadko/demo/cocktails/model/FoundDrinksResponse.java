
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FoundDrinksResponse {

    @Expose
    @SerializedName("drinks")
    private List<DrinkDetails> drinkDetails;

    @Nullable
    public List<DrinkDetails> getDrinkDetails() {
        return drinkDetails;
    }

    public void setDrinkDetails(List<DrinkDetails> drinkDetails) {
        this.drinkDetails = drinkDetails;
    }

}
