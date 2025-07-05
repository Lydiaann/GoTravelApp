package com.example.gotravelapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotravelapp.R;
import com.example.gotravelapp.database.Repository;
import com.example.gotravelapp.entities.Excursion;
import com.example.gotravelapp.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Executors;
import android.widget.Button;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private List<Vacation> allVacations = new ArrayList<>();
    private VacationAdapter vacationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

// Floating Action button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(VacationList.this, VacationDetails.class);
            startActivity(intent);
        });
// Recycler View
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        repository = new Repository(getApplication());

        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allVacations = repository.getAllVacations();
        vacationAdapter.setVacations(allVacations);

        Button btnGenerate = findViewById(R.id.btnGenerateCSV);

        btnGenerate.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                List<Vacation> vacations = repository.getAllVacations();  // or vacationDao.getAllVacations()
                generateCSVReport(vacations);
            });
        });
// Search View
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVacations(newText);
                return true;
            }
        });
    }
// Generate Report
    private void generateCSVReport(List<Vacation> vacations) {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Vacation Name, Start Date, End Date\n");

        for (Vacation v : vacations) {
            reportBuilder.append(v.getVacationName()).append(", ")
                    .append(v.getStartDate()).append(", ")
                    .append(v.getEndDate()).append("\n");
        }

        String reportText = reportBuilder.toString();

        // Start ReportActivity and pass the report text
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("reportText", reportText);

        runOnUiThread(() -> startActivity(intent));
    }

    private void filterVacations(String query) {
        vacationAdapter.filter(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }
// Back option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.sample) {
            repository = new Repository(getApplication());
//            Toast.makeText(VacationList.this, "put in sample data", Toast.LENGTH_LONG);
            Vacation vacation = new Vacation("Greece", "Hilton", "06/06/25", "06/25/25");
            repository.insert(vacation);
            vacation = new Vacation("Tokyo", "Holiday Inn", "06/15/25", "06/29/25");
            repository.insert(vacation);
            vacation = new Vacation("Spain", "Marriott", "07/09/25", "08/25/25");
            repository.insert(vacation);
            vacation = new Vacation("England", "Hyatt Hotel", "08/15/25", "09/01/25");
            repository.insert(vacation);
            vacation = new Vacation("Scotland", "Atholl Palace Hotel ", "11/01/25", "11/20/25");
            repository.insert(vacation);
            vacation = new Vacation("Germany", "Manhattan Hotel", "06/17/25", "07/19/25");
            repository.insert(vacation);
            vacation = new Vacation("France", "Hotel Maison Mere", "07/11/25", "08/02/25");
            repository.insert(vacation);
            vacation = new Vacation("Switzerland", "Four Seasons Hotel Des ", "09/15/25", "09/30/25");
            repository.insert(vacation);
            Excursion excursion = new Excursion("Sailing", 1, "06/15/25");
            repository.insert(excursion);
            excursion = new Excursion("Museum of Natural History", 2, "06/20/25");
            repository.insert(excursion);

            allVacations = repository.getAllVacations();
            vacationAdapter.setVacations(allVacations);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();

        allVacations = repository.getAllVacations();  // Keep this updated
        if (vacationAdapter != null) {
            vacationAdapter.setVacations(allVacations);


        }
    }
}

