package ivegsd.johnwkh.assignment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Sprite {
    private int x, y;
    private int spriteWidth, spriteHeight;
    private Bitmap bitmap;
    protected Paint paint;
    private boolean dragable;
    private boolean touched;
    private boolean bounce;
    private int vSpeed, hSpeed;

    public Sprite(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.spriteWidth = bitmap.getWidth();
        this.spriteHeight = bitmap.getHeight();
        this.x = x;
        this.y = y;
        this.vSpeed = 0;
        this.hSpeed = 0;
        this.bounce = false;
        this.dragable = false;
        paint = new Paint();
    }
    public int getX() {	return x; }
    public int getY() {	return y; }
    public int getSpriteWidth() { return spriteWidth; }
    public int getSpriteHeight() { return spriteHeight; }
    public int getHSpeed() { return hSpeed; }
    public int getVSpeed() { return vSpeed; }
    public boolean getDragable() { return dragable; }
    public boolean getBounce() { return bounce; }
    public boolean isTouched() { return touched; }
    public void setX(int x)	{ this.x = x; }
    public void setY(int y)	{ this.y = y; }
    public void setHSpeed(int hSpeed) { this.hSpeed = hSpeed; }
    public void setVSpeed(int vSpeed) { this.vSpeed = vSpeed; }
    public void setDragable(boolean dragable) { this.dragable = dragable; }
    public void setTouched(boolean touched) { this.touched = touched; }
    public void setBounce(boolean bounce) {	this.bounce = bounce; }
    public void move(int move_x, int move_y) {
        this.x += move_x;
        this.y += move_y;
    }
    public boolean handleActionDown(int eventX, int eventY) {
        if (eventX >= (x - spriteWidth/2) && (eventX <= (x + spriteWidth/2))) {
            if (eventY >= (y - spriteHeight/2) && (eventY <= (y + spriteHeight/2))) {
                setTouched(true);
                return true;
            } else {
                setTouched(false);
                return false;
            }
        } else {
            setTouched(false);
            return false;
        }

    }
    public void handleBounce(int left, int top, int right, int bottom) {
        if(bounce) {
            if(x < left + spriteWidth/2 || x > right - spriteWidth/2)
                hSpeed *= -1;
            if(y < top + spriteHeight/2 || y > bottom - spriteHeight/2)
                vSpeed *= -1;
        }
    }
    public void draw(Canvas canvas) {
        // TO BE COMPLETED
        canvas.drawBitmap(bitmap, x-spriteWidth/2,
                y-spriteHeight/2, paint);
    }
    public void update() {
        if(!touched) {
            x += hSpeed;
            y += vSpeed;
        }
    }
    public boolean collideWith(Sprite other) {
        if((Math.abs(this.x - other.x) <
                this.spriteWidth/2 + other.spriteWidth/2)
                && (Math.abs(this.y - other.y) <
                this.spriteHeight/2 + other.spriteHeight/2))
            return true;
        return false;
    }

}
