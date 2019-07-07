package project.cs4084.asteroids;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class UserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        final Button userAreaPause = (Button)findViewById(R.id.userAreaPause);

        // if user clicks Sounds On/Off the music will pause or play
        userAreaPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.mMediaPlayer.isPlaying()){
                    LoginActivity.mMediaPlayer.pause();
                } else {
                    LoginActivity.mMediaPlayer.start();
                }
            }
        });

        // Daily notification that will be set at a certain time
        findViewById(R.id.userAreaReminder).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Time for notification to show. Use Calendar class
                Calendar calendar = Calendar.getInstance();

                // time for notification to appear
                calendar.set(Calendar.HOUR_OF_DAY, 14);
                calendar.set(Calendar.MINUTE, 0);

                // Repeating notification works by creating and registering a broadcast receiver
                // which can be triggered by alarm manager
                Intent intent = new Intent(getApplicationContext(), Notification_receiver.class);

                // Alarm service requires pending intent as a paramater. Need unique request code any number
                // pass intent into this
                // Flag indicates the pending intent we created can be updated int eh future
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Instance of alarm manager
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                // alarm triggered even in sleep mode, need to get calender time, next is how often it should get called (interval day), pass pending intent
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent );

                Toast.makeText(getApplicationContext(), "Daily notification has been set", Toast.LENGTH_SHORT).show();

            }
        });
        // Cancel the daily notification from appearing
        findViewById(R.id.userAreaReminderCancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Notification_receiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.cancel(sender);
                Toast.makeText(getApplicationContext(), "Daily notification has been stopped", Toast.LENGTH_SHORT).show();

            }
        });

        // create links to the text fields is the activity_user_area
        final TextView userAreaWelcome = (TextView) findViewById(R.id.userAreaWelcome);
        final Button userAreaPlay = (Button) findViewById(R.id.userAreaPlay);
        final Button userAreaMiniGame = (Button) findViewById(R.id.userAreaMiniGame);

        // getIntent() gets all data that was passed to this activity
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        // personalized display to the user
        String message = name + " welcome to your account";
        userAreaWelcome.setText(message);

        // when user clicks on Play they are brought to the game
        // OnClickListener is abstract. It needs to have an override method in place.
        userAreaPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when user clicks do this
                // Intent is used changes to another app component.
                // going from UserAreaActivity to Game
                Intent gameIntent = new Intent(UserAreaActivity.this, Engine.class);
                LoginActivity.mMediaPlayer.stop();
                // To make intent happen. Take this activity and start the new activity
                // startActivity launches an activity.
                UserAreaActivity.this.startActivity(gameIntent);
            }
        });

        // when user clicks on Play Mini Game they are brought to the mini game
        userAreaMiniGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when user clicks do this
                // Intent is used changes to another app component.
                // going from UserAreaActivity to MiniGame
                Intent gameIntent = new Intent(UserAreaActivity.this, MiniGame.class);
                // To make intent happen. Take this activity and start the new activity
                // startActivity launches an activity.
                UserAreaActivity.this.startActivity(gameIntent);
            }
        });

    }
}
