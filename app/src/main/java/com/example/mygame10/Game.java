package com.example.mygame10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Game extends SurfaceView implements SurfaceHolder.Callback {

    public static final double SPEED_PIXELS_PER_SECOND = 100;
    private static double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private Highlight highlight;
    private GameLoop gameLoop;
    public static List<FallingObject> fallingObjectList = new ArrayList<FallingObject>();
    public static List<FallingObject> checkPositionList = new ArrayList<FallingObject>();
    public static List<FallingObject> wrongPositionList = new ArrayList<FallingObject>();
    public static List<DropObject> dropObjectList = new ArrayList<DropObject>();
    public static List<Blueprint> level1BlueprintElementList = new ArrayList<Blueprint>();
    public static List<Blueprint> checkPositionBlueprintList = new ArrayList<Blueprint>();
    private int positionX = 800;
    private int tilePositionX = 1000;
    private int positionY = 50;
    private int width = 100;
    private int height = 50;
    private int bX  = 800;
    private int bY = 1000;
    private int bR = 100;
    private int bB = 50;
    private int level = 1;
    private boolean falling = true;
    private boolean grounded = false;
    private String objectType;
    private int index = 0;
    private boolean wetBrick = false;
    private int fallingObjectListSizeCheck = 4;
    private final FallingObject fallingObject;
    private UserOption rainButton;
    private UserOption nextObjectButton;
    private UserOption previousObjectButton;
    private UserOption pauseButton;
    private UserOption restartButton;
    private UserOption difficultyButton;
    private UserOption rotateButton;
    private boolean endSpawn = false;
    private final Blueprint blueprintElement;
    private final Joystick joystick;
    private int joystickPointerId = 0;
    private boolean noRain;
    private boolean checkBuilding;
    private boolean levelFinish = false;
    private boolean nextLevel = false;
    private boolean beginning = true;
    private int precisionLevel;

    public Game(Context context) {
        super(context);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);
        fallingObject = new FallingObject(context,0,0,0,0,
                true,false, "Instance");
        rainButton = new UserOption (context, 1400, 100,
                "MAKE IT RAIN");
        nextObjectButton = new UserOption (context, 1500, 750,
                "NEXT");
        rotateButton = new UserOption (context, 1450, 550,
                "ROTATE");
        previousObjectButton = new UserOption (context, 1450, 950,
                "PREVIOUS");
        pauseButton = new UserOption (context, 1450, 350,
                "PAUSE");
        restartButton = new UserOption(context, 100, 250,
                "RESTART");
        difficultyButton = new UserOption (context, 100, 350,
                "DIFFICULTY : ");
        blueprintElement = new Blueprint(context, 0, 0, 0, 0);
        joystick = new Joystick(275, 700, 70,
                40);
        highlight = new Highlight (context, fallingObject);
        precisionLevel=40;

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Handle touch event actions
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                double touchPositionX = (double) event.getX();
                double touchPositionY = (double) event.getY();


                // RAIN button
                if (touchPositionX >= 1500 && touchPositionY >= 50
                        && touchPositionX <= 1715 && touchPositionY <= 100
                        &&!pauseButton.getPauseState()){
                    rainButton.setRainState(true);
                }

                // PAUSE button
                if (touchPositionX >= 1400 && touchPositionY >= 300
                        && touchPositionX <= 1700 && touchPositionY <= 350){
                    if (!pauseButton.getPauseState()){
                        pauseButton.setPauseState(true);
                    }
                    else {
                        pauseButton.setPauseState(false);
                        beginning = false;
                        nextLevel = false;
                        endSpawn = false;
                    }
                }

                // ROTATE button
                if (touchPositionX >= 1450 && touchPositionY >= 500
                        && touchPositionX <= 1700 && touchPositionY <= 550){
                    if (!rotateButton.getRotateState()){
                        rotateButton.setRotateState(true);
                    }
                    else {
                        rotateButton.setRotateState(false);
                    }
                }

                // RESTART button
                if (touchPositionX >= 100 && touchPositionY >= 200
                        && touchPositionX <= 300 && touchPositionY <= 250){
                    restartButton.setRestartState(true);
                    restart();
                }

                // DIFFICULTY button
                if (touchPositionX >= 100 && touchPositionY >= 300
                        && touchPositionX <= 600 && touchPositionY <= 350){
                    MAX_SPEED += 10;
                    precisionLevel -= 5;
                    if (precisionLevel == 0) {
                        precisionLevel = 40;
                        MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
                    }
                    difficultyButton.setDifficultyState(precisionLevel);
                }

                // NEXT button
                if (touchPositionX >= 1450 && touchPositionY >= 650
                        && touchPositionX <= 1620 && touchPositionY <= 800
                        && fallingObjectList.size() > 0 && fallingObjectList.size() == index+1
                        && !fallingObject.getLast()){
                }
                else if (touchPositionX >= 1450 && touchPositionY >= 650
                        && touchPositionX <= 1620 && touchPositionY <= 800
                        && fallingObjectList.size() > index){
                    index++;
                    nextObjectButton.setNext(index);
                }

                // PREVIOUS button
                if (touchPositionX >= 1500 && touchPositionY >= 850
                        && touchPositionX <= 1650 && touchPositionY <= 1000
                        && index > 0){
                    index--;
                    nextObjectButton.setNext(index);
                }
//                if (joystick.getIsPressed()){
//                    // joystick was pressed before this event
//                }
                else if(joystick.isPressed(touchPositionX, touchPositionY)) {
                    // joystick is pressed in this event -> setIsPressed(true) and store ID
                    joystickPointerId = event.getPointerId(event.getActionIndex());
                    joystick.setIsPressed(true);
                }
                else {
                    // joystick was not previously, and is not pressed in this event -> cast spell
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                // joystick was pressed previously and is now moved
                if (joystick.getIsPressed()) {
                    joystick.setActuator((double)event.getX(), (double)event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (joystickPointerId == event.getPointerId(event.getActionIndex())) {
                    // joystick was let go of -> setIsPressed(false) and resetActuator
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            gameLoop = new GameLoop(this, holder);
        }
        gameLoop.startLoop();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        pauseButton.draw(canvas);
        if (!beginning) {
            drawUPS(canvas);
            drawFPS(canvas);
            joystick.draw(canvas);
            rainButton.draw(canvas);
            nextObjectButton.draw(canvas);
            previousObjectButton.draw(canvas);
            restartButton.draw(canvas);
            difficultyButton.draw(canvas);
            rotateButton.draw(canvas);
        }
        if (beginning && level == 1) {
            pauseButton.setPauseState(true);
            Paint paint = new Paint();
            int color = ContextCompat.getColor(getContext(), R.color.control);
            paint.setColor(color);
            paint.setTextSize(50);
            canvas.drawText("Welcome Builder, today you are at the controls of a crane and ",
                    0,62,50,200, paint);
            canvas.drawText("your job is to create a sturdy building. Every 12 seconds, a new",
                    0,64,50,250, paint);
            canvas.drawText("brick or roof tile is going to be sent down. Using the joystick",
                    0,63,50,300, paint);
            canvas.drawText("on your left you need to  position it according to the blueprint",
                    0,64,50,350, paint);
            canvas.drawText("our architect gave us on the control screen of your crane.",
                    0,58,50,400, paint);
            canvas.drawText("To scroll through bricks and tiles touch NEXT or PREVIOUS",
                    0,57,50,450, paint);
            canvas.drawText("buttons. After you run out of bricks or tiles you can make it " +
                            "rain",
                    0,66,50,500, paint);
            canvas.drawText("by touching the MAKE IT RAIN button to check if the building",
                    0,60,50,550, paint);
            canvas.drawText("can survive many years. The crane computer will tell you if you",
                    0,63,50,600, paint);
            canvas.drawText("did your job right or highlight parts of the blueprint that " +
                            "are wrong.",
                    0,70,50,650, paint);
            canvas.drawText("You can touch the PAUSE the game for Builder's lunch break",
                    0,58,50,700, paint);
            canvas.drawText("or to get a snack and RESTART if your room Builder work mate",
                    0,60,50,750, paint);
            canvas.drawText("gave you a hug that moved the crane by mistake.",
                    0,47,50,800, paint);
            canvas.drawText("Now hit UNPAUSE to get to work!",
                    0,31,50,850, paint);
        }
        if (!pauseButton.getPauseState()) {
            for (Blueprint blueprintElement : level1BlueprintElementList) {
                blueprintElement.draw(canvas, blueprintElement);
            }
            for (FallingObject fallingObject : fallingObjectList) {
                fallingObject.draw(canvas, fallingObjectList.indexOf(fallingObject));
            }
            if (!fallingObjectList.isEmpty() && !rainButton.getRainState()) {
                highlight.draw(canvas);
            }
            for (DropObject dropObject : dropObjectList) {
                dropObject.draw(canvas);
            }
        }
        else if (pauseButton.getPauseState() && !nextLevel){
            Paint paint = new Paint();
            int color = ContextCompat.getColor(getContext(), R.color.control);
            paint.setColor(color);
            paint.setTextSize(100);
            canvas.drawText("GAME PAUSED", 550, 100, paint);
        }
        if (wrongPositionList.isEmpty() && endSpawn && dropObjectList.size() > 340) {
//        if (levelFinish == false){
            level++;
            nextLevel = true;
//            levelFinish = true;
            restart();
        }
        else if (endSpawn && dropObjectList.size() > 340) {
//        else {
            Paint paint = new Paint();
            int color = ContextCompat.getColor(getContext(), R.color.control);
            paint.setColor(color);
            paint.setTextSize(100);
            canvas.drawText("You should try again!", 600, 200, paint);
            for (FallingObject fallingObject : wrongPositionList) {
                highlight = new Highlight(getContext(), fallingObject);
                highlight.drawBlueprintHighlight(canvas, fallingObject);
            }
        }
        if (nextLevel && pauseButton.getPauseState()) {
            Paint paint = new Paint();
            Paint paint1 = new Paint();
            int color = ContextCompat.getColor(getContext(), R.color.control);
            paint.setColor(color);
            paint1.setColor(color);
            paint.setTextSize(100);
            paint1.setTextSize(50);
            canvas.drawText("Well done!!!", 600, 200, paint);
            canvas.drawText("Press RESUME to go to the next level", 450, 300, paint1);
        }
    }
    public void drawUPS (Canvas canvas){
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.ups);
        paint.setColor(color);
        paint.setTextSize(30);
        canvas.drawText("UPS: " + averageUPS,100,100, paint);

    }
    public void drawFPS (Canvas canvas){
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.ups);
        paint.setColor(color);
        paint.setTextSize(30);
        canvas.drawText("FPS: " + averageFPS,100,150, paint);

    }

    // create blueprint
    public void createBlueprint() {

        if (level == 1 && level1BlueprintElementList.size() < 18) {
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX,
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX - bR,
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX - bR,
                    bY - bB, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX - bR,
                    bY - (2 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX - bR,
                    bY - (3 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX - bR,
                    bY - (4 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX + bR,
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX + bR,
                    bY - bB, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX + bR,
                    bY - (2 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX + bR,
                    bY - (3 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX + bR,
                    bY - (4 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX - bR - (bR / 2),
                    bY - (5 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX - (bR / 2),
                    bY - (5 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX + (bR / 2),
                    bY - (5 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX + bR + (bR / 2),
                    bY - (5 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX - bR,
                    bY - (6 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX,
                    bY - (6 * bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX + bR,
                    bY - (6 * bB), bR, bB));
        }


        if (level == 2 && level1BlueprintElementList.size() < 56) {
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bB,
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bB+bR,
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bB+(2*bR),
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bB+(3*bR),
                    bY, bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bB,
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bB-bR,
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bB-(2*bR),
                    bY, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(3*bR),
                    bY, bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bR,
                    bY-bB, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(2*bR),
                    bY-bB, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(3*bR),
                    bY-bB, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bR,
                    bY-bB, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(2*bR),
                    bY-bB, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(3*bR),
                    bY-bB, bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bR,
                    bY-(2*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bR+bB,
                    bY-(2*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(3*bR),
                    bY-(2*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(3*bR)+bB,
                    bY-(2*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bB,
                    bY-(2*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bR,
                    bY-(2*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(2*bR)-bB,
                    bY-(2*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(3*bR),
                    bY-(2*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(3*bR),
                    bY-(3*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bR,
                    bY-(3*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bR,
                    bY-(3*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(3*bR),
                    bY-(3*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bB,
                    bY-(4*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bR+bB,
                    bY-(4*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(2*bR)+bB,
                    bY-(4*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(3*bR)+bB,
                    bY-(4*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bB,
                    bY-(4*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bR-bB,
                    bY-(4*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(2*bR)-bB,
                    bY-(4*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(3*bR),
                    bY-(4*bB), bB, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX,
                    bY-(5*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bR,
                    bY-(5*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(2*bR),
                    bY-(5*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(3*bR),
                    bY-(5*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bR,
                    bY-(5*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(2*bR),
                    bY-(5*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(3*bR),
                    bY-(5*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bB,
                    bY-(6*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bR+bB,
                    bY-(6*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(2*bR)+bB,
                    bY-(6*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(3*bR)+bB,
                    bY-(6*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bB,
                    bY-(6*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bR-bB,
                    bY-(6*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(2*bR)-bB,
                    bY-(6*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(3*bR)-bB,
                    bY-(6*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX,
                    bY-(7*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+bR,
                    bY-(7*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(2*bR),
                    bY-(7*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX+(3*bR),
                    bY-(7*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-bR,
                    bY-(7*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(2*bR),
                    bY-(7*bB), bR, bB));
            level1BlueprintElementList.add(new Blueprint(getContext(),
                    bX-(3*bR),
                    bY-(7*bB), bR, bB));
        }
    }


    public void update() {
        difficultyButton.setDifficultyState(precisionLevel);
        if (!pauseButton.getPauseState()) {
            joystick.update();
            if (dropObjectList.size() >= 350) {
                rainButton.setRainState(false);
                // code for check progress and next level
                checkBuilding();
            }
            if (level1BlueprintElementList.size()<18){
                createBlueprint();
            }

            if (level == 1 && fallingObjectList.size() >= 24) {
                endSpawn = true;
            }
            else if (level == 2 && fallingObjectList.size() >= 65) {
                endSpawn = true;
            }

            // checks if next object is ready to spawn
            if (FallingObject.readyToSpawn() && !endSpawn) {
                if (level == 1 && fallingObjectList.size() >=15) {
                    fallingObjectList.add(new FallingObject(getContext(),
                            tilePositionX, positionY, width, height, falling, grounded,
                            "roofTile"));
                }
                else if (level == 2 && fallingObjectList.size() >=45){
                    fallingObjectList.add(new FallingObject(getContext(),
                            tilePositionX, positionY, width, height, falling, grounded,
                            "roofTile"));
                }
                else {
                    fallingObjectList.add(new FallingObject(getContext(),
                            positionX, positionY, width, height, falling, grounded,
                            "brick"));
                }
            }
            if (DropObject.readyToSpawn() && endSpawn && rainButton.getRainState()) {
                dropObjectList.add(new DropObject(getContext(),
                        Math.random() * 2000, 0, falling, grounded));
            }
            // Update state of each object
            for (FallingObject fallingObject : fallingObjectList) {
                fallingObject.update();
            }
            for (DropObject dropObject : dropObjectList) {
                dropObject.update();
            }

            // check for collisions
            Iterator<FallingObject> iteratorCollider = fallingObjectList.iterator();
            while (iteratorCollider.hasNext()) {
                int colliderIndex;
                int collideeIndex;
                FallingObject collider = iteratorCollider.next();
                Iterator<DropObject> iteratorDropObject = dropObjectList.iterator();
                while (iteratorDropObject.hasNext()) {
                    DropObject drop = iteratorDropObject.next();
                    if (DropObject.collides(drop, collider)) {
                        dropObjectList.remove(drop);
                        if (collider.getObjectType().equals("brick")) {
                            wetBrick = true;
                        }
                        break;
                    }
                }
                colliderIndex = fallingObjectList.indexOf(collider);
                Iterator<FallingObject> iteratorCollidee = fallingObjectList.iterator();
                while (iteratorCollidee.hasNext()) {
                    FallingObject collidee = iteratorCollidee.next();
                    collideeIndex = fallingObjectList.indexOf(collidee);
                    if (colliderIndex != collideeIndex && collider.getFalling()
                            && collidee.getGrounded()) {
                        if (FallingObject.collides(collider, collidee)) {
                            // remove enemy if colliding
                            FallingObject replaceCollide = new FallingObject(getContext(),
                                    collider.getPositionX(), collider.getPositionY(),
                                    collider.getWidth(), collider.getHeight(),
                                    false, true, collider.getObjectType());
                            fallingObjectList.set(colliderIndex, replaceCollide);
                        }
                    }
                }
                if (wetBrick) {
                    fallingObjectList.remove(collider);
                    wetBrick = false;
                    break;
                } else {
                    wetBrick = false;
                }
            }
            if (!fallingObjectList.isEmpty() && !rainButton.getRainState() && !endSpawn) {
                highlight = new Highlight(getContext(), fallingObjectList.get(index));
                if (rotateButton.getRotateState()) {
                    fallingObjectList.set(index, new FallingObject(getContext(),
                            fallingObjectList.get(index).getPositionX(),
                            fallingObjectList.get(index).getPositionY(), height, height,
                            fallingObjectList.get(index).getFalling(),
                            fallingObjectList.get(index).getGrounded(),
                            fallingObjectList.get(index).getObjectType()));
                    rotateButton.setRotateState(false);
                }
            }
            else if (endSpawn && index == Game.fallingObjectList.size()-1) {
                highlight = new Highlight(getContext(), fallingObjectList.get(index));
                if (rotateButton.getRotateState()) {
                    fallingObjectList.set(index, new FallingObject(getContext(),
                            fallingObjectList.get(index).getPositionX(),
                            fallingObjectList.get(index).getPositionY(), height, height,
                            fallingObjectList.get(index).getFalling(),
                            fallingObjectList.get(index).getGrounded(),
                            fallingObjectList.get(index).getObjectType()));
                    rotateButton.setRotateState(false);
                }
            }
            if (dropObjectList.isEmpty()) {
                if (!fallingObjectList.isEmpty() && !fallingObjectList.get(index).getGrounded()) {

                    double velocityX = joystick.getActuatorX() * MAX_SPEED;
                    double velocityY = joystick.getActuatorY() * MAX_SPEED;

                    // Update position
                    FallingObject movingFallingObject = fallingObjectList.get(index);
                    double newPositionX = movingFallingObject.getPositionX() + velocityX;
                    double newPositionY = movingFallingObject.getPositionY() + velocityY;

                    FallingObject replace = new FallingObject(getContext(),
                            (int) newPositionX, (int) newPositionY,
                            fallingObjectList.get(index).getWidth(),
                            fallingObjectList.get(index).getHeight(),
                            fallingObjectList.get(index).getFalling(),
                            fallingObjectList.get(index).getGrounded(),
                            fallingObjectList.get(index).getObjectType());

                    fallingObjectList.set(index, replace);
                }
            }
        }
    }

    private void checkBuilding() {
        checkPositionList.addAll(fallingObjectList);
        checkPositionBlueprintList.addAll(level1BlueprintElementList);
        Iterator<Blueprint> blueprintIterator = level1BlueprintElementList.iterator();
        while (blueprintIterator.hasNext()) {
            int blueprintIndex;
            int objectIndex;
            Blueprint blueprint = blueprintIterator.next();
            blueprintIndex = level1BlueprintElementList.indexOf(blueprint);
            Iterator<FallingObject> iteratorObject = checkPositionList.iterator();
            while (iteratorObject.hasNext()) {
                FallingObject object = iteratorObject.next();
                objectIndex = checkPositionList.indexOf(object);
                int blueprintPositionX = level1BlueprintElementList.get(blueprintIndex)
                        .getPositionX();
                int blueprintPositionY = level1BlueprintElementList.get(blueprintIndex)
                        .getPositionY();
                int objectPositionX = checkPositionList.get(objectIndex).getPositionX();
                int objectPositionY = checkPositionList.get(objectIndex).getPositionY();
                if ((blueprintPositionX - objectPositionX) > -precisionLevel
                        && (blueprintPositionX - objectPositionX) < precisionLevel
                        && (blueprintPositionY - objectPositionY) > -precisionLevel
                        && (blueprintPositionY - objectPositionY) < precisionLevel) {
                    // approve build
                    checkPositionList.remove(object);
                    checkPositionBlueprintList.remove(blueprint);
                    break;
                } else {
                    continue;
                }
            }
        }
        Iterator<Blueprint> checkBlueprintIterator = checkPositionBlueprintList.iterator();
        while (checkBlueprintIterator.hasNext()) {
            Blueprint blueprint = checkBlueprintIterator.next();
            int blueprintPositionX = blueprint.getPositionX();
            int blueprintPositionY = blueprint.getPositionY();
            wrongPositionList.add(new FallingObject(getContext(),
                    blueprintPositionX, blueprintPositionY,
                    width, height, falling, grounded, objectType));
        }
    }

    public void systemPause () {
        gameLoop.stopLoop();
    }

    public void restart () {
        if (!nextLevel) {
            beginning = true;
        }
        pauseButton.setPauseState(true);
        dropObjectList.clear();
        fallingObjectList.clear();
        level1BlueprintElementList.clear();
        checkPositionList.clear();
        checkPositionBlueprintList.clear();
        wrongPositionList.clear();
        index = 0;
        fallingObjectListSizeCheck = 4;
        rainButton.setRainState(false);
    }

}
