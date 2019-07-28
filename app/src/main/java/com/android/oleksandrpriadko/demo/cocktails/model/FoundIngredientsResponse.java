
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FoundIngredientsResponse {

    @Expose
    @SerializedName("ingredients")
    private List<IngredientDetails> ingredientList;

    @Nullable
    public List<IngredientDetails> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(final List<IngredientDetails> ingredientList) {
        this.ingredientList = ingredientList;
    }
}
