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

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {
    Context context;
    ArrayList<String> dates;
    ArrayList<String> weights;
    ArrayList<String> goals;
    static String user;

    public Adapter(Context context, ArrayList<String> dates, ArrayList<String> weights, ArrayList<String> goals, String user) {
        this.context = context;
        this.dates = dates;
        this.weights = weights;
        this.goals = goals;
        Adapter.user = user;
    }

    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Give look to my rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_row, parent, false);

        return new Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {
        //Assign values to the views based on position of recycler view
        holder.dateView.setText(dates.get(position));
        holder.weightView.setText(weights.get(position));
        holder.goalView.setText(goals.get(position));
    }

    @Override
    public int getItemCount() {
        //Number of items to display
        return dates.size();
    }

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

        private void editWeight() {
            Intent intent = new Intent(itemView.getContext(), EditWeightScreen.class);
            intent.putExtra("user_key", user);
            intent.putExtra("date_key", dateView.getText().toString());
            intent.putExtra("weight_key", weightView.getText().toString());
            itemView.getContext().startActivity(intent);
        }
    }
}
