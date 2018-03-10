package com.gamecodeschool.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Dusan on 5.2.2018.
 */

public class GameView extends SurfaceView implements Runnable {

    // This is our thread
    Thread mGameThread = null;

    // We need a SurfaceHolder
    // When we use Paint and Canvas in a thread
    SurfaceHolder mOurHolder;

    // A boolean which we will set and unset
    // when the game is running- or not
    volatile boolean mPlaying;

    // Game is mPaused at the start
    boolean mPaused = true;

    // A Canvas and a Paint object
    Canvas mCanvas;
    Paint mPaint;

    // This variable tracks the game frame rate
    long mFPS;

    // The size of the screen in pixels
    int mScreenX;
    int mScreenY;

    // The players mPaddle
    Paddle mPaddle;

    // A mBall
    Ball mBall;

    // For sound FX
    SoundPool sp;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;
    int explodeID = -1;

    // The mScore
    int mScore = 0;

    // Lives
    int mLives = 3;

    /*
    *  When we call new() on gameView
    *  This custom constructor runs
    */

    public GameView(Context context, int x, int y) {
        /*
        *  The next line of code asks the
        *  SurfaceView class to set up
        */
        super(context);

        // Set the screen width and height
        mScreenX = x;
        mScreenY = y;

        // Initialize mOurHolder and mPaint objects
        mOurHolder = getHolder();
        mPaint = new Paint();

        // A new mPaddle
        mPaddle = new Paddle(mScreenX, mScreenY);

        // Create a mBall
        mBall = new Ball(mScreenX, mScreenY);

        /*
        * Instantiate our sound pool
        * dependent upon which version
        * of Android is present
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            sp = new SoundPool.Builder().setMaxStreams(5)
                    .setAudioAttributes(audioAttributes).build();
        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try {
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("beep1.ogg");
            beep1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("explode.ogg");
            explodeID = sp.load(descriptor, 0);
        } catch (IOException e) {
            Log.e("error", "failed to load sound files");
        }

        setupAndRestart();
    }

    public void setupAndRestart() {
        // Put the mBall back to the start
        mBall.reset(mScreenX, mScreenY);

        // if game over reset scores and mLives
        if (mLives == 0) {
            mScore = 0;
            mLives = 3;
        }
    }

    @Override
    public void run() {
        while (mPlaying) {
            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if (!mPaused) {
                update();
            }
            // Draw the frame
            draw();

            /*
               Calculate the FPS this frame
               We can then use the result to
               time animations in the update methods
            */
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame;
            }
        }
    }

    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.
    public void update() {
        // Move the mPaddle if required
        mPaddle.update(mFPS);

        mBall.update(mFPS);

        // Check for mBall colliding with mPaddlee
        if (RectF.intersects(mPaddle.getRect(), mBall.getRect())) {
            mBall.setRandomXVelocity();
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mPaddle.getRect().top - 2);

            mScore++;
            mBall.increaseVelocity();

            sp.play(beep1ID, 1, 1, 0, 0, 1);
        }

        // Bounce the mBall back when it hits the bottom of screen
        if (mBall.getRect().bottom > mScreenY) {
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mScreenY - 2);

            // Lose a life
            mLives--;
            sp.play(loseLifeID, 1, 1, 0, 0, 1);

            if (mLives == 0) {
                mPaused = true;
                setupAndRestart();
            }
        }

        // Bounce the mBall back when it hits the top of screen
        if (mBall.getRect().top < 0) {
            mBall.reverseYVelocity();
            mBall.clearObstacleY(12);

            sp.play(beep2ID, 1, 1, 0, 0, 1);
        }

        // If the mBall hits left wall bounce
        if (mBall.getRect().left < 0) {
            mBall.reverseXVelocity();
            mBall.clearObstacleX(2);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // If the mBall hits right wall bounce
        if (mBall.getRect().right > mScreenX) {
            mBall.reverseXVelocity();
            mBall.clearObstacleX(mScreenX - 22);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }
    }

    // Draw the newly update scene
    public void draw() {
        // Make sure drawing surface is valid or crash
        if (mOurHolder.getSurface().isValid()) {
            mCanvas = mOurHolder.lockCanvas();

            // Background bolor
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));

            // Brush color for drawing
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // mPaddle
            mCanvas.drawRect(mPaddle.getRect(), mPaint);

            // mBall
            mCanvas.drawRect(mBall.getRect(), mPaint);

            // Brush color for drawing
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // mScore
            mPaint.setTextSize(40);
            mCanvas.drawText("Score: " + mScore + "   Lives: " + mLives, 10, 50, mPaint);

            // Draw everything to the screen
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    // If the Activity is paused/stopped
    // shutdown thread
    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    // If the Activity starts/restarts
    // start our thread
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                mPaused = false;
                // Is the touch on the right or left?
                if (motionEvent.getX() > mScreenX / 2) {
                    mPaddle.setMovementState(mPaddle.RIGHT);
                } else {
                    mPaddle.setMovementState(mPaddle.LEFT);
                }
                break;
            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                mPaddle.setMovementState(mPaddle.STOPPED);
                break;
        }
        return true;
    }
}
