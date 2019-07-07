package project.cs4084.asteroids;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PlayerMiniGame extends GameObjectMiniGame {
    private Bitmap spritesheet;
    private int score;
    private boolean up;
    private boolean playing;
    private AnimationMiniGame animation = new AnimationMiniGame();
    private long startTime;

    public PlayerMiniGame(Bitmap res, int w, int h, int numFrames ) {
        x = 100;
        y = GamePanelMiniGame.HEIGHT / 2;
        dy = 0;
        score = 0;
        height = h;
        width = w;

        // store different sprites for the image of the player
        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;


        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i * width, 0, width, height);
        }

        // pass array into animations class
        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    // press down to make helicopter go up
    public void setUp(boolean b) {
        up = b;
    }
    public void update() {
        // increment score
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        if(elapsed > 100) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        // if up is true and we're pressing
        if(up) {
            dy -= 1;
        } else {
            dy += 1;
        }

        // cap the speed of the player
        if(dy > 5) {
            dy = 5;
        }
        if(dy < -5) {
            dy = -5;
        }

        y += dy * 2;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }

    public int getScore() {
        return score;
    }

    public boolean getPlaying() {
        return playing;
    }

    public void setPlaying(boolean b) {
        playing = b;
    }

    public void resetAcceleration() {
        dy = 0;
    }

    public void resetScore() {
        score = 0;
    }
}
