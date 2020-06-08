package com.example.mygame10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

class Highlight {
    private Context context;
    private FallingObject fallingObject;
    private Paint paint;

    public Highlight(Context context, FallingObject fallingObject) {

        this.context = context;
        this.fallingObject = fallingObject;

        this.paint = new Paint();
        int highlightColor = ContextCompat.getColor(context, R.color.highlightColor);
        paint.setColor(highlightColor);
        paint.setStyle(Paint.Style.STROKE);
    }

    public FallingObject getFallingObject() {
        return fallingObject;
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(fallingObject.getPositionX()-5, fallingObject.getPositionY()-5,
                fallingObject.getPositionX()+fallingObject.getWidth()+5,
                fallingObject.getPositionY()+fallingObject.getHeight()+5, paint);
    }
    public void drawBlueprintHighlight(Canvas canvas, FallingObject fallingObject) {
        canvas.drawRect(fallingObject.getPositionX()-5, fallingObject.getPositionY()-5,
                fallingObject.getPositionX()+fallingObject.getWidth()+5,
                fallingObject.getPositionY()+fallingObject.getHeight()+5, paint);
    }
}
