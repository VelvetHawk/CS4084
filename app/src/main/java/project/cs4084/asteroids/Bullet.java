package project.cs4084.asteroids;

public class Bullet
{
	// Starting coords
	private int startingX;
	private int startingY;

	// Current coords
	private int bulletX;            // Bullet X coord
	private int bulletY;            // Bullet Y coord

	// Ending coords
	private int endingX;
	private int endingY;

	private int bulletStrength;     // How much damage bullet will do
	private int bulletSpeed = 20;    // 20px per frame

	private int rotation;

	private boolean atEnd = false;

	public Bullet(int startingX, int startingY, int endingX, int endingY, int rotation)
	{
		this.startingX = startingX;
		this.startingY = startingY;

		this.bulletX = startingX;
		this.bulletY = startingY;

		this.endingX = endingX;
		this.endingY = endingY;

		this.rotation = rotation;

		bulletStrength = 1; // Default
	}

	public void setBulletStrength(int strength)
	{
		bulletStrength = strength;
	}

	public void advanceBullet() // Moves the bullet onto the next point on the travel path
	{
		int maxX = Ship.maxX;
		int maxY = Ship.maxY;

		// Calculate next step of movement
		double vectorX = endingX - bulletX;
		double vectorY = endingY - bulletY;

		double magnitude = Math.sqrt(Math.pow(vectorX, 2) + Math.pow(vectorY, 2));

		double normalisedX = vectorX / magnitude;
		double normalisedY = vectorY / magnitude;

		bulletX += (int) (bulletSpeed * normalisedX);
		bulletY += (int) (bulletSpeed * normalisedY);

		// Check bullet boundary, 5px of boundary
		if (!(bulletX < Ship.maxX-5 && bulletX > 5) || !(bulletY < Ship.maxY-5 && bulletY > 5))
			atEnd = true;
	}

	public boolean atEnd()
	{
		return atEnd;
	}

	public void setEnd(boolean end)
	{
		atEnd = end;
	}

	public int getBulletX()
	{
		return bulletX;
	}

	public int getBulletY()
	{
		return bulletY;
	}


}
