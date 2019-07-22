
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ListOfIngredientsResponse {

    @Expose
    private List<IngredientName> ingredientNameList;

    public List<IngredientName> getIngredientNameList() {
        return ingredientNameList;
    }

    public void setIngredientNameList(final List<IngredientName> ingredientNameList) {
        this.ingredientNameList = ingredientNameList;
    }
}
