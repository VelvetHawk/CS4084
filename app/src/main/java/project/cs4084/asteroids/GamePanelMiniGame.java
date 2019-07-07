package project.cs4084.asteroids;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;


// extend subclass of the View class, Dont want all view class for efficiency. this gamepanel is now a surface view
/*
A client may implement this interface to receive information about changes to the surface.
When used with a SurfaceView, the Surface being held is only available between calls to surfaceCreated(SurfaceHolder)
and surfaceDestroyed(SurfaceHolder). The Callback is set with SurfaceHolder.addCallback method.
 */
public class GamePanelMiniGame extends SurfaceView implements SurfaceHolder.Callback {

    // Width and height of background image
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 562;
    // Game wide final for how fast the background moves
    public static final int MOVESPEED = -5;
    // Smoke/Missile start time variable
    private long smokeStartTime;
    private long missileStartTime;
    // Reference to thread
    private MainThreadMiniGame thread;
    // Reference to BackgroundMiniGame
    private BackgroundMiniGame bg;
    // Reference to player
    private PlayerMiniGame player;
    // Arraylist of smoke objects
    private ArrayList<SmokeMiniGame> smoke;
    // Arraylist to hold missiles
    private ArrayList<MissileMiniGame> missile;
    // Arraylist of top borders
    private ArrayList<TopBorderMiniGame> topBorder;
    // Arraylist of bottom borders
    private ArrayList<BottomBorderMiniGame> bottomBorder;
    // Random number
    private Random rand = new Random();
    // border movement
    private boolean topDown = true;
    private boolean botDown = true;
    // increase these as score gets bigger
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean newGameCreated;

    // Increase to slow down difficulty progression, decrease to speed up
    private int progress = 20;

    // context is the entire MiniGame Class
    public GamePanelMiniGame(Context context) {
        // call to SurfaceViews super class, pass context to it
        super(context);

        // Add callback to the SurfaceHolder to intercept events
        getHolder().addCallback(this);

        // Instantiate the thread. Pass this holder into it
        thread = new MainThreadMiniGame(getHolder(), this);

        // Make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    // Override methods in SurfaceView
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Instantiate background with image in drawable
        bg = new BackgroundMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.spacebackground));

        // Instantiate player, each individual frame 65, height 25, 3 frames
        player = new PlayerMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25 , 3);

        // Instantiate smoke
        smoke = new ArrayList<SmokeMiniGame>();

        // Instantiate missiles
        missile = new ArrayList<MissileMiniGame>();

        // Instantiate borders
        topBorder = new ArrayList<TopBorderMiniGame>();
        bottomBorder = new ArrayList<BottomBorderMiniGame>();

        // small amount of smoke to come out
        smokeStartTime = System.nanoTime();

        // start time for missiles
        missileStartTime = System.nanoTime();

        // start thread and game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        // prevent infinite loop with a counter, try 1000 times
        int counter = 0;
        // loop until thread is stopped
        while(retry && counter < 1000) {
            counter++;
            // try to stop thread. caught exception if it doesnt stop. try again
            try {
                thread.setRunning(false);
                thread.join();
                // stop. exit loop after thread successfully joins
                retry = false;
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // want to not play the game until user has touched screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // if you press down
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            // player not playing yet
            if(!player.getPlaying()) {
                player.setPlaying(true);
                player.setUp(true);
            }
            // helicopter will fly up while pressing down
            player.setUp(true);
            return true;
        }
        // has the user released finger from phone
        if(event.getAction() == MotionEvent.ACTION_UP) {
            // no longer going up
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    // Always being called
    public void update() {
        // only update background if player is playing
        if(player.getPlaying()) {
            bg.update();
            player.update();

            // calculate the threshold of height the border can have based on the score
            // max and min border heart are updated, and the border switched direction when either max or min is met
            maxBorderHeight = 30 + player.getScore() / progress;
            // cap max border height so that borders can only take up a a total of 1/2 the screen
            if(maxBorderHeight > HEIGHT / 4) {
                maxBorderHeight = HEIGHT / 4;
            }
            // increase as the score goes up to make the game more difficult
            minBorderHeight = 5 + player.getScore() / progress;

            // check bottom border collision
            for (int i = 0; i < bottomBorder.size(); i++) {
                if(collision(bottomBorder.get(i), player)) {
                    player.setPlaying(false);
                }
            }

            // check top border collision
            for (int i = 0; i < topBorder.size(); i++) {
                if(collision(topBorder.get(i), player)) {
                    player.setPlaying(false);
                }
            }

            // update top border
            this.updateTopBorder();
            // update bottom border
            this.updateBottomBorder();
            // add missiles on timer
            long missilesElapsed = (System.nanoTime() - missileStartTime) / 1000000;

            // Higher the player score, the less delay time in missiles
            if(missilesElapsed > (2000 - player.getScore() / 4)) {
                // send the first missile down the middle
                if (missile.size() == 0) {
                    missile.add(new MissileMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.
                            missile), WIDTH + 10, HEIGHT / 2, 45, 15, player.getScore(), 13));
                } else {
                    // after the first missile, randomize the spawn of the Y position
                    missile.add(new MissileMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.
                            missile), WIDTH + 10, (int)(rand.nextDouble() * (HEIGHT - (maxBorderHeight * 2))+ maxBorderHeight), 45, 15, player.getScore(), 13));

                }
                // time reset
                missileStartTime = System.nanoTime();
            }

            // update the missiles after theyve been created
            // loop through every missile and see if it is in collision
            // collision method will compare two objects to see if collided
            for (int i = 0; i < missile.size(); i++) {
                missile.get(i).update();
                if(collision(missile.get(i), player)) {
                    // remove missile, stop playing and break
                    missile.remove(i);
                    player.setPlaying(false);
                    break;
                }
                // if missile is off the screen, remove it
                if(missile.get(i).getX() <- 100) {
                    missile.remove(i);
                    break;
                }
            }

            // add the smoke to the game, time now to smokestarttime started
            long elapsed = (System.nanoTime() - smokeStartTime) / 1000000;
            // delay for smoke
            if(elapsed > 120) {
                smoke.add(new SmokeMiniGame(player.getX(), player.getY() + 10));
                // reset
                smokeStartTime = System.nanoTime();
            }
            // remove smoke when off the screen
            for (int i = 0; i < smoke.size(); i++) {
                smoke.get(i).update();
                if(smoke.get(i).getX() < -10) {
                    smoke.remove(i);
                }
            }
        } else { // player collided
            newGameCreated = false;
            if(!newGameCreated) {
                newGame();
            }
        }
    }

    // return true if collision between objects
    public boolean collision(GameObjectMiniGame x, GameObjectMiniGame y) {
        if(Rect.intersects(x.getRectangle(), y.getRectangle())) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        // scaling with getWidth of screen
        final float scaleFactorX = (float)getWidth() / WIDTH;
        final float scaleFactorY = (float)getHeight() / HEIGHT;
        // create saved state of canvase before being scaled
        if(canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            // draw the player
            player.draw(canvas);
            // loop to iterate through smoke to draw the smoke
            for (SmokeMiniGame sp: smoke) {
                sp.draw(canvas);
            }
            // loop to iterate through missiles to draw the missiles
            for (MissileMiniGame m: missile) {
                m.draw(canvas);
            }
            // after scaling and drawing background go back to saved state otherwise it keeps getting bigger
            canvas.restoreToCount(savedState);

            // draw top border
            for (TopBorderMiniGame tb: topBorder) {
                tb.draw(canvas);
            }
            // draw bottom border
            for (BottomBorderMiniGame bb: bottomBorder) {
                bb.draw(canvas);
            }
        }
    }

    // create borders
    public void updateTopBorder() {
        // up until max border height and down until min border height
        // every 50 points, insert randomly placed top blocks that break the pattern
        if(player.getScore() % 50 == 0) {

            topBorder.add(new TopBorderMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.rockformation
            ), topBorder.get(topBorder.size() - 1).getX() + 20, 0, (int)((rand.nextDouble() * (maxBorderHeight)) + 1)));
        }
        for (int i = 0; i < topBorder.size(); i++) {
            topBorder.get(i).update();
            // if its off the map
            if(topBorder.get(i).getX() < -20) {
                topBorder.remove(i);
                // remove element of arraylist, replace it by adding a new one
                // calculate top down which determines the direction the border is moving
                if(topBorder.get(topBorder.size() - 1).getHeight() >= maxBorderHeight) {
                    topDown = false;
                }
                // get last element in list, last border
                if(topBorder.get(topBorder.size() - 1).getHeight() <= minBorderHeight) {
                    topDown = true;
                }
                // new border will have larger height else smaller height
                if(topDown) {
                    topBorder.add(new TopBorderMiniGame(BitmapFactory.decodeResource(getResources(),
                            R.drawable.rockformation),topBorder.get(topBorder.size() - 1).getX() + 20,
                            0, topBorder.get(topBorder.size() - 1).getHeight() + 1));
                } else {
                    topBorder.add(new TopBorderMiniGame(BitmapFactory.decodeResource(getResources(),
                            R.drawable.rockformation),topBorder.get(topBorder.size() - 1).getX() + 20,
                            0, topBorder.get(topBorder.size() - 1).getHeight() - 1));
                }
            }
        }

    }
    public void updateBottomBorder() {
        // every 40 points, insert randomly placed bottom blocks that break pattern
        if(player.getScore() % 40 == 0) {
            bottomBorder.add(new BottomBorderMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.rockformation),
                    bottomBorder.get(bottomBorder.size() - 1).getX() + 20, (int)((rand.nextDouble()
                    * maxBorderHeight) + (HEIGHT - maxBorderHeight))));
        }
        for (int i = 0; i < bottomBorder.size(); i++) {
            bottomBorder.get(i).update();
            // if border is moving off screen, remove it and add a corresponding new one
            if(bottomBorder.get(i).getX() < -20) {
                bottomBorder.remove(i);

                // determine if border will be moving up or down
                if (bottomBorder.get(bottomBorder.size() - 1).getY() <= HEIGHT - maxBorderHeight) {
                    botDown = true;
                }
                // get last element in list, last border
                if (bottomBorder.get(bottomBorder.size() - 1).getY() >= HEIGHT - minBorderHeight) {
                    botDown = false;
                }

                // adding new bars
                if (botDown) {
                    bottomBorder.add(new BottomBorderMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.rockformation),
                            bottomBorder.get(bottomBorder.size() - 1).getX() + 20, bottomBorder.get(bottomBorder.size() - 1).getY() + 1));
                } else {
                    bottomBorder.add(new BottomBorderMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.rockformation),
                            bottomBorder.get(bottomBorder.size() - 1).getX() + 20, bottomBorder.get(bottomBorder.size() - 1).getY() - 1));
                }
            }
        }
    }
    // called when player dies and resets game
    public void newGame() {
        bottomBorder.clear();
        topBorder.clear();
        missile.clear();
        smoke.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetAcceleration();
        player.resetScore();
        player.setY(HEIGHT / 2);

        // create the borders
        // create initial border until width 40 off the screen
        for (int i = 0; i*20 < WIDTH + 40; i++) {
            // first border created
            if(i == 0) {
                topBorder.add(new TopBorderMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.rockformation),
                        i* 20, 0, 10));
            } else {
                topBorder.add(new TopBorderMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.rockformation),
                        i* 20, 0, topBorder.get(i - 1).getHeight() + 1));
            }
        }
        for (int i = 0; i*20 < WIDTH + 40; i++) {
            if(i == 0) {
                bottomBorder.add(new BottomBorderMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.rockformation),
                        i * 20, HEIGHT - minBorderHeight));
            } else {
                bottomBorder.add(new BottomBorderMiniGame(BitmapFactory.decodeResource(getResources(), R.drawable.rockformation),
                        i * 20, bottomBorder.get(i - 1).getY() - 1));
            }
        }
    }
}
