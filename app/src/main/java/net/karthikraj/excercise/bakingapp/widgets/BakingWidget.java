package net.karthikraj.excercise.bakingapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import net.karthikraj.excercise.bakingapp.R;
import net.karthikraj.excercise.bakingapp.model.Ingredient;
import net.karthikraj.excercise.bakingapp.model.RecipeModel;
import net.karthikraj.excercise.bakingapp.recipieslist.MainActivity;

import java.util.List;
import java.util.Random;

/**
 * Created by karthik on 29/10/17.
 */

public class BakingWidget extends AppWidgetProvider {

    private static final String WIDGET_RECIPE = "widget_recipe";
    private static final String WIDGET_ACTION = "ACTION_GET_RECIPE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Random r = new Random();
        int recipe_id = r.nextInt(3);

        Intent it = new Intent(context, MainActivity.class);
        it.setAction(WIDGET_ACTION);
        it.putExtra(WIDGET_RECIPE,recipe_id+1);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,it,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setOnClickPendingIntent(R.id.widget_layout,pendingIntent);

        RecipeModel recipe = MainActivity.getRecipe(recipe_id+1);

        if(recipe != null){
            views.setTextViewText(R.id.recipe_name,recipe.getName());
            views.setTextViewText(R.id.recipe_ingredients,buildIngredientsCard(recipe.getIngredients()));
        }else{
            views.setTextViewText(R.id.recipe_name,"Loading data...");
            views.setTextViewText(R.id.recipe_ingredients,null);

        }


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static String buildIngredientsCard(List<Ingredient> list){

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
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

