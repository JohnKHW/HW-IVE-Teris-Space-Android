package ivegsd.johnwkh.assignment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

public class Background {
    private Sprite[][] bgSprite = new Sprite[3][3];
    private Bitmap bitmap;
    private float speed, currentSpeed, targetSpeed;
    private Vector2 edge;
    private int direction;
    private final int RIGHT = 1, LEFT = -1, MOVE_COUNT = 20;
    private final String TAG = "Debug.Log";
    private float rotate;
    private int horiMoveCount, currMoveCount, currMoveCountSet;

    public Background(Bitmap bitmap, float speed, Vector2 edge){
        this.bitmap = bitmap;
        this.speed = speed;
        this.edge = edge;
        targetSpeed = speed;
        currentSpeed = 0;
        //Log.d(TAG, "Vector2" + edge.toString());

        for(int i = 0; i < bgSprite.length; i++){
            for(int j = 0; j < bgSprite[i].length; j++){
                bgSprite[i][j] = new Sprite(bitmap,bitmap.getWidth() * j,bitmap.getHeight() * i);
                //Log.d(TAG, "bgSprite[" + i+"]["+j+"] X: " +  bitmap.getWidth() * j + ", Y: "+ bitmap.getHeight() * i);
            }
        }


    }

    public void setDirection(int direction){
        currMoveCountSet = 0;
        currMoveCount = 0;
        horiMoveCount = MOVE_COUNT;
        this.direction = direction;
    }

    public int getDirection(){
        return direction;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public float getSpeed(){
        return speed;
    }

    public void setSpeed(float speed){
        this.targetSpeed = speed;
    }

    public void speedUp(){
        this.targetSpeed = (float)Math.pow(this.speed, 2);
    }
    public void resetSpeed(){
        this.targetSpeed = this.speed;
    }

    public void update(){
        if(currentSpeed!=targetSpeed){
            if(currentSpeed > targetSpeed) currentSpeed --;
            else currentSpeed ++;
        }
        for(int i = 0; i < bgSprite.length; i++){
            for(int j = 0; j < bgSprite[i].length; j++){
                if(currMoveCountSet != horiMoveCount){
                    bgSprite[i][j].setX(Math.round(bgSprite[i][j].getX() +
                            speed * (horiMoveCount - currMoveCountSet) * direction / 2f));

                    currMoveCount ++;
                    if(currMoveCount >=9){
                        currMoveCountSet++;
                        currMoveCount = 0;
                    }

                }
                bgSprite[i][j].setY(Math.round(bgSprite[i][j].getY() + currentSpeed));


                if(bgSprite[i][j].getY() > edge.y*2){
                    bgSprite[i][j].setY(
                            bgSprite[(i+1)%bgSprite.length][j].getY() -
                                    bitmap.getHeight() + (int)currentSpeed);
                }
                if(direction == RIGHT &&
                        bgSprite[i][j].getX() > edge.x*2){
                    bgSprite[i][j].setX(
                            bgSprite[i][(j+1) % bgSprite[i].length].getX() -
                                    bitmap.getWidth() + (int)speed*(horiMoveCount - currMoveCountSet));
                }
                else if(direction == LEFT &&
                        bgSprite[i][j].getX() + edge.x < 0){
                    bgSprite[i][j].setX(
                            bgSprite[i][(j+2) % bgSprite[i].length].getX() +
                                    bitmap.getWidth() - (int)speed*(horiMoveCount - currMoveCountSet));
                }

            }
        }

    }

    public void draw(Canvas canvas){
        //canvas.rotate(rotate,  (edge.x / 2), (edge.y / 2f));
        for(int i = 0; i < bgSprite.length; i++){
            for(int j = 0; j < bgSprite[i].length; j++){
                bgSprite[i][j].draw(canvas);
            }
        }
    }

}
