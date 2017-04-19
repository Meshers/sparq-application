package com.sparq.quizpolls.application.userinterface;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sparq.quizpolls.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class NewEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private EditText eventNameText;
    private EditText agendaText;
    private EditText venueText;
    private TextInputLayout dateLayout, timeLayout;
    private EditText dateText;
    private EditText timeText;
    private EditText durationText;
    private SwitchCompat publicToggle;
    private Button addEvent;

    String eventName, agenda, venue;
    Date date;
    Time time;
    int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton newQuestion = (FloatingActionButton) findViewById(R.id.newQuestion);
//        newQuestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        initialzeViews();
    }

    public void initialzeViews(){

        eventNameText = (EditText) findViewById(R.id.event_name_text);
        agendaText = (EditText) findViewById(R.id.agenda_text);
        venueText = (EditText) findViewById(R.id.venue_text);
        dateText = (EditText) findViewById(R.id.date_text);
        timeText = (EditText) findViewById(R.id.time_text);
        durationText = (EditText) findViewById(R.id.duration_text);

        dateLayout = (TextInputLayout) findViewById(R.id.date_layout);
        timeLayout = (TextInputLayout) findViewById(R.id.time_layout);

        addEvent = (Button)findViewById(R.id.add_event);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventName = eventNameText.getText().toString();
                agenda = agendaText.getText().toString();
                venue = venueText.getText().toString();

            }
        });

        dateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            NewEventActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
//                dpd.setThemeDark(false);
//                dpd.vibrate(true);
//                dpd.dismissOnPause(false);
//                dpd.showYearPickerFirst(showYearFirst.isChecked());
                    dpd.setVersion(DatePickerDialog.Version.VERSION_2);
//                if (modeCustomAccentDate.isChecked()) {
//                    dpd.setAccentColor(Color.parseColor("#9C27B0"));
//                }
//                if (titleDate.isChecked()) {
//                    dpd.setTitle("DatePicker Title");
//                }
//                if (highlightDays.isChecked()) {
//                    Calendar date1 = Calendar.getInstance();
//                    Calendar date2 = Calendar.getInstance();
//                    date2.add(Calendar.WEEK_OF_MONTH, -1);
//                    Calendar date3 = Calendar.getInstance();
//                    date3.add(Calendar.WEEK_OF_MONTH, 1);
//                    Calendar[] days = {date1, date2, date3};
//                    dpd.setHighlightedDays(days);
//                }
//                if (limitSelectableDays.isChecked()) {
//                    Calendar[] days = new Calendar[13];
//                    for (int i = -6; i < 7; i++) {
//                        Calendar day = Calendar.getInstance();
//                        day.add(Calendar.DAY_OF_MONTH, i * 2);
//                        days[i + 6] = day;
//                    }
//                    dpd.setSelectableDays(days);
//                }
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            }
        });

        timeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                            NewEventActivity.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false
                    );
//                tpd.setThemeDark(modeDarkTime.isChecked());
//                tpd.vibrate(vibrateTime.isChecked());
//                tpd.dismissOnPause(dismissTime.isChecked());
//                tpd.enableSeconds(enableSeconds.isChecked());
//                tpd.setVersion(showVersion2.isChecked() ? TimePickerDialog.Version.VERSION_2 : TimePickerDialog.Version.VERSION_1);
//                if (modeCustomAccentTime.isChecked()) {
//                    tpd.setAccentColor(Color.parseColor("#9C27B0"));
//                }
//                if (titleTime.isChecked()) {
//                    tpd.setTitle("TimePicker Title");
//                }
//                if (limitSelectableTimes.isChecked()) {
//                    tpd.setTimeInterval(3, 5, 10);
//                }
                    tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            Log.d("TimePicker", "Dialog was cancelled");
                        }
                    });
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                }
            }
        });


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        dateText.setText(date);

        this.date = new Date(dayOfMonth, monthOfYear, year);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        String time = hourString+":"+minuteString+":"+secondString;
        timeText.setText(time);

        this.time = new Time(hourOfDay, minute, second);
    }

}
