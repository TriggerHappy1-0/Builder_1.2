package com.example.mygame10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

public class FallingObject {

    private static final double SPAWNS_PER_MINUTE = 5;
    private static final double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE / 60.0;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS / SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn;
    private final Joystick joystick;
    private int positionX, positionY, width, height;
    private boolean falling;
    private boolean grounded;
    private String objectType;
    private Paint brickPaint;
    private Paint tilePaint;
    private boolean isPressed;
    private boolean last;

    public FallingObject (Context context, int positionX, int positionY, int width, int height, boolean falling, boolean grounded, String objectType) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.falling = falling;
        this.grounded = grounded;
        this.objectType = objectType;

        this.tilePaint = new Paint();
        int tileColor = ContextCompat.getColor(context, R.color.tileColor);
        tilePaint.setColor(tileColor);
        this.brickPaint = new Paint();
        int brickColor = ContextCompat.getColor(context, R.color.brickColor);
        brickPaint.setColor(brickColor);

        joystick = new Joystick(0,0,0,0);
    }
    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean getFalling() {
        return falling;
    }

    public boolean getGrounded (){
        return grounded;
    }

    public String getObjectType (){
        return objectType;
    }

    public boolean getLast (){
        return last;
    }

    public boolean isPressed(double touchPositionX, double touchPositionY,
                             double checkPositionX, double checkPositionY,
                             double width, double height) {


        return touchPositionX >= checkPositionX
                && touchPositionY >= checkPositionY
                && touchPositionX <= checkPositionX+width
                && touchPositionY <= checkPositionY+height;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public boolean getIsPressed() {
        return isPressed;
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

    // draw the rectangles
    public void draw(Canvas canvas, int currentIndex) {
        int drawPositionX = positionX;
        int drawPositionY = positionY;
        int drawRight = positionX+width;
        int drawBottom = positionY+height;
        objectType = Game.fallingObjectList.get(currentIndex).getObjectType();
        if (objectType == "roofTile") {
            canvas.drawRect(drawPositionX, drawPositionY, drawRight, drawBottom, tilePaint);
            if (currentIndex == Game.fallingObjectList.size()+1) {
                last = true;
            }
            else {
                last = false;
            }
        }
        else if (objectType == "brick"){
            canvas.drawRect(drawPositionX, drawPositionY, drawRight, drawBottom, brickPaint);
            if (currentIndex == Game.fallingObjectList.size()+1) {
                last = true;
            }
            else {
                last = false;
            }
        }
    }

    // check if the rectangles intersect
    public static boolean collides (FallingObject collider, FallingObject collidee){

        return collider.getPositionX() < collidee.getPositionX() + collidee.getWidth()
                && collider.getPositionX() + collider.getWidth() > collidee.getPositionX()
                && collider.getPositionY() < collidee.getPositionY() + collidee.getHeight()
                && collider.getPositionY() + collider.getHeight() > collidee.getPositionY();
    }


    public void update() {

        // gravity
        if (positionY < 1000  && falling) {
            positionY = positionY + 1;

            //touchdown
            if (positionY >= 1000) {
                positionY = 1000;
                falling = false;
                grounded = true;
            }
        }
    }
 }




