
package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;

public class Drink {

    @Expose
    private String idDrink;
    @Expose
    private String strDrink;
    @Expose
    private String strDrinkThumb;

    public String getIdDrink() {
        return idDrink;
    }

    public void setIdDrink(String idDrink) {
        this.idDrink = idDrink;
    }

    public String getStrDrink() {
        return strDrink;
    }

    public void setStrDrink(String strDrink) {
        this.strDrink = strDrink;
    }

    public String getStrDrinkThumb() {
        return strDrinkThumb;
    }

    public void setStrDrinkThumb(String strDrinkThumb) {
        this.strDrinkThumb = strDrinkThumb;
    }

}
