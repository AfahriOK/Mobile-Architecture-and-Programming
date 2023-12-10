/**
 * @author Afahri Kerr (AfahriAKerr91@gmail.com)
 * @version 2.0
 * @since 12/07/2023
 * Purpose: Create an organized look for the user entries
 */

package com.zybooks.weighttrackerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * The Adapter class is used to create a card display on the weight screen using the user data
 */
public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    Context context;
    ArrayList<String> dates;
    ArrayList<String> weights;
    ArrayList<String> goals;
    static String user;

    public Adapter(Context context, ArrayList<String> dates, ArrayList<String> weights,
                   ArrayList<String> goals, String user) {
        this.context = context;
        this.dates = dates;
        this.weights = weights;
        this.goals = goals;
        Adapter.user = user;
    }

    /**
     * This method sets the card layout for the Adapter
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return The card layout to be displayed
     */
    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Tells the adapter what layout to use)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_row, parent, false);

        return new Adapter.MyViewHolder(view);
    }

    /**
     * This method sets the values from the arrays for each position in the views.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {
        //Assign values to the views based on position in recycler view
        holder.dateView.setText(dates.get(position));
        holder.weightView.setText(weights.get(position));
        holder.goalView.setText(goals.get(position));
    }

    /**
     * This method is used to get the count of cards to be created.
     * @return Size of the dates array.
     */
    @Override
    public int getItemCount() {
        return dates.size();
    }

    /**
     * This class sets the properties for the card view.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateView;
        TextView weightView;
        TextView goalView;
        Button editButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dateView = itemView.findViewById(R.id.date_view);
            weightView = itemView.findViewById(R.id.weight_view);
            goalView = itemView.findViewById(R.id.goal_view);
            editButton = itemView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(view -> editWeight());
        }

        /**
         * This method sends the user to the edit weight screen and passes the data of the card.
         */
        private void editWeight() {
            Intent intent = new Intent(itemView.getContext(), EditWeightScreen.class);
            intent.putExtra("user_key", user);
            intent.putExtra("date_key", dateView.getText().toString());
            intent.putExtra("weight_key", weightView.getText().toString());
            itemView.getContext().startActivity(intent);
        }
    }
}
