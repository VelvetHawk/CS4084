package project.cs4084.asteroids;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BackgroundMiniGame {

    private Bitmap image;
    // background coords
    private int x, y, dx;

    // background will be image passed into this
    public BackgroundMiniGame(Bitmap res) {
        image = res;
        dx = GamePanelMiniGame.MOVESPEED;
    }

    public void update() {
        // Get background to scroll
        x += dx;
        // Need new background when its off screen
        if(x < -GamePanelMiniGame.WIDTH) {
            x = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
        // need to draw second image infront so it appears to scroll naturally
        if(x < 0) {
            canvas.drawBitmap(image, x + GamePanelMiniGame.WIDTH, y, null);
        }
    }
}
