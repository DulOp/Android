package dulop.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Dusan on 12.2.2018.
 */

public class EnemyShip {

    private Bitmap bitmap;
    private int x, y;
    private int speed = 1;

    private int maxX;
    private int minX;

    private int maxY;
    private int minY;

    private Rect hitBox;

    public Rect getHitBox() {
        return hitBox;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public EnemyShip(Context context, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.enemy);
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        Random generator = new Random();
        speed = generator.nextInt(6) + 10;

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();

        hitBox = new Rect(x - 1, y - 1, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
    }

    // Make an enemy out of bounds and force a re-spawn
    public void setX(int x) {
        this.x = x;
    }

    public void update(int playerSpeed) {
        x -= playerSpeed / 2;
        x -= speed;

        if (x < minX - bitmap.getWidth()) {
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        hitBox.left = x - 1;
        hitBox.top = y - 1;
        hitBox.right = x + bitmap.getWidth() - 1;
        hitBox.bottom = y + bitmap.getHeight() - 1;
    }
}
