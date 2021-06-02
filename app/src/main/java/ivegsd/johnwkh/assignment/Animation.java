package ivegsd.johnwkh.assignment;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Animation {
    private Bitmap sprite; // sprite sheet (assume each frame is horizontal in the sprite sheet)
    private Rect frameRect; // frame rectangle
    private int frameCount; // the number of frames
    private int frameWidth; //average width of each frame
    private int frameHeight; //average height of each frame
    private AnimationDirection animationDirection;

    public Animation(Bitmap _sprite, int _frameCount, AnimationDirection _animationDirection)
    {
        this.sprite = _sprite;
        this.frameCount = _frameCount;
        this.animationDirection = _animationDirection;
        initialize();
    }

    private void initialize()
    {
        switch (animationDirection){
            case Horizontal:
                frameWidth = Math.round(sprite.getWidth() / frameCount);
                frameHeight = sprite.getHeight();
                break;
            case Vertical:
                frameWidth = sprite.getWidth();
                frameHeight = Math.round(sprite.getHeight() / frameCount);
                break;
            case Both:
                frameWidth = Math.round(sprite.getWidth() / frameCount);
                frameHeight = Math.round(sprite.getHeight() / frameCount);
                break;
        }
        frameRect = new Rect(0, 0, frameWidth, frameHeight);
    }

    //getter
    public Bitmap getSprite() { return sprite; }

    public int getFrameCount(){ return  frameCount; }

    public  Rect getFrameRect() { return frameRect; }

    public int getFrameWidth(){ return  frameWidth; }

    public int getFrameHeight(){ return  frameHeight; }
}
