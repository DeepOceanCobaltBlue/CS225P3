import javax.swing.*;
import java.awt.*;

/* Work Log: add your name in brackets, the date, and a brief summary of what you contributed that day.
The assignment details that code you wrote requires a comment with your name above it. We will implement
that using this process. Log your work up here, and if you make a revision to existing code then add your
name in a comment on the same line to not interfere with other important documentation requirements.

3/11    [chris]     - Created class, added work log comment.
3/13    [chris]     - added minimum field variables to test gui, used UML
3/14    [chris]     - added getNextPosition() method, no code
3/15    [chris]     - added test code to getNextPosition() to test updating position in GUI
3/18    [Kat]       - added constructor to allow LinkedList to be passed in from importCarFromFile
3/21    [chris]     - implemented UML design
3/22    [tre]       - add method for setting the cars current path index
3/26    [Kat]       - added getter for checkpointIndex, added lastCheckpoint field to hold the index of the last
                      checkpoint passed for carPanel info
3/26    [Kat]       - changed checkpoints to be an int[] for ease in displaying in carPanel, added getters and setters
                      for checkpoints and checkpointIndex

 */

/**
 * Car sprites that move along the raceTrack
 */
public class Car extends JLabel {
    /* ___ FIELD VARIABLES ___ */
    /* Used to determine which sprite to draw when a car changes direction */
    enum direction {UP, DOWN, LEFT, RIGHT}

    /* The unaltered starting speed of a car */
    private int baseSpeed;
    /* Used for determining sprite to draw when a car changes direction */
    private direction currDir;
    /* Name assigned to each Car or players name */
    private String name;
    /* This speed is the altered, current, and variable speed of a car used to update position */
    private int speed;
    /* Series of checkpoint numbers car must reach to complete race */
    private int[] checkpoints;
    /* Current point car is heading towards */
    private int checkpointIndex;
    /* Holds the index value of the Track.path point the car is currently at */
    private int currentIndexOnTrackPointPath;
    /* Cars x position on raceTrack panel */
    private int posX;
    /* Cars y position on raceTrack panel */
    private int posY;
    private boolean wasRotated;

    /* ___ CONSTRUCTORS ___ */
    public Car() {
        this.baseSpeed = 0;
        this.currDir = direction.UP;
        this.name = null;
        this.speed = 0;
        this.checkpoints = null;
        this.checkpointIndex = 0;
        this.currentIndexOnTrackPointPath = 0;
        this.posX = 0;
        this.posY = 0;
        this.wasRotated = false;
    }

    /**
     * Create a new car with the color name <code>name</code>, a sprite image of <code>image</code>,
     * an array of assigned checkpoints for this car, and a starting position of <code>startingX</code> and
     * <code>startingY</code> on the game canvas.
     * @param name The name of the color. The name represents a color.
     * @param carImage The image used for the car's sprite.
     * @param checkpoints The checkpoints assigned to this car.
     * @param startingX The coordinate along the x-axis the car should start on.
     * @param startingY The coordinate along the y-axis the car should start on.
     */
    public Car(String name, Image carImage, int[] checkpoints, int startingX, int startingY) {
        this();
        this.name = name;
        this.checkpoints = checkpoints;
        this.posY = startingY;
        this.posX = startingX;
        this.setIcon(new ImageIcon(carImage));
        setBaseSpeedByColor();
        this.speed = this.baseSpeed;
    }

    /* ___ FUNCTIONS ___ */

    /**
     * Sets the cars base speed depending on the color of the car.
     */
    private void setBaseSpeedByColor() {
        switch(this.name) {
            case "purple":
            case "blue":
                this.baseSpeed = 1;
                break;
            case "green":
            case "yellow":
                this.baseSpeed = 2;
                break;
            case "orange":
            case "red":
                this.baseSpeed = 3;
                break;
        }
    }

    /**
     * Checks if the car should be rotated.
     * @param next The next point the car will occupy.
     */
    public void checkForRotation(Point next) {
        // if orientation = down and x changes =>  turn left or turn right
        // change sprite
        if( (next.y - this.posY) >= 1)  { // Moving downwards
            if(!(this.currDir.equals(direction.DOWN))) {
                this.currDir = direction.DOWN;
                this.wasRotated = true;
            }
        }
        if( (next.y - this.posY) < 0)  { // Moving upwards
            if(!(this.currDir.equals(direction.UP))) {
                this.currDir = direction.UP;
                this.wasRotated = true;
            }

        }
        if( (next.x - this.posX) >= 1)  { // Moving right
            if(!(this.currDir.equals(direction.RIGHT))) {
                this.currDir = direction.RIGHT;
                this.wasRotated = true;
            }

        }
        if( (next.x - this.posX) < 0)  { // Moving left
            if(!(this.currDir.equals(direction.LEFT))) {
                this.currDir = direction.LEFT;
                this.wasRotated = true;
            }

        }

    }

    /**
     * Increments the index of the car on the tracks list of points specifying the
     * path the car will take around the track.
     * @param amount The amount to add to the cars index along the tracks point list.
     */
    public void incrementCurrentIndexOnTrackPointPath(int amount) {
        this.currentIndexOnTrackPointPath += amount;
    }

    /**
     * Checks if the cars index along the tracks path is greater than <code>length</code>, and
     * clamps it at the last index.
     * @param length The length to check against.
     */
    public void checkIndexRange(int length) {
        if(this.currentIndexOnTrackPointPath >= length) {
            setCurrentIndexOnTrackPointPath((this.currentIndexOnTrackPointPath - length));
        }
    }

    /**
     * Increments the index of the cars checkpoint array returning <code>true</code> only
     * if the checkpoint index is equal to or greater than the checkpoints array length.
     * @return False if the last checkpoint hasn't been reached, otherwise true.
     */
    public boolean incrementCheckpointIndex() {
        if(this.checkpointIndex < (this.checkpoints.length - 1)) {
            this.checkpointIndex++;
            return false;
        }
        return true; // final checkpoint reached by racer
    }

    /* ___ ACCESSORS / MUTATORS ___ */
    public Point getPosition() {
        return new Point(this.posX, this.posY);
    }
    public void setNextPosition(Point p) {
        if(this.posX == 0 && this.posY == 0) {
            // initialize first position
            this.posX = p.x;
            this.posY = p.y;
            return;
        }
        checkForRotation(p);
        this.posX = p.x;
        this.posY = p.y;
    }
    public int getCurrentIndexOnTrackPointPath() {
        return this.currentIndexOnTrackPointPath;
    }
    public void setCurrentIndexOnTrackPointPath(int index) {
        this.currentIndexOnTrackPointPath = index;
    }
    public int getCheckpointIndex() {
        return this.checkpointIndex;
    }
    public int[] getCheckpoints() {
        return this.checkpoints;
    }
    public int getCheckpointAtIndex(int index) {
        return this.checkpoints[index];
    }
    public void setCheckpoints(int[] checkpoints) {
        this.checkpoints = checkpoints;
    }
    public int getBaseSpeed() {
        return baseSpeed;
    }
    public int getSpeed() {
        return this.speed;
    }
    public void setSpeed(int value) {
        this.speed = value;
    }
    public int getCurrDir() {
        return this.currDir.ordinal();
    }
    public boolean getWasRotated() {
        return this.wasRotated;
    }
    public void setWasRotated(boolean b) {
        this.wasRotated = b;
    }
    public String getName() {
        return this.name;
    }
}

