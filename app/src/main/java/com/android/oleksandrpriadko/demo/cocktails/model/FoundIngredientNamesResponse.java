
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FoundIngredientNamesResponse {

    @Expose
    @SerializedName("drinks")
    private List<IngredientName> ingredientList;

    @Nullable
    public List<IngredientName> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(final List<IngredientName> ingredientList) {
        this.ingredientList = ingredientList;
    }
}
