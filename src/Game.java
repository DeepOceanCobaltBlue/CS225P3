import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Scanner;


/* Work Log: add your name in brackets, the date, and a brief summary of what you contributed that day.
The assignment details that code you wrote requires a comment with your name above it. We will implement
that using this process. Log your work up here, and if you make a revision to existing code then add your
name in a comment on the same line to not interfere with other important documentation requirements.

3/11    [chris]     - Created class, added work log comment.
                    - wrote play() method and added field variable declarations
                    - added default constructor
3/13    [chris]     - added testing code for gui.
3/14    [chris]     - testing gameLoop method for desired functionality, added updateCarPosition() from the UML
3/17    [joey]      - updated gameLoop to incorporate the timer
3/20    [joey]      - added new Instant attribute "startTime"
3/21    [joey]      - added getCurrentTime() method
3/18    [Kat]       - adding code for importing Car and Track data from files
3/21    [tre]       - replace timer delay with constant
3/22    [tre]       - implement updateCarPositions() which now has the cars loop indefinitely around the track
3/22    [chris]     - added initializeGameWindow() to create game from options selected in application
3/23    [chris]     - added control functions array, merged branches and troubleshot, implemented game start method
                    - sequence using actionListener and controlFunction.

 */
public class Game implements ActionListener {
    /**
     * The delay in milliseconds of the game clock timer.
     */
    public static final int TIMER_DELAY = 50;
    private Track raceTrack;
    private Car[] racers;
    private GUI gui;
    private Timer gameClock;
    private Object[] controlFunctions;
    //test
    private Instant startTime = Instant.now();
    private boolean play;


    public Game() {
        this.raceTrack = null;
        this.racers = null;
        this.gui = null;
        this.play = false;
        this.controlFunctions = new Object[2];
        initControlFunctions();
    }

    private void initControlFunctions() {
        JButton continueButton = new JButton();
        continueButton.addActionListener(this);
        this.controlFunctions[0] = continueButton;
    }

    public void play() {
        this.gui = new GUI(this.controlFunctions); // TODO: this isn't working
    }

    private void gameLoop() {
        // TODO: clock starts prior to game start
        if(play) {
            gameClock = new Timer(TIMER_DELAY, e -> {
                updateCarPositions();
                // Instant updatedTime = Instant.now();
                this.gui.updateTimer(getCurrentTime().getSeconds());
            });
            gameClock.start();
        }
    }


    public Duration getCurrentTime(){
        Instant currentTime = Instant.now();
        Duration totalTime = Duration.between(startTime,currentTime);
        return totalTime;
    }

    /**
     * Determines the cars next position along the track and moves the car to that position.
     *
     */
    private void updateCarPositions() {
        for(Car car: this.racers) {
            car.setNextPosition(this.raceTrack.getNextPointOnPath(car.getCurrentPointOnPathIndex()));
            int nextTilePathIndex = car.getCurrentPointOnPathIndex() + 1;

            /* Sets the cars new position along the track. When the car reaches the end of the track
            * the position is reset to the start of the track. */
            car.setCurrentIndexAlongTrackPath(nextTilePathIndex >= raceTrack.getPath().size() - 1 ? 0 : nextTilePathIndex);

            this.gui.drawNewCarPositions();
        }
    }

    public Car importCarFromFile(String fileName) throws IOException {
        Car importCar;
        LinkedList<String> data;

        data = importData(fileName);
        importCar = new Car(data);

        return importCar;
    }

    public Track importTrackFromFile(String fileName) throws IOException {
        Track importTrack;
        LinkedList<String> data;

        data = importData(fileName);
        importTrack = new Track(data);

        return importTrack;
    }


    public LinkedList<String> importData(String fileName) throws IOException {
        FileInputStream inFS = null;
        LinkedList<String> data;
        try {
            inFS = new FileInputStream(fileName);
            data = extractInfoFromFile(inFS);
        } catch (FileNotFoundException e) {
            data = new LinkedList<String>();
        } finally {
            if (inFS != null) {
                inFS.close();
            }
        }
        return data;
    }

    public LinkedList<String> extractInfoFromFile(FileInputStream fIS) {
        LinkedList<String> entryList = new LinkedList<String>();
        Scanner scnr = new Scanner(fIS);
        while (scnr.hasNextLine()) {
            entryList.add(scnr.nextLine());
        }
        return entryList;
    }

    public void initializeGameWindow(JButton pressed) {
        // TODO: Create JButton[] of control functions that affect the state of the game then pass it as part of gameAssets to GUI object
        Object[] args = this.gui.extractGameArgs(pressed);
        this.racers = (Car[]) args[0];
        String filename = ((String) args[1]);
        try {
            this.raceTrack = importTrackFromFile(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Object[] assets = new Object[] { this.racers, this.raceTrack};
        this.gui.gameAssetsSelected(assets);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: for now, there is only one control button
        initializeGameWindow((JButton) e.getSource());
        this.play = true;
        gameLoop();
    }
}
