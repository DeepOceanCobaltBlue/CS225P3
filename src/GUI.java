import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/* Work Log: add your name in brackets, the date, and a brief summary of what you contributed that day.
The assignment details that code you wrote requires a comment with your name above it. We will implement
that using this process. Log your work up here, and if you make a revision to existing code then add your
name in a comment on the same line to not interfere with other important documentation requirements.

3/11    [chris]     - Created class, added work log comment.
                    - added field variables, wrote constructors and createGUI method
                    - Worked on the gameWindow in createGUI method
3/13    [chris]     - worked on getting tiles to display in gameWindowPanel
3/14    [chris]     - added JLayeredPane and carPanel implementation.
3/15    [chris]     - tested how to get cars to move, no dedicated path. Got it working
                    - added empty bottom panel to display information about racers to user
                    - rearranged code by adding createWindow methods to simplify createGUI()
3/17    [Joey]      - added updateTimer() method
3/20    [Joey]      - Updated updateTimer() to keep time better
3/20    [Kat]       - added Car display panels to bottom of UI showing current position - only shows 2 right now
3/21    [tre]       - Replace tile width and tile height with constants.
3/22    [tre]       - replace TILE_WIDTH and TILE_HEIGHT constants with TILE_SIZE
                    - add cars to carPanel
                    - replace magic numbers of value 50 with TILE_SIZE constant
3/22    [chris]     - wrote a couple methods to extract game options from selection window
                    - then trigger the Game class to create the new game window and display it.
3/23    [chris]     - added controlFunctions support
3/25    [Kat]       - reconfigured carPanels to display appropriate number of cars
3/25    [chris]     - added turning methods to swap sprites based on direction
3/25    [Kat]       - changed carPanel to pull name of car for identification, also changed to displaying speed and
                      last checkpoint Passed
3/26    [Kat]       - changed carPanel to show speed (still), checkpoint list, and checkpoint Index
3/26    [Kat]       - changed endGame to take in parameter of victory car, and open JOptionPane to show a message of
                      which car won.
3/27    [Kat]       - added comments for GUI functions starting at createMenuWindow to actionPerformed


 */

/**
 * The various windows displayed to the user. This class creates and swaps between them based on user input.
 */
public class GUI implements ActionListener{
    // TODO: 3/21/2023 TILE_SIZE may need to be moved to another file.
    /**
     * The width of game tiles in pixels.
     */
    public static final int TILE_SIZE = 50;

    /* ___ FIELD VARIABLES ___ */
    /* Base frame for the application */
    private JFrame rootFrame;
    /* Start menu */
    private JPanel menuWindowPanel;
    /* Window to display game, track, cars, and movement */
    private JPanel gameWindowPanel;
    private JPanel startGameOptionsWindowPanel;
    /* Objects used by 'Game' that have a graphical component */
    private Object[] gameAssets;
    /* Car sprites as well as other images used by the application */
    private Image[] images;
    /* gameAsset - the Track object to be drawn */
    private Track gameTrack;
    /* gameAsset - the Car objects to be drawn */
    private Car[] gameCars;
    /* Bottom panels of gameWindow that display racer information */
    private JLabel[][] carPanelLabels;
    /* Displays the elapsed time of the race */
    private JLabel timeLabel;
    /* Buttons used by Game class to alter the state of the game */
    private Object[] controls;
    /* Displays the combined Track tile sprites and the Car's sprites */
    private JLayeredPane centerPanel;
    /* Counter for number of cars selected to participate in a race */
    private int carsSelected;
    /* counter for the number of tracks selected to race on (limit = 1) */
    private int trackSelected;

    /* ___ CONSTRUCTORS ___ */
    public GUI(Object[] controls) {
        this.rootFrame = new JFrame();
        this.menuWindowPanel = new JPanel();
        this.gameWindowPanel = new JPanel();
        this.gameAssets = null;
        this.controls = controls;
        this.carsSelected = 0;
        this.trackSelected = 0;
        createGUI(); // initialize menuWindow and application framework
    }

    /* ___ FUNCTIONS ___ */
    /**
     * initialize and create the portions of the gui necessary to start the application and
     * launch the start window(menuWindowPanel).
     */
    private void createGUI() {
        this.rootFrame.setPreferredSize(new Dimension(1000, 700));
        this.rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.rootFrame.setTitle("Track Star Racer! 9001");
        this.rootFrame.setResizable(false);

        loadImages();
        createMenuWindow();
        createGameOptionsWindow();

        this.rootFrame.setContentPane(this.menuWindowPanel); // starting window
        this.rootFrame.pack();
        this.rootFrame.setVisible(true);
    }

    /* Window Creation Methods */
    /**
     * this window presents the user with the available cars and tracks to choose from.
     */
    private void createGameOptionsWindow() {
        // root panel
        this.startGameOptionsWindowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.startGameOptionsWindowPanel.setPreferredSize(new Dimension(1000, 700));

        // root panels "content panel", display panels composed onto this one
        JPanel optionsRootPanel = new JPanel(new BorderLayout());
        optionsRootPanel.setPreferredSize(new Dimension(1000, 700));
        optionsRootPanel.setBackground(Color.BLUE);

        // gameAsset display panels
        JPanel topPanel = new JPanel();     // feedback
        JLabel topLabel = new JLabel(new ImageIcon(this.images[30]));
        topPanel.setPreferredSize(new Dimension(1000, 100));
        topPanel.add(topLabel);

        JPanel centerInfoPanel = new JPanel();
        centerInfoPanel.setBorder(new LineBorder(Color.BLACK));
        String instructions = "Instructions:\n" +
                "1) Select between 1 and 3 cars.\n" +
                "2) Select 1 available track.\n" +
                "3) Press 'Continue' button below to begin the race!\n\n" +
                "Notes:\n" +
                "The cars vary in performance!\n" +
                "Red & Orange are super cars\nYellow & Green are street-legal\nBlue & Purple are barely mobile\n" +
                "Now lets go racing!";
        JTextArea instructionsLabel = new JTextArea();
        instructionsLabel.setWrapStyleWord(true);
        instructionsLabel.setLineWrap(true);
        instructionsLabel.setColumns(24);
        instructionsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        instructionsLabel.setText(instructions);
        centerInfoPanel.add(instructionsLabel);


        JPanel leftPanel = new JPanel(new GridLayout(3, 2, 10, 10));    // display cars
        leftPanel.setBorder(new LineBorder(Color.BLACK));
        leftPanel.setPreferredSize(new Dimension(300, 500));

        JPanel rightPanel = new JPanel(new GridLayout(2, 2, 10, 10));   // display tracks
        rightPanel.setBorder(new LineBorder(Color.BLACK));
        rightPanel.setPreferredSize(new Dimension(350, 500));

        JPanel bottomPanel = new JPanel();  // buttons
        bottomPanel.setPreferredSize(new Dimension(1000, 100));

        // bottom panel components and settings
        JButton startButton = (JButton) this.controls[0]; // control function
        startButton.setText("Continue");
        startButton.setPreferredSize(new Dimension(200, 40));
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        bottomPanel.add(startButton);

        // right panel components and settings, disabled due to limited implementation
        JButton track1Btn = makeTrackOptionButton(1);
        JButton track2Btn = makeTrackOptionButton(2);
        track2Btn.setEnabled(false);
        JButton track3Btn = makeTrackOptionButton(3);
        track3Btn.setEnabled(false);
        JButton track4Btn = makeTrackOptionButton(4);
        track4Btn.setEnabled(false);
        rightPanel.add(track1Btn);
        rightPanel.add(track2Btn);
        rightPanel.add(track3Btn);
        rightPanel.add(track4Btn);

        // Left panel components and settings
        JButton car1Btn = makeCarOptionsButton(2);
        car1Btn.setText("blue");
        JButton car2Btn = makeCarOptionsButton(6);
        car2Btn.setText("green");
        JButton car3Btn = makeCarOptionsButton(10);
        car3Btn.setText("orange");
        JButton car4Btn = makeCarOptionsButton(14);
        car4Btn.setText("purple");
        JButton car5Btn = makeCarOptionsButton(18);
        car5Btn.setText("red");
        JButton car6Btn = makeCarOptionsButton(22);
        car6Btn.setText("yellow");
        leftPanel.add(car1Btn);
        leftPanel.add(car2Btn);
        leftPanel.add(car3Btn);
        leftPanel.add(car4Btn);
        leftPanel.add(car5Btn);
        leftPanel.add(car6Btn);

        // Compose Window elements
        optionsRootPanel.add(topPanel, BorderLayout.NORTH);
        optionsRootPanel.add(leftPanel, BorderLayout.WEST);
        optionsRootPanel.add(rightPanel, BorderLayout.EAST);
        optionsRootPanel.add(bottomPanel, BorderLayout.SOUTH);
        optionsRootPanel.add(centerInfoPanel, BorderLayout.CENTER);
        this.startGameOptionsWindowPanel.add(optionsRootPanel);
    }

    /**
     * This window presents the user the choice to start a new game, make a car, or make a track
     */
    private void createMenuWindow() {
        // root panel
        this.menuWindowPanel.setPreferredSize(new Dimension(1000, 700));
        this.menuWindowPanel.setLayout(new BorderLayout());

        // root panels "content panel", all assets composed onto this one
        JLayeredPane menuRootPane = new JLayeredPane();

        // Background image pane
        JLabel backgroundImage = new JLabel(new ImageIcon(this.images[1]));
        JPanel backgroundImagePane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        backgroundImagePane.setBounds(0, 0, 1000, 700);
        backgroundImagePane.setOpaque(false);
        backgroundImagePane.add(backgroundImage);

        // button pane
        JPanel buttonPane = new JPanel(new GridBagLayout());
        buttonPane.setBounds(0, 0, 1000, 700);
        buttonPane.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(15, 0, 15, 0);

        JButton startButton = new JButton("Start New Game");
        startButton.addActionListener(this);
        startButton.setPreferredSize(new Dimension(400, 40));
        constraints.gridy = 0;
        buttonPane.add(startButton, constraints);

        JButton createCarButton = new JButton("Design a Car");
        createCarButton.addActionListener(this);
        createCarButton.setPreferredSize(new Dimension(400, 40));
        constraints.gridy = 1;
        buttonPane.add(createCarButton, constraints);

        JButton createTrackButton = new JButton("Create a Track");
        createTrackButton.addActionListener(this);
        createTrackButton.setPreferredSize(new Dimension(400, 40));

        constraints.gridy = 2;
        buttonPane.add(createTrackButton, constraints);

        menuRootPane.add(backgroundImagePane, new Integer(0));
        menuRootPane.add(buttonPane, new Integer(1));
        this.menuWindowPanel.add(menuRootPane, BorderLayout.CENTER);
    }

    /**
     * This window shows the race being played out, and keeps track of all the car's statuses
     */
    private void createGameWindow() {
        /* _Game Window_ */
        this.gameWindowPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.gameWindowPanel.setPreferredSize(new Dimension(1000,700));

        JPanel topGamePanel = new JPanel();
        topGamePanel.setLayout(new BoxLayout(topGamePanel, BoxLayout.X_AXIS));
        topGamePanel.setBackground(Color.BLACK);
        JPanel bottomGamePanel = createGameWindowInfoPanel(this.gameCars.length);

        // checkered flag side panels
        JPanel leftRootPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        leftRootPanel.setBounds(0, 0, 100, 500);
        leftRootPanel.setBackground(Color.BLACK);

        JLabel leftImageLabel = new JLabel();
        leftImageLabel.setIcon(new ImageIcon(this.images[0]));
        leftRootPanel.add(leftImageLabel);

        JPanel rightRootPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rightRootPanel.setBackground(Color.BLACK);

        JLabel rightImageLabel = new JLabel();
        rightImageLabel.setIcon(new ImageIcon(this.images[0]));
        rightRootPanel.add(rightImageLabel);

        // Where the game will be displayed
        this.centerPanel = new JLayeredPane();
        this.centerPanel.setPreferredSize(new Dimension(800, 500));

        // Panel to house the racetrack Tile sprites
        JPanel gameTilePanel = new JPanel(new GridBagLayout());
        gameTilePanel.setBackground(new Color(67, 174, 32));
        gameTilePanel.setBounds(50, 20, 700, 500);
        GridBagConstraints constraints2 = new GridBagConstraints();
        for (int row = 0; row < this.gameTrack.getRaceTrack().length; row++) {
            for (int col = 0; col < this.gameTrack.getRaceTrack()[0].length; col++) {
                constraints2.gridx = col;
                constraints2.gridy = row;
                gameTilePanel.add(this.gameTrack.getTileAtPoint(row, col), constraints2);
            }
        }

        /*  transparent panel for cars to move across using (x,y) coordinate values, cars are drawn over
         *  the racetrack sprites.
         */
        JPanel carPanel = new JPanel();
        carPanel.setOpaque(false);
        carPanel.setBounds(50, 0, 700, 500);
        carPanel.setLayout(null);

        for(Car car : this.gameCars) {
            /* Assign each car a random starting location */
            Random rng = new Random();
            car.incrementCurrentIndexOnTrackPointPath(rng.nextInt(this.gameTrack.getPath().size()));
            carPanel.add(car);
        }
        // Compose gameplay area
        this.centerPanel.add(gameTilePanel, new Integer(1));
        this.centerPanel.add(carPanel, new Integer(2));

        // feedback panel for race timer
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setPreferredSize(new Dimension(150, 100));
        feedbackPanel.setBorder(new LineBorder(Color.BLACK));

        JLabel feedbackTitleLabel = new JLabel("Race Clock");
        feedbackTitleLabel.setFont(new Font("Helvetica", Font.PLAIN,25 ));

        this.timeLabel = new JLabel("00:00:00");
        this.timeLabel.setBounds(0,5,50,50);
        this.timeLabel.setFont(new Font("Helvetica", Font.PLAIN,25 ));

        feedbackPanel.add(feedbackTitleLabel);
        feedbackPanel.add(this.timeLabel);
        ((JPanel)bottomGamePanel.getComponent(0)).add(feedbackPanel);

        // Display panels for information about each car
        JPanel[] carInfoPanels = new JPanel[gameCars.length];
        JLabel[] carNameLabels = new JLabel[gameCars.length];
        this.carPanelLabels = new JLabel[gameCars.length][3]; // [# of cars][ x and y values ]

        GridBagConstraints layoutConstraints = new GridBagConstraints();
        layoutConstraints.weightx = 1.0;
        layoutConstraints.weighty = 1.0;

        for (int i = 0; i < gameCars.length; i++) {
            carInfoPanels[i] = new JPanel(new GridBagLayout());
            carInfoPanels[i].setBorder(new LineBorder(Color.green));
            carInfoPanels[i].setPreferredSize(new Dimension(225, 100));

            carNameLabels[i] = new JLabel(gameCars[i].getName());
            this.carPanelLabels[i][0] = new JLabel();
            this.carPanelLabels[i][1] = new JLabel();
            this.carPanelLabels[i][2]= new JLabel();

            layoutConstraints.gridy = 0;
            layoutConstraints.gridx = 0;
            layoutConstraints.gridwidth = 2;
            carInfoPanels[i].add(carNameLabels[i], layoutConstraints);

            layoutConstraints.gridwidth = 1;
            layoutConstraints.gridy = 1;

            JLabel speedLabel = new JLabel("Current Speed: ");
            carInfoPanels[i].add(speedLabel, layoutConstraints);

            layoutConstraints.gridx = 1;
            carInfoPanels[i].add(carPanelLabels[i][0], layoutConstraints);

            layoutConstraints.gridx = 0;
            layoutConstraints.gridy = 2;
            JLabel pathLabel = new JLabel("Checkpoint Path: ");
            carInfoPanels[i].add(pathLabel, layoutConstraints);

            layoutConstraints.gridx = 1;
            carInfoPanels[i].add(carPanelLabels[i][1], layoutConstraints);

            layoutConstraints.gridx = 0;
            layoutConstraints.gridy = 3;
            JLabel indexLabel = new JLabel("Checkpoint Index: ");
            carInfoPanels[i].add(indexLabel, layoutConstraints);

            layoutConstraints.gridx = 1;
            carInfoPanels[i].add(carPanelLabels[i][2], layoutConstraints);


            // BottomGamePanel created in createGameWindowInfoPanel() and has a JPanel added to it
            ((JPanel)bottomGamePanel.getComponent(0)).add(carInfoPanels[i]);
        }

        // Compose overall game window
        topGamePanel.add(leftRootPanel);
        topGamePanel.add(this.centerPanel);
        topGamePanel.add(rightRootPanel);

        this.gameWindowPanel.add(topGamePanel);
        this.gameWindowPanel.add(bottomGamePanel);
    }

    /* Window creation helper methods */

    /**
     * Creates the buttons representing which cars can be selected
     * @param index - imageID
     * @return - option button for this car
     */
    private JButton makeCarOptionsButton(int index) {
        JButton button = new JButton();
        button.setDoubleBuffered(true);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setIcon(new ImageIcon(this.images[index]));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.addActionListener(this);
        return button;
    }

    /**
     * Creates the buttons for the tracks that can be selected
     * @param index - which track to load
     * @return - button to select this track
     */
    private JButton makeTrackOptionButton(int index) {
        JButton button = new JButton("Track" + index);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setIcon(new ImageIcon(this.images[(index + 25)]));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.addActionListener(this);
        return button;
    }

    /**
     * Swaps the color of the button to indicate selected or not selected
     * @param button - the button pressed
     */
    private void cycleButtonHighlight(JButton button) {
        Color color = new Color(92, 255, 63, 126);
        if((this.carsSelected >= 3) && (!(button.getBackground().equals(color)))) {
            return;
        }
        if (button.getBackground().equals(color)) {
            button.setBackground(null);
            button.setSelected(false);
            this.carsSelected--;
        } else {
            button.setBackground(color);
            button.setSelected(true);
            this.carsSelected++;
        }

    }

    /**
     * checks to make sure at least one car and exactly one track has been selected before allowing the continue button
     * to be pressed
     */
    private void checkForStartConditions() {
        if((this.carsSelected >= 1) && (this.carsSelected < 4) && (this.trackSelected == 1)) {
            ((JButton)this.controls[0]).setEnabled(true);
        } else {
            ((JButton)this.controls[0]).setEnabled(false);
        }
    }

    /**
     * ensures only one button is selected for tracks
     * @param button - only this track is selected
     */
    private void singleSelectionOfButtons(JButton button) {
        this.trackSelected = 1;
        for(Component c : button.getParent().getComponents()) {
            c.setBackground(null);
            ((JButton)c).setSelected(false);
        }
        button.setBackground(new Color(92, 255, 63, 126));
        button.setSelected(true);
    }

    /**
     * This window acts as the overall container for the speed panel and the car info panel
     * @param length - number of cars
     * @return - panel containing the information display panel
     */
    private JPanel createGameWindowInfoPanel(int length) {
        JPanel bottomGamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        JPanel infoPanel = new JPanel(new GridLayout(1, length, 10, 10));
        bottomGamePanel.add(infoPanel);
        return bottomGamePanel;
    }

    /* Class Functions */

    /**
     * Loads images from disk for the car sprites, track icons, and other images used in window creation
     */
    private void loadImages() {
        this.images = new Image[31];
        try {
            this.images[0] = ImageIO.read(new File("Sprites\\Checkered.png"));
            this.images[1] = ImageIO.read(new File("Sprites\\MenuImage.png"));

            this.images[2] = ImageIO.read(new File("Sprites\\carSprites\\blueCarUp.png"));
            this.images[3] = ImageIO.read(new File("Sprites\\carSprites\\blueCarDown.png"));
            this.images[4] = ImageIO.read(new File("Sprites\\carSprites\\blueCarLeft.png"));
            this.images[5] = ImageIO.read(new File("Sprites\\carSprites\\blueCarRight.png"));

            this.images[6] = ImageIO.read(new File("Sprites\\carSprites\\greenCarUp.png"));
            this.images[7] = ImageIO.read(new File("Sprites\\carSprites\\greenCarDown.png"));
            this.images[8] = ImageIO.read(new File("Sprites\\carSprites\\greenCarLeft.png"));
            this.images[9] = ImageIO.read(new File("Sprites\\carSprites\\greenCarRight.png"));

            this.images[10] = ImageIO.read(new File("Sprites\\carSprites\\orangeCarUp.png"));
            this.images[11] = ImageIO.read(new File("Sprites\\carSprites\\orangeCarDown.png"));
            this.images[12] = ImageIO.read(new File("Sprites\\carSprites\\orangeCarLeft.png"));
            this.images[13] = ImageIO.read(new File("Sprites\\carSprites\\orangeCarRight.png"));

            this.images[14] = ImageIO.read(new File("Sprites\\carSprites\\purpleCarUp.png"));
            this.images[15] = ImageIO.read(new File("Sprites\\carSprites\\purpleCarDown.png"));
            this.images[16] = ImageIO.read(new File("Sprites\\carSprites\\purpleCarLeft.png"));
            this.images[17] = ImageIO.read(new File("Sprites\\carSprites\\purpleCarRight.png"));

            this.images[18] = ImageIO.read(new File("Sprites\\carSprites\\redCarUp.png"));
            this.images[19] = ImageIO.read(new File("Sprites\\carSprites\\redCarDown.png"));
            this.images[20] = ImageIO.read(new File("Sprites\\carSprites\\redCarLeft.png"));
            this.images[21] = ImageIO.read(new File("Sprites\\carSprites\\redCarRight.png"));

            this.images[22] = ImageIO.read(new File("Sprites\\carSprites\\yellowCarUp.png"));
            this.images[23] = ImageIO.read(new File("Sprites\\carSprites\\yellowCarDown.png"));
            this.images[24] = ImageIO.read(new File("Sprites\\carSprites\\yellowCarLeft.png"));
            this.images[25] = ImageIO.read(new File("Sprites\\carSprites\\yellowCarRight.png"));

            this.images[26] = ImageIO.read(new File("Sprites\\TrackIcons\\Track1Icon.png"));
            this.images[27] = ImageIO.read(new File("Sprites\\TrackIcons\\Track2Icon.png"));
            this.images[28] = ImageIO.read(new File("Sprites\\TrackIcons\\Track3Icon.png"));
            this.images[29] = ImageIO.read(new File("Sprites\\TrackIcons\\Track4Icon.png"));

            this.images[30] = ImageIO.read(new File("Sprites\\horizontalCheckered.png"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Switches active window
     * @param newWindow - the window to display
     */
    private void swapWindow(JPanel newWindow) {
        this.rootFrame.setContentPane(newWindow);
        this.rootFrame.pack();
        this.rootFrame.revalidate();
        this.rootFrame.repaint();
        this.rootFrame.setVisible(true);
    }

    /**
     * redraws the car sprites in their new positions according to their movement parameters and runs checks to see if
     * cars have passed through any checkpoints in the process
     */
    public void drawNewCarPositions() {
        for(int i = 0; i < this.gameCars.length; i++) {
            // position has been updated, set car sprite bounds to new position
            this.gameCars[i].setBounds(this.gameCars[i].getPosition().x, this.gameCars[i].getPosition().y, TILE_SIZE, TILE_SIZE);
            if(this.gameCars[i].getWasRotated()) {
                swapCarSprite(i);
            }
            // update information about each car
            this.carPanelLabels[i][0].setText(this.gameCars[i].getSpeed() * 15 + " mph");
            this.carPanelLabels[i][1].setText("" + makeCheckpointsReadable(this.gameCars[i].getCheckpoints()));
            int checkpointIndex = this.gameCars[i].getCheckpointIndex();
            String checkpointProgress = checkpointIndex +
                    "/" +
                    this.gameCars[i].getCheckpoints().length +
                    "  " +
                    "Next point: " +
                    this.gameCars[i].getCheckpointAtIndex(checkpointIndex);
            this.carPanelLabels[i][2].setText(checkpointProgress);
        }
    }

    /**
     * changes spirte for car when it turns
     * @param i - index of car to update
     */
    private void swapCarSprite(int i) {
        this.gameCars[i].setWasRotated(false);
        int dir = this.gameCars[i].getCurrDir();
        switch(this.gameCars[i].getName()) {
            case "blue":
                this.gameCars[i].setIcon(new ImageIcon(this.images[(2 + dir)]));
                break;
            case "green":
                this.gameCars[i].setIcon(new ImageIcon(this.images[(6 + dir)]));
                break;
            case "orange":
                this.gameCars[i].setIcon(new ImageIcon(this.images[(10 + dir)]));
                break;
            case "purple":
                this.gameCars[i].setIcon(new ImageIcon(this.images[(14 + dir)]));
                break;
            case "red":
                this.gameCars[i].setIcon(new ImageIcon(this.images[(18 + dir)]));
                break;
            case "yellow":
                this.gameCars[i].setIcon(new ImageIcon(this.images[(22 + dir)]));
                break;
        }

    }

    /**
     * Opens message window listing the victor of the race
     * @param victoryCar - the car that won the race
     */
    public void endGame(Car victoryCar) {
        JOptionPane.showMessageDialog(rootFrame.getContentPane(),victoryCar.getName() + " is the winner of the race!");
    }

    /**
     * extracts car and track objects from existing panels containing such
     * @param component - used to backtrack up the window component hierarchy to extract selections
     * @return - raceCar array and filename of track to import
     */
    public Object[] extractGameArgs(JButton component) {
        Object[] args = new Object[2];
        // get parent root panel
        JPanel panel = (JPanel) component.getParent();
        JPanel rootPanel = (JPanel) panel.getParent();
        // local variables containing game argument selection panels
        Component[] cars = ((JPanel) rootPanel.getComponent(1)).getComponents();
        Component[] tracks = ((JPanel) rootPanel.getComponent(2)).getComponents();

        ArrayList<Car> racers = new ArrayList<Car>();
        for(Component car : cars ) {
            JButton carBtn = (JButton) car;
            if(carBtn.isSelected()) {
                String name = carBtn.getText();
                Image image = getCarSpriteFromText(name); // Car default image is 'Up'
                racers.add(new Car(name, image, null, 0, 0));
            }
        }
        this.gameCars = new Car[racers.size()];
        args[0] = racers.toArray(this.gameCars);

        for(Component track : tracks ) {
            JButton trackBtn = (JButton) track;
            if(trackBtn.isSelected()) {
                String file = ("Tracks\\" + trackBtn.getText() + ".csv");
                args[1] = file;
            }
        }

        return args;
    }

    /**
     * Determines which car image to use from car name
     * @param name - name of car, i.e. "blue"
     * @return - sprite for that car, matching color
     */
    private Image getCarSpriteFromText(String name) {
        switch(name) {
            // default sprite is 'Up'
            case "blue":
                return images[2];
            case "green":
                return images[6];
            case "orange":
                return images[10];
            case "purple":
                return images[14];
            case "red":
                return images[18];
            case "yellow":
                return images[22];

        }
        return null;
    }

    /**
     * Passes car and track objects to the main game window
     * @param gameAssets - raceCars and raceTrack objects to display
     */
    public void gameAssetsSelected(Object[] gameAssets) {
        this.gameAssets = gameAssets;
        this.gameCars = ((Car[])this.gameAssets[0]);
        this.gameTrack = ((Track)this.gameAssets[1]);
        createGameWindow();
        swapWindow(this.gameWindowPanel);
    }

    /**
     * Updates the game clock output to display hours, minuts, and seconds elapsing
     * @param elapsedSeconds - used to display time elapsed in seconds
     */
    public void updateTimer(double elapsedSeconds){
        int hours = (int) (elapsedSeconds / 3600);
        int minutes = (int) (elapsedSeconds / 60);
        int seconds = (int) (elapsedSeconds % 60);
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        this.timeLabel.setText(timeString);

    }

    /**
     * Converts array of checkpoint indexes into a string listing all of the checkpoints
     * @param checkpointList - a raceCars assigned route of checkpoints
     * @return - converted string
     */
    private String makeCheckpointsReadable(int[] checkpointList) {
        StringBuilder output = new StringBuilder();
        for (int checkpointIndex : checkpointList) {
            output.append((checkpointIndex));
            output.append("-");
        }
        output.deleteCharAt(output.length() - 1); // remove last dash
        return output.toString();
    }

    /**
     * linked functions for when game buttons are pressed
     * @param e - button pressed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton pressed = ((JButton)e.getSource());
        String text = pressed.getText();
        switch(text) {
            // TODO: 3/21/2023 Magic strings should probably be replaced with constants.
            case "Start New Game":
                swapWindow(this.startGameOptionsWindowPanel);
                // swapWindow(this.startGameOptionsWindowPanel);
                break;
            case "Start Game":
                swapWindow(this.gameWindowPanel);
                break;
            case "blue":
                cycleButtonHighlight(pressed);
                break;
            case "green":
                cycleButtonHighlight(pressed);
                break;
            case "orange":
                cycleButtonHighlight(pressed);
                break;
            case "purple":
                cycleButtonHighlight(pressed);
                break;
            case "red":
                cycleButtonHighlight(pressed);
                break;
            case "yellow":
                cycleButtonHighlight(pressed);
                break;
            case "Track1":
                singleSelectionOfButtons(pressed);
                break;
            case "Track2":
                singleSelectionOfButtons(pressed);
                break;
            case "Track3":
                singleSelectionOfButtons(pressed);
                break;
            case "Track4":
                singleSelectionOfButtons(pressed);
                break;
            default:
                swapWindow(this.menuWindowPanel);
                break;
        }
        checkForStartConditions();
    }

}
