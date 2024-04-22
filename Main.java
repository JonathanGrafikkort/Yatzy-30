import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //create range[] for intScannerAnswer and set up the scanner "input"
        int[] range = new int[3];
        range[0] = 2;
        range[1] = 10;
        range[2] = 1;

        //request player amount
        System.out.println("How many players?(2-10)");
        int playerAmount = intScannerAnswer(range);
        System.out.println("Player amount: " + playerAmount);

        //name players
        String[] playerNames = nameGetter(playerAmount);

        //Call the main game method after telling the player that the game is starting
        System.out.println("Press enter to start game.");
        pressEnterToContinue();
        String winningPlayer = mainGameLoop(playerAmount, playerNames);

        //print the winning player name
        System.out.println(winningPlayer + "wins");
    }

    public static String mainGameLoop(int playerAmount, String[] playerNames) {
        //main method which calls most other methods

        //init playerHealth[] with correct length and value for each player
        //playerHealth[even] is reserved for player names
        //playerHealth[odd] is reserved for player health
        int[] playerHealth = new int[playerAmount];
        Arrays.fill(playerHealth, 30);

        //keeps track of how many players HP is under 0
        int amountOfDeadPlayers = 0;

        //stores data from diceRoller method
        int totalDiceValue;

        //init currentPlayer
        int currentPlayer = 0;

        //used to determine who the damage coming from the battle function will land on
        int targetedPlayer;

        //used for calling intScannerAnswer method
        int[] range = new int[3];

        //used when calling diceRoller method
        boolean battleMode = false;

        //used to check how much damage is dealt
        int damageDealt;


        while (true) {

            //select the next active player and make sure that it is not dead
            while (true) {
                currentPlayer++;
                if (currentPlayer > playerAmount - 1) {
                    currentPlayer = 0;
                }
                if (playerHealth[currentPlayer] <= 0) {
                    System.out.println(playerNames[currentPlayer] + " is dead. Moving on to the next player...");
                } else {
                    break;
                }
            }

            for (int i = 0; i < playerAmount; i++) {
                if (playerHealth[i] <= 0) {
                    amountOfDeadPlayers++;
                }
            }
            if (amountOfDeadPlayers == playerAmount - 1) {
                for (int i = 0; i < playerAmount; i++) {
                    if (playerHealth[i] >= 1) {
                        return playerNames[i];
                    }
                }
            }

            //tell the player who's turn it is
            System.out.println(playerNames[currentPlayer] + "'s turn");

            //call diceRoller method
            //set range[] to correct values
            range[0] = 1;
            range[1] = 6;
            range[2] = 1;
            totalDiceValue = diceRoller(playerNames[currentPlayer], playerHealth[currentPlayer], range, battleMode);
            //set range[2] back to zero
            range[2] = 0;

            //print players score
            System.out.println(playerNames[currentPlayer] + "'s score: " + totalDiceValue);


            //if score > 30, take them to the battle section
            if (totalDiceValue > 30) {

                System.out.println("Choose a player to attack:");
                preBattleRenderer(playerNames, playerAmount, playerHealth, currentPlayer);

                range[0] = 1;
                range[1] = playerAmount;
                range[2] = 1;

                targetedPlayer = intScannerAnswer(range) - 1;

                System.out.println("Target: " + playerNames[targetedPlayer + 1] +
                        " (player no." + (targetedPlayer + 1) + ").");

                //deal damage using battle() function
                damageDealt = battle(totalDiceValue, playerNames[currentPlayer], playerHealth[currentPlayer]);
                //calculate new playerHealth and tell the player what their new health is
                playerHealth[targetedPlayer] = playerHealth[targetedPlayer] - damageDealt;
                System.out.println(playerNames[targetedPlayer] + "'s new health is " + playerHealth[targetedPlayer] + ".");


                //if score is equal to 30, deal no damage and initiate no battle
            } else if (totalDiceValue == 30) {
                System.out.println("the player takes no damage and does not attack another");

            }
            //if score is under 30, deal damage equal to 30 - score
            else {
                playerHealth[currentPlayer] = playerHealth[currentPlayer] - (30 - totalDiceValue);
                System.out.println( " " + (30 - totalDiceValue) +
                        " damage dealt. New health: " + playerHealth[currentPlayer] );
            }

            System.out.println("Enter to switch to next player");
            pressEnterToContinue();

        }


    }

    public static void preBattleRenderer(String[] playerNames, int playerAmount, int[] playerHealth, int currentPlayer) {
        //small method used for rendering the different players and their HP

        System.out.println("""
                
                Player name            HP  
               """);

        //for every player that's not the current player, render their name and HP as well as player number
        for (int i = 0; i < playerAmount; i++) {
            if (i != currentPlayer) {
                System.out.format("""
                                 %1$-21s  %2$-2d 
                                """
                        , playerNames[i], playerHealth[i], i + 1);
            }
        }

        System.out.println("\n(Select a player between " + 1 + " and " + (playerAmount) + ").");
    }

    public static int battle(int totalDiceValue, String currentPlayer, int playerHP) {
        //method for battling against other players, called when total dice score > 30

        //How much damage is dealt
        int damageDealt;

        //multiplies amount of dice used to attack by the balue of one such dice
        int damageMultiplier = totalDiceValue - 30;

        //used when calling diceRoller method
        boolean battleMode = true;

        //used when calling diceRoller method
        int[] range = new int[3];


        System.out.println("Rolling battle dice...");

        //set range to correct values (so that the only valid input is equal to damageMultiplier)
        range[0] = damageMultiplier;
        range[1] = damageMultiplier;
        range[2] = 1;
        damageDealt = diceRoller(currentPlayer, playerHP, range, battleMode);

        return damageDealt;


    }

    public static void diceRenderingMethod(int[] dice, String currentPlayer, int playerHP) {
        //method for printing out the UI element of the dice rolling phase

        //diceRendering[0-5] is reserved for values of dice
        //diceRendering[6-11] is reserved for which dice are saved (1 = saved, 0 = not saved)
        String[] diceRendering = new String[12];

        //assign correct values to diceRendering[]
        //pos 0-5 is used for display values
        //pos 6-11 is used for lock symbols
        for (int i = 0; i < 6; i++) {
            diceRendering[i] = String.valueOf(dice[i]);

            if (dice[i + 6] == 1) {
                diceRendering[i + 6] = "*";
            } else {
                diceRendering[i + 6] = "";
            }
        }

        //print out the main UI using the System.out.format function
        System.out.format(
                """
                                    
                             Player:  %1$-21s 
                                    
                               Dice:  1  2  3  4  5  6 
                                    
                             Result:  %2$s%3$-1s %4$s%5$-1s %6$s%7$-1s %8$s%9$-1s %10$s%11$-1s %12$s%13$-1s
                                    
                        """
                , currentPlayer, diceRendering[0], diceRendering[6], diceRendering[1]
                , diceRendering[7], diceRendering[2], diceRendering[8], diceRendering[3]
                , diceRendering[9], diceRendering[4], diceRendering[10], diceRendering[5], diceRendering[11]
        );

        //print out the HP module as well
        System.out.format("""
                            Your HP: %-2d          
                        """
                , playerHP
        );
    }

    public static int diceRoller(String currentPlayer, int playerHP, int[] range, boolean battleMode) {
        //method which rolls dice, calls the rendering method to display those dice, asks
        //the player what dice they want to save until they have saved all the dice and
        //then returns the total value of the saved dice to the main game loop

        //init scannerInput and savedScannerInput
        Scanner scannerInput = new Scanner(System.in);
        String savedScannerInput;


        //init boolean hasSavedDice (used in dice-saving while loop)
        boolean hasSavedDice;

        //init diceData[] with a length of 12
        //diceData[0-5] is reserved for values of dice
        //diceData[6-11] is reserved for booleans of which dice are saved
        int[] diceData = new int[12];
        //keeps track of how many dice are saved currently
        int savedDiceAmount = 0;
        //used for tracking the collective sum of the dices
        int totalValue = 0;

        //init possibleSelections, used in battleMode
        //when possibleSelections == 0, return totalValue
        int possibleSelections;


        Random diceValue = new Random();

        //roll the dice
        while (true) {

            //roll new random values for the dice
            for (int i = 0; i < 6; i++) {
                //check if currently rolling dice are locked
                if (diceData[i + 6] != 1) {
                    //roll random number between 1 and 6
                    diceData[i] = diceValue.nextInt(1, 7);
                }
            }

            //return the total value of the saved dice if all the dice have been saved
            if (savedDiceAmount == 6) {
                for (int i = 0; i < 6; i++) {
                    totalValue = totalValue + diceData[i];
                }
                return totalValue;
            }

            //reset possibleSelections
            possibleSelections = 0;
            //if battleMode is enabled, have to search through every non-locked dice to make sure the player is able to make a selection
            if (battleMode) {
                for (int i = 0; i < 6; i++) {
                    if (diceData[i + 6] != 1 && diceData[i] == range[0]) {
                        possibleSelections++;
                        System.out.println("DEBUG: possible selections = " + possibleSelections);
                    }
                }

                //if there are no possible selections, run through all dice, save the value of the locked ones to totalValue, and return totalValue
                if (possibleSelections == 0) {
                    if (savedDiceAmount == 0) {
                        System.out.println("No dice were saved.");
                    }
                    for (int i = 0; i < 6; i++) {
                        if (diceData[i + 6] == 1) {
                            totalValue = totalValue + diceData[i];
                        }
                    }
                    return totalValue;
                }
            }


            //print the UI for the newly rolled dice
            diceRenderingMethod(diceData, currentPlayer, playerHP);

            System.out.println("\n" +
                    "Input which dice to keep " +
                    "Exit input-sequence by pressing enter.");

            //if battleMode is enabled, tell the player that they may only keep dice with a value equal to the damage multiplier
            if (battleMode) {
                System.out.println("You may only keep dice with the value of " + range[1] + ".");
            }

            hasSavedDice = false;
            //save the dice
            while (true) {

                //read from cmd line next answer
                savedScannerInput = scannerInput.nextLine();

                //if player has saved at least 1 dice and has input 'quit', the program moves on
                // to rolling the next set of (unsaved) dice
                if (Objects.equals(savedScannerInput, "")) {
                    if (hasSavedDice) {
                        break;
                    } else {
                        System.out.println("Save at least one dice");
                    }
                }

                //check whether the given input is an integer and fits within the given range.
                // if not, inform the user how to fix the problem
                else {
                    try {

                        //check if input is within range
                        //AND check if battleMode is NOT enabled
                        //AND if input is not already saved
                        //OR
                        //check if dice value equals range[0]
                        //AND check if battleMode is enabled
                        //AND check if input is not already saved
                        if (Integer.parseInt(savedScannerInput) <= range[1]
                                && Integer.parseInt(savedScannerInput) >= range[0]
                                && diceData[Integer.parseInt(savedScannerInput) + 5] != 1
                                && !battleMode
                                ||
                                diceData[Integer.parseInt(savedScannerInput) - 1] == range[0]
                                        && battleMode
                                        && diceData[Integer.parseInt(savedScannerInput) + 5] != 1) {

                            //increase savedDiceAmount by 1 and set hasSavedDice to true,
                            // so that the loop may be exited by entering "quit"
                            hasSavedDice = true;
                            savedDiceAmount = savedDiceAmount + 1;
                            diceData[Integer.parseInt(savedScannerInput) + 5] = 1;
                            System.out.println("Dice saved.");
                        }

                        //if above does not apply, try following errors:
                        else {

                            //dice has already been saved
                            if (diceData[Integer.parseInt(savedScannerInput) + 5] == 1) {
                                System.out.println("Dice already saved");

                                //in battleMode, dice value does not match target value
                            } else if (battleMode) {
                                System.out.println("Invalid dice value");

                            }

                            //input is not within range
                            else {
                                System.out.println("Input not within range");
                            }

                        }
                    }

                    //if above fails, input is either not an integer or does not fit within range 0-11.
                    //re-check range (with/without battle mode enabled) (for error message continuity) and check if input is an integer
                    catch (Exception E) {
                        try {

                            if (battleMode) {
                                System.out.println("Invalid input");

                            } else {
                                System.out.println("Input not within range");
                            }

                        } catch (Exception e) {
                            System.out.println("Invalid input");
                        }
                    }
                }
            }
        }
    }

    public static String[] nameGetter(int nameAmount) {
        //method which returns set amount of names

        //init Scanner scannerInput and names[]
        Scanner scannerInput = new Scanner(System.in);
        String[] names = new String[nameAmount];

        //init local counting variable
        int i = 0;
        while (i < nameAmount) {
            System.out.println("Name no." + (i + 1));
            names[i] = scannerInput.nextLine();


            //check if name is short enough to fit in the renderingMethod UI
            if (names[i].length() >= 21) {
                System.out.println("Name is not allowed to exceed 21 characters");
            }

            //else initiate duplicate checker
            else {
                //check if name is first name
                if (i == 0) {
                    i++;
                }

                //else run duplicate checker
                else {

                    //if name duplicate is found, re-enter name for that player. else continue with next name
                    //breaking loop doesn't let y reach i value thus doesn't let i increase to next name
                    for (int y = 0; true; y++) {

                        if (Objects.equals(names[i], names[y])) {

                            if (i != y) {
                                System.out.println("Two names are not allowed to be identical");
                                break;
                            }
                        }

                        //if no duplicate is found, exit loop and increase i by one
                        if (i == y) {
                            i++;
                            break;
                        }
                    }
                }
            }
        }

        return names;
    }

    public static int intScannerAnswer(int[] range) {
        //method which makes sure to only return an integer after asking a
        //range[0,1] is reserved for range values
        //range[2] is reserved for range if segment

        //init scanner & input memory
        Scanner scannerInput = new Scanner(System.in);
        String savedInput;

        //if range is enabled, try to return integer within range
        if (range[2] == 1) {
            while (true) {
                try {
                    savedInput = scannerInput.nextLine();

                    if (range[0] <= Integer.parseInt(savedInput) && Integer.parseInt(savedInput) <= range[1]) {
                        return Integer.parseInt(savedInput);
                    } else {
                        System.out.println(
                                "Input not within range");
                    }
                } catch (Exception E) {
                    System.out.println(
                            "Invalid input");
                }
            }
        }

        //if range is not enabled, only check for integer answer
        else {
            while (true) {
                try {
                    return Integer.parseInt(scannerInput.nextLine());
                } catch (Exception E) {
                    System.out.println(
                            "Invalid input");
                }
            }
        }

    }

    public static void pressEnterToContinue() {
        //über-short method simply used when you want a "press any key to continue" moment
        Scanner pressEnter = new Scanner(System.in);
        pressEnter.nextLine();
    }
}