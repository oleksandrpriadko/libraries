package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;

public class IngredientName {

    @Expose
    private String strIngredient1;

    public String getStrIngredient1() {
        return strIngredient1;
    }

    public void setStrIngredient1(final String strIngredient1) {
        this.strIngredient1 = strIngredient1;
    }
}
