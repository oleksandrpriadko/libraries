package com.android.oleksandrpriadko.demo.cocktails.search;

import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink;
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient;
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension;

import java.util.List;

import androidx.annotation.NonNull;

public interface SearchRepoListener extends RetrofitRepoExtension.Listener {

    default void onDrinksFound(@NonNull final List<Drink> drinkList) {
    }

    default void onIngredientsLoaded(@NonNull final List<Ingredient> ingredientList) {
    }

    default void noIngredientsFound() {

    }

    default void noDrinksFound() {
    }

}
