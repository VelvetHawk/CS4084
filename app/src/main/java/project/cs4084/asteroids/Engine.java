package project.cs4084.asteroids;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import java.io.IOException;

public class Engine extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener
{
	private Display display;
	private GamePanel gamePanel;            // The game view
	private DisplayThread displayThread;    // The display thread
	private Joystick joystick;

	private MediaPlayer player_shoot;
	public static MediaPlayer player_explode;
	public MediaPlayer songs;
	private int current_song;

	public static boolean fire; // Limit firing speed


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game_screen);

		display = getWindowManager().getDefaultDisplay();

		joystick = (Joystick) findViewById(R.id.joystick);      // Retrieve from layout
		gamePanel = (GamePanel) findViewById(R.id.game_panel);  // Retrieve from layout

		// Create thread
		displayThread = new DisplayThread(gamePanel.returnSurfaceHolder(), this, joystick);
		displayThread.setEngine(this);

		displayThread.setGamePanel(gamePanel);
		gamePanel.setThread(displayThread);
		gamePanel.setJoystick(joystick);
		gamePanel.setDisplay(display);

		// joystick.setZOrderOnTop(true);
		joystick.setZOrderMediaOverlay(true);
		joystick.getHolder().setFormat(PixelFormat.TRANSPARENT);
		joystick.setPanel(gamePanel);

		// Set up media players
		player_shoot = MediaPlayer.create(this, R.raw.shoot_1);
		// player_shoot.setOnPreparedListener(this);

		player_explode = MediaPlayer.create(this, R.raw.asteroid_explosion);
		// player_explode.setOnPreparedListener(this);

		current_song = (int) (Math.random() * 4);
		switch (current_song)
		{
			case 0:     songs = MediaPlayer.create(this, R.raw.mariachi_madness);   break;
			case 1:     songs = MediaPlayer.create(this, R.raw.back_in_black);      break;
			case 2:     songs = MediaPlayer.create(this, R.raw.feel_good_inc);      break;
			case 3:     songs = MediaPlayer.create(this, R.raw.pokemon_theme);      break;
		}
		songs.setOnPreparedListener(this);

		// Limit firing
		fire = false;
	}

	@Override
	public void onDestroy()
	{
		player_shoot.stop();
		player_shoot.release();

		player_explode.stop();
		player_explode.release();

		songs.stop();
		songs.release();

		super.onDestroy();
	}

	public void fire(View view)
	{
		if (fire)
		{
			player_shoot.start();
			fire = false;
			gamePanel.createBullet(gamePanel.getShip().fire());
		}
	}

	@Override
	public void onPrepared(MediaPlayer player)
	{
		// Start background music
		songs.start();
	}

	public void boost(View view)
	{
		if (gamePanel.getShip().boosting)
			gamePanel.getShip().boosting = false;
		else
		{
			gamePanel.getShip().boosting = true;
			gamePanel.getShip().boost();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		if (mp == songs)
		{
			current_song = (current_song+1) % 4;
			switch (current_song)
			{
				case 0:     songs = MediaPlayer.create(this, R.raw.mariachi_madness);   break;
				case 1:     songs = MediaPlayer.create(this, R.raw.back_in_black);      break;
				case 2:     songs = MediaPlayer.create(this, R.raw.feel_good_inc);      break;
				case 3:     songs = MediaPlayer.create(this, R.raw.pokemon_theme);      break;
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		// Do nothing
	}
}
