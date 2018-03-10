package dulop.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Dusan on 11.2.2018.
 */

public class TDView extends SurfaceView implements Runnable {

    private Context context;

    private int screenX;
    private int screenY;

    volatile boolean playing;
    Thread gameThread = null;
    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public EnemyShip enemy4;

    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private float distanceCrossed;
    private long timeTaken;
    private long timeStarted;
    private float bestDistance;

    private boolean gameEnded;

    public TDView(Context context, int x, int y) {
        super(context);

        this.context = context;

        screenX = x;
        screenY = y;

        ourHolder = getHolder();
        paint = new Paint();

        /*player = new PlayerShip(context, x, y);
        enemy1 = new EnemyShip(context, x, y);
        enemy2 = new EnemyShip(context, x, y);
        enemy3 = new EnemyShip(context, x, y);
        enemy4 = new EnemyShip(context, x, y);

        int numSpecs = 40;

        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(x, y);
            dustList.add(spec);
        }*/
        startGame();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void startGame() {
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context,screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);
        enemy4 = new EnemyShip(context, screenX, screenY);

        int numSpecs = 40;

        for (int i = 0; i < numSpecs; i++) {
            SpaceDust spec = new SpaceDust(screenX, screenY);
            dustList.add(spec);
        }

        distanceCrossed = 0;
        bestDistance = 0;
        timeTaken = 0;

        timeStarted = System.currentTimeMillis();

        gameEnded = false;
    }

    private void update() {

        boolean hitDetected = false;

        if (Rect.intersects(player.getHitBox(), enemy1.getHitBox())) {
            hitDetected = true;
            enemy1.setX(-200);
        }
        if (Rect.intersects(player.getHitBox(), enemy2.getHitBox())) {
            hitDetected = true;
            enemy2.setX(-200);
        }
        if (Rect.intersects(player.getHitBox(), enemy3.getHitBox())) {
            hitDetected = true;
            enemy3.setX(-200);
        }
        if (Rect.intersects(player.getHitBox(), enemy4.getHitBox())) {
            hitDetected = true;
            enemy4.setX(-200);
        }

        if (hitDetected) {
            player.reduceShieldStrength();
            if (player.getShieldSrength() < 0) {
                gameEnded = true;
            }
        }

        player.update();

        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());
        enemy4.update(player.getSpeed());

        for (SpaceDust sd : dustList) {
            sd.update(player.getSpeed());
        }

        if (!gameEnded) {
            distanceCrossed += player.getSpeed();

            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        if (distanceCrossed < bestDistance) {
            bestDistance = distanceCrossed;
        }
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // for debugging
            /*paint.setColor(Color.argb(255, 255, 255, 255));

            canvas.drawRect(player.getHitBox().left,
                    player.getHitBox().top,
                    player.getHitBox().right,
                    player.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy1.getHitBox().left,
                    enemy1.getHitBox().top,
                    enemy1.getHitBox().right,
                    enemy1.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy2.getHitBox().left,
                    enemy2.getHitBox().top,
                    enemy2.getHitBox().right,
                    enemy2.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy3.getHitBox().left,
                    enemy3.getHitBox().top,
                    enemy3.getHitBox().right,
                    enemy3.getHitBox().bottom,
                    paint);

            canvas.drawRect(enemy4.getHitBox().left,
                    enemy4.getHitBox().top,
                    enemy4.getHitBox().right,
                    enemy4.getHitBox().bottom,
                    paint); */

            paint.setColor(Color.argb(255, 255, 255, 255));

            /*for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }*/

            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy1.getBitmap(),
                    enemy1.getX(),
                    enemy1.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy2.getBitmap(),
                    enemy2.getX(),
                    enemy2.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy3.getBitmap(),
                    enemy3.getX(),
                    enemy3.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy4.getBitmap(),
                    enemy4.getX(),
                    enemy4.getY(),
                    paint);

            if (!gameEnded) {
                // draw the hud
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Best Distance: " + bestDistance + " KM", 10, 20, paint);
                canvas.drawText("Time: " + timeTaken + "s", screenX / 2, 20, paint);
                canvas.drawText("Distance: " +
                        distanceCrossed / 1000 +
                        " KM", screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield: " + player.getShieldSrength(), 10, screenY - 20, paint);
                canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS", (screenX / 3) * 2, screenY - 20, paint);
            } else {
                player.setX(-200);
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX / 2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText("Best Distance: " +
                    bestDistance + " KM", screenX / 2, 160, paint);
                canvas.drawText("Time: " + timeTaken +
                    "s", screenX / 2, 200, paint);
                canvas.drawText("Distance: " +
                        distanceCrossed / 1000 + " KM", screenX / 2, 240, paint);
                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX / 2, 350, paint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {

        }
    }

    // Clean up our thread if the game is interrupted or the player quits
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    // Make a new thread and start it
    // Execution mover to our R
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                if (gameEnded) {
                    startGame();
                    player.setX(50);
                }
                break;
        }
        return true;
    }

}
