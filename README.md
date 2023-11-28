# Puzzle Game

## Project Description

Puzzle Game is a Java-based desktop application that allows users to solve sliding puzzles. Each puzzle is generated from a selected image, which is divided into a grid of tiles that the player must slide into the correct positions to recreate the original image. The game features multiple difficulty levels, sound effects, and a graphical user interface.

### What Your Application Does

- Allows users to select an image to play as a sliding puzzle.
- Divides the selected image into a grid of tiles based on the difficulty level.
- Shuffles the tiles and allows the player to slide them to solve the puzzle.
- Tracks the time taken to solve the puzzle and displays a timer on the screen.
- Plays sound effects when tiles are placed correctly and when the puzzle is solved.
- Provides a hint by showing the full image.
- Displays a victory message upon completion of the puzzle.

### Why You Used the Technologies You Used

The application is built using Java Swing, which is a part of the Java Foundation Classes (JFC). Swing provides a set of lightweight components for building graphical user interfaces (GUIs) in Java. Java Swing was chosen for its portability across different platforms, its comprehensive set of GUI components, and its integration with the core Java libraries.

Java's sound API is used to handle audio playback, providing an immersive experience with sound effects that respond to user interactions.

### Challenges Faced and Future Features

Challenges:
- Implementing the logic for shuffling and validating the position of puzzle pieces.
- Ensuring that the puzzle is solvable after shuffling.
- Managing the game state, including the timer and high score tracking.

Future Features:
- Implementing additional difficulty levels and customizable grid sizes.
- Adding support for user-uploaded images to create custom puzzles.
- Introducing a leaderboard to track high scores among multiple players.
- Implementing save and load functionality to allow players to resume unfinished puzzles.

## How to Install and Run the Project

1. Ensure you have Java installed on your system. You can download it from the [official Oracle website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
2. Download the source code for the Puzzle Game from the repository.
3. Navigate to the directory containing the downloaded source code using a terminal or command prompt.
4. Compile the Java files using the following command:
5. Run the compiled program using the Java interpreter:

# How to Use the Project

1. Upon launching the application, select an image from the startup panel to start the game.
2. Click and drag the puzzle pieces to move them into the correct position.
3. Use the "Hint" button if you need to see the full image.
4. Solve the puzzle before the timer runs out.
5. Click "Reset" to restart the current level or "Main Menu" to select a new image.

## Credits

- Puzzle Game was developed by Roberto Myftaraga.
- Sound effects provided by [[Sound Effect Source]](https://pixabay.com/).
- Images used in the puzzles are sourced from [[Image Source]](https://openai.com/dall-e-3).
- Java Swing and Java's sound API are used for the application's development.
