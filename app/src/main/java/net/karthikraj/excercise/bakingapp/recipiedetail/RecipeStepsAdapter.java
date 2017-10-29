package net.karthikraj.excercise.bakingapp.recipiedetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.karthikraj.excercise.bakingapp.R;
import net.karthikraj.excercise.bakingapp.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 29/10/17.
 */

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {

    public List<Step> stepsArray;
    private Context mContext;
    private final ListItemClickListener listItemClickListener;

    public interface ListItemClickListener{
        void onListItemClick(Step step);
    }

    public RecipeStepsAdapter(Context context, List<Step> stepsArray, ListItemClickListener listener){
        this.stepsArray = stepsArray;
        this.mContext = context;
        listItemClickListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_steps,null);
        return new RecipeStepsAdapter.ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bindStepData(stepsArray.get(position),position);
    }

    @Override
    public int getItemCount() {
        return stepsArray.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_steps_description) TextView stepsDescriptionTextView;
        private Step step;
        private ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        private void bindStepData(Step step, int count){
            this.step = step;
            stepsDescriptionTextView.setText(step.getShortDescription());
        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onListItemClick(step);
        }
    }
}
