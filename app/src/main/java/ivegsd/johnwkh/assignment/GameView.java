package ivegsd.johnwkh.assignment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

public class GameView extends View {
    private Point gameboardSize;

    public GameView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        gameboardSize = GetGameboardSize(canvas);

        int startPosX = (canvas.getWidth() - gameboardSize.x) / 2;
        int startPosY = canvas.getHeight() - gameboardSize.y;

        //Draw the background
        Paint bg_paint = new Paint();
        bg_paint.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(0, 0, gameboardSize.x, gameboardSize.y, bg_paint);

        //Draw gridlines (Assume the screen is Portrait
        Paint grid_paint = new Paint();
        grid_paint.setColor(getResources().getColor(R.color.gridline));
        grid_paint.setStrokeWidth(5);
        //canvas.drawRect(0, 0, gameboardSize.x, gameboardSize.y, grid_paint);

        Paint oddBoard_paint = new Paint();
        oddBoard_paint.setColor(getResources().getColor(R.color.oddBoard));
        Paint evenBoard_paint = new Paint();
        evenBoard_paint.setColor(getResources().getColor(R.color.evenBoard));


        int cellWidth = gameboardSize.x/10;
        int cellHeight = gameboardSize.y/20;

        for(int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                Paint drawPaint = oddBoard_paint;
                if(i%2==0 &&j%2==0 || i%2!=0 &&j%2!=0) drawPaint = evenBoard_paint;

                canvas.drawRect(i*cellWidth+startPosX, j*cellHeight+startPosY,
                        (i+1)*cellWidth+startPosX, (j+1)*cellHeight+startPosY, drawPaint);
            }
        }

        //Horizontal
        for(int i=1; i<20; i++) {
            canvas.drawLine(startPosX, i * cellHeight+startPosY,
                    gameboardSize.x + startPosX, i * cellHeight+startPosY, grid_paint);
        }
        //Vertical
        for(int i=1; i<10; i++){
            canvas.drawLine(i * cellWidth + startPosX, startPosY,
                    i * cellWidth+startPosX, gameboardSize.y + startPosY, grid_paint);
        }
    }

    private Point GetGameboardSize(Canvas canvas){
        if(getWidth() * 2.5 > canvas.getHeight()){
            return  new Point((int)(canvas.getHeight() / 2.5), (int)(canvas.getHeight() / 2.5 * 2));
        }
        else{
            return  new Point(getWidth(),getWidth() * 2);
        }
    }
}
