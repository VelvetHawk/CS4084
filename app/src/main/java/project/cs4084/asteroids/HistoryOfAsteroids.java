package project.cs4084.asteroids;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class HistoryOfAsteroids extends YouTubeBaseActivity {
    private YouTubePlayerView youTubePlayerView;
    Button button;

    private YouTubePlayer.OnInitializedListener onInitializedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*LoginActivity.mMediaPlayer.stop();*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_of_asteroids);

        button = (Button)findViewById(R.id.button);
        final Button historyPause = (Button)findViewById(R.id.historyPause);
        if(LoginActivity.mMediaPlayer.isPlaying()){
            historyPause.setText("Mute");
        } else {
            historyPause.setText("Unmute");
        }
        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.view);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("Fjzc1Cyprzg");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(LoginActivity.mMediaPlayer.isPlaying()) {
                    Toast.makeText(getApplicationContext(), "You may want to mute the music first! ;)", Toast.LENGTH_SHORT).show();
                } else {
                    youTubePlayerView.initialize("AIzaSyA872DpXYYOJiIC4wpZ36XS9HOVif50sJY", onInitializedListener);
                }

            }
        });
        // if user clicks play as guest bring them to the game without logging in
        historyPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.mMediaPlayer.isPlaying()){
                    LoginActivity.mMediaPlayer.pause();
                    historyPause.setText("Unmute");
                } else {
                    LoginActivity.mMediaPlayer.start();
                    historyPause.setText("Mute");
                }
            }
        });
    }
}
