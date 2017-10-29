package net.karthikraj.excercise.bakingapp.networking;

import net.karthikraj.excercise.bakingapp.model.RecipeModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by karthik on 29/10/17.
 */

public interface RetrofitApiInterface {
    @GET("baking.json")
    Call<List<RecipeModel>> getRecipes();

}
