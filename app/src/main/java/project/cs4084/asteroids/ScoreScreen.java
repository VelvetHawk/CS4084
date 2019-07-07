package project.cs4084.asteroids;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ScoreScreen extends AppCompatActivity
{
	private static int asteroids_hit;
	private static int bullets_fired;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_screen);
		LoginActivity.viewScores = false;
	}

	public void playAgain(View v)
	{
		startActivity(new Intent(getBaseContext(), Engine.class));
	}

	public void home(View v)
	{
		startActivity(new Intent(getBaseContext(), LoginActivity.class));
	}

	public static void setScoreData(int asteroids, int bullets)
	{
		asteroids_hit = asteroids;
		bullets_fired = bullets;
	}
}
