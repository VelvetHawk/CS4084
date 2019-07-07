package project.cs4084.asteroids;

import android.graphics.Matrix;

public class Ship
{
	private int MAX_HP = 3;     // Maximum HP that the ship can have
	private int current_HP;     // Current HP of the ship
	private int rotation = 90;    // The current angle 0 - 360 of the ship, starts at 90
	private boolean collided;

	private int shipX;              // centre X
	private int shipY;              // center y

	public int noseX;              // nose X
	public int noseY;              // nose Y

	private double shipSlope;  // Passed onto the bullet

	private double boostDistance = 5; // 5px per frame, potentially needs to be reduced

	public int leftCornerX;
	public int leftCornerY;

	public int projectedX;
	public int projectedY;

	public static int maxX; // Of screen
	public static int maxY; // Of screen

	private int bitmapX;
	private int bitmapY;

	public boolean boosting = false;

	public Ship()
	{
		current_HP = MAX_HP;
	}

	public Ship(int screenMaxX, int screenMaxY, int bitmapX, int bitmapY)
	{
		current_HP = MAX_HP;
		collided = false;
		System.out.println("Bitmap X: " + bitmapX + "\tBitmap Y: " + bitmapY); // TODO: REMOVE LATER
		System.out.println("Screen X: " + screenMaxX + "\tScreenY: " + screenMaxY); // TODO: remove later
		leftCornerX = screenMaxX / 2;
		leftCornerY = screenMaxY / 2;

		// Calculated from origin, so no need for origin (X, Y) as it is (0, 0)
		// boostDistance = Math.sqrt(Math.pow(maxX, 2) + Math.pow(maxY, 2)) / 100;

		shipX = leftCornerX + (bitmapX/2);
		shipY = leftCornerY + (bitmapY/2);

		System.out.println("Ship X: " + shipX + "\tShip Y: " + shipY); // TODO: remove later

		maxX = screenMaxX;
		maxY = screenMaxY;

		this.bitmapX = bitmapX;
		this.bitmapY = bitmapY;

		noseX = shipX + (bitmapX / 2);
		noseY = shipY;

		projectedX = maxX;
		projectedY = shipY;
	}

	public void takeDamage(int damage)
	{
		current_HP -= damage;
	}

	public void setHP(int HP)
	{
		this.current_HP = HP;
	}

	public int getHP()
	{
		return current_HP;
	}

	public void setRotation(int rotation)
	{
		this.rotation = rotation % 360;
	}

	public void rotate(int rotation)
	{
		this.rotation += rotation;

		Matrix transform = new Matrix();
		// This is to rotate about the Rectangles center

		noseX = shipX + (this.bitmapX / 2);
		noseY = shipY;

		if (rotation > 90)
			transform.setRotate(90 - this.rotation, shipX, shipY);
		else
			transform.setRotate(this.rotation - 90, shipX, shipY);


		// Create new float[] to hold the rotated coordinates
		float[] pts = {noseX, noseY};

		// Use the Matrix to map the points
		transform.mapPoints(pts);

		// NOTE: pts will be changed by transform.mapPoints call
		// after the call, pts will hold the new cooridnates

		// Now, create a new Point from our new coordinates
		noseX = (int) pts[0];
		noseY = (int) pts[1];

		// Ensure rotation is within 0 - 360 range
		if (this.rotation > 360)
			this.rotation = this.rotation % 360;
		else if (this.rotation < 0)
			this.rotation = 360 - this.rotation;
	}

	public void calculateProjectedCoords()
	{
		// Calculate projected X and Y (Intersection with screen edge)
		double d = Math.sqrt(((maxX/2)*(maxX/2)) + ((maxY/2)*(maxY/2))); // 5px added
		double angle =  Math.toRadians(this.rotation);

		double x = Math.sin(angle) * d;
		double y = -Math.cos(angle) * d;

		if(x > maxX/2)
		{
			double clipFraction = ((maxX+10)/2) / x; // amount to shorten the vector
			x *= clipFraction;
			y *= clipFraction;
		}
		else if(x < -maxX/2)
		{
			double clipFraction = ((-maxX-10)/2) / x; // amount to shorten the vector
			x *= clipFraction;
			y *= clipFraction;
		}

		x += maxX/2;
		y += maxY/2;

		projectedX = (int) x;
		projectedY = (int) y;


		// Calculate slope
		if (shipX - noseX != 0)
			shipSlope = (double)(shipY - noseY) / (double)(shipX - noseX);
	}

	public void setSlope(double slope)
	{
		shipSlope = slope;
	}

	public double getSlope()
	{
		return shipSlope;
	}

	public int getRotation()
	{
		return rotation;
	}

	public int getX()
	{
		return shipX;
	}

	public int getY()
	{
		return shipY;
	}

	public void setX(int newX)
	{
		this.shipX = newX;
	}

	public void setY(int newY)
	{
		this.shipY = newY;
	}

	public void boost()
	{
		if (boosting)
		{
			// Calculate next step of movement
			double vectorX = projectedX - shipX;
			double vectorY = projectedY - shipY;

			double magnitude = Math.sqrt(Math.pow(vectorX, 2) + Math.pow(vectorY, 2));

			double normalisedX = vectorX / magnitude;
			double normalisedY = vectorY / magnitude;

			shipX += (int) (boostDistance * normalisedX);
			shipY += (int) (boostDistance * normalisedY);

			leftCornerX += (int) (boostDistance * normalisedX);
			leftCornerY += (int) (boostDistance * normalisedY);

			// Loop ship back around
			if (shipX > maxX)
			{
				shipX -= maxX;
				leftCornerX -= maxX;
			}
			else if (shipX < 0)
			{
				shipX += maxX;
				leftCornerX += maxX;
			}

			if (shipY > maxY)
			{
				shipY -= maxY;
				leftCornerY -= maxY;
			}
			else if (shipY < 0)
			{
				shipY += maxY;
				leftCornerY += maxY;
			}
		}
	}

	public Bullet fire()
	{
		// (int startingX, int startingY, int endingX, int endingY, int rotation)
		return new Bullet(shipX, shipY, projectedX, projectedY, rotation);
	}

	public boolean hasCollided()
	{
		return collided;
	}

	public void setCollided(boolean collided)
	{
		this.collided = collided;
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
