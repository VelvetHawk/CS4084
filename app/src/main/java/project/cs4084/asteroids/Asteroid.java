package project.cs4084.asteroids;

public class Asteroid
{
	// Path asteroid will take
	public int[] pathX;
	public int[] pathY;

	// Calculation
	private int period; // Period of sine wave
	private int range; // Range of sine wave

	// Current position on path
	private int position;
	private int speed;  // Control for # frames after which asteroid moves

	// Number of shots left to kill asteroid
	private int health;

	private int bitmapX;
	private int bitmapY;

	private boolean atEnd;

	public Asteroid(int maxX, int maxY, int bitmapX, int bitmapY)
	{
		position = 0;
		speed = 0;
		health = 1;
		atEnd = false;

		// Image data
		this.bitmapX = bitmapX;
		this.bitmapY = bitmapY;

		// Path of 40 movements
		pathX = new int[40];
		pathY = new int[40];


		// Random number to decide if linear or wave path
		// TODO: Uncomment this to use both path systems, using only linear for now
		//if ((int)(Math.random() * 2) % 2 == 1) // 50% chance for either pathing
			createLinearPath(maxX, maxY);
		//else
		//	createWavePath();
	}

	public void createLinearPath(int maxX, int maxY)
	{
		boolean top = false;
		boolean bottom = false;
		boolean right = false; // TODO: Unused, maybe remove later
		boolean left = false;

		// Decide starting coords
		switch ((int)(Math.random() * 4))
		{
			case 0: // Left edge, y = 0
				left = true;

				pathY[0] = -15; // Start 15px behind edge
				pathX[0] = (int)(Math.random() * maxX);

				break;

			case 1: // Top edge, x = max
				top = true;

				pathX[0] = maxX + 15; // Start 15px behind edge
				pathY[0] = (int)(Math.random() * maxY);

				break;

			case 2: // Right edge, y = max
				right = true;

				pathY[0] = maxY + 15; // Start 15px behind edge
				pathX[0] = (int)(Math.random() * maxX);

				break;

			case 3: // Bottom edge, x = 0
				bottom = true;

				pathX[0] = -15; // Start 15px behind edge
				pathY[0] = (int)(Math.random() * maxY);

				break;
		}

		/*
			# NOTE #
			For now, all asteroids travel to the opposing side of the screen
			if using a linear path (eg, top --> bottom, left --> right, etc)
		 */

		// Decide ending coords
		if (top)
		{
			pathX[pathX.length-1] = -15;
			pathY[pathX.length-1] = (int)(Math.random() * maxY);
		}
		else if (bottom)
		{
			pathX[pathX.length-1] = maxX + 15;
			pathY[pathX.length-1] = (int)(Math.random() * maxY);
		}
		else if (left)
		{
			pathY[pathX.length-1] = maxY + 15;
			pathX[pathX.length-1] = (int)(Math.random() * maxX);
		}
		else
		{
			pathY[pathX.length-1] = -15;
			pathX[pathX.length-1] = (int)(Math.random() * maxX);
		}

		// Get difference
		int diffX = pathX[pathX.length-1] - pathX[0]; // End - start
		int diffY = pathY[pathX.length-1] - pathY[0]; // End - start

		// Make sure difference has equal steps, using path.length
		diffX = diffX / pathX.length;
		diffY = diffY / pathX.length;

		// Plot path in between
		for (int i = 1; i < pathX.length-1; i++)
		{
			pathX[i] = pathX[i-1] + diffX; // Previous coord +- difference
			pathY[i] = pathY[i-1] + diffY; // Previous coord +- difference
		}
	}

	public void createWavePath()
	{
		period = 60;
		range = Ship.maxX / 2;

		for (int i = 1; i < pathX.length; i++)
		{
			// TODO: For testing purposes, fix later to be a random edge of screen
			pathX[i] = Ship.maxX / 2;
			pathY[i] = Ship.maxY / 2;

			pathX[i] += (Ship.maxY / 60) * i; // Speed of 1/60 of the screen
			pathY[i] += (int)(Math.cos(i * 2 * Math.PI / period) * (range / 2) + (range / 2));
		}
	}

	public void advanceAsteroid()
	{
		speed++;
		if (position == pathX.length-1)
			atEnd = true;
		else if (speed % 6 == 5) // Advance every 5 frames
		{
			speed = 0; // To avoid overflowing int, due to player playing the game for a long time
			position++;
		}
	}

	public int getX()
	{
		return pathX[position];
	}

	public int getY()
	{
		return pathY[position];
	}

	public int getCenterX()
	{
		return pathX[position] + (bitmapX / 2);
	}

	public int getCenterY()
	{
		return pathY[position] + (bitmapY / 2);
	}

	public boolean atEnd()
	{
		return atEnd;
	}

	public void setEnd(boolean end)
	{
		atEnd = end;
	}

	public int getBitmapX()
	{
		return bitmapX;
	}

	public int getBitmapY()
	{
		return bitmapY;
	}
}
