package project.cs4084.asteroids;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BottomBorderMiniGame extends GameObjectMiniGame{
    private Bitmap image;

    public BottomBorderMiniGame(Bitmap res, int x, int y) {
        height = 200;
        width = 20;

        this.x = x;
        this.y = y;

        dx = GamePanelMiniGame.MOVESPEED;
        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update() {
        x += dx;
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(image, x, y, null);
        } catch(Exception ex) {

        }
    }
}
