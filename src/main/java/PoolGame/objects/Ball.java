package PoolGame.objects;

import PoolGame.Config;
import PoolGame.strategy.BallStrategy;
import PoolGame.strategy.BlueStrategy;
import PoolGame.strategy.BrownStrategy;
import PoolGame.strategy.PocketStrategy;
import javafx.scene.paint.Paint;

/** Holds information for all ball-related objects. */
public class Ball {

    private Paint colour;
    private double xPosition;
    private double yPosition;
    private double startX;
    private double startY;
    private double xVelocity;
    private double yVelocity;
    private double mass;
    private double radius;
    private boolean isCue;
    private boolean isActive;
    private PocketStrategy strategy;

    private int points;

    private final double MAXVEL = 20;

    public Ball(String colour, double xPosition, double yPosition, double xVelocity, double yVelocity, double mass,
            boolean isCue, PocketStrategy strategy, int points) {
        this.colour = Paint.valueOf(colour);
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.startX = xPosition;
        this.startY = yPosition;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.mass = mass;
        this.radius = 10;
        this.isCue = isCue;
        this.isActive = true;
        this.strategy = strategy;
        this.points = points;
    }

    /**
     * Updates ball position per tick.
     */
    public void tick() {
        xPosition += xVelocity;
        yPosition += yVelocity;
    }

    /**
     * Resets ball position, velocity, and activity.
     */
    public void reset() {
        resetPosition();
        isActive = true;
        strategy.reset();
    }

    /**
     * Resets ball position and velocity.
     */
    public void resetPosition() {
        xPosition = startX;
        yPosition = startY;
        xVelocity = 0;
        yVelocity = 0;
    }

    /**
     * Removes ball from play.
     * 
     * @return true if ball is successfully removed
     */
    public boolean remove() {
        if (strategy.remove()) {
            isActive = false;
            return true;
        } else {
            resetPosition();
            return false;
        }
    }

    /**
     * Sets x-axis velocity of ball.
     * 
     * @param xVelocity of ball.
     */
    public void setxVel(double xVelocity) {
        if (xVelocity > MAXVEL) {
            this.xVelocity = MAXVEL;
        } else if (xVelocity < -MAXVEL) {
            this.xVelocity = -MAXVEL;
        } else {
            this.xVelocity = xVelocity;
        }
    }

    /**
     * Sets y-axis velocity of ball.
     * 
     * @param yVelocity of ball.
     */
    public void setyVel(double yVelocity) {
        if (yVelocity > MAXVEL) {
            this.yVelocity = MAXVEL;
        } else if (yVelocity < -MAXVEL) {
            this.yVelocity = -MAXVEL;
        } else {
            this.yVelocity = yVelocity;
        }
    }

    /**
     * Sets x-axis position of ball.
     * 
     * @param xPosition of ball.
     */
    public void setxPos(double xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * Sets y-axis position of ball.
     * 
     * @param yPosition of ball.
     */
    public void setyPos(double yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * Getter method for radius of ball.
     * 
     * @return radius length.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Getter method for x-position of ball.
     * 
     * @return x position.
     */
    public double getxPos() {
        return xPosition + Config.getTableBuffer();
    }

    /**
     * Getter method for y-position of ball.
     * 
     * @return y position.
     */
    public double getyPos() {
        return yPosition + Config.getTableBuffer();
    }

    /**
     * Getter method for starting x-position of ball.
     * 
     * @return starting x position.
     */
    public double getStartXPos() {
        return startX;
    }

    /**
     * Getter method for starting y-position of ball.
     * 
     * @return starting y position.
     */
    public double getStartYPos() {
        return startY;
    }

    /**
     * Getter method for starting mass of ball.
     * 
     * @return mass.
     */
    public double getMass() {
        return mass;
    }

    /**
     * Getter method for colour of ball.
     * 
     * @return colour.
     */
    public Paint getColour() {
        return colour;
    }

    /**
     * Getter method for x-axis velocity of ball.
     * 
     * @return x velocity.
     */
    public double getxVel() {
        return xVelocity;
    }

    /**
     * Getter method for y-axis velocity of ball.
     * 
     * @return y velocity.
     */
    public double getyVel() {
        return yVelocity;
    }

    /**
     * Getter method for whether ball is cue ball.
     * 
     * @return true if ball is cue ball.
     */
    public boolean isCue() {
        return isCue;
    }

    /**
     * Getter method for whether ball is active.
     * 
     * @return true if ball is active.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Getter method for points of ball.
     *
     * @return points.
     */
    public int getPoints() {
        return points;
    }

    public PocketStrategy getStrategy() {
        return this.strategy;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setStartPos(double xStart, double yStart) {
        this.startX = xStart;
        this.startY = yStart;
    }

    /**
     * Immediately removes the ball from the game (used in cheating)
     *
     */
    public void removeImmediately() {
        strategy.remove();
        this.isActive = false;
    }

    /**
     * Returns a copy of this ball instance
     *
     * @return Ball
     */
    public Ball clone() {
        Paint colour = this.colour;
        double xPosition = this.getxPos()- Config.getTableBuffer();
        double yPosition = this.getyPos()- Config.getTableBuffer();
        double startX = this.getStartXPos();
        double startY = this.getStartYPos();
        double xVelocity = this.getxVel();
        double yVelocity = this.getyVel();
        double mass = this.getMass();
        boolean isCue = this.isCue();
        boolean isActive = this.isActive();
        PocketStrategy strat = null;
        if (this.colour.equals(Paint.valueOf("red")) || this.colour.equals(Paint.valueOf("yellow")) || this.colour.equals(Paint.valueOf("orange")) || this.colour.equals(Paint.valueOf("white"))) {
            strat = new BallStrategy(this.getStrategy());
        } else if (this.colour.equals(Paint.valueOf("blue")) || this.colour.equals(Paint.valueOf("green")) || this.colour.equals(Paint.valueOf("purple"))) {
            strat = new BlueStrategy(this.getStrategy());
        } else if (this.colour.equals(Paint.valueOf("brown")) || this.colour.equals(Paint.valueOf("black"))) {
            strat = new BrownStrategy(this.getStrategy());
        };
        int points = this.getPoints();
        Ball ball = new Ball(colour.toString(), xPosition, yPosition, xVelocity, yVelocity, mass, isCue, strat, points);
        ball.setActive(isActive);
        ball.setStartPos(startX, startY);
        return ball;
    }

}
