package com.android.oleksandrpriadko.demo.cocktails.search;

import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails;
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName;
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension;

import java.util.List;

import androidx.annotation.NonNull;

public interface LoadingListener extends RetrofitRepoExtension.Listener {

    default void onDrinksFound(@NonNull final List<DrinkDetails> foundDrinkDetails) {
    }

    default void onFilterByIngredientDone(@NonNull final List<DrinkDetails> foundDrinkDetails) {
    }

    default void onListOfIngredientsLoaded(@NonNull final List<IngredientName> ingredientNames) {
    }

    default void noDrinksFound() {
    }

}
