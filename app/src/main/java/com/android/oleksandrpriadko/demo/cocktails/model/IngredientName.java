package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IngredientName {

    @Expose
    @NonNull
    @SerializedName("strIngredient1")
    public String strIngredient1 = "";

    @Expose
    private String idIngredient = "";

    @Expose
    private String strType = "";

    @Expose
    private String strDescription = "";

    public void setIdIngredient(final String idIngredient) {
        this.idIngredient = idIngredient;
    }

    public void setStrType(final String strType) {
        this.strType = strType;
    }

    public void setStrDescription(final String strDescription) {
        this.strDescription = strDescription;
    }

    @Nullable
    public String getIdIngredient() {
        return idIngredient;
    }

    @Nullable
    public String getStrType() {
        return strType;
    }

    @Nullable
    public String getStrDescription() {
        return strDescription;
    }
}
