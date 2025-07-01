package com.example.gotravelapp.UI;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.net.ParseException;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotravelapp.R;
import com.example.gotravelapp.database.Repository;
import com.example.gotravelapp.entities.Excursion;
import com.example.gotravelapp.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class VacationDetails extends AppCompatActivity {
    private static final int REQUEST_CODE_EXCURSION = 1;
    private static final String CHANNEL_ID = "vacation_alert_channel";
    private int vacationID;
    private String name;
    private String hotel;
    private String setStartDate;
    private String setEndDate;

    private EditText editName;
    private EditText editHotel;
    private EditText startEditDate;
    private EditText endEditDate;

    private Vacation currentVacation;
    private int numExcursions;

    private Repository repository;
    private ExcursionAdapter excursionAdapter;
    private RecyclerView recyclerView;

    Random rand = new Random();
    public static int numAlert = 0;

    List<Excursion> filteredExcursions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Initialize Repository
        repository = new Repository(getApplication());

        // View references
        editName = findViewById(R.id.vacationname);
        editHotel = findViewById(R.id.hotelname);
        startEditDate = findViewById(R.id.vacationstartdate);
        endEditDate = findViewById(R.id.vacationenddate);
        recyclerView = findViewById(R.id.excursionrecyclerview);

        // Retrieve intent data
        vacationID = getIntent().getIntExtra("id", 0);
        name = getIntent().getStringExtra("name");
        hotel = getIntent().getStringExtra("hotel");
        setStartDate = getIntent().getStringExtra("startDate");
        setEndDate = getIntent().getStringExtra("endDate");

        // Populate fields
        editName.setText(name);
        editHotel.setText(hotel);
        if (setStartDate != null) startEditDate.setText(setStartDate);
        if (setEndDate != null) endEditDate.setText(setEndDate);

        currentVacation = new Vacation(vacationID, name, hotel, setStartDate, setEndDate);

// Setup RecyclerView & Adapter
        excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadExcursions(); // Populate initial data

// Date picker setup
        setupDatePickers();
        createNotificationChannel();

// FAB - takes user to add excursion
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
//            Excursion selectedExcursion = null;
            intent.putExtra("excursionID", -1);
            intent.putExtra("vacationID", vacationID);
            intent.putExtra("name", currentVacation.getVacationName());
            intent.putExtra("hotel", currentVacation.getVacationHotel());
            intent.putExtra("startDate", currentVacation.getStartDate());
            intent.putExtra("endDate", currentVacation.getEndDate());
            intent.putExtra("startDate", startEditDate.getText().toString());  // ✅ pass start date
            intent.putExtra("endDate", endEditDate.getText().toString());
            startActivityForResult(intent, REQUEST_CODE_EXCURSION);
//            startActivity(intent);
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Vacation Alerts";
            String description = "Notifications for vacation start/end alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void setupDatePickers() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener startDatePicker = (view, year, month, day) -> {
            calendarStart.set(year, month, day);
            startEditDate.setText(sdf.format(calendarStart.getTime()));
        };

        DatePickerDialog.OnDateSetListener endDatePicker = (view, year, month, day) -> {
            calendarEnd.set(year, month, day);
            endEditDate.setText(sdf.format(calendarEnd.getTime()));
        };

        startEditDate.setOnClickListener(v -> new DatePickerDialog(
                VacationDetails.this, startDatePicker,
                calendarStart.get(Calendar.YEAR),
                calendarStart.get(Calendar.MONTH),
                calendarStart.get(Calendar.DAY_OF_MONTH)).show());

        endEditDate.setOnClickListener(v -> new DatePickerDialog(
                VacationDetails.this, endDatePicker,
                calendarEnd.get(Calendar.YEAR),
                calendarEnd.get(Calendar.MONTH),
                calendarEnd.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void loadExcursions() {
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getVacationID() == vacationID) {
                filteredExcursions.add(e);
            }
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }

// Generates a pop up to confirm vacation was added
    private void showConfirmationDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // only finish after user confirms
                })
                .setCancelable(false)
                .show();
    }

// Checks date format
    public boolean isValidDate(String dateStr) {
        String regex = "^(0[1-9]|1[0-2])/([0-2][0-9]|3[01])/\\d{2}$";
        if (!dateStr.matches(regex)) {
            return false; // Pattern does not match MM/dd/yy
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        sdf.setLenient(false); // strict parsing

        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException | java.text.ParseException e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
// Saves vacation
        if (id == R.id.saveVacation) {

            String startDateStr = startEditDate.getText().toString();
            String endDateStr = endEditDate.getText().toString();

// Validate format
            if (!isValidDate(startDateStr) || !isValidDate(endDateStr)) {
                Toast.makeText(this, "Dates must be in MM/dd/yy format", Toast.LENGTH_LONG).show();
                return true; // stop save
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            try {
                Date startDate = sdf.parse(startDateStr);
                Date endDate = sdf.parse(endDateStr);

                if (endDate.before(startDate)) {
                    Toast.makeText(this, "End date must be after start date", Toast.LENGTH_LONG).show();
                    return true; // Stop saving
                }
            } catch (ParseException | java.text.ParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_LONG).show();
                return true;
            }
                Vacation vacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(), startDateStr, endDateStr);
                if (vacationID == 0) {
                    repository.insert(vacation);
                    showConfirmationDialog("Vacation Added", vacation.getVacationName() + " was added!");
                } else {
                    repository.update(vacation);
                }
//                this.finish();
            return true;
        }
// Deletes vacation
        if (id == R.id.deleteVacation) {
            for (Vacation vacation : repository.getAllVacations()) {
                if (vacation.getVacationID() == vacationID) currentVacation = vacation;
            }

            numExcursions = 0;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getVacationID() == vacationID) ++numExcursions;
            }

            if (numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(this, currentVacation.getVacationName() + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (item.getItemId() == R.id.vacationnotify) {
            String startDateStr = startEditDate.getText().toString();
            String endDateStr = endEditDate.getText().toString();
            String vacationName = editName.getText().toString();
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            try {
                // Parse and set start date alarm
                Date startDate = sdf.parse(startDateStr);
                if (startDate != null) {
                    long trigger = startDate.getTime();
                    Intent startIntent = new Intent(VacationDetails.this, MyReceiver.class);
                    startIntent.putExtra("key", "Vacation \"" + vacationName + "\" starts today!");
                    PendingIntent startSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, startIntent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, startSender);
                }

                // Parse and set end date alarm
                Date endDate = sdf.parse(endDateStr);
                if (endDate != null) {
                    long trigger = endDate.getTime();
                    Intent endIntent = new Intent(VacationDetails.this, MyReceiver.class);
                    endIntent.putExtra("key", "Vacation \"" + vacationName + "\" ends today!");
                    PendingIntent endSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, endIntent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, endSender);
                }

                Toast.makeText(this, "Vacation alerts set!", Toast.LENGTH_SHORT).show();
            } catch (ParseException | java.text.ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to parse date(s)", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (item.getItemId() == R.id.share) {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TITLE, "Vacation Shared");
            //need to put the items into a constructed String or else each item will replace the last
            StringBuilder shareData = new StringBuilder();
            shareData.append("Vacation title: ").append(editName.getText().toString()).append("\n");
            shareData.append("Hotel name: ").append(editHotel.getText().toString()).append("\n");
            shareData.append("Start Date: ").append(startEditDate.getText().toString()).append("\n");
            shareData.append("End Date: ").append(endEditDate.getText().toString()).append("\n");
            for (int i = 0; i < filteredExcursions.size(); i++) {
                shareData.append("Excursion ").append(i + 1).append(": ").append(filteredExcursions.get(i).getExcursionName()).append("\n");
                shareData.append("Excursion ").append(i + 1).append(" Date: ").append(filteredExcursions.get(i).getExcursionDate()).append("\n");
            }
            sentIntent.putExtra(Intent.EXTRA_TEXT, shareData.toString());
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);
            return true;
        }

        if (id == R.id.addSampleExcursions) {
            if (vacationID == -1) {
                Toast.makeText(this, "Please save vacation before adding excursions", Toast.LENGTH_LONG).show();
            } else {
                int excursionID = repository.getAllExcursions().isEmpty()
                        ? 1
                        : repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;

                repository.insert(new Excursion(excursionID, "Monkey Park", vacationID, "06/28/25"));
                repository.insert(new Excursion(++excursionID, "Mini Golf", vacationID, "07/28/25"));

                loadExcursions(); // Refresh list
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getVacationID() == vacationID) {
                filteredExcursions.add(e);
            }
        }
        excursionAdapter.setExcursions(filteredExcursions); // updates the RecyclerView
    } // Refresh excursions every time screen resumes
}

