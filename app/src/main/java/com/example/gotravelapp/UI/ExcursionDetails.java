package com.example.gotravelapp.UI;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Spinner;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.ParseException;

import com.example.gotravelapp.R;
import com.example.gotravelapp.database.Repository;
import com.example.gotravelapp.entities.Excursion;
import com.example.gotravelapp.entities.Vacation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {
    String excursionName;

    int excursionID;
    int vacationID;
    EditText editName;
    EditText editNote;
    Button editExcursionDate;
    Repository repository;
    DatePickerDialog.OnDateSetListener excursionDate;
    final Calendar myCalendarStart = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);
        repository = new Repository(getApplication());

//        excursionName = getIntent().getStringExtra("name");
        editName = findViewById(R.id.excursionName);
        editNote = findViewById(R.id.note);
        editExcursionDate = findViewById(R.id.excursionDate);
//        editName.setText("name");

        String excursionDateStr = getIntent().getStringExtra("excursionDate");
        String excursionNameStr = getIntent().getStringExtra("excursionName");

        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        if (excursionNameStr != null) {
            editName.setText(excursionNameStr);
        }

        if (excursionDateStr != null) {
            editExcursionDate.setText(excursionDateStr);
        }

        // Date picker setter
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        excursionDate = (view, year, month, dayOfMonth) -> {
            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, month);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editExcursionDate.setText(sdf.format(myCalendarStart.getTime()));
        };

        editExcursionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editExcursionDate.getText().toString();
                if(info.isEmpty()) info="06/25/25";
                try{
                    myCalendarStart.setTime(sdf.parse(info));
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }

                new DatePickerDialog(ExcursionDetails.this,
                        excursionDate,
                        myCalendarStart.get(Calendar.YEAR),
                        myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
        Spinner spinner = findViewById(R.id.spinner);
        ArrayList<Vacation> vacationArrayList = new ArrayList<>(repository.getAllVacations());
        ArrayAdapter<Vacation> vacationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vacationArrayList);
        spinner.setAdapter(vacationAdapter);

        // Set the spinner to the correct vacation based on vacationID
        for (int i = 0; i < vacationArrayList.size(); i++) {
            if (vacationArrayList.get(i).getVacationID() == vacationID) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    private void updateLabelStart () {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editExcursionDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    public boolean isValidDate(String dateStr) {
        if (dateStr == null) return false;
        String regex = "^(0[1-9]|1[0-2])/([0-2][0-9]|3[01])/\\d{2}$";
        if (!dateStr.matches(regex)) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        return true;
    }

    private void showConfirmationDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false) // disables tapping outside to dismiss
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    // Finish the activity only after the user taps OK
                    finish();
                })
                .create();
        // Prevent Android from auto-dismissing due to lifecycle
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Your code to reload updated excursion data and update UI fields
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getExcursionID() == excursionID) {
                editName.setText(e.getExcursionName());
                editExcursionDate.setText(e.getExcursionDate());
                // Update other fields if needed
                break;
            }
        }
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        String dateInput = editExcursionDate.getText().toString();

        if (!isValidDate(dateInput)) {
            Toast.makeText(this, "Date must be in MM/dd/yy format", Toast.LENGTH_LONG).show();
            return true; // Prevent proceeding if invalid
        }

// Save excursion
        if (item.getItemId() == R.id.saveExcursion) {

            String excursionDateStr = editExcursionDate.getText().toString();
            String excursionName = editName.getText().toString();

            if (!isValidDate(excursionDateStr)) {
                Toast.makeText(this, "Date must be in MM/dd/yy format", Toast.LENGTH_LONG).show();
                return true;
            }

// Try to get vacation dates from Intent
            String vacationStart = getIntent().getStringExtra("startDate");
            String vacationEnd = getIntent().getStringExtra("endDate");

// If they are null, fetch them from repository based on vacationID
            if (vacationStart == null || vacationEnd == null) {
                Vacation currentVacation = null;
                for (Vacation v : repository.getAllVacations()) {
                    if (v.getVacationID() == vacationID) {
                        currentVacation = v;
                        break;
                    }
                }
                if (currentVacation != null) {
                    vacationStart = currentVacation.getStartDate();
                    vacationEnd = currentVacation.getEndDate();
                } else {
                    Toast.makeText(this, "Unable to load associated vacation", Toast.LENGTH_LONG).show();
                    return true;
                }
            }

// Validate excursion date is within vacation range
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            sdf.setLenient(false);

            try {
                Date excursionDate = sdf.parse(excursionDateStr);
                Date startDate = sdf.parse(vacationStart);
                Date endDate = sdf.parse(vacationEnd);

                if (excursionDate.before(startDate) || excursionDate.after(endDate)) {
                    Toast.makeText(this, "Excursion date must be within vacation dates", Toast.LENGTH_LONG).show();
                    return true;
                }

                Excursion updatedExcursion = new Excursion(excursionID, excursionName, vacationID, excursionDateStr);

                if (excursionID == -1) {
                    int newID = repository.getAllExcursions().isEmpty() ? 1 :
                            repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;
                    updatedExcursion.setExcursionID(newID);
                    repository.insert(updatedExcursion);
                    Toast.makeText(this, "Excursion " + excursionName + " was added", Toast.LENGTH_SHORT).show();
                } else {
                    repository.update(updatedExcursion);
                }

                finish(); // Done
                return true;

            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_LONG).show();
                return true;
            } catch (java.text.ParseException e) {
                throw new RuntimeException(e);
            }
        }

// Share excursion
        if (item.getItemId() == R.id.share) {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
            sentIntent.putExtra(Intent.EXTRA_TITLE, "Message Name");
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);
            return true;
        }

// Creates alert for excursion
        if (item.getItemId() == R.id.notify) {
            String dateFromScreen = editExcursionDate.getText().toString();
            String myFormat = "MM/dd/yy"; // format
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException | java.text.ParseException e) {
                e.printStackTrace();
            }
            try {
                Long trigger = myDate.getTime();
                Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
                String excursionName = editName.getText().toString();
                intent.putExtra("key", excursionName + " starts today!");
//                intent.putExtra("key", "Excursion starts today!");
                PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

//Deletes excursion
        if (item.getItemId() == R.id.deleteExcursion) {
            Excursion currentExcursion = null;
            for (Excursion e : repository.getAllExcursions()) {
                if (e.getExcursionID() == excursionID) {
                    currentExcursion = e;
                    break;
                }
            }
            if (currentExcursion != null) {
                repository.delete(currentExcursion);
                Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionName() + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ExcursionDetails.this, "Excursion not found", Toast.LENGTH_LONG).show();
            }

            ExcursionDetails.this.finish();
            return true;
        }
// If none of the above matched, call super:
        return super.onOptionsItemSelected(item);
    }
}