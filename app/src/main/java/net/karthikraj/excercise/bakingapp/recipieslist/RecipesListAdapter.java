package net.karthikraj.excercise.bakingapp.recipieslist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.karthikraj.excercise.bakingapp.R;
import net.karthikraj.excercise.bakingapp.model.RecipeModel;
import net.karthikraj.excercise.bakingapp.recipiedetail.RecipieDetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 29/10/17.
 */

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.ViewHolder> {

    private List<RecipeModel> recipeModelList;
    private Context mContext;

    public RecipesListAdapter(Context pContext, List<RecipeModel> recipes){
        setHasStableIds(true);
        this.recipeModelList = recipes;
        this.mContext = pContext;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipe_list,null);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindRecipeData(recipeModelList.get(position));

    }

    @Override
    public int getItemCount() {
        return recipeModelList.size();
    }

    public void updateData(List<RecipeModel> recipes){
        recipeModelList = recipes;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private static final String RECIPE_TAG = "recipe_id";

        @BindView(R.id.tv_recipie_name)
        TextView recipeNameText;
        @BindView(R.id.tv_recipie_servings) TextView recipeServingsText;
        @BindView(R.id.recipe_image)
        ImageView imageViewRecipe;
        @BindView(R.id.cardView)
        CardView recipeView;
        private RecipeModel recipe;

        private ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            recipeView.setOnClickListener(this);
        }

        private void bindRecipeData(RecipeModel recipe){
            this.recipe = recipe;
            recipeNameText.setText(recipe.getName());
            String nServings = recipe.getServings() + " " + mContext.getString(R.string.recipie_servings_string);
            recipeServingsText.setText(nServings);

            if(recipe.getImage().length() > 0) {
                imageViewRecipe.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(recipe.getImage()).into(imageViewRecipe);
            }
        }

        @Override
        public void onClick(View v) {
            Intent recipeDetialsActivityIntent = new Intent(v.getContext(), RecipieDetailsActivity.class);
            recipeDetialsActivityIntent.putExtra(RECIPE_TAG,recipe.getId());
            recipeDetialsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(recipeDetialsActivityIntent);
        }
    }


}
