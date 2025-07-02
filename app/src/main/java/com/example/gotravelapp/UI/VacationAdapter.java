package com.example.gotravelapp.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotravelapp.R;
import com.example.gotravelapp.entities.Excursion;
import com.example.gotravelapp.entities.Vacation;

import java.util.ArrayList;
import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private List<Vacation> filteredVacations = new ArrayList<>();
    private List<Vacation> allVacations = new ArrayList<>();
    private final Context context;
    private final LayoutInflater mInflater;

    public VacationAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Vacation current = filteredVacations.get(position);
                    Intent intent = new Intent(context, VacationDetails.class);
                    intent.putExtra("id", current.getVacationID());
                    intent.putExtra("name", current.getVacationName());
                    intent.putExtra("hotel", current.getVacationHotel());
                    intent.putExtra("startDate", current.getStartDate());
                    intent.putExtra("endDate", current.getEndDate());
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        if (!filteredVacations.isEmpty()) {
            Vacation current = filteredVacations.get(position);
            holder.vacationItemView.setText(current.getVacationName());
        } else {
            holder.vacationItemView.setText("No vacation name");
        }
    }
    @Override
    public int getItemCount() {
        return filteredVacations.size();
    }

    public void setVacations(List<Vacation> vacations) {
        allVacations = new ArrayList<>(vacations);
        filteredVacations = new ArrayList<>(vacations);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredVacations = new ArrayList<>(allVacations);
        } else {
            List<Vacation> filteredList = new ArrayList<>();
            String lowerQuery = query.toLowerCase();
            for (Vacation vacation : allVacations) {
                if (vacation.getVacationName().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(vacation);
                }
            }
            filteredVacations = filteredList;
        }
        notifyDataSetChanged();
    }
}
