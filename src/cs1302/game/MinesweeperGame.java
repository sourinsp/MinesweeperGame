package cs1302.game;

import java.util.Scanner;
import java.io.*;
import java.util.NoSuchElementException;

/**.
 *The class helps to run the game and read in the file
 *to generate a Minesweeper game to play.
 * 
 * @author Sourin Paturi
 * 
 */

public class MinesweeperGame {

    int rows;
    int cols;
    int arraySize;
    int mineNum;
    int roundsCount;
    int revealSq;
    boolean[][] trueGrid;
    String[][] mineGrid;
    boolean isLost = false;
    double score;
    Scanner promptLine = null;
    
    /**
     * Constructs a {@code MinesweeperGame} object that runs a game of
     * Minesweeper. The values of {@code rows}, {@code cols}, and
     * {@code mineNum} are read in through {@code textFile} and {@code textRead}
     * . Makes sure the values fit the required standards and results in
     * exceptions and errors if the {@code createMineField()} method cannot be
     * run properly.
     * 
     * @param seed
     *            refers to the file location from where information is read
     *            from
     */
    MinesweeperGame(String seed) {

        String formatError = "Seedfile Format Error: Cannot create game with "
            + seed + ", because it is not formatted correctly.";
        String nfError = "Seedfile Not Found Error: Cannot create game with "
            + seed
            + ", because it cannot be found or cannot be read due to permission.";

        try {
            File textFile = new File(seed);
            Scanner textRead = new Scanner(textFile);

            rows = textRead.nextInt();
            cols = textRead.nextInt();
            mineNum = textRead.nextInt();

            arraySize = rows * cols;

            if (rows < 5 || cols < 5) {
                System.err.println(maxError);
                System.exit(3);
            }

            if (mineNum > arraySize) {
                System.err.println(formatError);
                System.exit(1);
            }

            createMineField(rows, cols);

            for (int i = 0; i < mineNum; i++) {

                int mineX = textRead.nextInt();
                int mineY = textRead.nextInt();

                if (((mineX > rows) || (mineX < 0))
                    || ((mineY > cols) || (mineY < 0))) {
                    System.err.println(formatError);
                    System.exit(1);
                }

                trueGrid[mineX][mineY] = true;

            }

            textRead.close();
        } catch (FileNotFoundException f) {
            System.err.println(nfError);
            System.exit(1);
        } catch (NumberFormatException n) {
            System.err.println(nfError);
            System.exit(1);
        }
    }

    /**
     * Creates the mine field for {@Code MinesweeperGame}.
     * 
     * @param rows
     *            the amount of rows for mine field generation.
     * @param cols
     *            the amount of cols for mine field generation.
     */
    void createMineField(int rows, int cols) {

        mineGrid = new String[rows][cols];
        trueGrid = new boolean[rows][cols];

        for (int i = 0; i < mineGrid.length; i++) {
            for (int j = 0; j < mineGrid[i].length; j++) {
                mineGrid[i][j] = "";
                trueGrid[i][j] = false;
            }
        }

    }

    /**
     * Starts up the game loop and runs all of the methods for
     * {@code MinesweeperGame}. Continues until the method {@code isLost()}
     * becomes true or if the method {@code isWon()} becomes true.
     */
    void play() {
        printWelcome();
        promptLine = new Scanner(System.in);
        printMineField();
        while (true) {
            promptUser();

            if (isLost() == true) {
                System.out.println(loss);
                System.exit(0);
            }
            isWon();
        }
    }

    /**
     * Sets up the input line where the use may input any commands that affect
     * the mine field. Helps to process the commands for taking in
     * {@code helpGrid}, {@code revealGrid} {@code markGrid}, {@code guessGrid}
     * and {@code nofogGrid}. Changes the values inside of the mine field
     */
    void promptUser() {
        System.out.print("minesweeper-alpha: ");
        String inputString = promptLine.nextLine().trim();
        // inputString = inputString.trim().replaceAll("\t", " ");
        inputString = inputString.trim().replaceAll("\\s+", " ");
        String[] inputArray = inputString.split(" ");
        String commPrompt = inputArray[0];
        try {
            if (commPrompt.contentEquals("h")
                || commPrompt.contentEquals("help")) {
                helpGrid();
                roundsCount++;
                System.out.println("Rounds Completed: " + roundsCount);
            } else if (commPrompt.contentEquals("q")
                       || commPrompt.contentEquals("quit")) {
                System.out.println("Quitting the game..." + "\nBye");
                System.exit(0);

            } else if (commPrompt.contentEquals("r")
                       || commPrompt.contentEquals("reveal")) {
                int commX = Integer.parseInt(inputArray[1]);
                int commY = Integer.parseInt(inputArray[2]);
                revealGrid(commX, commY);
                roundsCount++;
                System.out.println("Rounds Completed: " + roundsCount);
                printMineField();

            } else if (commPrompt.contentEquals("m")
                       || commPrompt.contentEquals("mark")) {
                int commX = Integer.parseInt(inputArray[1]);
                int commY = Integer.parseInt(inputArray[2]);
                markGrid(commX, commY);
                roundsCount++;
                System.out.println("Rounds Completed: " + roundsCount);
                printMineField();

            } else if (commPrompt.contentEquals("g")
                       || commPrompt.contentEquals("guess")) {
                int commX = Integer.parseInt(inputArray[1]);
                int commY = Integer.parseInt(inputArray[2]);
                guessGrid(commX, commY);
                roundsCount++;
                System.out.println("Rounds Completed: " + roundsCount);
                printMineField();

            } else if (commPrompt.contentEquals("nofog")) {
                roundsCount++;
                System.out.println("Rounds Completed: " + roundsCount);
                nofogGrid();

            } else {
                System.out.println(inputError);
            }
            // promptLine.close();
        } catch (NumberFormatException e) {
            System.out.println(inputError);
        }
    }

    /**
     * Reveals the value behind the covered cell, if it is a mine {@code isLost}
     * is set to true causing the game to end. Otherwise replaces null value
     * with the returned value of @ countMine()} .
     * 
     * @param row
     *            the specific row value that you would like to uncover and
     *            check
     * @param col
     *            the specific column value that you would like to uncover and
     *            check
     */
    void revealGrid(int row, int col) {
        if (trueGrid[row][col] == true) {
            isLost = true;
        } else {
            mineGrid[row][col] = "" + countMine(row, col);
            revealSq++;
        }
    }

    /**
     * Sets the value for a flag on the specified mine field location.
     * 
     * @param row
     * the specific row value that you would like to mark with a
     * flag.
     * @param col
     * the specific column value that you would like to mark with a
     *flag.
     */
    void markGrid(int row, int col) {
        mineGrid[row][col] = "F";

    }

    /**
     * Sets the value for a guess on the specified mine field location.
     * 
     * @param row the specific row value that you would like to mark with an
     * uncertain guess.
     * @param col the specific column value that you would like to mark with an
     * uncertain guess.
     */
    void guessGrid(int row, int col) {
        mineGrid[row][col] = "?";
    }

    /**
     * Prints out the {@code help} string which details most of the possible
     * commands in the {@code promptUser()} method where each command is tied to
     * a word or letter.
     */
    void helpGrid() {
        String help = "\n\nCommands Available...\n"
            + "- Reveal: r/reveal row col\n"
            + "-   Mark: m/mark   row col\n"
            + "-  Guess: g/guess  row col\n" + "-   Help: h/help\n"
            + "-   Quit: q/quit\n\n";
        System.out.println(help);
    }

    /**
     * Sets the conditions for what constitutes a win in the
     * {@code MinesweeperGame} so that the system may exit the while loop and
     * leave the game gracefully with a final score. 
     */
    void isWon() {
        boolean allMinesRevealed = true;
        boolean allSquaresRevealed = true;

        for (int i = 0; i < trueGrid.length; i++) {
            for (int j = 0; j < trueGrid[0].length; j++) {
                if (trueGrid[i][j]) {
                    if (mineGrid[i][j] != "F") {
                        allMinesRevealed = false;
                    }
                } else {
                    if (mineGrid[i][j] == "F" || mineGrid[i][j] == "?"
                        || mineGrid[i][j] == "") {
                        allSquaresRevealed = false;
                    }
                }
            }
        }
        if (allMinesRevealed && allSquaresRevealed) {
            score = 100.0 * ((rows * cols) / roundsCount);
            System.out.print(dog + " " + score);
            System.exit(0);
        }
    }

    /**
     * Simply, relays the isLost value in the form of a method so that it may
     * end the while loop in {@code play()} and exits the game.
     * 
     * @return isLost the boolean that sets if the game has met the conditions
     *         for a lost
     */
    boolean isLost() {
        return isLost;
    }

    // --------------------------------------------------------------------------------/
    /**
     * 
     * Searches the surrounding cells of the selected cell and locates and mines
     * surrounding it with {@code surrMine} being counted.
     * 
     * @param row
     *            the specific row of cell that you are checking around in the
     *            mine field.
     * @param col
     *            the specific column of the cell that you are checking around
     *            in the mine field.
     * @return surrMine the integer total number of mines surrounding the chosen
     *         cell in sides and corners.
     */
    int countMine(int row, int col) {
        int surrMine = 0;

        // Left-side check
        if (col - 1 >= 0 && trueGrid[row][col - 1]) {
            surrMine++;
            //      System.out.println("1");
        }

        // Right-side check
        if ((col + 1 <= cols - 1) && trueGrid[row][col + 1]) {
            surrMine++;
            //      System.out.println("2");
        }

        // Top-side check
        if (row - 1 >= 0 && trueGrid[row - 1][col]) {
            surrMine++;
            //      System.out.println("3");
        }

        // Bottom-side check
        if ((row + 1 <= rows - 1) && trueGrid[row + 1][col]) {
            surrMine++;
            //      System.out.println("4");
        }

        // Top-left-corner check
        if (row - 1 >= 0 && col - 1 >= 0 && trueGrid[row - 1][col - 1]) {
            surrMine++;
            //      System.out.println("5");
        }

        // Top-right-corner check
        if ((row - 1 >= 0) && (col + 1 <= cols - 1)
            && trueGrid[row - 1][col + 1]) {
            surrMine++;
            //      System.out.println("6");
        }

        // Bottom-left-corner check
        if ((row + 1 <= rows - 1) && (col - 1 >= 0)
            && trueGrid[row + 1][col - 1]) {
            surrMine++;
            //      System.out.println("7");
        }

        // Bottom-right-corner check
        if ((row + 1 <= rows - 1) && (col + 1 <= cols - 1)
            && trueGrid[row + 1][col + 1]) {
            surrMine++;
            //      System.out.println("8");
        }

        return surrMine;
    }

    // ------------------------------------------------------------------------------------//
    /**
     * Prints out the welcome screen and is the first thing called in
     * {@code play()}. Writes out the {@code MinesweeperGame} title in ASCII
     * characters.
     */
    void printWelcome() {
        System.out.println(welcome);
    }

    /**
     * Prints out the mine field given the mineGrid's initial size after
     * generating it from {@code createMineField()}. Updates any grid changes
     * from any of the {@code promptUser()} methods that are being called to
     * update the mine field.
     */
    void printMineField() {
        for (int i = 0; i < mineGrid.length; i++) {
            if (i < 10) {
                System.out.print(i + " ");
            } else {
                System.out.print(i);
            }
            for (int j = 0; j <= mineGrid[0].length; j++) {
                if (j == mineGrid[0].length) {
                    System.out.print("|");
                } else {
                    if (cols <= 10) {
                        if (mineGrid[i][j].equals("")) {
                            System.out.print("|   ");
                        } else {
                            System.out.print("| " + mineGrid[i][j] + " ");
                        }
                    } else {
                        if (mineGrid[i][j].equals("")) {
                            System.out.print("|    ");
                        } else {
                            System.out.print("|  " + mineGrid[i][j] + " ");
                        }
                    }
                }
            }
            System.out.print("\n");
        }
        if (cols <= 10) {
            for (int j = 0; j < mineGrid[0].length; j++) {
                if (j == 0) {
                    System.out.print("    " + j);
                } else {
                    System.out.print("   " + j);
                }
            }
        } else {
            for (int j = 0; j < mineGrid[0].length; j++) {
                if (j == 0) {
                    System.out.print("     " + j);
                } else {
                    if (j < 10) {
                        System.out.print("    " + j);
                    } else {
                        System.out.print("   " + j);
                    }
                }
            }
        }
        System.out.println("\n");
    }

     /**
     * Works similarly to {@code printMineField} but instead of printing out the
     * mine field blank or with user changes, it simply reveals the the
     * locations of the mines in the field and surrounds them with "< >".
     * 
     */
        
    void nofogGrid() {
        nofogGridRow();
        nofogGridCol();
    }
        
    /**
     * Works similarly to {@code printMineField} but instead of printing out the
     * mine field blank or with user changes, it simply reveals the the
     * locations of the mines in the rows and surrounds them with "< >".
     * 
     */
    void nofogGridRow() {
        for (int i = 0; i < mineGrid.length; i++) {
            if (i < 10) {
                System.out.print(i + " ");
            } else {
                System.out.print(i);
            }
            for (int j = 0; j <= mineGrid[0].length; j++) {
                if (j == mineGrid[0].length) {
                    System.out.print("|");
                } else {
                    if (cols <= 10) {
                        if (mineGrid[i][j].equals("")) {
                            if (trueGrid[i][j]) {
                                System.out.print("|< >");
                            } else {
                                System.out.print("|   ");
                            }
                        } else {
                            if (trueGrid[i][j]) {
                                System.out.print("|<" + mineGrid[i][j] + ">");
                            } else {
                                System.out.print("|   ");
                            }
                        }
                    } else {
                        if (mineGrid[i][j].equals("")) {
                            if (trueGrid[i][j]) {
                                System.out.print("| < >");
                            } else {
                                System.out.print("|    ");
                            }
                        } else {
                            if (trueGrid[i][j]) {
                                System.out.print("| <" + mineGrid[i][j] + ">");
                            } else {
                                System.out.print("|    ");
                            }
                        }
                    }
                }
            }
            System.out.print("\n");
        }
    }
        
    /**
     * Works similarly to {@code printMineField} but instead of printing out the
     * mine field blank or with user changes, it simply reveals the the
     * locations of the mines in the columns and surrounds them with "< >".
     * 
     */
    void nofogGridCol() {
        
        if (cols <= 10) {
            for (int j = 0; j < mineGrid[0].length; j++) {
                if (j == 0) {
                    System.out.print("    " + j);
                } else {
                    System.out.print("   " + j);
                }
            }
        } else {
            for (int j = 0; j < mineGrid[0].length; j++) {
                if (j == 0) {
                    System.out.print("     " + j);
                } else {
                    if (j < 10) {
                        System.out.print("    " + j);
                    } else {
                        System.out.print("   " + j);
                    }
                }
            }
        }
        System.out.println("\n\n");
    }

    /**
     * Helps to print out {@code dog} and to display the exit screen after you
     * win in {@code MinesweeperGame}.
     */
    void printWin() {
        System.out.print(dog + " " + score);

    }

    /**
     * Helps to print out {@code loss} and to display the exit screen after you
     * lose in {@code MinesweeperGame}.
     */
    void printLoss() {
        System.out.println(loss);
    }

    String help = "\n\nCommands Available...\n"
        + "- Reveal: r/reveal row col\n" + "-   Mark: m/mark   row col\n"
        + "-  Guess: g/guess  row col\n" + "-   Help: h/help\n"
        + "-   Quit: q/quit\n\n";
    String welcome = "\n  /\\/\\ (_)_ __   ___  _____      _____  ___ _ __   ___ _ __\n"
        + " /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|\n"
        + "/ /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |\n"
        + "\\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|\n"
        + "                 A L P H A   E D I T I O N |_| v2020.sp\n\n";
    String loss = "\nOh no... You revealed a mine!\n"
        + "  __ _  __ _ _ __ ___   ___    _____   _____ _ __\n"
        + " / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|\n"
        + "| (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |\n"
        + " \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|\n"
        + " |___/\n";

    String dog = "\n" + "░░░░░░░░░▄░░░░░░░░░░░░░░▄░░░░ \"So Doge\"\n"
        + "░░░░░░░░▌▒█░░░░░░░░░░░▄▀▒▌░░░\n"
        + "░░░░░░░░▌▒▒█░░░░░░░░▄▀▒▒▒▐░░░ \"Such Score\"\n"
        + "░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░░\n"
        + "░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐░░░ \"Much Minesweeping\n"
        + "░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░░\n"
        + "░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒▌░░ \"Wow\"\n"
        + "░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░░\n"
        + "░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄▌░\n"
        + "░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒▌░\n"
        + "▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒▐░\n"
        + "▐▒▒▐▀▐▀▒░▄▄▒▄▒▒▒▒▒▒░▒░▒░▒▒▒▒▌\n"
        + "▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒▒▒░▒░▒░▒▒▐░\n"
        + "░▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒░▒░▒░▒░▒▒▒▌░\n"
        + "░▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▒▄▒▒▐░░\n"
        + "░░▀▄▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▄▒▒▒▒▌░░\n"
        + "░░░░▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀░░░ CONGRATULATIONS!\n"
        + "░░░░░░▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀░░░░░ YOU HAVE WON!\n"
        + "░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▀▀░░░░░░░░ SCORE: ";
    String quit = "Quitting the game...\nBye!";

    String maxError = "Seedfile Value Error: Cannot create a mine field"
        + " with that many rows and/or columns!";

    String inputError = "Input Error: Command not recognized!";

}
