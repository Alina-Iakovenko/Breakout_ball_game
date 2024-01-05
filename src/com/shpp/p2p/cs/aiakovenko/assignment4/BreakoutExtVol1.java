package com.shpp.p2p.cs.aiakovenko.assignment4;

import acm.graphics.*;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
/**
 * The player should hit the ball with the paddle to break all bricks.
 * Information about the total quantity of bricks and broken brick is on the left top corner.
 * The speed of the ball changes every tenth of a brick is broken and is shown under bricks info.
 * Bricks with different colors have different values and effect to score.
 * The Score is on the right bottom corner.
 * When the ball is falling down, the attempt is failed.
 * There are 3 attempts in the game shown in the left bottom corner.
 * */
public class BreakoutExtVol1 extends WindowProgram {
    /* General block */
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 800;
    /**
     * Height should be at least
     * 30px (Paddle offset) + 70px (Bricks offset) + 10px (Ball Diameter)
     * = 110px + (NBrick_row * 8px (Brick height)) + some px for menu and bounds
     */
    public static final int APPLICATION_HEIGHT = 600;
    /**
     * Delay between frames for animation
     */
    private double pauseTime = 15;
    /**
     * Number of turns
     */
    private static int nturns = 3;
    /**
     * ArrayList to get info about left tries
     */
    ArrayList<GOval> remainedBalls;
    /**
     * Boolean for start/finish of the game
     */
    private boolean gameIsStarted = false;
    /**
     * Label with info how to start the game
     */
    GLabel start;
    /**
     * Variable for score
     */
    int score = 0;
    /**
     * Label to get info about the score
     */
    GLabel scoreRate;
    /* Paddle block */
    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;
    /**
     * Variable to create the paddle
     */
    GRect paddle;
    /* Brick block */
    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;
    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;
    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 5;
    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;
    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;
    /**
     * Total number of bricks
     */
    private final int TOTAL_BRICKS = NBRICK_ROWS * NBRICKS_PER_ROW;
    /**
     * Number of remained bricks
     */
    private int brokenBricks = 0;
    /**
     * Lable to get info about number of bricks
     */
    GLabel bricksInfo;
    /* Ball block */
    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 5;
    private static final int BALL_DIAMETER = BALL_RADIUS * 2;
    /**
     * Displacement of the ball
     */
    private double vx = 0; // will be changed with random generator
    private double vy = 3.0; //will be changed according to falling down or up
    /**
     * Label to get info about speed
     */
    GLabel speedInfo;
    /**
     * Speed of the ball
     */
    int speed = 0;
    /**
     * Variable for the ball
     */
    GOval ball;

    public void run() {
        buildTheWall(); // paint bricks
        getBricksInfo(brokenBricks); // print info about total and remained bricks
        getSpeedInfoAndSetSpeed(brokenBricks); // print info about ball's speed
        getNumberOfTries(); // print info about left tries
        getScoreRate(score); // print info about score
        createPaddle(); // create the paddle
        createBall(); // create the ball
        getStartText(); // instruction how to start
        addMouseListeners(); // add MouseListener to conduct the game
        while (true) {
            if (gameIsStarted==true) {
                startToPlay(); // start of the ball's movement
            }
        }
    }
    /**
     * Sets the rules for ball's moving
     */
    private void startToPlay() {
        /* Choose the random angle of the ball's falling */
        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5)) {
            vx = -vx;
        }
        while (gameIsStarted) {
            vy += 3.0 / getHeight(); // change the ball's speed according to the direction of movement and height
            // System.out.println(vy); // the row to check if the speed is changing
            movingBallAndBouncingOffWalls(); // rules for ball movement in the window and when meet a left, top and right window's bound
            hittingWithPaddle(rgen); // rules for ball when meet the paddle
            brakingBricks(rgen); // rules for ball when meet the brick
            fallingOut(); // when the ball falls out of bottom bound
            pause(pauseTime); // set pause for animation
        }
    }
    /**
     * Clicked mouse removes instruction how to start the game,
     * makes the ball visible and changes boolean
     * to make available "startToPlay()" method in "run"
     */
    public void mouseClicked(MouseEvent clickToStart) {
        remove(start);
        ball.setVisible(true);
        if (!gameIsStarted) {
            gameIsStarted = true;
        }
    }
    /**
     * Mouse movement moves the paddle
     * between windows bounds on the same height
     */
    public void mouseMoved(MouseEvent mouseMove) {
        double newX = 0;
        /* Setting rules for movement between windows bounds */
        if ((mouseMove.getX() + PADDLE_WIDTH) > getWidth()) {
            newX = mouseMove.getX() - PADDLE_WIDTH; // stop the paddle before the right wall
        } else if ((mouseMove.getX()) - PADDLE_WIDTH < 0) {
            newX = 0; // stop the paddle before the left wall
        } else {
            newX = mouseMove.getX() - PADDLE_WIDTH; //
        }
        paddle.setLocation(newX, getHeight() - PADDLE_Y_OFFSET);
    }
    /**
     * Adds text to labels with setted font and color
     * and creates the blank for label
     */
    private GLabel setTextForLabel(String string) {
        GLabel label = new GLabel(string, 0, 0);
        label.setFont(Font.DIALOG_INPUT);
        label.setColor(Color.black);
        add(label);
        return label;
    }
    /**
     * Lable with instruction how to star the game
     * disposed in the center part of the window
     */
    private void getStartText() {
        start = setTextForLabel("Click the mouse button to start");
        start.setLocation((int) ((getWidth() - start.getWidth()) / 2), (getHeight() / 2 + BALL_DIAMETER * 2));
    }
    /**
     * Lable for game over
     * disposed in the center part of the window
     */
    private void getGameOverText() {
        start = setTextForLabel("Game is over");
        start.setLocation((int) ((getWidth() - start.getWidth()) / 2), (getHeight() / 2 + BALL_DIAMETER * 2));
    }
    /**
     * Lable with result of the game
     * disposed in the center part of the window
     */
    private void getResult() {
        GLabel result = setTextForLabel("You`ve broke " + brokenBricks + " bricks of " + TOTAL_BRICKS);
        result.setLocation((int) ((getWidth() - result.getWidth()) / 2), (paddle.getY() - ball.getY() / 4));
    }
    /**
     * Lable with the number of remaining attempts
     * disposed in the left bottom part of the window
     */
    private void getNumberOfTries() {
        GLabel tries = setTextForLabel("You have: ");
        tries.setLocation(BALL_DIAMETER, getHeight() - 5);

        double x = BALL_DIAMETER + tries.getWidth();
        double y = getHeight() - 5 - BALL_DIAMETER;
        remainedBalls = new ArrayList<>();
        for (int i = 0; i < nturns; i++) {
            remainedBalls.add(new GOval(x, y, BALL_DIAMETER, BALL_DIAMETER));
            remainedBalls.get(i).setFilled(true);
            remainedBalls.get(i).setColor(Color.GRAY);
            add(remainedBalls.get(i));
            x += BALL_DIAMETER * 1.5;
        }
    }
    /**
     * Lable with the score rate
     * disposed in the right bottom part of the window
     */
    private void getScoreRate(int score) {
        if (scoreRate != null) {
            remove(scoreRate);
        }
        scoreRate = setTextForLabel("Your score is:" + score);
        scoreRate.setLocation(getWidth() - scoreRate.getWidth() - 30, getHeight() - 5);
    }
    /**
     * Lable with the number of remaining and total bricks
     * disposed in the left top part of the window
     */
    private void getBricksInfo(int brokenBricks) {
        if (bricksInfo != null) {
            remove(bricksInfo);
        }
        bricksInfo = setTextForLabel("There are " + (TOTAL_BRICKS - brokenBricks) + " of " + TOTAL_BRICKS + " bricks left");
        bricksInfo.setLocation(BALL_DIAMETER, BALL_DIAMETER);
    }
    /**
     * Sets the speed according to broken bricks
     * and print the lable with speed number
     * disposed in the left top part of the window
     */
    private void getSpeedInfoAndSetSpeed(int brokenBricks) {
        if (speedInfo != null) {
            remove(speedInfo);
        }
        if ((int) (brokenBricks % (TOTAL_BRICKS / 10.0)) == 0) {
            speed++;
            pauseTime -= 1;
        }
        speedInfo = setTextForLabel("Your current speed is " + speed + " of 10");
        speedInfo.setLocation(BALL_DIAMETER, BALL_DIAMETER * 2);
    }
    /**
     * Increases the score according to the color of broken brick
     */
    private void addScore(GObject collidingObject) {
        remove(scoreRate);
        Color colorOfBrick = collidingObject.getColor(); // receive the color of broken brick
        if (colorOfBrick == Color.RED) {
            score += 50;
        } else if (colorOfBrick == Color.ORANGE) {
            score += 40;
        } else if (colorOfBrick == Color.YELLOW) {
            score += 30;
        } else if (colorOfBrick == Color.GREEN) {
            score += 20;
        } else if (colorOfBrick == Color.CYAN) {
            score += 10;
        } else if (colorOfBrick == Color.BLUE) {
            score += 5;
        }
        getScoreRate(score);
    }
    /**
     * Paints default number of rows
     * with default number of bricks
     * with set width and height
     * colored in next of 5 set colors every 2 rows
     */
    private void buildTheWall() {
        int colorIndex = 0;
        double yBrick = BRICK_Y_OFFSET;
        for (int i = 0; i < NBRICK_ROWS; i++) {
            colorIndex = (i / 2) % 5; //get index for next of 5 color in every 2nd row
            Color color = getColorForRow(colorIndex);
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                double xBrick = BRICK_SEP / 2;
                if (j != 0) {
                    xBrick = j * (moldBrick(xBrick, yBrick, color).getWidth() + BRICK_SEP) + BRICK_SEP / 2;
                } // set the x-coordinate for next brick
                add(moldBrick(xBrick, yBrick, color)); // add the brick
            }
            yBrick += BRICK_HEIGHT + BRICK_SEP; // set the y-coordinate for the next row of bricks
        }
    }
    /**
     * Sets the color to index
     */
    private Color getColorForRow(int colorIndex) {
        Color color = new Color(0, 0, 0);
        switch (colorIndex) {
            case 0:
                color = Color.RED;
                break;
            case 1:
                color = Color.ORANGE;
                break;
            case 2:
                color = Color.YELLOW;
                break;
            case 3:
                color = Color.GREEN;
                break;
            case 4:
                color = Color.CYAN;
                break;
            case 5:
                color = Color.BLUE;
        }
        return color;
    }
    /**
     * Creates the brick's blank to use it when builds the wall
     */
    private GObject moldBrick(double xBrick, double yBrick, Color color) {
        double brickWidth = (getWidth() - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW; // calculating brick`s width
        GRect brick = new GRect(xBrick, yBrick, brickWidth, BRICK_HEIGHT);
        brick.setFilled(true);
        brick.setColor(color);
        return brick;
    }
    /**
     * Creates the black invisible ball
     * disposed between the paddle and the latest row of bricks
     * in the center of the window
     */
    private void createBall() {
        int yStart = (getHeight() - PADDLE_Y_OFFSET) -
                ((getHeight() - PADDLE_Y_OFFSET) -
                        (NBRICK_ROWS * BRICK_HEIGHT + NBRICK_ROWS * BRICK_SEP + BRICK_Y_OFFSET)
                ) / 2;
        ball = new GOval((getWidth() - BALL_DIAMETER) / 2, yStart, BALL_DIAMETER, BALL_DIAMETER);
        ball.setFilled(true);
        ball.setColor(Color.DARK_GRAY);
        ball.setVisible(false); // for case when starting text is on the same height
        add(ball);
    }
    /**
     * Creates the black rectangle paddle with set width and height
     * disposed on the bottom of the window with set offset
     * in the center of the window
     */
    private void createPaddle() {
        paddle = new GRect((getWidth() - PADDLE_WIDTH) / 2, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setColor(Color.DARK_GRAY);
        add(paddle);
    }
    /**
     * Sets rules for ball movement in the window
     * and when meet a left, top and right window's bound
     */
    private void movingBallAndBouncingOffWalls() {
        if (ball.getX() >= 0 ||
                ball.getX() + BALL_DIAMETER <= getWidth() ||
                ball.getY() >= 0 ||
                ball.getY() < getHeight()) {
            ball.move(vx, vy);
        }
        /* Bouncing off right and left windows bound */
        if ((ball.getX() <= 0) ||
                (ball.getX() + BALL_DIAMETER >= getWidth())) {
            vx = -vx;
        }
        /* Bouncing off top windows bound */
        if (ball.getY() <= 0) {
            vy = -vy;
        }
    }
    /**
     * Sets rules when the ball falls out of bottom bound
     */
    private void fallingOut() {
        if (ball.getY() > getHeight()) {
            nturns--;
            remainedBalls.get(nturns).setFilled(false); // make unfilled 1 ball that indicate tries
            remove(ball); // remove the ball to be able to create a new more for next try
            gameIsStarted = false; // change boolean to pause the game and makes possible starting of next try only after mouse click
            if (nturns > 0) { // if tries aren`t ended
                createBall(); // create new ball
                getStartText(); // print starting text
            } else { // if tries are ended
                getGameOverText();
                getResult(); // print info about broken bricks
            }
//            vy = -vy; // line for lazy test
        }
    }
    /**
     * Checks if the ball contacts another object and return the object if true
     */
    private GObject getCollidingObject() {
        GObject object = null; // creat the blank for the object

        /* Gets 4 points of vertexes of a square that is described around a ball to use it for checking if there is a contact  */
        GPoint leftTopBall = new GPoint(ball.getX(), ball.getY());
        GPoint rightTopBall = new GPoint(ball.getX() + BALL_DIAMETER, ball.getY());
        GPoint leftBottomBall = new GPoint(ball.getX(), ball.getY() + BALL_DIAMETER);
        GPoint rightBottomBall = new GPoint(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER);
        /* Creats an array of the points */
        GPoint[] ballCorners = {
                leftTopBall,
                rightTopBall,
                leftBottomBall,
                rightBottomBall
        };
        /* Checks if any of the points is on another object */
        for (int i = 0; i < ballCorners.length; i++) {
            object = getElementAt(ballCorners[i].getX(), ballCorners[i].getY());
            if (object != null) {
                break;
            }
        }
        return object;
    }
    /**
     * Changes the angle of ball's movement when meet the paddle
     */
    private void hittingWithPaddle(RandomGenerator rgen) {
        if (getCollidingObject() == paddle) {
            ball.setLocation(ball.getX(), getHeight() - PADDLE_Y_OFFSET - BALL_DIAMETER);
            vx = rgen.nextDouble(1.0, 3.0);

            vy = -vy;
        }
    }
    /**
     * Sets rules when the ball collides the brick
     * */
    private void brakingBricks(RandomGenerator rgen) {
        if (getCollidingObject() != paddle
                && getCollidingObject() != null
                && !(getCollidingObject() instanceof GLabel)
                && !(getCollidingObject() instanceof GOval)){
            addScore(getCollidingObject()); // change the score
            brokenBricks++; //increase the number of broken bricks
            getSpeedInfoAndSetSpeed(brokenBricks); // check if it's time to change the speed and reprint info about it
            getBricksInfo(brokenBricks); // change the info about broken bricks
            remove(getCollidingObject()); // remove the brick collided with the ball
            /* When all bricks are broken */
            if (brokenBricks == TOTAL_BRICKS) {
                gameIsStarted = false;
                remove(speedInfo);
                remove(ball);
                GLabel winner = setTextForLabel("You`re the WINNER!!!");
                winner.setLocation((int) ((getWidth() - winner.getWidth()) / 2), (getHeight() / 2));
                add(winner);
            }
            /* Change the direction of ball's movement */
            vy = -vy;
            vx = rgen.nextDouble(1.0, 3.0);
            /* Прибрати зміну кута по х при зіткненні зі стіною, цеглинкою, і ракеткою */
//            if (rgen.nextBoolean(0.5)) {
//                vx = -vx;
//            }
        }
    }
}

