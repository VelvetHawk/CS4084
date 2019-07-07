package project.cs4084.asteroids;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class MissileMiniGame extends GameObjectMiniGame{

    private int score;
    private int speed;
    private Random rand = new Random();
    private AnimationMiniGame animation = new AnimationMiniGame();
    private Bitmap spritesheet;

    public MissileMiniGame(Bitmap res, int x, int y, int w, int h, int s, int numFrames) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;

        speed = 7 + (int) (rand.nextDouble() * score / 30);

        // cap the missile speed
        if (speed > 40) {
            speed = 40;
        }

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i *height, width, height);
        }

        // send the array into the animation class
        animation.setFrames(image);
        // if the missile is faster the delay is less then the missile turns faster
        animation.setDelay(100 - speed);
    }

    // update and draw methods
    public void update() {
        x -= speed;
        animation.update();
    }
    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        } catch (Exception ex) {

        }
    }

    @Override
    public int getWidth() {
        // offset the tail of player so it wont explode
        return width - 10;
    }
}
