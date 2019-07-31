package com.android.oleksandrpriadko.demo.cocktails.drinkdetails;

import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink;
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient;
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension;

import androidx.annotation.NonNull;

public interface DrinkDetailsRepoListener extends RetrofitRepoExtension.Listener {

    default void onDrinkLoaded(@NonNull final Drink drink) {
    }

    default void onIngredientLoaded(@NonNull final Ingredient ingredient) {
    }

}
