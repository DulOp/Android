package dulop.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Dusan on 12.2.2018.
 */

public class PlayerShip {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 0;
    private boolean boosting;

    private final int GRAVITY = -12;

    private int maxY;
    private int minY;

    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    // hit box
    private Rect hitBox;

    private int shieldSrength;

    public Rect getHitBox() {
        return hitBox;
    }

    public PlayerShip(Context context, int screenX, int screenY) {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        boosting = false;
        shieldSrength = 2;
        maxY = screenY - bitmap.getHeight();
        minY = 0;

        hitBox = new Rect(x - 1, y - 1, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
    }

    public int getShieldSrength() {
        return shieldSrength;
    }

    public void reduceShieldStrength() {
        shieldSrength --;
    }

    public void update() {
        if (boosting) {
            speed += 1;
        } else {
            speed -= 2;
        }

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        y -= speed + GRAVITY;

        if (y < minY) {
            y = minY;
        }

        if (y > maxY) {
            y = maxY;
        }

        hitBox.left = x - 1;
        hitBox.top = y - 1;
        hitBox.right = x + bitmap.getWidth() - 1;
        hitBox.bottom = y + bitmap.getHeight() - 1;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting() {
        boosting = false;
    }
}
