import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
3/20    [Kat]       - added Car display panels to bottom of UI showing current position
3/21    [tre]       - Replace tile width and tile height with constants.
3/22    [tre]       - replace TILE_WIDTH and TILE_HEIGHT constants with TILE_SIZE
                    - add cars to carPanel
                    - replace magic numbers of value 50 with TILE_SIZE constant
3/22    [chris]     - wrote a couple methods to extract game options from selection window
                    - then trigger the Game class to create the new game window and display it.
3/23    [chris]     - added controlFunctions support


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
    /* Objects used by 'Game' that have a graphical component */
    private JPanel carCreationWindowPanel;
    private JPanel trackCreationWindowPanel;
    private JPanel startGameOptionsWindowPanel;
    private Object[] gameAssets;
    private Image[] images;
    private Track gameTrack;
    private Car[] gameCars;
    private JLabel[][] carPanelSpeedLabels;
    private JLabel timeLabel;
    private Object[] controls;

    /* ___ CONSTRUCTORS ___ */
    public GUI(Object[] controls) {
        this.rootFrame = new JFrame();
        this.menuWindowPanel = new JPanel();
        this.gameWindowPanel = new JPanel();
        this.gameAssets = null;
        this.controls = controls;
        createGUI();
    }

    /* ___ FUNCTIONS ___ */

    /**
     * Initialize and compose the graphical interface of the application
     */
    private void createGUI() {
        //this.rootFrame.setContentPane(this.contentPanel);
        this.rootFrame.setPreferredSize(new Dimension(1000, 700));
        this.rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.rootFrame.setTitle("Track Star Racer! 9001");
        this.rootFrame.setResizable(false);

        loadImages();
        createMenuWindow();
        createGameOptionsWindow();

        // TODO: this will initially be the menuWindow not the gameWindowPanel
         this.rootFrame.setContentPane(this.menuWindowPanel);
        // this.rootFrame.setContentPane(this.gameWindowPanel);
        // this.rootFrame.setContentPane(this.startGameOptionsWindowPanel);

        this.rootFrame.pack();
        this.rootFrame.setVisible(true);
    }

    private void loadImages() {
        this.images = new Image[12];
        try {
            this.images[0] = ImageIO.read(new File("Sprites\\Checkered.png"));
            this.images[1] = ImageIO.read(new File("Sprites\\MenuImage.png"));
            this.images[2] = ImageIO.read(new File("Sprites\\carSprites\\blueCar.png"));
            this.images[3] = ImageIO.read(new File("Sprites\\carSprites\\greenCar.png"));
            this.images[4] = ImageIO.read(new File("Sprites\\carSprites\\orangeCar.png"));
            this.images[5] = ImageIO.read(new File("Sprites\\carSprites\\purpleCar.png"));
            this.images[6] = ImageIO.read(new File("Sprites\\carSprites\\redCar.png"));
            this.images[7] = ImageIO.read(new File("Sprites\\carSprites\\yellowCar.png"));
            this.images[8] = ImageIO.read(new File("Sprites\\TrackIcons\\Track1Icon.png"));
            this.images[9] = ImageIO.read(new File("Sprites\\TrackIcons\\Track2Icon.png"));
            this.images[10] = ImageIO.read(new File("Sprites\\TrackIcons\\Track3Icon.png"));
            this.images[11] = ImageIO.read(new File("Sprites\\TrackIcons\\Track4Icon.png"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void swapWindow(JPanel newWindow) {
        this.rootFrame.setContentPane(newWindow);
        this.rootFrame.pack();
        this.rootFrame.revalidate();
        this.rootFrame.repaint();
        this.rootFrame.setVisible(true);
    }
    private void createGameOptionsWindow() {
        // TODO: disable continue until at least 2 cars and 1 track are selected
        // TODO: Add information to center

        this.startGameOptionsWindowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.startGameOptionsWindowPanel.setPreferredSize(new Dimension(1000, 700));

        JPanel optionsRootPanel = new JPanel(new BorderLayout());
        optionsRootPanel.setPreferredSize(new Dimension(1000, 700));
        optionsRootPanel.setBackground(Color.BLUE);

        JPanel topPanel = new JPanel();     // feedback
        topPanel.setPreferredSize(new Dimension(1000, 100));

        JPanel leftPanel = new JPanel(new GridLayout(3, 2, 10, 10));    // display cars
        leftPanel.setPreferredSize(new Dimension(300, 500));
        leftPanel.setBorder(new LineBorder(Color.green));

        JPanel rightPanel = new JPanel(new GridLayout(2, 2, 10, 10));   // display tracks
        rightPanel.setPreferredSize(new Dimension(350, 500));
        rightPanel.setBorder(new LineBorder(Color.RED));

        JPanel bottomPanel = new JPanel();  // buttons
        bottomPanel.setPreferredSize(new Dimension(1000, 100));

        // bottom panel components and settings
        JButton startButton = (JButton) this.controls[0];
        startButton.setText("Continue");
        startButton.setPreferredSize(new Dimension(200, 40));
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        bottomPanel.add(startButton);

        // TODO: track button text must match track file name not including .csv, this is added later.
        // right panel components and settings
        JButton track1Btn = makeTrackOptionButton(1);
        JButton track2Btn = makeTrackOptionButton(2);
        JButton track3Btn = makeTrackOptionButton(3);
        JButton track4Btn = makeTrackOptionButton(4);
        rightPanel.add(track1Btn);
        rightPanel.add(track2Btn);
        rightPanel.add(track3Btn);
        rightPanel.add(track4Btn);

        // Left panel components and settings
        JButton car1Btn = makeCarOptionsButton(1);
        car1Btn.setText("blue");
        JButton car2Btn = makeCarOptionsButton(2);
        car2Btn.setText("green");
        JButton car3Btn = makeCarOptionsButton(3);
        car3Btn.setText("orange");
        JButton car4Btn = makeCarOptionsButton(4);
        car4Btn.setText("purple");
        JButton car5Btn = makeCarOptionsButton(5);
        car5Btn.setText("red");
        JButton car6Btn = makeCarOptionsButton(6);
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
        this.startGameOptionsWindowPanel.add(optionsRootPanel);
    }
    private JButton makeCarOptionsButton(int index) {
        JButton button = new JButton();
        button.setDoubleBuffered(true);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setIcon(new ImageIcon(this.images[(index + 1)]));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.addActionListener(this);
        return button;
    }
    private JButton makeTrackOptionButton(int index) {
        JButton button = new JButton("Track" + index);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setIcon(new ImageIcon(this.images[(index + 7)]));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.addActionListener(this);
        return button;
    }
    private void createTrackCreationWindow() {

    }
    private void createCarCreationWindow() {

    }
    private void createMenuWindow() {
        this.menuWindowPanel.setPreferredSize(new Dimension(1000, 700));
        this.menuWindowPanel.setLayout(new BorderLayout());

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
    private void createGameWindow() {
        /* _Game Window_ */
        this.gameWindowPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.gameWindowPanel.setPreferredSize(new Dimension(1000,700));

        JPanel topGamePanel = new JPanel();
        topGamePanel.setLayout(new BoxLayout(topGamePanel, BoxLayout.X_AXIS));
        topGamePanel.setBackground(Color.BLACK);
        JPanel bottomGamePanel = createGameWindowInfoPanel();

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
        JLayeredPane centerPanel = new JLayeredPane();
        centerPanel.setPreferredSize(new Dimension(800, 500));

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
            // TODO: SET starting POSITIONs for each car to be one of the Track checkpoints
            // TODO: max cars per track = number of checkpoints.
            carPanel.add(car);
        }
        // Compose gameplay area
        centerPanel.add(gameTilePanel, new Integer(1));
        centerPanel.add(carPanel, new Integer(2));

        // feedback panel for race timer
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setPreferredSize(new Dimension(250, 100));
        this.timeLabel = new JLabel("00:00:00");
        this.timeLabel.setBounds(0,5,100,50);
        this.timeLabel.setFont(new Font("Helvetica", Font.PLAIN,25 ));
        feedbackPanel.add(this.timeLabel);
        ((JPanel)bottomGamePanel.getComponent(0)).add(feedbackPanel);

        // car specific panels in info panel
        // TODO: make dynamic - this needs to display the number of cars not a fixed number, i.e. 2
        JPanel[] carInfoPanels = new JPanel[2];
        JLabel[] carInfoPanelLabels = new JLabel[2];
        carPanelSpeedLabels = new JLabel[2][2];

        GridBagConstraints layoutConstraints = new GridBagConstraints();
        layoutConstraints.insets = new Insets(10,10,10,10);
        layoutConstraints.weightx = 1;
        layoutConstraints.weighty = 1;

        for (int i = 0; i < 2; i++) {
            carInfoPanels[i] = new JPanel(new GridBagLayout());
            carInfoPanels[i].setBorder(new LineBorder(Color.RED));
            carInfoPanels[i].setPreferredSize(new Dimension(250, 100));

            carInfoPanelLabels[i] = new JLabel("Car " + (i + 1));
            carPanelSpeedLabels[0][i] = new JLabel("50");
            carPanelSpeedLabels[0][i].setPreferredSize(new Dimension(50, 50));
            carPanelSpeedLabels[1][i] = new JLabel("" + gameCars[i].getPosition().y);

            layoutConstraints.gridy = 0;
            layoutConstraints.gridx = 0;
            layoutConstraints.gridwidth = 2;
            carInfoPanels[i].add(carInfoPanelLabels[i], layoutConstraints);

            layoutConstraints.gridwidth = 1;
            layoutConstraints.gridy = 1;
            carInfoPanels[i].add(new JLabel("X position:"), layoutConstraints);

            layoutConstraints.gridx = 1;
            carInfoPanels[i].add( carPanelSpeedLabels[0][i], layoutConstraints);

            layoutConstraints.gridx = 0;
            layoutConstraints.gridy = 2;
            carInfoPanels[i].add(new JLabel("Y position:"), layoutConstraints);

            layoutConstraints.gridx = 1;
            carInfoPanels[i].add(carPanelSpeedLabels[1][i], layoutConstraints);

            // BottomGamePanel created in createGameWindowInfoPanel() and has a JPanel added to it
            ((JPanel)bottomGamePanel.getComponent(0)).add(carInfoPanels[i]);
        }

        // Compose overall game window
        topGamePanel.add(leftRootPanel);
        topGamePanel.add(centerPanel);
        topGamePanel.add(rightRootPanel);

        this.gameWindowPanel.add(topGamePanel);
        this.gameWindowPanel.add(bottomGamePanel);
    }

    private JPanel createGameWindowInfoPanel() {
        JPanel bottomGamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        // info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 4));
        infoPanel.setPreferredSize(new Dimension(1000, 200));
        infoPanel.setBackground(Color.PINK); // REMOVE
        infoPanel.setBorder(new LineBorder(Color.RED)); // REMOVE

        bottomGamePanel.add(infoPanel);

        return bottomGamePanel;
    }

    public void drawNewCarPositions() {
        for(int i = 0; i < 2; i++) {
            this.gameCars[i].setBounds(this.gameCars[i].getPosition().x, this.gameCars[i].getPosition().y, TILE_SIZE, TILE_SIZE);
            carPanelSpeedLabels[0][i].setText("" + gameCars[i].getPosition().x);
            carPanelSpeedLabels[1][i].setText("" + gameCars[i].getPosition().y);
        }
    }

    private void cycleButtonHighlight(JButton button) {
        Color color = new Color(92, 255, 63, 126);
        if(button.getBackground().equals(color)) {
            button.setBackground(null);
            button.setSelected(false);
        } else {
            button.setBackground(color);
            button.setSelected(true);
        }
    }
    private void singleSelectionOfButtons(JButton button) {
        for(Component c : button.getParent().getComponents()) {
            c.setBackground(null);
            ((JButton)c).setSelected(false);
        }
        button.setBackground(new Color(92, 255, 63, 126));
        button.setSelected(true);
    }

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
                Image image = getCarSpriteFromText(name);
                racers.add(new Car(name, image));
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

    private Image getCarSpriteFromText(String name) {
        switch(name) {
            case "blue":
                return images[2];
            case "green":
                return images[3];
            case "orange":
                return images[4];
            case "purple":
                return images[5];
            case "red":
                return images[6];
            case "yellow":
                return images[7];

        }
        return null;
    }

    /* ___ ACCESSORS / MUTATORS ___ */
    public void gameAssetsSelected(Object[] gameAssets) {
        this.gameAssets = gameAssets;
        this.gameCars = ((Car[])this.gameAssets[0]);
        this.gameTrack = ((Track)this.gameAssets[1]);
        createGameWindow();
        swapWindow(this.gameWindowPanel);
    }


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
            case "Design a Car":
                swapWindow(this.carCreationWindowPanel);
                break;
            case "Create a Track":
                swapWindow(this.trackCreationWindowPanel);
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
    }

    public void updateTimer(double elapsedSeconds){
        int hours = (int) (elapsedSeconds / 3600);
        int minutes = (int) (elapsedSeconds / 60);
        int seconds = (int) (elapsedSeconds % 60);
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        this.timeLabel.setText(timeString);

    }

    /* ___ ACCESSORS / MUTATORS ___ */

}
