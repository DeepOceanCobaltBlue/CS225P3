import java.awt.*;
import java.util.Arrays;

/* Work Log: add your name in brackets, the date, and a brief summary of what you contributed that day.
The assignment details that code you wrote requires a comment with your name above it. We will implement
that using this process. Log your work up here, and if you make a revision to existing code then add your
name in a comment on the same line to not interfere with other important documentation requirements.

3/11    [chris]     - Created class, added work log comment.
3/13    [chris]     - added minimum field variables to test gui, used UML
3/14    [chris]     - added getNextPosition() method, no code
3/15    [chris]     - added test code to getNextPosition() to test updating position in GUI


 */
public class Car {
    /* ___ FIELD VARIABLES ___ */
    private Image sprite;
    private int posX;
    private int posY;

    /* ___ CONSTRUCTORS ___ */

    /* ___ FUNCTIONS ___ */

    /* ___ ACCESSORS / MUTATORS ___ */
    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public Image getSprite() {
        return sprite;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    static int trackPosX, trackPosY, i = 0;
    public void getNextPosition() {
        // TODO: update positions with next track coordinate
        this.posX += 1;
        this.posY += 1;
        System.out.println("this.posX = " + this.posX);
        System.out.println("this.posY = " + this.posY);
    }
}
