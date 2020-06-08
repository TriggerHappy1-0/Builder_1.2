package com.example.mygame10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class DropObject {

    private static final double SPAWNS_PER_MINUTE = 10720;
    private static final double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE / 60.0;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS / SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn;
    private double dropLeft, dropTop, dropRight, dropBottom;
    private boolean falling;
    private boolean grounded;
    private Paint dropPaint;
    private Context context;

    public DropObject (Context context, double dropLeft, double dropTop, boolean falling, boolean grounded) {
        this.dropLeft = dropLeft;
        this.dropTop = dropTop;
        this.falling = falling;
        this.grounded = grounded;

        this.dropPaint = new Paint();
        int dropColor = ContextCompat.getColor(context, R.color.dropObjectColor);
        dropPaint.setColor(dropColor);

    }
    public double getDropLeft() {
        return dropLeft;
    }

    public double getDropTop() {
        return dropTop;
    }

    public double getDropRight() {
        return dropRight;
    }

    public double getDropBottom() {
        return dropBottom;
    }

    public boolean getFalling() {
        return falling;
    }

    public boolean getGrounded (){
        return grounded;
    }

    public static boolean readyToSpawn() {
        if (updatesUntilNextSpawn <= 0){
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        }
        else {
            updatesUntilNextSpawn--;
            return false;
        }
    }

    // draw the drops
    public void draw(Canvas canvas) {
        double drawPositionX = dropLeft;
        double drawPositionY = dropTop;
            canvas.drawCircle((float)drawPositionX, (float)drawPositionY, 5,  dropPaint);
    }

    // check if the drops touch the objects
    public static boolean collides (DropObject drop, FallingObject colidee){

        return drop.getDropLeft() < colidee.getPositionX() + colidee.getWidth()
                && drop.getDropLeft() + drop.getDropRight() > colidee.getPositionX()
                && drop.getDropTop() < colidee.getPositionY() + colidee.getHeight()
                && drop.getDropTop() + drop.getDropBottom() > colidee.getPositionY();
    }


    public void update() {

        // gravity
        if (dropTop < 1030  && falling) {
            dropTop = dropTop + 25;

            //touchdown
            if (dropTop >= 1030) {
                dropTop = 1030;
                falling = false;
                grounded = true;
            }
        }

    }
}





