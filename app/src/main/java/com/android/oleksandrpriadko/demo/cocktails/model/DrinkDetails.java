package com.android.oleksandrpriadko.demo.cocktails.model;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.MeasureUnit;
import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.MeasureUnitsConverter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class DrinkDetails {

    @Expose
    private String dateModified;
    @Expose
    private String idDrink;
    @Expose
    private String strAlcoholic;
    @Expose
    private String strCategory;
    @Expose
    private String strCreativeCommonsConfirmed;
    @Expose
    private String strDrink;
    @Expose
    private Object strDrinkAlternate;
    @Expose
    private Object strDrinkDE;
    @Expose
    private Object strDrinkES;
    @Expose
    private Object strDrinkFR;
    @Expose
    private String strDrinkThumb;
    @SerializedName("strDrinkZH-HANS")
    private Object strDrinkZHHANS;
    @SerializedName("strDrinkZH-HANT")
    private Object strDrinkZHHANT;
    @Expose
    private String strGlass;
    @Expose
    private String strIBA;
    @Expose
    private String strIngredient1;
    @Expose
    private String strIngredient10;
    @Expose
    private String strIngredient11;
    @Expose
    private String strIngredient12;
    @Expose
    private String strIngredient13;
    @Expose
    private String strIngredient14;
    @Expose
    private String strIngredient15;
    @Expose
    private String strIngredient2;
    @Expose
    private String strIngredient3;
    @Expose
    private String strIngredient4;
    @Expose
    private String strIngredient5;
    @Expose
    private String strIngredient6;
    @Expose
    private String strIngredient7;
    @Expose
    private String strIngredient8;
    @Expose
    private String strIngredient9;
    @Expose
    private String strInstructions;
    @Expose
    private Object strInstructionsDE;
    @Expose
    private Object strInstructionsES;
    @Expose
    private Object strInstructionsFR;
    @SerializedName("strInstructionsZH-HANS")
    private Object strInstructionsZHHANS;
    @SerializedName("strInstructionsZH-HANT")
    private Object strInstructionsZHHANT;
    @Expose
    private String strMeasure1;
    @Expose
    private String strMeasure10;
    @Expose
    private String strMeasure11;
    @Expose
    private String strMeasure12;
    @Expose
    private String strMeasure13;
    @Expose
    private String strMeasure14;
    @Expose
    private String strMeasure15;
    @Expose
    private String strMeasure2;
    @Expose
    private String strMeasure3;
    @Expose
    private String strMeasure4;
    @Expose
    private String strMeasure5;
    @Expose
    private String strMeasure6;
    @Expose
    private String strMeasure7;
    @Expose
    private String strMeasure8;
    @Expose
    private String strMeasure9;
    @Expose
    private String strTags;
    @Expose
    private Object strVideo;

    private List<String> ingredientsNameList = new ArrayList<>();
    private List<String> ingredientsMeasureList = new ArrayList<>();

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getIdDrink() {
        return idDrink;
    }

    public void setIdDrink(String idDrink) {
        this.idDrink = idDrink;
    }

    public String getStrAlcoholic() {
        return strAlcoholic;
    }

    public void setStrAlcoholic(String strAlcoholic) {
        this.strAlcoholic = strAlcoholic;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public String getStrCreativeCommonsConfirmed() {
        return strCreativeCommonsConfirmed;
    }

    public void setStrCreativeCommonsConfirmed(String strCreativeCommonsConfirmed) {
        this.strCreativeCommonsConfirmed = strCreativeCommonsConfirmed;
    }

    public String getStrDrink() {
        return strDrink;
    }

    public void setStrDrink(String strDrink) {
        this.strDrink = strDrink;
    }

    public Object getStrDrinkAlternate() {
        return strDrinkAlternate;
    }

    public void setStrDrinkAlternate(Object strDrinkAlternate) {
        this.strDrinkAlternate = strDrinkAlternate;
    }

    public Object getStrDrinkDE() {
        return strDrinkDE;
    }

    public void setStrDrinkDE(Object strDrinkDE) {
        this.strDrinkDE = strDrinkDE;
    }

    public Object getStrDrinkES() {
        return strDrinkES;
    }

    public void setStrDrinkES(Object strDrinkES) {
        this.strDrinkES = strDrinkES;
    }

    public Object getStrDrinkFR() {
        return strDrinkFR;
    }

    public void setStrDrinkFR(Object strDrinkFR) {
        this.strDrinkFR = strDrinkFR;
    }

    public String getStrDrinkThumb() {
        return strDrinkThumb;
    }

    public void setStrDrinkThumb(String strDrinkThumb) {
        this.strDrinkThumb = strDrinkThumb;
    }

    public Object getStrDrinkZHHANS() {
        return strDrinkZHHANS;
    }

    public void setStrDrinkZHHANS(Object strDrinkZHHANS) {
        this.strDrinkZHHANS = strDrinkZHHANS;
    }

    public Object getStrDrinkZHHANT() {
        return strDrinkZHHANT;
    }

    public void setStrDrinkZHHANT(Object strDrinkZHHANT) {
        this.strDrinkZHHANT = strDrinkZHHANT;
    }

    public String getStrGlass() {
        return strGlass;
    }

    public void setStrGlass(String strGlass) {
        this.strGlass = strGlass;
    }

    public String getStrIBA() {
        return strIBA;
    }

    public void setStrIBA(String strIBA) {
        this.strIBA = strIBA;
    }

    public String getStrIngredient1() {
        return strIngredient1;
    }

    public void setStrIngredient1(String strIngredient1) {
        this.strIngredient1 = strIngredient1;
    }

    public String getStrIngredient10() {
        return strIngredient10;
    }

    public void setStrIngredient10(String strIngredient10) {
        this.strIngredient10 = strIngredient10;
    }

    public String getStrIngredient11() {
        return strIngredient11;
    }

    public void setStrIngredient11(String strIngredient11) {
        this.strIngredient11 = strIngredient11;
    }

    public String getStrIngredient12() {
        return strIngredient12;
    }

    public void setStrIngredient12(String strIngredient12) {
        this.strIngredient12 = strIngredient12;
    }

    public String getStrIngredient13() {
        return strIngredient13;
    }

    public void setStrIngredient13(String strIngredient13) {
        this.strIngredient13 = strIngredient13;
    }

    public String getStrIngredient14() {
        return strIngredient14;
    }

    public void setStrIngredient14(String strIngredient14) {
        this.strIngredient14 = strIngredient14;
    }

    public String getStrIngredient15() {
        return strIngredient15;
    }

    public void setStrIngredient15(String strIngredient15) {
        this.strIngredient15 = strIngredient15;
    }

    public String getStrIngredient2() {
        return strIngredient2;
    }

    public void setStrIngredient2(String strIngredient2) {
        this.strIngredient2 = strIngredient2;
    }

    public String getStrIngredient3() {
        return strIngredient3;
    }

    public void setStrIngredient3(String strIngredient3) {
        this.strIngredient3 = strIngredient3;
    }

    public String getStrIngredient4() {
        return strIngredient4;
    }

    public void setStrIngredient4(String strIngredient4) {
        this.strIngredient4 = strIngredient4;
    }

    public String getStrIngredient5() {
        return strIngredient5;
    }

    public void setStrIngredient5(String strIngredient5) {
        this.strIngredient5 = strIngredient5;
    }

    public String getStrIngredient6() {
        return strIngredient6;
    }

    public void setStrIngredient6(String strIngredient6) {
        this.strIngredient6 = strIngredient6;
    }

    public String getStrIngredient7() {
        return strIngredient7;
    }

    public void setStrIngredient7(String strIngredient7) {
        this.strIngredient7 = strIngredient7;
    }

    public String getStrIngredient8() {
        return strIngredient8;
    }

    public void setStrIngredient8(String strIngredient8) {
        this.strIngredient8 = strIngredient8;
    }

    public String getStrIngredient9() {
        return strIngredient9;
    }

    public void setStrIngredient9(String strIngredient9) {
        this.strIngredient9 = strIngredient9;
    }

    public String getStrInstructions() {
        return strInstructions;
    }

    public void setStrInstructions(String strInstructions) {
        this.strInstructions = strInstructions;
    }

    public Object getStrInstructionsDE() {
        return strInstructionsDE;
    }

    public void setStrInstructionsDE(Object strInstructionsDE) {
        this.strInstructionsDE = strInstructionsDE;
    }

    public Object getStrInstructionsES() {
        return strInstructionsES;
    }

    public void setStrInstructionsES(Object strInstructionsES) {
        this.strInstructionsES = strInstructionsES;
    }

    public Object getStrInstructionsFR() {
        return strInstructionsFR;
    }

    public void setStrInstructionsFR(Object strInstructionsFR) {
        this.strInstructionsFR = strInstructionsFR;
    }

    public Object getStrInstructionsZHHANS() {
        return strInstructionsZHHANS;
    }

    public void setStrInstructionsZHHANS(Object strInstructionsZHHANS) {
        this.strInstructionsZHHANS = strInstructionsZHHANS;
    }

    public Object getStrInstructionsZHHANT() {
        return strInstructionsZHHANT;
    }

    public void setStrInstructionsZHHANT(Object strInstructionsZHHANT) {
        this.strInstructionsZHHANT = strInstructionsZHHANT;
    }

    public String getStrMeasure1() {
        return strMeasure1;
    }

    public void setStrMeasure1(String strMeasure1) {
        this.strMeasure1 = strMeasure1;
    }

    public String getStrMeasure10() {
        return strMeasure10;
    }

    public void setStrMeasure10(String strMeasure10) {
        this.strMeasure10 = strMeasure10;
    }

    public String getStrMeasure11() {
        return strMeasure11;
    }

    public void setStrMeasure11(String strMeasure11) {
        this.strMeasure11 = strMeasure11;
    }

    public String getStrMeasure12() {
        return strMeasure12;
    }

    public void setStrMeasure12(String strMeasure12) {
        this.strMeasure12 = strMeasure12;
    }

    public String getStrMeasure13() {
        return strMeasure13;
    }

    public void setStrMeasure13(String strMeasure13) {
        this.strMeasure13 = strMeasure13;
    }

    public String getStrMeasure14() {
        return strMeasure14;
    }

    public void setStrMeasure14(String strMeasure14) {
        this.strMeasure14 = strMeasure14;
    }

    public String getStrMeasure15() {
        return strMeasure15;
    }

    public void setStrMeasure15(String strMeasure15) {
        this.strMeasure15 = strMeasure15;
    }

    public String getStrMeasure2() {
        return strMeasure2;
    }

    public void setStrMeasure2(String strMeasure2) {
        this.strMeasure2 = strMeasure2;
    }

    public String getStrMeasure3() {
        return strMeasure3;
    }

    public void setStrMeasure3(String strMeasure3) {
        this.strMeasure3 = strMeasure3;
    }

    public String getStrMeasure4() {
        return strMeasure4;
    }

    public void setStrMeasure4(String strMeasure4) {
        this.strMeasure4 = strMeasure4;
    }

    public String getStrMeasure5() {
        return strMeasure5;
    }

    public void setStrMeasure5(String strMeasure5) {
        this.strMeasure5 = strMeasure5;
    }

    public String getStrMeasure6() {
        return strMeasure6;
    }

    public void setStrMeasure6(String strMeasure6) {
        this.strMeasure6 = strMeasure6;
    }

    public String getStrMeasure7() {
        return strMeasure7;
    }

    public void setStrMeasure7(String strMeasure7) {
        this.strMeasure7 = strMeasure7;
    }

    public String getStrMeasure8() {
        return strMeasure8;
    }

    public void setStrMeasure8(String strMeasure8) {
        this.strMeasure8 = strMeasure8;
    }

    public String getStrMeasure9() {
        return strMeasure9;
    }

    public void setStrMeasure9(String strMeasure9) {
        this.strMeasure9 = strMeasure9;
    }

    public String getStrTags() {
        return strTags;
    }

    public void setStrTags(String strTags) {
        this.strTags = strTags;
    }

    public Object getStrVideo() {
        return strVideo;
    }

    public void setStrVideo(Object strVideo) {
        this.strVideo = strVideo;
    }

    public List<SpannableStringBuilder> getListOfIngredientsNamesAndMeasureUnits(@NonNull final MeasureUnit measureUnit) {
        List<SpannableStringBuilder> ingredients = new ArrayList<>();


        if (canIngredientBeAdded(strIngredient1)) {
            ingredients.add(createSpannable(strIngredient1, MeasureUnitsConverter.Companion.convert(strMeasure1, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient2)) {
            ingredients.add(createSpannable(strIngredient2, MeasureUnitsConverter.Companion.convert(strMeasure2, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient3)) {
            ingredients.add(createSpannable(strIngredient3, MeasureUnitsConverter.Companion.convert(strMeasure3, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient4)) {
            ingredients.add(createSpannable(strIngredient4, MeasureUnitsConverter.Companion.convert(strMeasure4, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient5)) {
            ingredients.add(createSpannable(strIngredient5, MeasureUnitsConverter.Companion.convert(strMeasure5, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient6)) {
            ingredients.add(createSpannable(strIngredient6, MeasureUnitsConverter.Companion.convert(strMeasure6, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient7)) {
            ingredients.add(createSpannable(strIngredient7, MeasureUnitsConverter.Companion.convert(strMeasure7, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient8)) {
            ingredients.add(createSpannable(strIngredient8, MeasureUnitsConverter.Companion.convert(strMeasure8, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient9)) {
            ingredients.add(createSpannable(strIngredient9, MeasureUnitsConverter.Companion.convert(strMeasure9, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient10)) {
            ingredients.add(createSpannable(strIngredient10, MeasureUnitsConverter.Companion.convert(strMeasure10, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient11)) {
            ingredients.add(createSpannable(strIngredient11, MeasureUnitsConverter.Companion.convert(strMeasure11, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient12)) {
            ingredients.add(createSpannable(strIngredient12, MeasureUnitsConverter.Companion.convert(strMeasure12, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient13)) {
            ingredients.add(createSpannable(strIngredient13, MeasureUnitsConverter.Companion.convert(strMeasure13, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient14)) {
            ingredients.add(createSpannable(strIngredient14, MeasureUnitsConverter.Companion.convert(strMeasure14, measureUnit)));
        }
        if (canIngredientBeAdded(strIngredient15)) {
            ingredients.add(createSpannable(strIngredient15, MeasureUnitsConverter.Companion.convert(strMeasure15, measureUnit)));
        }
        return ingredients;
    }

    private SpannableStringBuilder createSpannable(@NonNull final String name,
                                                   @NonNull final String measure) {
        String nameTrimmed = name.trim();
        String measureTrimmed = measure.trim();

        String patternFormat = measureTrimmed.isEmpty() ? "%s" : "%s (%s)";
        String whole = String.format(patternFormat, nameTrimmed, measureTrimmed);
        int indexOfMeasure = measureTrimmed.isEmpty() ? nameTrimmed.length() : whole.indexOf("(");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(whole);
        spannableStringBuilder.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                indexOfMeasure,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }


    public List<String> getListOfIngredientsNames() {
        if (ingredientsNameList != null && !ingredientsNameList.isEmpty()) {
            return ingredientsNameList;
        } else {
            ingredientsNameList = new ArrayList<>();

            if (canIngredientBeAdded(strIngredient1)) {
                ingredientsNameList.add(strIngredient1);
            }
            if (canIngredientBeAdded(strIngredient2)) {
                ingredientsNameList.add(strIngredient2);
            }
            if (canIngredientBeAdded(strIngredient3)) {
                ingredientsNameList.add(strIngredient3);
            }
            if (canIngredientBeAdded(strIngredient4)) {
                ingredientsNameList.add(strIngredient4);
            }
            if (canIngredientBeAdded(strIngredient5)) {
                ingredientsNameList.add(strIngredient5);
            }
            if (canIngredientBeAdded(strIngredient6)) {
                ingredientsNameList.add(strIngredient6);
            }
            if (canIngredientBeAdded(strIngredient7)) {
                ingredientsNameList.add(strIngredient7);
            }
            if (canIngredientBeAdded(strIngredient8)) {
                ingredientsNameList.add(strIngredient8);
            }
            if (canIngredientBeAdded(strIngredient9)) {
                ingredientsNameList.add(strIngredient9);
            }
            if (canIngredientBeAdded(strIngredient10)) {
                ingredientsNameList.add(strIngredient10);
            }
            if (canIngredientBeAdded(strIngredient11)) {
                ingredientsNameList.add(strIngredient11);
            }
            if (canIngredientBeAdded(strIngredient12)) {
                ingredientsNameList.add(strIngredient12);
            }
            if (canIngredientBeAdded(strIngredient13)) {
                ingredientsNameList.add(strIngredient13);
            }
            if (canIngredientBeAdded(strIngredient14)) {
                ingredientsNameList.add(strIngredient14);
            }
            if (canIngredientBeAdded(strIngredient15)) {
                ingredientsNameList.add(strIngredient15);
            }
            return ingredientsNameList;
        }
    }

    public List<String> getListOfIngredientsMeasures() {
        if (ingredientsMeasureList != null && !ingredientsMeasureList.isEmpty()) {
            return ingredientsMeasureList;
        } else {
            ingredientsMeasureList = new ArrayList<>();

            if (canIngredientBeAdded(strIngredient1)) {
                ingredientsMeasureList.add(strMeasure1);
            }
            if (canIngredientBeAdded(strIngredient2)) {
                ingredientsMeasureList.add(strMeasure2);
            }
            if (canIngredientBeAdded(strIngredient3)) {
                ingredientsMeasureList.add(strMeasure3);
            }
            if (canIngredientBeAdded(strIngredient4)) {
                ingredientsMeasureList.add(strMeasure4);
            }
            if (canIngredientBeAdded(strIngredient5)) {
                ingredientsMeasureList.add(strMeasure5);
            }
            if (canIngredientBeAdded(strIngredient6)) {
                ingredientsMeasureList.add(strMeasure6);
            }
            if (canIngredientBeAdded(strIngredient7)) {
                ingredientsMeasureList.add(strMeasure7);
            }
            if (canIngredientBeAdded(strIngredient8)) {
                ingredientsMeasureList.add(strMeasure8);
            }
            if (canIngredientBeAdded(strIngredient9)) {
                ingredientsMeasureList.add(strMeasure9);
            }
            if (canIngredientBeAdded(strIngredient10)) {
                ingredientsMeasureList.add(strMeasure10);
            }
            if (canIngredientBeAdded(strIngredient11)) {
                ingredientsMeasureList.add(strMeasure11);
            }
            if (canIngredientBeAdded(strIngredient12)) {
                ingredientsMeasureList.add(strMeasure12);
            }
            if (canIngredientBeAdded(strIngredient13)) {
                ingredientsMeasureList.add(strMeasure13);
            }
            if (canIngredientBeAdded(strIngredient14)) {
                ingredientsMeasureList.add(strMeasure14);
            }
            if (canIngredientBeAdded(strIngredient15)) {
                ingredientsMeasureList.add(strMeasure15);
            }
            return ingredientsMeasureList;
        }
    }

    private boolean canIngredientBeAdded(final String ingredient) {
        return ingredient != null && !ingredient.isEmpty();
    }


}
