package ivegsd.johnwkh.assignment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class Button extends Sprite {
    private ColorFilter colorFilter;

    public Button(Bitmap bitmap, int x, int y) {
        super(bitmap, x, y);
    }

    @Override
    public void draw(Canvas canvas) {
        if(isTouched()){
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0.1f);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
        }
        else{
            paint.setColorFilter(null);
        }
        super.draw(canvas);
    }
}
