package com.example.mygame10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

class Blueprint {
    private static final double SPAWNS_PER_MINUTE = 60;
    private static final double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE / 60.0;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS / SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn;
    private Paint blueprintPaint;
    private Context context;
    private int positionX;
    private int positionY;
    private int right;
    private int bottom;

    public Blueprint(Context context, int positionX, int positionY, int right, int bottom) {

        this.positionX = positionX;
        this.positionY = positionY;
        this.right = right;
        this.bottom = bottom;

        this.blueprintPaint = new Paint();
        int blueprintColor = ContextCompat.getColor(context, R.color.blueprint);
        blueprintPaint.setColor(blueprintColor);
        blueprintPaint.setStyle(Paint.Style.STROKE);
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
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

    public void draw(Canvas canvas, Blueprint blueprintElement) {
        canvas.drawRect((float)blueprintElement.getPositionX(),
                (float)blueprintElement.getPositionY(),
                (float)blueprintElement.getPositionX()+(float)blueprintElement.getRight(),
                (float)blueprintElement.getPositionY()+(float)blueprintElement.getBottom(),
                blueprintPaint);
    }
}
