package net.karthikraj.excercise.bakingapp.recipiedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.karthikraj.excercise.bakingapp.R;
import net.karthikraj.excercise.bakingapp.model.RecipeModel;
import net.karthikraj.excercise.bakingapp.model.Step;
import net.karthikraj.excercise.bakingapp.recipieslist.MainActivity;

import static net.karthikraj.excercise.bakingapp.recipiedetail.StepsVideoActivity.STEP_EXTRA;
import static net.karthikraj.excercise.bakingapp.recipiedetail.StepsVideoActivity.STEP_NUMS_EXTRA;

public class RecipieDetailsActivity extends AppCompatActivity implements RecipeDetailFragment.StepClickListener, StepsVideoFragment.StepChangeListener{


    public static final String RECIPE_TAG = "recipe_id";
    private RecipeModel mRecipe;
    private int num_steps;
    private FragmentManager fragmentManager;
    private int recipe_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipie_details);

        recipe_id = getIntent().getIntExtra(RECIPE_TAG,-1);
        mRecipe = MainActivity.getRecipe(recipe_id);
        num_steps = mRecipe.getSteps().size();

        //set ActionBar and FragmentManager
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mRecipe.getName());

        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onStepClicked(Step step) {
        if(getResources().getBoolean(R.bool.isTablet)) {
            //Replace the steps video fragment

            StepsVideoFragment stepFrag = StepsVideoFragment.newInstance(step,num_steps);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_fragment_container,stepFrag)
                    .addToBackStack(null)
                    .commit();
        }else {
            //Create new activity with video and details
            Intent videoFragmentActivity = new Intent(RecipieDetailsActivity.this, StepsVideoActivity.class);
            videoFragmentActivity.putExtra(STEP_EXTRA, step);
            videoFragmentActivity.putExtra(RECIPE_TAG, recipe_id);
            videoFragmentActivity.putExtra(STEP_NUMS_EXTRA, num_steps);
            startActivity(videoFragmentActivity);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onNextClick(int id) {
        Step nextStep = mRecipe.getSteps().get(id+1);
        if(!getResources().getBoolean(R.bool.isTablet)) {
            StepsVideoFragment stepFrag = StepsVideoFragment.newInstance(nextStep,num_steps);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_fragment_container,stepFrag)
                    .addToBackStack(null)
                    .commit();
        }else{
            StepsVideoFragment stepFrag = StepsVideoFragment.newInstance(nextStep,num_steps);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_fragment_container,stepFrag)
                    .commit();
        }
    }

    @Override
    public void onPrevClick(int id) {
        Step previousStep = mRecipe.getSteps().get(id - 1);
        if(!getResources().getBoolean(R.bool.isTablet)) {
            StepsVideoFragment stepFrag = StepsVideoFragment.newInstance(previousStep, num_steps);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_fragment_container, stepFrag)
                    .addToBackStack(null)
                    .commit();
        }else{
            StepsVideoFragment stepFrag = StepsVideoFragment.newInstance(previousStep, num_steps);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_fragment_container, stepFrag)
                    .commit();

        }
    }
}
