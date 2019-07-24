package com.android.oleksandrpriadko.demo.cocktails.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredients")
public class IngredientName {

    @PrimaryKey
    @ColumnInfo(name = "name")
    @Expose
    @NonNull
    @SerializedName("strIngredient1")
    public String strIngredient1 = "";

    public String getStrIngredient1() {
        return strIngredient1;
    }

    public void setStrIngredient1(final String strIngredient1) {
        this.strIngredient1 = strIngredient1;
    }
}
