package whowideweb.utilities.valarmclock;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ValarmClockActivity extends Activity {
	Toast mToast;
	
	private TextView mDateDisplay;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    
    static final int DATE_DIALOG_ID = 0;	

    private TextView mTimeDisplay;
    private Button mPickTime;
    
    private int mHour;
    private int mMinute;
    
    static final int TIME_DIALOG_ID = 1;
    
    public ToggleButton btnOneShot;
    public ToggleButton mbutton;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // capture our View elements
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);

        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        // capture our View elements
        mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
        mPickTime = (Button) findViewById(R.id.pickTime);

        // add a click listener to the button
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        // get the current time
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        
        // Watch for button clicks.
        btnOneShot = (ToggleButton)findViewById(R.id.one_shot);
        btnOneShot.setOnClickListener(mOneShotListener);
        mbutton = (ToggleButton)findViewById(R.id.start_repeating);
        mbutton.setOnClickListener(mStartStopRepeatingListener);
        
        // display the current date (this method is below)
        updateDisplay();
    }
    
    private OnClickListener mOneShotListener = new OnClickListener() {
        public void onClick(View v) {
        	if (btnOneShot.isChecked()) {
	            // When the alarm goes off, we want to broadcast an Intent to our
	            // BroadcastReceiver.  Here we make an Intent with an explicit class
	            // name to have our own receiver (which has been published in
	            // AndroidManifest.xml) instantiated and called, and then create an
	            // IntentSender to have the intent executed as a broadcast.
	            Intent intent = new Intent(ValarmClockActivity.this, OneShotAlarm.class);
	            PendingIntent sender = PendingIntent.getBroadcast(ValarmClockActivity.this,
	                    0, intent, 0);
	
	            // We want the alarm to go off 10 seconds from now.
	            Calendar calendar = Calendar.getInstance();
	            calendar.set(mYear, mMonth, mDay, mHour, mMinute, 0);
	
	            // Schedule the alarm!
	            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	
	            // Tell the user about what we did.
	            if (mToast != null) {
	                mToast.cancel();
	            }
	            mToast = Toast.makeText(ValarmClockActivity.this, R.string.one_shot_scheduled,
	                    Toast.LENGTH_LONG);
	            mToast.show();
        	} else {
                // Create the same intent, and thus a matching IntentSender, for
                // the one that was scheduled.
                Intent intent = new Intent(ValarmClockActivity.this, OneShotAlarm.class);
                PendingIntent sender = PendingIntent.getBroadcast(ValarmClockActivity.this,
                        0, intent, 0);
                
                // And cancel the alarm.
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                am.cancel(sender);

                // Tell the user about what we did.
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(ValarmClockActivity.this, R.string.one_shot_unscheduled,
                        Toast.LENGTH_LONG);
                mToast.show();
        	}
        }
    };
    
    private OnClickListener mStartStopRepeatingListener = new OnClickListener() {
        public void onClick(View v) {
        	if (mbutton.isChecked()) {
	            // When the alarm goes off, we want to broadcast an Intent to our
	            // BroadcastReceiver.  Here we make an Intent with an explicit class
	            // name to have our own receiver (which has been published in
	            // AndroidManifest.xml) instantiated and called, and then create an
	            // IntentSender to have the intent executed as a broadcast.
	            // Note that unlike above, this IntentSender is configured to
	            // allow itself to be sent multiple times.
	            Intent intent = new Intent(ValarmClockActivity.this, RepeatingAlarm.class);
	            PendingIntent sender = PendingIntent.getBroadcast(ValarmClockActivity.this,
	                    0, intent, 0);
	            
	            // set the alarm based on the alarm clock
	            Calendar calendar = Calendar.getInstance();
	            calendar.set(mYear, mMonth, mDay, mHour, mMinute, 0);
	            long firstTime =  calendar.getTimeInMillis();
	
	            // Schedule the alarm!
	            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            am.setRepeating(AlarmManager.RTC_WAKEUP,
	                            firstTime, 750, sender);
	
	            // Tell the user about what we did.
	            if (mToast != null) {
	                mToast.cancel();
	            }
	            mToast = Toast.makeText(ValarmClockActivity.this, R.string.repeating_scheduled,
	                    Toast.LENGTH_LONG);
	            mToast.show();
        	} else {
                // Create the same intent, and thus a matching IntentSender, for
                // the one that was scheduled.
                Intent intent = new Intent(ValarmClockActivity.this, RepeatingAlarm.class);
                PendingIntent sender = PendingIntent.getBroadcast(ValarmClockActivity.this,
                        0, intent, 0);
                
                // And cancel the alarm.
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                am.cancel(sender);

                // Tell the user about what we did.
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(ValarmClockActivity.this, R.string.repeating_unscheduled,
                        Toast.LENGTH_LONG);
                mToast.show();
        	}
        }
    };
    
    // updates the date in the TextView
    private void updateDisplay() {
        mDateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("-")
                    .append(mDay).append("-")
                    .append(mYear).append(" "));
        mTimeDisplay.setText(
                new StringBuilder()
                        .append(pad(mHour)).append(":")
                        .append(pad(mMinute)));
    }
    
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
            
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
	    case TIME_DIALOG_ID:
	        return new TimePickerDialog(this,
	                mTimeSetListener, mHour, mMinute, true);
	    }
        return null;
    }
    
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                updateDisplay();
            }
        };
}