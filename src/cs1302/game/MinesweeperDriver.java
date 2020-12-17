package cs1302.game;

/**.
 * The driver class helps to run the methods from the Minesweeper game.
 * It serves as the backbone of the sode and helps to take in the arguents.
 * @author Sourin Paturi
 */
public class MinesweeperDriver {
        

     /**
     * Creates an instantiation of {@code MinesweeperGame} and it also runs the methods 
     * from the {@code MinesweeperGame} class while processing text file info.
     * @param args the command-line arguments that takes in command and text file
     */
    public static void main(String[] args) {
                
        if (args[0].equals("--gen")) {
            System.out.println("Seedfile generation not supported.");
            System.exit(2);
        }
                // first arg must be seed and number of args must be 2
        if (!args[0].equals("--seed") || args.length != 2) {
            System.out
                  .println("Unable to interpret supplied command-line arguments.");
            System.exit(1);
        }
                
        MinesweeperGame mg = new MinesweeperGame(args[1]);
        mg.play();

    }

}

