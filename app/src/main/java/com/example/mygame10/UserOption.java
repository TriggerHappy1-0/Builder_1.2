package com.example.mygame10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

public class UserOption {
    private final Paint paintErase;
    private boolean rainState;
    private String optionName;
    private Paint paint;
    private float positionX;
    private float positionY;
    private int next;
    private boolean pauseState;
    private boolean restartState;
    private int difficultyState;
    private boolean rotateState;

    public UserOption(Context context, float positionX, float positionY, String optionName) {
        this.optionName = optionName;
        this.positionX = positionX;
        this.positionY = positionY;

        paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.dropObjectColor);
        paint.setColor(color);
        paint.setTextSize(50);
        paintErase = new Paint();
        int eraseColor = ContextCompat.getColor(context, R.color.eraseColor);
        paintErase.setColor(eraseColor);
        paintErase.setTextSize(50);
    }
    public void setRainState(boolean rainState) {
        this.rainState = rainState;
    }

    public void setPauseState(boolean pauseState) {
        this.pauseState = pauseState;
    }

    public void setRestartState(boolean restartState) {
        this.restartState = restartState;
    }

    public void setDifficultyState(int difficultyState) {
        this.difficultyState = difficultyState;
    }


    public void setRotateState(boolean rotateState) {
        this.rotateState = rotateState;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public boolean getRainState() {
        return rainState;
    }

    public boolean getPauseState() {
        return pauseState;
    }

    public boolean getRestartState() {
        return restartState;
    }

    public boolean getRotateState() {
        return rotateState;
    }

//    public int getDifficultyState() {
//        return difficultyState;
//    }

    public int getNext() {
        return next;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public String getOptionName() {
        return optionName;
    }

    public void draw(Canvas canvas) {

        if (pauseState) {
            canvas.drawText("RESUME", 1450, 350, paint);
            if (optionName != "PAUSE") {
                canvas.drawText(optionName, positionX, positionY, paint);
            }
        }
        else {
            canvas.drawText(optionName, positionX, positionY, paint);
            switch (difficultyState){
                case 40:
//                    canvas.drawText("NIGHTMARE", 400, 350, paintErase);
                    canvas.drawText("PIECE OF CAKE", 400, 350, paint);
                    break;
                case 35:
//                    canvas.drawText("PIECE OF CAKE", 400, 350, paintErase);
                    canvas.drawText("BEGINNER", 400, 350, paint);
                    break;
                case 30:
//                    canvas.drawText("BEGINNER", 400, 350, paintErase);
                    canvas.drawText("EASY", 400, 350, paint);
                    break;
                case 25:
//                    canvas.drawText("EASY", 400, 350, paintErase);
                    canvas.drawText("NORMAL", 400, 350, paint);
                    break;
                case 20:
//                    canvas.drawText("NORMAL", 400, 350, paintErase);
                    canvas.drawText("TRICKY", 400, 350, paint);
                    break;
                case 15:
//                    canvas.drawText("TRICKY", 400, 350, paintErase);
                    canvas.drawText("HARD", 400, 350, paint);
                    break;
                case 10:
//                    canvas.drawText("HARD", 400, 350, paintErase);
                    canvas.drawText("VERY HARD", 400, 350, paint);
                    break;
                case 5:
//                    canvas.drawText("VERY HARD", 400, 350, paintErase);
                    canvas.drawText("NIGHTMARE", 400, 350, paint);
                    break;
            }
        }

    }

}
