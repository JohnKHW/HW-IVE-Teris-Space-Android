package ivegsd.johnwkh.assignment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private ivegsd.johnwkh.assignment.MainThread thread;

    private Paint paint;
    private Point cellSize, gameboardSize, startPos;

    private final float IMAGE_SCALE = 3f;
    private Button[] buttons = new Button[6];
    private Button btnRotClock, btnRotAntiClock, btnMovLeft, btnMovRight,
            btnAccelerate, btnPause, btnResume, btnBack;

    private Bitmap imgBack, imgExplosion, blockBit, imgRotBit_Clock, imgRotBit_AntiClock,
            imgMovBit_Left, imgMovBit_Right, imgAccelerate, imgPause, imgResume, imgBackground;

    private Background background;

    private SoundPool soundPool;
    private MediaPlayer bgSound, loseSound;
    private int btnSound, collideSound, lvUpSound;
    private final float bgSoundVolume = 0.5f, btnSoundVolume = 0.7f,
            collideSoundVolume = 1, loseSoundVolume = 1, lvUpSoundVolume = 1;

    private final String TAG = "Debug.Log";
    private final int CLOCKWISE = 1, ANTI_CLOCKWISE = 3;
    private final int MOVE_LEFT = -1, MOVE_RIGHT = 1;
    private final int BOARD_HEIGHT = 20;
    private final int BOARD_WIDTH = 10;
    private final int BLOCK_LENGTH = 4;
    private final int BLOCK_SHAPE[][] ={
            {1,3,5,7}, // I
            {0,2,3,5}, // Z
            {1,3,2,4}, // S
            {1,3,2,5}, // T
            {0,1,3,5}, // L
            {1,3,5,4}, // J
            {0,1,2,3}, // O
    };

    private int field[][] = new int[BOARD_HEIGHT][BOARD_WIDTH];
    private Point[] dropBlock = new Point[BLOCK_LENGTH];
    private Point[] tempBlock = new Point[BLOCK_LENGTH];
    private List<Point[]> futureBlock = new ArrayList<>(4);
    private List<Integer> colorNum = new ArrayList<>(5);

   // private int colorNum = 0;

    private Animation blockTiles, explosion;
    private Rect tilesRect, exploRect;
    private int exploRectCount;

    private int nextUpdateTime = 1000;
    private final int SPEEDUP_TIME = 5;
    private final int BASIC_SCORE = 10;
    private int nextScore = 30;

    private long updateTime = nextUpdateTime;
    private long elapsedTime, lastTime;
    private int footStep;
    private boolean isGameOver;
    private int score = 0;
    private int currLevel = 1;
    private boolean isPause;

    public MainGamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);

        paint = new Paint();
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new ivegsd.johnwkh.assignment.MainThread(getHolder(), this);

        //TO BE COMPLETED
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            try {
                //TO BE COMPLETED
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if(!isPause && !isGameOver){
                for(int i = 0; i < buttons.length; i++){
                    buttons[i].handleActionDown((int)event.getX(), (int)event.getY());
                }
            }
            else if(!isGameOver) {
                btnResume.handleActionDown((int)event.getX(), (int)event.getY());
                btnBack.handleActionDown((int)event.getX(), (int)event.getY());
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            if(btnMovLeft.isTouched()){
                horizontalMove(MOVE_LEFT);
                background.setDirection(MOVE_LEFT);
                soundPool.play(btnSound, btnSoundVolume, btnSoundVolume, 1, 0, 1);
            }
            else if(btnMovRight.isTouched()){
                horizontalMove(MOVE_RIGHT);
                background.setDirection(MOVE_RIGHT);
                soundPool.play(btnSound, btnSoundVolume, btnSoundVolume, 1, 0, 1);
            }
            else if(btnRotClock.isTouched()){
                rotate(CLOCKWISE);
                soundPool.play(btnSound, btnSoundVolume, btnSoundVolume, 1, 0, 1);
            }
            else if(btnRotAntiClock.isTouched()){
                rotate(ANTI_CLOCKWISE);
                soundPool.play(btnSound, btnSoundVolume, btnSoundVolume, 1, 0, 1);
            }
            else if(btnAccelerate.isTouched()){
                updateTime = SPEEDUP_TIME;
                background.speedUp();
                background.setDirection(0);
                soundPool.play(btnSound, btnSoundVolume, btnSoundVolume, 1, 0, 1);
            }
            else if(btnPause.isTouched()){
                isPause = true;
                soundPool.play(btnSound, btnSoundVolume, btnSoundVolume, 1, 0, 1);
                try{
                    bgSound.pause();
                }
                catch(Exception e){
                    Log.w(TAG, e    );
                }
            }
            else if(btnResume.isTouched()){
                isPause = false;
                soundPool.play(btnSound, btnSoundVolume, btnSoundVolume, 1, 0, 1);
                bgSound.seekTo(bgSound.getCurrentPosition());
                bgSound.start();
                btnResume.setTouched(false);
            }
            else if(btnBack.isTouched()){
                bgSound.stop();
                Intent data = new Intent();
                data.putExtra("Score", 0);
                ((Activity)getContext()).setResult(Activity.RESULT_OK, data);
                ((Activity)getContext()).finish();
            }
            else if(isGameOver){
                Intent data = new Intent();
                data.putExtra("Score", score);
                ((Activity)getContext()).setResult(Activity.RESULT_OK, data);
                ((Activity)getContext()).finish();
                loseSound.stop();
            }
            for(int i = 0; i < buttons.length; i++){
                buttons[i].setTouched(false);
            }

        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE){
            for(int i = 0; i < buttons.length; i++){
                if(buttons[i].isTouched()) buttons[i].handleActionDown((int)event.getX(), (int)event.getY());
            }
            if(btnResume.isTouched()) btnResume.handleActionDown((int)event.getX(), (int)event.getY());
        }
        return true;
    }

    protected void initGame() {
        gameboardSize = GetGameboardSize(getWidth(), getHeight()).toPoint();
        cellSize = new Vector2(gameboardSize.x/ BOARD_WIDTH, gameboardSize.y/ BOARD_HEIGHT).toPoint();
        startPos = new Vector2((getWidth()-gameboardSize.x)/2f, getHeight()-gameboardSize.y).toPoint();

        loadImgContent();
        loadSoundContent();
        loadBtnContent();
        loadBlockContent();

        bgSound.start();
        background = new Background(imgBackground, 5, new Vector2(getWidth(), getHeight()));

        lastTime = System.currentTimeMillis();
    }

    private void loadBlockContent(){
        for(int i = 0; i < BLOCK_LENGTH; i++){
            dropBlock[i] = new Point();
            tempBlock[i] = new Point();
        }
        randomBlock();
        dropBlock = futureBlock.get(0);
        futureBlock.remove(0);
        for(int i = 0; i < 4; i++){
            randomBlock();
        }
    }
    private void loadBtnContent(){
        btnMovLeft = new Button(imgMovBit_Left,
                (int)(cellSize.x * IMAGE_SCALE /2), (int)(cellSize.x * IMAGE_SCALE /2));

        btnMovRight = new Button(imgMovBit_Right,
                (int)(cellSize.x * IMAGE_SCALE * 1.5f), (int)(cellSize.x * IMAGE_SCALE /2));

        btnAccelerate = new Button(imgAccelerate,
                (int)(cellSize.x * IMAGE_SCALE * 1f), (int)(cellSize.x * IMAGE_SCALE * 1.25f));

        btnRotClock = new Button(imgRotBit_Clock,
                getWidth() - (int)(cellSize.x * IMAGE_SCALE /2), (int)(cellSize.x * IMAGE_SCALE /2));
        btnRotAntiClock = new Button(imgRotBit_AntiClock,
                getWidth() - (int)(cellSize.x * IMAGE_SCALE * 1.5f), (int)(cellSize.x * IMAGE_SCALE /2));
        btnPause = new Button(imgPause,
                getWidth() - (int)(cellSize.x * IMAGE_SCALE * 1), (int)(cellSize.x * IMAGE_SCALE * 1.25f));

        btnResume = new Button(imgResume,
                getWidth()/2, (int)(getHeight()/2f));
        btnBack = new Button(imgBack,
                getWidth()/2, (int)(getHeight() - imgBack.getHeight()));

        buttons[0] = btnMovRight;
        buttons[1] = btnMovLeft;
        buttons[2] = btnRotAntiClock;
        buttons[3] = btnRotClock;
        buttons[4] = btnAccelerate;
        buttons[5] = btnPause;
    }
    //load all sound
    private void loadSoundContent(){
        bgSound = MediaPlayer.create(getContext(), R.raw.bg_sound);
        bgSound.setLooping(true);
        bgSound.setVolume(bgSoundVolume, bgSoundVolume);

        loseSound = MediaPlayer.create(getContext(), R.raw.lose);
        loseSound.setVolume(loseSoundVolume, loseSoundVolume);

        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 0);
        btnSound = soundPool.load(getContext(), R.raw.btn_sound, 0);
        collideSound = soundPool.load(getContext(), R.raw.collide_sound, 0);
        lvUpSound = soundPool.load(getContext(), R.raw.levelup, 0);
    }
    private void loadImgContent(){
        blockBit = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tiles),
                Math.round(cellSize.x * 8), Math.round(cellSize.y),false);

        blockTiles = new Animation(blockBit, 8, AnimationDirection.Horizontal);
        tilesRect = blockTiles.getFrameRect();

        imgExplosion = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img_explosion),
                Math.round(cellSize.x * 8 * 6), Math.round(cellSize.y * 8 * 6),false);

        explosion = new Animation(imgExplosion, 6, AnimationDirection.Both);
        exploRect = explosion.getFrameRect();

        imgRotBit_Clock = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_clock),
                (int)(cellSize.x * IMAGE_SCALE), (int)(cellSize.y * IMAGE_SCALE),false);

        imgRotBit_AntiClock = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_anticlock),
                (int)(cellSize.x * IMAGE_SCALE), (int)(cellSize.y * IMAGE_SCALE),false);

        imgPause = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_pause),
                (int)(cellSize.x * IMAGE_SCALE), (int)(cellSize.y * IMAGE_SCALE),false);

        imgMovBit_Left = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_left),
                (int)(cellSize.x * IMAGE_SCALE), (int)(cellSize.y * IMAGE_SCALE),false);

        imgMovBit_Right = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_right),
                (int)(cellSize.x * IMAGE_SCALE), (int)(cellSize.y * IMAGE_SCALE),false);

        imgAccelerate = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_accele),
                (int)(cellSize.x * IMAGE_SCALE), (int)(cellSize.y * IMAGE_SCALE),false);

        imgResume = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_resume),
                (int)(cellSize.x * IMAGE_SCALE*4), (int)(cellSize.y * IMAGE_SCALE*4),false);

        imgBack = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_back),
                (int)(cellSize.x * IMAGE_SCALE*2), (int)(cellSize.y * IMAGE_SCALE*2),false);

        imgBackground = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(),R.drawable.img_background),
                getWidth(), getHeight(),false);

        //imgBackground = BitmapFactory.decodeResource(getResources(), R.drawable.img_background);
    }

    protected void update() {
        if(isPause) return;

        elapsedTime += System.currentTimeMillis() - lastTime;

        if(!isGameOver){
            background.update();
            if(elapsedTime >= updateTime){
                elapsedTime = 0;
                for (int i = 0; i < BLOCK_LENGTH; i++) {
                    tempBlock[i] = new Point(dropBlock[i].x, dropBlock[i].y);
                    dropBlock[i].y += 1;
                }
                footStep++;
                //printField();
                //printA();
                //printB();
                if (!checkCollider())
                {
                    if(footStep <= 5){
                        Log.d(TAG, "Gameover");
                        isGameOver = true;
                        gameOver();
                        return;
                    }
                    else{
                        for (int i = 0; i < BLOCK_LENGTH; i++) {
                            field[tempBlock[i].y][tempBlock[i].x] = colorNum.get(0);
                        }
                        soundPool.play(collideSound, collideSoundVolume, collideSoundVolume, 1, 0, 1);
                        dropBlock = futureBlock.get(0);
                        futureBlock.remove(0);
                        colorNum.remove(0);
                        background.resetSpeed();
                        randomBlock();
                        footStep = 0;
                    }
                }
                addScore();
                checkLvUp();
            }
        }
        else if(exploRectCount < Math.pow(explosion.getFrameCount(), 2)){
            exploRect.left = exploRectCount%explosion.getFrameCount() *
                    explosion.getFrameWidth();
            exploRect.right = exploRect.left + explosion.getFrameWidth();
            exploRect.top = exploRectCount / explosion.getFrameCount() *
                    explosion.getFrameWidth();
            exploRect.bottom = exploRect.top + explosion.getFrameWidth();
            exploRectCount++;
        }
        lastTime = System.currentTimeMillis();

    }
    
    private void addScore(){
        int k = BOARD_HEIGHT -1;
        int bonus = 0;
        for (int i = BOARD_HEIGHT -1; i > 0; i--){
            int widthCount=0;
            for (int j = 0; j < BOARD_WIDTH; j++){
                if (field[i][j] != 0){
                    widthCount++;
                }
                field[k][j] = field[i][j];
            }
            if (widthCount < BOARD_WIDTH) {
                k--;
            }
            else if(widthCount >= BOARD_WIDTH){
                bonus++;

            }
        }
        for(int i = 0; i < bonus; i++){
            score += (BASIC_SCORE * bonus);
        }

    }

    private void gameOver(){
        bgSound.stop();
        loseSound.start();
    }

    private void randomBlock(){
        colorNum.add(1 + new Random().nextInt(BLOCK_SHAPE.length));
        int n = (colorNum.get(colorNum.size()-1) - 1);
        Point[] addBlock = new Point[BLOCK_LENGTH];
        for (int i = 0; i < BLOCK_LENGTH; i++)
        {
            addBlock[i] = new Point(BLOCK_SHAPE[n][i] % 2, BLOCK_SHAPE[n][i] / 2);
            addBlock[i].x += (BOARD_WIDTH /2f - 1);
            addBlock[i].y -= 4;
        }

        futureBlock.add(addBlock);
        updateTime = nextUpdateTime;
    }
    private void checkLvUp(){
        if(score >= nextScore){
            currLevel++;
            nextScore = Math.round(nextScore * (2 + 0.5f));
            soundPool.play(lvUpSound, lvUpSoundVolume, lvUpSoundVolume, 1, 0, 1);
            nextUpdateTime *= 0.9f;
            updateTime = nextUpdateTime;
        }
    }

    private void horizontalMove(int dx){
        for (int i = 0; i < BLOCK_LENGTH; i++)  {
            tempBlock[i] = new Point(dropBlock[i].x, dropBlock[i].y);
            dropBlock[i].x += dx;
        }
        if (!checkCollider()) {
            for (int i = 0; i < BLOCK_LENGTH; i++){
                dropBlock[i] = new Point(tempBlock[i].x, tempBlock[i].y);
            }
        }
    }

    private void rotate(int dir){

        for(int i = 0; i < dir; i++){
            Point p = dropBlock[1]; //center of rotation
            for (int j = 0; j < BLOCK_LENGTH; j++){
                int x = dropBlock[j].y-p.y;
                int y = dropBlock[j].x-p.x;
                //dropBlock[j].x = p.x - x;
                //dropBlock[j].y = p.y + y;
                dropBlock[j] = new Point(p.x - x, p.y + y);
            }
        }
        if (!checkCollider()) {
            for (int i = 0; i < BLOCK_LENGTH; i++){
                dropBlock[i] = new Point(tempBlock[i].x, tempBlock[i].y);
            }
        }
    }

    private void printA(){
        Log.d(TAG, "***********");
        for(int i = 0; i < BLOCK_LENGTH ; i++){
            Log.d("TAG", "A "+i+ " : "+ dropBlock[i].x + ", "+ dropBlock[i].y);
        }
    }
    private void printB(){

        for(int i = 0; i < BLOCK_LENGTH ; i++){
            Log.d("TAG", "B "+i+ " : "+ tempBlock[i].x + ", "+ tempBlock[i].y);
        }
        Log.d(TAG, "----------------");
    }
    private void printField(){
        String fieldText = "";
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                fieldText += " [" + field[i][j] + "] ";
            }
            fieldText += "\n";
        }
        Log.d("Debug.Log", "printField: \n" + fieldText);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void render(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        background.draw(canvas);
        for (Button button : buttons) {
            button.draw(canvas);
        }
        drawGameboard(canvas);
        drawBlock(canvas);
        if(isPause){
            canvas.drawColor(Color.argb(0.5f, 0,0,0));
            btnResume.draw(canvas);
            btnBack.draw(canvas);
        }
        if(isGameOver){
            canvas.drawColor(Color.argb(0.5f, 0,0,0));
            Vector2 pos = new Vector2(getWidth()/2 - explosion.getFrameWidth()/2,
                    getHeight()/2 - explosion.getFrameHeight());
            RectF posRect = new RectF(pos.x, pos.y,
                    pos.x + explosion.getFrameWidth(),
                    pos.y + explosion.getFrameWidth());
            canvas.drawBitmap(imgExplosion, exploRect, posRect, paint);
        }
        drawText(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                Paint.FILTER_BITMAP_FLAG));
    }

    private Vector2 getPos(int x, int y){
        return Vector2.add(
                new Vector2(x * (float)blockTiles.getFrameWidth(),
                        y * (float)blockTiles.getFrameWidth()), new Vector2(startPos));
    }
    private Vector2 getOutSidePos(int x, int y, int index){
        return new Vector2((x-3.5f) * blockTiles.getFrameWidth(),
                (y + 5 * (index+2)) * blockTiles.getFrameWidth());
    }

    private boolean checkCollider(){

        for (int i = 0; i < BLOCK_LENGTH; i++) {
            if(dropBlock[i].y < 0) continue;
            if (dropBlock[i].x < 0 || dropBlock[i].x >= BOARD_WIDTH || dropBlock[i].y >= BOARD_HEIGHT) {
                return false;
            } else if (field[dropBlock[i].y][dropBlock[i].x] > 0) {
                return false;
            }
        }
        return true;
    }

    private void drawText(Canvas canvas){
        paint.setTextSize(cellSize.x);
        if(isPause||isGameOver) paint.setColor(getResources().getColor(R.color.rankName));
        else paint.setColor(Color.WHITE);
        Rect textBounds = new Rect();

        String mText = "Score: "+ score;
        paint.getTextBounds(mText, 0, mText.length(), textBounds);
        canvas.drawText(mText,
                getWidth()/2 - (textBounds.width() / 2f), cellSize.y * 2, paint);

        mText = "Level: " + currLevel;
        paint.getTextBounds(mText, 0, mText.length(), textBounds);
        canvas.drawText(mText,
                getWidth()/2 - (textBounds.width() / 2f), cellSize.y * 3, paint);

        mText = "Remain: "+ (nextScore - score);
        paint.getTextBounds(mText, 0, mText.length(), textBounds);
        canvas.drawText(mText,
                getWidth()/2 - (textBounds.width() / 2f), cellSize.y * 4, paint);

        if(isGameOver){
            paint.setTextSize(cellSize.x * 1.2f);
            paint.setColor(Color.WHITE);
            Paint highLight = new Paint();
            highLight.setColor(getResources().getColor(R.color.highLight));

            mText = "PRESS ANY TO CONTINUE!";
            paint.getTextBounds(mText, 0, mText.length(), textBounds);
            canvas.drawRect(
                    getWidth()/2 - textBounds.width() / 2f,
                    getHeight()/2 - textBounds.height(),
                    textBounds.width() + getWidth()/2 - textBounds.width() / 2f,
                    getHeight()/2,
                    highLight);
            canvas.drawText(mText,
                    getWidth()/2 - textBounds.width() / 2f,
                    getHeight()/2,
                    paint);


            mText = "YOU LOSE";
            paint.getTextBounds(mText, 0, mText.length(), textBounds);
            canvas.drawRect(
                    getWidth()/2 - textBounds.width() / 2f,
                    getHeight()/2 - textBounds.height() * 2,
                    textBounds.width() + getWidth()/2 - textBounds.width() / 2f,
                    getHeight()/2 - textBounds.height(),
                    highLight);
            canvas.drawText(mText,
                    getWidth()/2 - textBounds.width() / 2f,
                    getHeight()/2 - textBounds.height(),
                    paint);

        }
    }

    private void drawBlock(Canvas canvas){
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (field[i][j] == 0) continue;
                tilesRect.left = field[i][j] * blockTiles.getFrameWidth();
                tilesRect.right = tilesRect.left + blockTiles.getFrameWidth();

                Vector2 pos = getPos(j, i);
                RectF posRect = new RectF(pos.x, pos.y,
                        pos.x + blockTiles.getFrameWidth(),
                        pos.y + blockTiles.getFrameWidth());

                canvas.drawBitmap(blockBit, tilesRect, posRect, paint);
            }
        }

        tilesRect.left = colorNum.get(0) * blockTiles.getFrameWidth();
        tilesRect.right = tilesRect.left + blockTiles.getFrameWidth();
        for (int i = 0; i < BLOCK_LENGTH; i++) {
            if(dropBlock[i].y < 0) continue;
            Vector2 pos = getPos(dropBlock[i].x, dropBlock[i].y);
            RectF posRect = new RectF(pos.x, pos.y,
                    pos.x + blockTiles.getFrameWidth(),
                    pos.y + blockTiles.getFrameWidth());

            canvas.drawBitmap(blockBit, tilesRect, posRect, paint);
        }

        for(int i = 0; i < futureBlock.size(); i++){
            tilesRect.left = colorNum.get(i+1) * blockTiles.getFrameWidth();
            tilesRect.right = tilesRect.left + blockTiles.getFrameWidth();
            for (int j = 0; j < BLOCK_LENGTH; j++) {

                Vector2 pos = getOutSidePos(futureBlock.get(i)[j].x, futureBlock.get(i)[j].y, i);
                RectF posRect = new RectF(pos.x, pos.y,
                        pos.x + blockTiles.getFrameWidth(),
                        pos.y + blockTiles.getFrameWidth());

                canvas.drawBitmap(blockBit, tilesRect, posRect, paint);
            }
        }
    }

    private void drawGameboard(Canvas canvas) {
        Paint grid_paint = new Paint();
        grid_paint.setColor(getResources().getColor(R.color.gridline));
        grid_paint.setStrokeWidth(5);

        Paint oddBoard_paint = new Paint();
        oddBoard_paint.setColor(getResources().getColor(R.color.oddBoard));
        Paint evenBoard_paint = new Paint();
        evenBoard_paint.setColor(getResources().getColor(R.color.evenBoard));

        for(int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                Paint drawPaint = oddBoard_paint;
                if(i%2==0 &&j%2==0 || i%2!=0 &&j%2!=0) drawPaint = evenBoard_paint;

                canvas.drawRect(
                        i * cellSize.x + startPos.x,
                        j * cellSize.y + startPos.y,
                        (i+1) * cellSize.x + startPos.x,
                        (j+1) * cellSize.y + startPos.y, drawPaint);
            }
        }

        //drawBoardLine(canvas, grid_paint);
    }

    private void drawBoardLine(Canvas canvas, Paint grid_paint){
        //Horizontal
        for(int i=1; i<20; i++) {
            canvas.drawLine(startPos.x,
                    i * cellSize.y + startPos.y,
                    gameboardSize.x + startPos.x,
                    i * cellSize.y + startPos.y, grid_paint);
        }
        //Vertical
        for(int i=1; i<10; i++){
            canvas.drawLine(i * cellSize.x + startPos.x, startPos.y,
                    i * cellSize.x + startPos.x,
                    gameboardSize.y + startPos.y, grid_paint);
        }
    }

    private Vector2 GetGameboardSize(float _width, float _height){
        if(_width * 2.5f > _height){
            return  new Vector2(_height / 2.5f, _height / 2.5f * 2);
        }
        else{
            return  new Vector2(_width,_width * 2f);
        }
    }


}
