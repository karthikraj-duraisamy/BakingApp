package net.karthikraj.excercise.bakingapp.recipieslist;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.karthikraj.excercise.bakingapp.R;
import net.karthikraj.excercise.bakingapp.model.RecipeModel;
import net.karthikraj.excercise.bakingapp.networking.RetrifitApiClient;
import net.karthikraj.excercise.bakingapp.networking.RetrofitApiInterface;
import net.karthikraj.excercise.bakingapp.utils.InternetUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.layout_error_message)
    LinearLayout errorViewLayout;
    @BindView(R.id.tv_error_message_display)
    TextView errorMessageTextView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;
    public static List<RecipeModel> recipeList;
    private RecipesListAdapter recipesListAdapter;
    private Snackbar mSnackBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showRecipieListView();

        //set ActionBar and FragmentManager
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

    }



    private void fetchRecipiesWebData(){
        progressBar.setVisibility(View.VISIBLE);
        mSnackBar = Snackbar.make(mRecyclerView, R.string.msg_loading_please_wait, Snackbar.LENGTH_LONG);
        mSnackBar.show();

        RetrofitApiInterface apiService =
                RetrifitApiClient.getClient().create(RetrofitApiInterface.class);

        Call<List<RecipeModel>> apiCall = apiService.getRecipes();

        apiCall.enqueue(new Callback<List<RecipeModel>>() {
            @Override
            public void onResponse(Call<List<RecipeModel>> call, Response<List<RecipeModel>> response) {
                mSnackBar.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                recipeList = response.body();
                mRecyclerView.setLayoutManager(getGridLayoutManager());
                recipesListAdapter = new RecipesListAdapter(getApplicationContext(),recipeList);
                mRecyclerView.setAdapter(recipesListAdapter);
            }
            @Override
            public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                mSnackBar.setText(R.string.errmsg_fetch_failed);
                mSnackBar.setDuration(Snackbar.LENGTH_SHORT);
                mSnackBar.show();
                errorMessageTextView.setText(R.string.errmsg_fetch_failed);
                showErrorView();
            }
        });
    }

    public static RecipeModel getRecipe(int id) {
        if(recipeList != null) {
            return recipeList.get(id-1);
        } else{
            return null;
        }
    }


    private GridLayoutManager getGridLayoutManager(){
        int mNoOfColumns = calculateNoOfColumns();
        return new GridLayoutManager(this, mNoOfColumns, GridLayoutManager.VERTICAL, false);
    }


    private int calculateNoOfColumns() {
        int noOfColumns = 1;
        if(getResources().getBoolean(R.bool.isTablet)) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                noOfColumns = 3;
            } else {
                noOfColumns = 2;
            }
        } else {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                noOfColumns = 2;
            } else {
                noOfColumns = 1;
            }
        }
        return noOfColumns;
    }

    private void showErrorView() {
        errorViewLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showRecipieListView() {
        if(InternetUtils.isConnected(MainActivity.this)) {
            errorViewLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            fetchRecipiesWebData();
        } else {
            errorMessageTextView.setText(R.string.msg_err_internet_failiure);
            showErrorView();
        }
    }

}
