package net.karthikraj.excercise.bakingapp.recipiedetail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.karthikraj.excercise.bakingapp.R;
import net.karthikraj.excercise.bakingapp.model.Ingredient;
import net.karthikraj.excercise.bakingapp.model.RecipeModel;
import net.karthikraj.excercise.bakingapp.model.Step;
import net.karthikraj.excercise.bakingapp.recipieslist.MainActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.karthikraj.excercise.bakingapp.recipiedetail.RecipieDetailsActivity.RECIPE_TAG;

/**
 * Created by karthik on 29/10/17.
 */

public class RecipeDetailFragment extends Fragment implements RecipeStepsAdapter.ListItemClickListener{

    @BindView(R.id.ingredients_list)
    TextView ingredients_list;
    @BindView(R.id.recyclerView)
    RecyclerView stepsList;
    public static final String RECIPE_PARCELABLE = "recipe_parcelable";
    private RecipeModel recipeModel;
    private RecipeStepsAdapter recipeStepsAdapter;

    StepClickListener stepClickListener;

    public interface StepClickListener{
        void onStepClicked(Step step);
    }

    //Mandatory empty constructor
    public RecipeDetailFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        ButterKnife.bind(this,rootView);
        ingredients_list.setText(buildIngredientsCard(recipeModel.getIngredients()));
        stepsList.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeStepsAdapter = new RecipeStepsAdapter(getContext(), recipeModel.getSteps(),this);
        stepsList.setAdapter(recipeStepsAdapter);
        stepsList.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        stepsList.setNestedScrollingEnabled(false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            stepClickListener = (StepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement StepClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stepClickListener = null;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            int recipe_id = getActivity().getIntent().getIntExtra(RECIPE_TAG,-1);
            recipeModel = MainActivity.getRecipe(recipe_id);
    }

    private String buildIngredientsCard(List<Ingredient> list){

        StringBuilder stringBuilder = new StringBuilder();

        for(Ingredient ingredient : list){
            stringBuilder.append("\u2022 ")
                    .append(ingredient.getQuantity())
                    .append(" ")
                    .append(ingredient.getMeasure())
                    .append(" of ")
                    .append(ingredient.getIngredient())
                    .append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void onListItemClick(Step step) {
        stepClickListener.onStepClicked(step);
    }

}
