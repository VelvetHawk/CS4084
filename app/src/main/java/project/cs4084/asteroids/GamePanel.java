package project.cs4084.asteroids;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
	private DisplayThread thread;
	private Joystick joystick;
	private Context context;
	private Ship ship;
	private ArrayList<Bullet> bullets = new ArrayList<>();
	private ArrayList<Asteroid> asteroids = new ArrayList<>();
	private Display display;
	private boolean surfaceReady;

	// Minimum time before asteroid spawns
	private final int MIN_TIME = 45; // 45 frames, approx 1.5 seconds // TODO: Reevaluate this, might be too fast
	private int fire_time = 0;
	private int current_time = 0; // Check if minimum time has elapsed

	// Score data
	private int bullets_fired;
	private int asteroids_destroyed;


	public boolean stop = false; // TODO: REMOVE LATER

	public GamePanel(Context context)
	{
		super(context);
		this.context = context;
		getHolder().addCallback(this);
		setFocusable(false);
		//this.setZOrderOnTop(false);
	}

	public GamePanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		getHolder().addCallback(this);
		this.context = context;
		setFocusable(false);
		//this.setZOrderOnTop(false);
	}

	public GamePanel(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		getHolder().addCallback(this);
		this.context = context;
		setFocusable(false);
		//this.setZOrderOnTop(false);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		// Do nothing
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		boolean ended = false;
		while (!ended)
		{
			try
			{
				thread.setRunning(false);
				thread.join();
			}
			catch (Exception e)
			{
				// Do nothing
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		surfaceReady = true;

		if (thread != null)
		{
			thread.setRunning(true);
			thread.start();

			Point displaySize = new Point();
			display.getSize(displaySize);

			Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.triangle);

			ship = new Ship(displaySize.x, displaySize.y, bmp.getWidth(), bmp.getHeight());

			// TODO: Decide if we want asteroids on spawn?
			createAsteroid();
			createAsteroid();
			createAsteroid();
			createAsteroid();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return super.onTouchEvent(event);
	}

	public void update(Canvas canvas)
	{
		if (surfaceReady)
		{
			// Check to spawn new asteroids
			if (current_time >= MIN_TIME) // Minimum time has elapsed
			{
				if (((int)(Math.random() * 3)) == 2) // 33.3% chance each frame to spawn
				{
					createAsteroid();
					current_time = 0;
				}
				else
					current_time++; // May be used to later check average spawn of asteroids
			}
			else
				current_time++;

			ship.calculateProjectedCoords();
			ship.boost(); // If boost is held down, move ship

			// Check for allowing the user to fire again
			if (fire_time == 18) // User can only fire once every 18 frames
			{
				fire_time = 0;
				Engine.fire = true;
			}
			else
				fire_time++;

			// Move bullets
			for (int i = 0; i < bullets.size(); i++)
			{
				bullets.get(i).advanceBullet();
				if (bullets.get(i).atEnd())
				{
					bullets.remove(i);
					i--; // MAke sure not to skip index
				}
			}

			// Move asteroids
			for (int i = 0; i < asteroids.size(); i++)
			{
				asteroids.get(i).advanceAsteroid();
				if (asteroids.get(i).atEnd())
				{
					asteroids.remove(i);
					i--; // MAke sure not to skip index
				}
			}

			// Check collisions (bullet to asteroid)
			for (int i = 0; i < bullets.size(); i++)
			{
				for (int j = 0; j < asteroids.size(); j++)
				{
					// Bullets have radius of 5px by default

					// Asteroids have radius of either X or Y divided by 2
					int dx = bullets.get(i).getBulletX() - asteroids.get(j).getCenterX();
					int dy = bullets.get(i).getBulletY() - asteroids.get(j).getCenterY();

					int distance = (int) Math.sqrt(dx * dx + dy * dy);

					if (distance < 5 + (asteroids.get(j).getBitmapX() / 2))
					{
						// If explosion sound is playing, restart it
						//if (!Engine.player_explode.isPlaying())
						//	Engine.player_explode.stop();
						Engine.player_explode.start();

						// Delete bullet
						bullets.get(i).setEnd(true);
						// Delete asteroid
						asteroids.get(j).setEnd(true);

						asteroids_destroyed++;
					}
				}
			}

			// Check collision (ship to asteroids) - Decreasing border by 5px
			int lowestX = ship.leftCornerX +5;
			int lowestY = ship.leftCornerY +5;

			int highestX = ship.leftCornerX + ship.getBitmapX() -5;
			int highestY = ship.leftCornerY + ship.getBitmapY() -5;

			int closestX;
			int closestY;

			for (int i = 0; i < asteroids.size() && !ship.hasCollided(); i++)
			{
				if (!asteroids.get(i).atEnd())
				{
					// Find closest X coord - SAdjustment of 7px
					if (asteroids.get(i).getCenterX() < lowestX +7)
						closestX = lowestX +7;
					else if (asteroids.get(i).getCenterX()  > highestX -7)
						closestX = highestX -7;
					else
						closestX = asteroids.get(i).getCenterX();

					// Find closest Y coord
					if (asteroids.get(i).getCenterY() < lowestY +7)
						closestY = lowestY +7;
					else if (asteroids.get(i).getCenterY() > highestY -7)
						closestY = highestY -7;
					else
						closestY = asteroids.get(i).getCenterY();

					// Check overlap
					int a = Math.abs(asteroids.get(i).getCenterX() - closestX);
					int b = Math.abs(asteroids.get(i).getCenterY() - closestY);

					double distance = (int) Math.sqrt((a * a) + (b * b));

					// TODO: Check if the 0.9 is too much or little
					if (distance < (asteroids.get(i).getBitmapX() / 2) * 0.8) // Collision, 80% of half width
						ship.setCollided(true);
				}
			}

			if (ship.hasCollided())
			{
				ScoreScreen.setScoreData(asteroids_destroyed, bullets_fired);
				LoginActivity.viewScores = true;
				System.exit(0);
			}

		}
	}

	public void draw(Canvas canvas)
	{
		try
		{
			if (getHolder().getSurface().isValid())
			{
				// Create ship
				Matrix matrix = new Matrix();
				matrix.preRotate(ship.getRotation());

				Bitmap _bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.triangle);
				// Rotated version
				Bitmap bmp = Bitmap.createBitmap(_bmp, 0, 0, _bmp.getWidth(), _bmp.getHeight(), matrix, true);


				Bitmap rock_a = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid_a);
				Matrix m = new Matrix();
				m.preRotate(90);
				Bitmap bmp_2 = Bitmap.createBitmap(rock_a, 0, 0, rock_a.getWidth(), rock_a.getHeight(), m, true);

				Paint colours = new Paint();

				// Dark background
				colours.setARGB(255, 50, 50, 50);
				colours.setStyle(Paint.Style.FILL);
				canvas.drawRect(0, 0, getWidth(), getHeight(), colours);

				// Draw bullets - Red
				colours.setARGB(255, 0, 255, 255);
				for (int i = 0; i < bullets.size(); i++)
					canvas.drawCircle(bullets.get(i).getBulletX(), bullets.get(i).getBulletY(), 5, colours);

				// Draw asteroid
				colours.setARGB(255, 255, 255, 255);
				for (int i = 0; i < asteroids.size(); i++)
				{
					canvas.drawBitmap(bmp_2, asteroids.get(i).getX(), asteroids.get(i).getY(), colours);
				}

				canvas.drawBitmap(bmp, ship.leftCornerX, ship.leftCornerY, colours);

			}
		}
		catch (Exception e)
		{
			// Do nothing
			e.printStackTrace(); // Remove later
		}
	}

	public void setThread(DisplayThread thread)
	{
		this.thread = thread;
	}

	public SurfaceHolder returnSurfaceHolder()
	{
		return this.getHolder();
	}

	public void setJoystick(Joystick joystick)
	{
		this.joystick = joystick;
	}

	public Ship getShip()
	{
		return ship;
	}

	public void createBullet(Bullet bullet)
	{
		bullets.add(bullet);
		bullets_fired++;
	}

	public void createAsteroid()
	{
		// TODO: Change this to later use the more than 1 bitmap type
		Bitmap asteroid = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid_a);
		// Pass information about the image being used as well
		asteroids.add(new Asteroid(Ship.maxX, Ship.maxY, asteroid.getWidth(), asteroid.getHeight()));
	}

	public void setDisplay(Display display)
	{
		this.display = display;
	}

	public int getAsteroidsDestroyed()
	{
		return asteroids_destroyed;
	}

	public int getBulletsFired()
	{
		return bullets_fired;
	}
}
