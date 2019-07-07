package project.cs4084.asteroids;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Joystick extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener
{
	private float centerX;
	private float centerY;
	private float baseRadius;
	private float capRadius;
	private Context context;
	private GamePanel gamePanel;
	// Calculation values
	private int turning; // 0 = no, -1 = left, 1 = right
	// private double previous_slope = 0;
	// private boolean areSlopesSame = true;
	private float previousX = centerX;
	private float previousY = centerY;


	public Joystick(Context context)
	{
		super(context);
		getHolder().addCallback(this);
		setOnTouchListener(this);
		//super.setZOrderOnTop(true);
		//super.setZOrderMediaOverlay(true);
		this.context = context;
		getHolder().setFormat(PixelFormat.TRANSPARENT);
	}

	public Joystick(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		getHolder().addCallback(this);
		setOnTouchListener(this);
		//super.setZOrderOnTop(true);
		//super.setZOrderMediaOverlay(true);
		this.context = context;
		getHolder().setFormat(PixelFormat.TRANSPARENT);
	}

	public Joystick(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		getHolder().addCallback(this);
		setOnTouchListener(this);
		//super.setZOrderOnTop(true);
		//super.setZOrderMediaOverlay(true);
		this.context = context;
		getHolder().setFormat(PixelFormat.TRANSPARENT);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder)
	{
		setupDimensions();
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
	{

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder)
	{

	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent)
	{
		if (view.equals(this))
		{
			if (motionEvent.getAction() != motionEvent.ACTION_UP)
			{
				float displacement = (float) Math.sqrt(Math.pow(motionEvent.getX() - centerX, 2)
						+ Math.pow(motionEvent.getY() - centerY, 2));
				if (displacement < baseRadius)
					drawJoystick(motionEvent.getX(), motionEvent.getY());
				else
				{
					float ratio = baseRadius / displacement;
					float constrainedX = centerX + (motionEvent.getX() - centerX) * ratio;
					float constrainedY = centerY + (motionEvent.getY() - centerY) * ratio;
					drawJoystick(constrainedX, constrainedY);
					setRotation(constrainedX, constrainedY);
				}
			}
			else    // Makes Joystick reset upon user letting go
			{
				drawJoystick(centerX, centerY);
			}
		}
		return true;
	}

	private void setupDimensions()
	{
		centerX = getWidth() / 2;
		centerY = getHeight() / 2;
		baseRadius = Math.min(getWidth(), getHeight()) / 3;
		capRadius = Math.min(getWidth(), getHeight()) / 5;
		drawJoystick(centerX, centerY);
	}

	private void drawJoystick(float newX, float newY)
	{
		if (getHolder().getSurface().isValid())
		{
			Canvas canvas = this.getHolder().lockCanvas();
			Paint colors = new Paint();
			Paint transparent = new Paint();

			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			colors.setStyle(Paint.Style.FILL);
			canvas.drawRect(0, 0, getWidth(), getHeight(), colors);


			colors.setARGB(150, 100, 100, 100);
			canvas.drawCircle(centerX, centerY, baseRadius, colors);
//			Bitmap _bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cirlce_base);
//			Bitmap bmp = Bitmap.createBitmap(_bmp, 0, 0, _bmp.getWidth(), _bmp.getHeight(), new Matrix(), true);

			//canvas.drawBitmap(bmp, getWidth() / 2, getHeight() / 2, colors);

			colors.setARGB(230, 180, 180, 180);
			canvas.drawCircle(newX, newY, capRadius, colors);

			getHolder().unlockCanvasAndPost(canvas);
		}
	}

	private void setRotation(float x2, float y2)
	{
//		if      (previousX >= x2 && previousY >= y2)      System.out.println("Turning Left");
//		else if (previousX >= x2 && previousY <= y2)      System.out.println("Turning Left"); // Formet right A
//		else if (previousX <= x2 && previousY <= y2)      System.out.println("Turning Left"); // Former right B
//		else                                              System.out.println("Turning Left");
//
//		// Record current values
//		previousX = x2;
//		previousY = y2;

//		double slope = (centerY - y2) / (centerX - x2);
//		System.out.println("Slope: " + slope);
//
//		// 0 = no, -1 = left, 1 = right
//		if (slope > previous_slope)  {
//			turning = -1;
//			areSlopesSame = false;
//		}
//		else if (slope < previous_slope) {
//			turning = 1;
//			areSlopesSame = false;
//		}
//		else  {
//			turning = 0;
//			areSlopesSame = true;
//		}

		// previous_slope
		//double slope_2 = (centerY - y2) / (centerX - x2);

		// Calculates the acute angle between the two slopes
		// double angle = Math.toDegrees(Math.atan(Math.abs((previous_slope - slope_2) / (1 + (previous_slope * slope_2)))));

		// Check which direction user is moving hand

		boolean goingRight = false;
		boolean goingUp = false;
		int angle = 0;

		// Detect quadrant
		int quadrant = 0; // 0, 1, 2, 3

		if (x2 >= centerX && y2 >= centerY)         quadrant = 0;
		else if (x2 < centerX && y2 >= centerY)     quadrant = 1;
		else if (x2 < centerX && y2 < centerY)      quadrant = 2;
		else                                        quadrant = 3;

		switch (quadrant)
		{
			case 0: // Top right
				if (x2 >= previousX)    goingRight = true;
				if (y2 >= previousY)    goingUp = true;

				if (goingUp && !goingRight)         angle = 5;
				else if (!goingUp && goingRight)    angle = -5;

				break;

			case 1: // Top left
				if (x2 >= previousX)    goingRight = true;
				if (y2 >= previousY)    goingUp = true;

				if (goingUp && goingRight)         angle = -5;
				else if (!goingUp && !goingRight)    angle = 5;

				break;

			case 2: // Bottom left
				if (x2 >= previousX)    goingRight = true;
				if (y2 >= previousY)    goingUp = true;

				if (goingUp && !goingRight)         angle = -5;
				else if (!goingUp && goingRight)    angle = 5;

				break;

			case 3: // Bottom right
				if (x2 >= previousX)    goingRight = true;
				if (y2 >= previousY)    goingUp = true;

				if (goingUp && goingRight)         angle = 5;
				else if (!goingUp && !goingRight)    angle = -5;

				break;
		}

		gamePanel.getShip().rotate(angle);

		// Record previous values
		// previous_slope = slope_2;
		previousX = x2;
		previousY = y2;
	}

	public int getTurning()
	{
		return turning;
	}

//	public double getPreviousSlope()
//	{
//		return previous_slope;
//	}
//
//	public boolean areSlopesSame()
//	{
//		return areSlopesSame;
//	}

	public void setPanel(GamePanel panel)
	{
		this.gamePanel = panel;
	}
}

