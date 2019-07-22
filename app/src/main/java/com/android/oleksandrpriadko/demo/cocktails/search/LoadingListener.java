package com.android.oleksandrpriadko.demo.cocktails.search;

import com.android.oleksandrpriadko.demo.cocktails.model.Drink;
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName;
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension;

import java.util.List;

import androidx.annotation.NonNull;

public interface LoadingListener extends RetrofitRepoExtension.Listener {

    default void onFilterByIngredient(@NonNull final List<Drink> foundDrinks) {
    }

    default void onListOfIngredients(@NonNull final List<IngredientName> ingredientNames) {
    }

}
