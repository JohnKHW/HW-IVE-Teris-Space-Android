package ivegsd.johnwkh.assignment;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private boolean running;
    private SurfaceHolder surfaceHolder;
    private MainGamePanel gamePanel;
    private long tickCount=0;

    public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Canvas canvas;
        this.gamePanel.initGame();  // Called once before enter game loop
        while (running) {
            canvas = null;
            try {
                tickCount++;
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    //Game Loop
                    this.gamePanel.update();
                    this.gamePanel.render(canvas);
                    sleep(5);
                }
            } catch(Exception ex) {

            } finally {
                if(canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

}
