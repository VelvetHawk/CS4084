package project.cs4084.asteroids;


import android.graphics.Canvas;
import android.view.SurfaceHolder;

// Main game loop will run here
public class MainThreadMiniGame extends Thread {

    // cap the FPS to 30
    private int fps = 30;
    private double averageFPS;

    private SurfaceHolder surfaceHolder;

    // reference to gamepanel
    private GamePanelMiniGame gamePanel;
    private boolean running;

    // applies to all instances
    public static Canvas canvas;

    // Constructor
    public MainThreadMiniGame(SurfaceHolder surfaceHolder, GamePanelMiniGame gamePanel) {
        // Call super class of Thread
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    // Override the run method in the Thread Class
    @Override
    public void run() {
        // cap fps to 30
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        // The milliseconds each game loop should take
        long targetTime = 1000/fps;

        while(running) {
            // get start time in nano seconds
            startTime = System.nanoTime();
            canvas = null;

            // Try lock the canvas for pixel editing
            try {
                canvas = this.surfaceHolder.lockCanvas();
                /*
                * synchronized methods enable a simple strategy for preventing thread interference and memory consistency errors:
                * if an object is visible to more than one thread, all reads or writes to that object's variables are done through synchronized methods.
                */
                synchronized (surfaceHolder) {
                    // go through the game loop, update and draw
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception ex) {

            }
            // after update and draw, get time in ms. first bracket gives time in nano seconds for one loop
            timeMillis = (System.nanoTime()-startTime) / 1000000;
            // how long to wait before going through loop again, target time minus time it took
            waitTime = targetTime - timeMillis;

            // Make thread wait that amount of time
            try {
                this.sleep(waitTime);
            } catch(Exception ex) {

            }
            finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            // Through loop once, 1/30th of a second, frameCount incrememts and can calculate avg fps
            if(frameCount == fps) {
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                // reset variables
                frameCount = 0;
                totalTime = 0;
                // print fps to console
                System.out.println(averageFPS);
            }
        }
    }
    public void setRunning(boolean bool) {
        running = bool;
    }
}
