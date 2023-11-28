/**

 * File: PuzzleGame.java

 * Author: Roberto Myftaraga

 * Date: 11/28/2023

 */



import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuzzleGame extends JFrame {
    private static final String SOUND_PATH = "/sounds/";
    private static final String IMAGE_PATH = "/images/";
    private static final String BORDER_IMAGE = "/images/border.png";
    private static final String BACKGROUND_IMAGE = "/images/background.png";
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private PuzzlePanel panel;
    private JLabel[] pieces;
    private Point[] correctPositions;
    private List<Point> initialPositions;
    private int level = 1;
    private int[] levelPieces = {4, 9, 16, 25};
    private JLabel timerLabel;
    private int timeLeft = 60;
    private static final int IMG_WIDTH = 400;
    private static final int IMG_HEIGHT = 400;
    private int highScore = 0;
    private JLabel highScoreLabel;
    private String imageFile = "1.png"; // Default image file
    private Clip popSound;
    private Clip victorySound;
    private Clip finalVictorySound;
    

    public PuzzleGame() {
        initUI();
        initSound();
    }

    private void initUI() {
        configureLookAndFeel();
        createStartupPanel();
    }

    private void configureLookAndFeel() {
        UIManager.put("OptionPane.background", Color.DARK_GRAY);
        UIManager.put("Panel.background", Color.DARK_GRAY);
    }

    private void createStartupPanel() {
        JPanel startupPanel = new JPanel();
        startupPanel.setLayout(new GridLayout(0, 3, 10, 10));
        startupPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        ImageOption[] imageOptions = createImageOptions();
        addImageOptionButtons(startupPanel, imageOptions);

        createStartupFrame(startupPanel);
    }

    private ImageOption[] createImageOptions() {
        return new ImageOption[]{
            new ImageOption("Image 1", "1.png"),
            new ImageOption("Image 2", "2.png"),
            new ImageOption("Image 3", "3.png"),
            new ImageOption("Image 4", "4.png"),
            new ImageOption("Image 5", "5.png")
        };
    }

    private void addImageOptionButtons(JPanel panel, ImageOption[] imageOptions) {
        for (ImageOption option : imageOptions) {
            JButton button = createImageOptionButton(option);
            panel.add(button);
        }
    }

    private JButton createImageOptionButton(ImageOption option) {
        URL imageUrl = getClass().getResource(IMAGE_PATH + option.getFile());
        if (imageUrl == null) {
            System.err.println("Image not found: " + option.getFile());
            return null;
        }
        ImageIcon icon = new ImageIcon(imageUrl);
        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
    
        JButton button = new JButton(option.getName(), icon);
        styleButton(button);
        button.addActionListener(e -> selectImageAndStart(option));
        return button;
    }

    private void styleButton(JButton button) {
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Serif", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 51, 51));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(21, 24, 38));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(51, 51, 51));
            }
        });
    }

    private void selectImageAndStart(ImageOption option) {
        imageFile = option.getFile();
        startGame();
        disposeStartupFrame();
    }

    private void disposeStartupFrame() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private void createStartupFrame(JPanel panel) {
        JFrame frame = new JFrame("Image Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.setVisible(true);
    }
       private void initSound() {
        popSound = loadSound("pop.au");
        victorySound = loadSound("victory.au");
        finalVictorySound = loadSound("finalVictory.au");
    }
    class ImageOption {
        private String name;
        private String file;
        private ImageIcon icon;
    
        public ImageOption(String name, String file) {
            this.name = name;
            this.file = file;
        }
    
        public String getName() {
            return name;
        }
    
        public String getFile() {
            return file;
        }
    
        public ImageIcon getIcon() {
            if (icon == null) {
                // Lazy loading of the image
                URL imageUrl = getClass().getResource(IMAGE_PATH + file);
                if (imageUrl != null) {
                    ImageIcon originalIcon = new ImageIcon(imageUrl);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledImage);
                } else {
                    System.err.println("Image not found: " + file);
                }
            }
            return icon;
        }
    }
    class ImageOptionRenderer extends JLabel implements ListCellRenderer<ImageOption> {
        public ImageOptionRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ImageOption> list, ImageOption value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.getName());
            setIcon(value.getIcon());

            Color background = isSelected ? Color.DARK_GRAY : Color.LIGHT_GRAY;
            Color foreground = isSelected ? Color.WHITE : Color.BLACK;

            setBackground(background);
            setForeground(foreground);

            return this;
        }
    }

    public void startGame() {
        BufferedImage borderImage = null;
        try {
            borderImage = ImageIO.read(getClass().getResource(BORDER_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        panel = new PuzzlePanel(level, borderImage);
        panel.setDoubleBuffered(true);
        panel.setLayout(null);
        JLabel title = new JLabel("The Pieces");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE); // Change text color to white
        title.setBounds(10, 10, 100, 20);
        panel.add(title);

        timerLabel = new JLabel("Time left: 60");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timerLabel.setForeground(Color.WHITE); // Change text color to white
        timerLabel.setBounds(120, 10, 100, 20);
        panel.add(timerLabel);

        highScoreLabel = new JLabel("High Score: " + highScore);
        highScoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        highScoreLabel.setForeground(Color.WHITE); // Change text color to white
        highScoreLabel.setBounds(230, 10, 100, 20);
        panel.add(highScoreLabel);

        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(new Color(70, 130, 180));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                resetButton.setBackground(new Color(100, 150, 200));
            }
            public void mouseExited(MouseEvent evt) {
                resetButton.setBackground(new Color(70, 130, 180));
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset the level
                resetPieces();
            }
        });
        resetButton.setBounds(340, 10, 80, 20);
        panel.add(resetButton);

        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Arial", Font.BOLD, 14));
        mainMenuButton.setBackground(new Color(70, 130, 180));
        mainMenuButton.setForeground(Color.WHITE);
        mainMenuButton.setFocusPainted(false);
        mainMenuButton.setBorderPainted(false);
        mainMenuButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                mainMenuButton.setBackground(new Color(100, 150, 200));
            }
            public void mouseExited(MouseEvent evt) {
                mainMenuButton.setBackground(new Color(70, 130, 180));
            }
        });
        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Go back to main menu
                dispose();
                new PuzzleGame();
            }
        });
        mainMenuButton.setBounds(430, 10, 120, 20);
        panel.add(mainMenuButton);
        JButton hintButton = new JButton("Hint");
    hintButton.setFont(new Font("Arial", Font.BOLD, 14));
    hintButton.setBackground(new Color(70, 130, 180));
    hintButton.setForeground(Color.WHITE);
    hintButton.setFocusPainted(false);
    hintButton.setBorderPainted(false);
    hintButton.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent evt) {
            hintButton.setBackground(new Color(100, 150, 200));
        }
        public void mouseExited(MouseEvent evt) {
            hintButton.setBackground(new Color(70, 130, 180));
        }
    });
    hintButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            showFullImage();
        }
    });
    hintButton.setBounds(560, 10, 80, 20); // Adjust the x position to place it next to the mainMenuButton
    panel.add(hintButton);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time left: " + timeLeft);
                if (timeLeft <= 0) {
                    ((Timer) e.getSource()).stop();
                    showVictoryDialog1();
                    level = 1;
                    resetPieces();
                }
            }
        });
        timer.start();

        addPieces();
        add(panel);
        setTitle("Puzzle Game");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Simple Puzzle Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void showFullImage() {
        // Create a new JFrame to display the full image
        JFrame fullImageFrame = new JFrame("Puzzle Hint");
        fullImageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fullImageFrame.setLayout(new BorderLayout());

        // Load the full image from the cache
        BufferedImage fullImage = getCachedImage(imageFile);
        if (fullImage != null) {
            ImageIcon imageIcon = new ImageIcon(fullImage);
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);

            // Add the image to the frame
            fullImageFrame.add(imageLabel, BorderLayout.CENTER);

            // Set the frame size to the image size
            fullImageFrame.pack();

            // Center the frame on the screen
            fullImageFrame.setLocationRelativeTo(null);

            // Make the frame visible
            fullImageFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to load the full image.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void addPieces() {
        pieces = new JLabel[levelPieces[level - 1]];
        correctPositions = new Point[levelPieces[level - 1]];
    
        int piecesPerRow = (int) Math.sqrt(levelPieces[level - 1]);
    
        initialPositions = new ArrayList<>();
        int spacing = 40; // The space between the pieces
        int initialPiecesPerRow = (int) Math.ceil(Math.sqrt(levelPieces[level - 1])); // Calculate the number of pieces per row for the initial positions
        int verticalOffset1 = (Toolkit.getDefaultToolkit().getScreenSize().height - 400) / 2; // Calculate the y-coordinate of the red area box
        for (int i = 0; i < levelPieces[level - 1]; i++) {
            int x = (i % initialPiecesPerRow) * (spacing + 60); // Calculate the x position
            int y = (i / initialPiecesPerRow) * (spacing + 60); // Calculate the y position
            initialPositions.add(new Point(spacing + x, verticalOffset1 + y));
        }
        // Shuffle the initial positions
        Collections.shuffle(initialPositions);
    
        BufferedImage img = getCachedImage(imageFile);
        if (img == null) {
            System.err.println("Failed to load image: " + imageFile);
            return; // Exit the method if the image cannot be loaded
        }

        // Resize the image
        Image scaledImage = img.getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH);
        img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
    
        int pieceWidth = img.getWidth() / piecesPerRow;
        int pieceHeight = img.getHeight() / piecesPerRow;
    
        int offset = (Toolkit.getDefaultToolkit().getScreenSize().width - IMG_WIDTH) / 2;
        int verticalOffset = (Toolkit.getDefaultToolkit().getScreenSize().height - 450) / 2;
        for (int i = 0; i < levelPieces[level - 1]; i++) {
            int x = (i % piecesPerRow) * pieceWidth;
            int y = (i / piecesPerRow) * pieceHeight;
    
            if (x + pieceWidth > img.getWidth()) {
                pieceWidth = img.getWidth() - x;
            }
            if (y + pieceHeight > img.getHeight()) {
                pieceHeight = img.getHeight() - y;
            }
    
            BufferedImage subImg = img.getSubimage(x, y, pieceWidth, pieceHeight);
            ImageIcon icon = new ImageIcon(subImg);
            pieces[i] = new JLabel(icon);
            pieces[i].setSize(icon.getIconWidth(), icon.getIconHeight());
    
            // Get the border's insets
            Insets insets = getInsets();
    
            // Set the location of the pieces
            pieces[i].setLocation(initialPositions.get(i).x - insets.left, initialPositions.get(i).y - insets.top);
    
            correctPositions[i] = new Point(offset + pieces[i].getWidth() * (i % piecesPerRow), verticalOffset + pieces[i].getHeight() * (i / piecesPerRow));
    
            final int index = i;
    
            MouseAdapter ma = new MouseAdapter() {
                private Point offset;
                private Rectangle oldBounds;

                @Override
                public void mousePressed(MouseEvent e) {
                    offset = e.getPoint();
                    oldBounds = new Rectangle(pieces[index].getBounds());
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Point point = e.getPoint();
                    JLabel label = (JLabel) e.getSource();
                    int newX = label.getX() + point.x - offset.x;
                    int newY = label.getY() + point.y - offset.y;
                
                    // Update the location of the label
                    label.setLocation(newX, newY);
                
                    // Calculate the area that needs to be repainted
                    Rectangle newBounds = label.getBounds();
                    Rectangle repaintBounds = oldBounds.union(newBounds);
                
                    // Repaint only the affected area
                    panel.repaint(repaintBounds);
                
                    // Update oldBounds for the next call
                    oldBounds = newBounds;
                }


                @Override
                public void mouseReleased(MouseEvent e) {
                    JLabel label = (JLabel) e.getSource();
                    boolean isCorrect = false;
                    for (Point correctPosition : correctPositions) {
                        int dx = Math.abs(label.getX() - correctPosition.x);
                        int dy = Math.abs(label.getY() - correctPosition.y);
                        if (dx < 50 && dy < 50) {
                            label.setLocation(correctPosition);
                            isCorrect = true;
                            break;
                        }
                    }
                    if (!isCorrect) {
                        label.setLocation(initialPositions.get(index));
                    } else {
                        if (popSound != null) {
                            popSound.setFramePosition(0);
                            popSound.start();
                        } else {
                            System.out.println("popSound is null");
                        }
                    }
                
                    if (checkSolution()) {
                        if(level > highScore) {
                            highScore = level * 3;
                            highScoreLabel.setText("High Score: " + highScore);
                        }
                        resetPieces();

                        

                    }
                }
            };
    
            pieces[i].addMouseListener(ma);
            pieces[i].addMouseMotionListener(ma);

            panel.add(pieces[i]);
        }
    }

    private BufferedImage getCachedImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        } else {
            try {
                BufferedImage image = ImageIO.read(getClass().getResourceAsStream(IMAGE_PATH + path));
                imageCache.put(path, image);
                return image;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private Clip loadSound(String soundFileName) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(SOUND_PATH + soundFileName);
            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to get a scaled instance of an image
    private BufferedImage getScaledImage(BufferedImage src, int width, int height) {
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        // Improve quality
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the scaled image
        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();

        return resizedImg;
    }
    public void showVictoryDialog() {
        // Create a new JFrame as the overlay
        JFrame overlay = new JFrame();
        overlay.setUndecorated(true);
        overlay.setBackground(new Color(0, 0, 0, 200)); // Semi-transparent black
        overlay.setSize(this.getSize());
        overlay.setLocation(this.getLocation());
    
        JDialog victoryDialog = new JDialog(overlay, "Victory", true);
        victoryDialog.setUndecorated(true); // Remove window decorations
        victoryDialog.setBackground(new Color(0, 0, 0, 0)); // Set transparent background
        victoryDialog.setLayout(null); // Use a null layout
    
        // Load the image
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/victory.png"));
    
        // Get the image's width and height
        int imgWidth = originalIcon.getIconWidth();
        int imgHeight = originalIcon.getIconHeight();
    
        // Add the image to the dialog
        JLabel imageLabel = new JLabel(originalIcon);
        imageLabel.setBounds(0, 0, imgWidth, imgHeight); // Set the label's position and size
        victoryDialog.add(imageLabel);
    
        // Add a close button to the dialog
        JButton closeButton = new JButton("Next Level");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set the font
        closeButton.setForeground(Color.WHITE); // Set the text color
        closeButton.setBackground(Color.ORANGE); // Set the background color
        closeButton.setFocusPainted(false); // Remove focus border
        int buttonWidth = 150;
        int buttonHeight = 30;
        int buttonX = (imgWidth - buttonWidth) / 2; // Center the button horizontally
        int buttonY = imgHeight + 10; // Position the button below the image
        closeButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        closeButton.addActionListener(e -> {
            victoryDialog.dispose();
            overlay.dispose(); // Dispose the overlay when the dialog is closed
        });
        victoryDialog.add(closeButton);
    
        // Increase the dialog's height to accommodate the button
        victoryDialog.setSize(imgWidth, imgHeight + buttonHeight + 20); // Add 20 for some padding

        victoryDialog.setLocationRelativeTo(overlay);
        overlay.setVisible(true); // Show the overlay
        victoryDialog.setVisible(true); // Show the dialog
    }
public void gameCompleted() {
        // Create a new JFrame as the overlay
        JFrame overlay = new JFrame();
        overlay.setUndecorated(true);
        overlay.setBackground(new Color(0, 0, 0, 200)); // Semi-transparent black
        overlay.setSize(this.getSize());
        overlay.setLocation(this.getLocation());
    
        JDialog victoryDialog = new JDialog(overlay, "Victory", true);
        victoryDialog.setUndecorated(true); // Remove window decorations
        victoryDialog.setBackground(new Color(0, 0, 0, 0)); // Set transparent background
        victoryDialog.setLayout(null); // Use a null layout
    
        // Load the image
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/final.png"));
    
        // Get the image's width and height
        int imgWidth = originalIcon.getIconWidth();
        int imgHeight = originalIcon.getIconHeight();
    
        // Add the image to the dialog
        JLabel imageLabel = new JLabel(originalIcon);
        imageLabel.setBounds(0, 0, imgWidth, imgHeight); // Set the label's position and size
        victoryDialog.add(imageLabel);
    
        // Add a close button to the dialog
               // Add a close button to the dialog
        JButton closeButton = new JButton("Main Menu");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set the font
        closeButton.setForeground(Color.WHITE); // Set the text color
        closeButton.setBackground(Color.ORANGE); // Set the background color
        closeButton.setFocusPainted(false); // Remove focus border
        int buttonWidth = 150;
        int buttonHeight = 30;
        int buttonX = (imgWidth - buttonWidth) / 2; // Center the button horizontally
        int buttonY = imgHeight + 10; // Position the button below the image
        closeButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        closeButton.addActionListener(e -> {
            victoryDialog.dispose();
            overlay.dispose(); // Dispose the overlay when the dialog is closed
        });
        victoryDialog.add(closeButton);
    
        // Increase the dialog's height to accommodate the button
        victoryDialog.setSize(imgWidth, imgHeight + buttonHeight + 20); // Add 20 for some padding

        victoryDialog.setLocationRelativeTo(overlay);
        overlay.setVisible(true); // Show the overlay
        victoryDialog.setVisible(true); // Show the dialog
    }
        public void showVictoryDialog1() {
        // Create a new JFrame as the overlay
        JFrame overlay = new JFrame();
        overlay.setUndecorated(true);
        overlay.setBackground(new Color(0, 0, 0, 200)); // Semi-transparent black
        overlay.setSize(this.getSize());
        overlay.setLocation(this.getLocation());
    
        JDialog victoryDialog = new JDialog(overlay, "Defeated", true);
        victoryDialog.setUndecorated(true); // Remove window decorations
        victoryDialog.setBackground(new Color(0, 0, 0, 0)); // Set transparent background
        victoryDialog.setLayout(null); // Use a null layout
    
        // Load the image
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/lost.png"));
    
        // Get the image's width and height
        int imgWidth = originalIcon.getIconWidth();
        int imgHeight = originalIcon.getIconHeight();
    
        // Add the image to the dialog
        JLabel imageLabel = new JLabel(originalIcon);
        imageLabel.setBounds(0, 0, imgWidth, imgHeight); // Set the label's position and size
        victoryDialog.add(imageLabel);
    
        // Add a close button to the dialog
        JButton closeButton = new JButton("Try Again");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set the font
        closeButton.setForeground(Color.BLACK); // Set the text color
        closeButton.setBackground(Color.WHITE); // Set the background color
        closeButton.setFocusPainted(false); // Remove focus border
        int buttonWidth = 150;
        int buttonHeight = 30;
        int buttonX = (imgWidth - buttonWidth) / 2; // Center the button horizontally
        int buttonY = imgHeight + 10; // Position the button below the image
        closeButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        closeButton.addActionListener(e -> {
            victoryDialog.dispose();
            overlay.dispose(); // Dispose the overlay when the dialog is closed
        });
        victoryDialog.add(closeButton);
    
        // Increase the dialog's height to accommodate the button
        victoryDialog.setSize(imgWidth, imgHeight + buttonHeight + 20); // Add 20 for some padding

        victoryDialog.setLocationRelativeTo(overlay);
        overlay.setVisible(true); // Show the overlay
        victoryDialog.setVisible(true); // Show the dialog
    }


    public boolean checkSolution() {
        for (int i = 0; i < pieces.length; i++) {
            if (!pieces[i].getLocation().equals(correctPositions[i])) {
                return false; // Solution is incorrect
            }
        }
    
        // If we reach this point, all pieces are in the correct position
        if (victorySound != null) {
            victorySound.setFramePosition(0);
            victorySound.start();
        }
    
            if(level > highScore) {
                highScore = level * 3;
                highScoreLabel.setText("High Score: " + highScore);
            }
            level++;
            resetPieces();
            showVictoryDialog();
    
        return true; // Solution is correct
    }


    public void resetPieces() {
        for (JLabel piece : pieces) {
            panel.remove(piece);
        }
        if (level > levelPieces.length) {
            if (finalVictorySound != null) {
                finalVictorySound.setFramePosition(0);
                finalVictorySound.start();
            }
            gameCompleted();
            dispose();
            new PuzzleGame();
        } else {
            timeLeft = 60 + level * 20;
            timerLabel.setText("Time left: " + timeLeft);
            panel.setLevel(level);
            addPieces();
            panel.revalidate();
            panel.repaint();
        }
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            PuzzleGame ex = new PuzzleGame();
            ex.setVisible(true);
        });
    }
}
class PuzzlePanel extends JPanel {
    private int level;
    private BufferedImage borderImage;
    private BufferedImage backgroundImage; // Background image

    public PuzzlePanel(int level, BufferedImage borderImage) {
        super(true); // Enable double buffering by passing true to the JPanel constructor
        this.level = level;
        this.borderImage = borderImage;
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        int gridSize = (int) Math.sqrt(level);
        int cellSize = 400 / gridSize;
        int offset = (Toolkit.getDefaultToolkit().getScreenSize().width - 400) / 2;
        int verticalOffset = (Toolkit.getDefaultToolkit().getScreenSize().height - 450) / 2;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                g.drawRect(offset + i * cellSize, verticalOffset + j * cellSize, cellSize, cellSize);
            }
        }

        // Calculate the x and y coordinates of the top-left corner of the border
        int borderX = offset;
        int borderY = verticalOffset;

        int newWidth = (int) (700);
        int newHeight = (int) (700);

        // Scale the image to the new size
        Image scaledImage = borderImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        // Calculate the new x and y coordinates to center the image
        int newX = borderX - (newWidth - 400) / 2;
        int newY = borderY - (newHeight - 400) / 2;

        // Draw the scaled image at the new position
        g.drawImage(scaledImage, newX, newY, null);
    }
     private void drawPiece(Graphics g, JLabel piece) {
        // Draw only the piece that has moved
        g.drawImage(((ImageIcon) piece.getIcon()).getImage(), piece.getX(), piece.getY(), null);
    }
}