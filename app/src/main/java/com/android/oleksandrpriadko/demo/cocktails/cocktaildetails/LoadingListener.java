package com.android.oleksandrpriadko.demo.cocktails.cocktaildetails;

import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails;
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension;

import androidx.annotation.NonNull;

public interface LoadingListener extends RetrofitRepoExtension.Listener {

    default void onDrinkDetails(@NonNull final DrinkDetails drinkDetails) {
    }

}
