package project.cs4084.asteroids;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This activity uses the LoginRequest class to handle connection to database using volley
 */
public class LoginActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    public static MediaPlayer mMediaPlayer;
	public static boolean viewScores = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    if (viewScores)
	    {
		    startActivity(new Intent(this, ScoreScreen.class));
	    }

        // Remove the title bar
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        // this line says which xml file to work from
        setContentView(R.layout.activity_login);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.videoplayback);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	    setVolumeControlStream(AudioManager.STREAM_MUSIC); // TODO: CHECK IF NEEDED
        mMediaPlayer.setLooping(true);
        if(!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        } else {
            mMediaPlayer.pause();
        }

        // hold reference to all fields and buttons in activity_register xml file
        // final because this is the only variable its being assigned to
        // findViewById looks at activity_register and finds a view
        // casted as (EditText) so that findViewById knows what to look for
        final EditText loginID = (EditText) findViewById(R.id.loginID);
        final EditText loginPassword = (EditText) findViewById(R.id.loginPassword);
        // Login button
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final Button loginCredits = (Button) findViewById(R.id.loginCredits);
        final Button loginGuest = (Button) findViewById(R.id.loginGuest);
        final Button loginAbout = (Button) findViewById(R.id.loginAbout);
        // PLAY PAUSE MUSIC
        final Button loginPauseMusic = (Button) findViewById(R.id.loginPauseMusic);
        /*loginPauseMusic.setText("Mute");*/
        // Register link
        final TextView loginRegisterLink = (TextView) findViewById(R.id.loginRegisterLink);

        // when user clicks on register link they are brought to register page. need a setOnClickListener
        // OnClickListener is abstract. It needs to have an override method in place.
        loginRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when user clicks do this
                // Intent is used changes to another app component.
                // going from LoginActivity to RegisterActivity
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                // To make intent happen. Take this activity and start the new activity
                // startActivity launches an activity.
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        // want to create request when user clicks login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // execute when user clicks log in
                final String username = loginID.getText().toString();
                final String password = loginPassword.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // more complicated than register
                        // convert the response to JSON object
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success) {
                                // get details from the jsonResponse and send to user area activity
                                String name = jsonResponse.getString("name");
                                int highscore = jsonResponse.getInt("highscore");

                                // have this info want to pass over to user area
                                Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                                // using intent can pass extra info over
                                intent.putExtra("name", name);
                                intent.putExtra("username", username);
                                intent.putExtra("highscore", highscore);

                                // open activity
                                LoginActivity.this.startActivity(intent);
                                // now go to UserAreaActivity and retreive data from there

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed").setNegativeButton("Retry", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                // need to add this request to request queue for volley
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                // add to queue
                queue.add(loginRequest);
                // need to add permission to manifest to allow INTERNET
            }
        });
        // if user clicks play as guest bring them to the game without logging in
        loginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when user clicks do this
                // Intent is used changes to another app component.
                // going from LoginActivity to RegisterActivity
                Intent gameIntent = new Intent(LoginActivity.this, Engine.class);
                mMediaPlayer.stop();
                // To make intent happen. Take this activity and start the new activity
                // startActivity launches an activity.
                LoginActivity.this.startActivity(gameIntent);
            }
        });

        // if user clicks play as guest bring them to the game without logging in
        loginAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when user clicks do this
                // Intent is used changes to another app component.
                // going from LoginActivity to RegisterActivity
                Intent gameIntent = new Intent(LoginActivity.this, HistoryOfAsteroids.class);
                // To make intent happen. Take this activity and start the new activity
                // startActivity launches an activity.
                LoginActivity.this.startActivity(gameIntent);
            }
        });
        // if user clicks play as guest bring them to the game without logging in
        loginCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when user clicks do this
                // Intent is used changes to another app component.
                // going from LoginActivity to RegisterActivity
                Intent gameIntent = new Intent(LoginActivity.this, ProjectCredits.class);
                // To make intent happen. Take this activity and start the new activity
                // startActivity launches an activity.
                LoginActivity.this.startActivity(gameIntent);
            }
        });

        // if user clicks play as guest bring them to the game without logging in
        loginPauseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                } else {
                    mMediaPlayer.start();
                }
            }
        });
    }

/*    @Override
    protected void onPause() {
        mMediaPlayer.stop();
        // stop the music
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
        } else {
            mMediaPlayer.pause();
        }
    }

    // turn off music when this activity closes
    public void onDestroy() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        super.onDestroy();
    }


    boolean doubleBackToExitPressedOnce = false;

    // click back twice to exit app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void onPrepared(MediaPlayer player)
    {
        mMediaPlayer.start();
    }
}
