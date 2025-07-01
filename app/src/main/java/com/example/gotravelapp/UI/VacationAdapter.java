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

import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    public class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Vacation current= mVacations.get(position);
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

    private static List<Vacation> mVacations;
    private final Context context;
    private final LayoutInflater mInflater;

    public VacationAdapter(Context context) {
        mInflater= LayoutInflater.from(context);
        this.context=context;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.vacation_list_item,parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationAdapter.VacationViewHolder holder, int position) {
        if(mVacations!=null) {
            Vacation current = mVacations.get(position);
            String name = current.getVacationName();
            holder.vacationItemView.setText(name);
        }
        else {
            holder.vacationItemView.setText("No vacation name");
        }
    }
    public void setVacations(List<Vacation> vacations) {
        mVacations=vacations;
        notifyDataSetChanged();
    }
    public void setExcursions(List<Excursion> filteredExcursions) {
    }

    @Override
    public int getItemCount() {
        if(mVacations!=null) {
            return mVacations.size();
        }
        else {
            return 0;
        }
    }
}
