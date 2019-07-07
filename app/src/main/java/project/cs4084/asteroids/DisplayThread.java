package project.cs4084.asteroids;

import android.content.Context;

import android.graphics.Canvas;

import android.view.SurfaceHolder;

public class DisplayThread extends Thread
{
	private boolean running;                // Control for running state of thread
	private Canvas canvas;                  // Used for drawing to
	private int FPS = 30;                   // Maximum FPS for game
	private SurfaceHolder surfaceHolder;
	private Context context;
	private GamePanel gamePanel;
	private Joystick joystick;
	private Engine engine;

	/**
	 * Main constructor
	 */
	public DisplayThread(SurfaceHolder surfaceHolder, Context context, Joystick joystick)
	{
		super();
		this.surfaceHolder = surfaceHolder;
		this.context = context;
		// Start thread
		running = true;
		this.joystick = joystick;
	}

	/**
	 * Main body of thread
	 */
	@Override
	public void run()
	{
		// Frame values
		long startTime;
		long timeMillis;
		long waitTime;
		long totalTime = 0;
		int frameCount = 0;
		long targetTime = 1000 / FPS;

		// Average FPS
		double averageFPS;

		// Game Loop
		while (running)
		{
			startTime = System.nanoTime();

			// Main body
			canvas = gamePanel.returnSurfaceHolder().lockCanvas();
			synchronized (gamePanel.returnSurfaceHolder())
			{
				try
				{
					this.gamePanel.update(canvas);
					this.gamePanel.draw(canvas);

					if (canvas != null)
						gamePanel.returnSurfaceHolder().unlockCanvasAndPost(canvas);
				}
				catch (Exception e)
				{
					setRunning(false);
				}
			}

			// Subtract excess time from wait time
			timeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - timeMillis;

			// Delay time
			try
			{
				this.sleep(waitTime);
			}
			catch(Exception e)
			{
				// Do nothing
			}

			// Add execution of this frame and increment frame counter
			totalTime += System.nanoTime() - startTime;
			frameCount++;

			// On the last frame, print information
			if (frameCount == FPS)
			{
				averageFPS = 1000 / ((totalTime/frameCount) / 1000000);
				frameCount = 0;
				totalTime = 0;
				System.out.println(averageFPS); // Remove later
			}
		}
	}

	/**
	 * Control for the thread to run or not
	 * @param toRun Boolean value to signify if the thread should keep running or stop
	 */
	public void setRunning(boolean toRun)
	{
		running = toRun;
	}

	public void setGamePanel(GamePanel panel)
	{
		this.gamePanel = panel;
	}

	public void setEngine(Engine engine)
	{
		this.engine = engine;
	}
}
