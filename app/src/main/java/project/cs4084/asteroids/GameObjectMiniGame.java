package project.cs4084.asteroids;

import android.graphics.Rect;

// Not instantiated. All objects will extend this Class
public abstract class GameObjectMiniGame {

    // all objects in the game will have these attributes
    protected int x;
    protected int y;
    // y vector
    protected int dy;
    // x vector
    protected int dx;
    protected int width;
    protected int height;

    // need to check for collisions

    // get the rectangle space the object takes up
    public Rect getRectangle() {
        return new Rect(x, y, x + height, y + height);
    }

    /*
    SETTERS AND GETTERS
     */

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
