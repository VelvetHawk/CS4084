package project.cs4084.asteroids;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class SmokeMiniGame extends GameObjectMiniGame {

    public int radius;

    // set the x and y in the super object
    public SmokeMiniGame(int x, int y) {
        super.x = x;
        super.y = y;
        radius = 5;
    }

    // update and draw methods
    public void update() {
        // speed of smoke
        x -= 10;
    }

    public void draw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x - radius, y - radius, radius, paint);
        canvas.drawCircle(x - radius + 2, y - radius - 2, radius, paint);
        canvas.drawCircle(x - radius + 4, y - radius + 1, radius, paint);
    }
}
