package com.example.gotravelapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotravelapp.R;
import com.example.gotravelapp.database.Repository;
import com.example.gotravelapp.entities.Excursion;
import com.example.gotravelapp.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(VacationList.this, VacationDetails.class);
            startActivity(intent);
        });
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        repository = new Repository(getApplication());
        VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Vacation> allVacations=repository.getAllVacations();
        vacationAdapter.setVacations(allVacations);
//        System.out.println(getIntent().getStringExtra("Test"));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            this.finish();
            return true;
        }

        if(item.getItemId()==R.id.sample){
            repository = new Repository(getApplication());
//            Toast.makeText(VacationList.this, "put in sample data", Toast.LENGTH_LONG);
            Vacation vacation = new Vacation( "Greece", "The Hilton", "06/06/25", "06/25/25");
            repository.insert(vacation);
            vacation = new Vacation( "Tokyo", "The Holiday Inn", "06/15/25", "06/29/25");
            repository.insert(vacation);
            Excursion excursion = new Excursion( "Sailing", 1,  "06/15/25");
            repository.insert(excursion);
            excursion = new Excursion( "Museum of Natural History",2,  "06/20/25");
            repository.insert(excursion);

            RecyclerView recyclerView=findViewById(R.id.recyclerview);
            final VacationAdapter vacationAdapter=new VacationAdapter(this);
            recyclerView.setAdapter(vacationAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<Vacation> allVacations=repository.getAllVacations();
            vacationAdapter.setVacations(allVacations);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        List<Vacation> allVacations = repository.getAllVacations();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);
    }
}

