package com.android.oleksandrpriadko.demo.cocktails.drinkdetails;

import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails;
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientDetails;
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName;
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension;

import androidx.annotation.NonNull;

public interface LoadingListener extends RetrofitRepoExtension.Listener {

    default void onDrinkDetailsLoaded(@NonNull final DrinkDetails drinkDetails) {
    }

    default void onIngredientDetailsLoaded(@NonNull final IngredientDetails ingredient) {}

}
