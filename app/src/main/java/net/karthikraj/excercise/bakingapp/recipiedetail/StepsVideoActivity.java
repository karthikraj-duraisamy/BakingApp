package net.karthikraj.excercise.bakingapp.recipiedetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.karthikraj.excercise.bakingapp.R;
import net.karthikraj.excercise.bakingapp.model.RecipeModel;
import net.karthikraj.excercise.bakingapp.model.Step;
import net.karthikraj.excercise.bakingapp.recipieslist.MainActivity;

import static net.karthikraj.excercise.bakingapp.recipiedetail.RecipieDetailsActivity.RECIPE_TAG;

/**
 * Created by karthik on 29/10/17.
 */

public class StepsVideoActivity extends AppCompatActivity implements StepsVideoFragment.StepChangeListener {

    public static final String STEP_EXTRA = "step_extra";
    public static final String STEP_NUMS_EXTRA = "step_nums_extra";
    private FragmentManager fragmentManager;

    private RecipeModel mRecipe;
    private int num_steps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_activity);

        Step stepExtra = (Step) getIntent().getExtras().get(STEP_EXTRA);
        int numOfSteps = getIntent().getIntExtra(STEP_NUMS_EXTRA, 0);

        int recipe_id = getIntent().getIntExtra(RECIPE_TAG,-1);
        mRecipe = MainActivity.getRecipe(recipe_id);
        num_steps = mRecipe.getSteps().size();

        //set ActionBar and FragmentManager
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mRecipe.getName());

        fragmentManager = getSupportFragmentManager();
        StepsVideoFragment stepFrag = StepsVideoFragment.newInstance(stepExtra, numOfSteps);
        fragmentManager.beginTransaction()
                .add(R.id.video_fragment_container,stepFrag)
                .commit();
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
