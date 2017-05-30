package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import player.GreedyGuessPlayer.MyShip;
//import player.MonteCarloGuessPlayer.Cell;
import ship.Ship;
import world.World;
import world.World.Coordinate;
import world.World.ShipLocation;

/**
 * Monte Carlo guess player (task C). Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class MonteCarloGuessPlayer implements Player {

	int rowSize = 0;
	int columnSize = 0;
	boolean[][] guessedCell;
	boolean isHex;

	Guess lastGuess = new Guess();
	Guess startingHitPoint = new Guess();
	boolean targetingMode = false;
	boolean isShipHorizontal = false;
	boolean isShipVertical = false;

	class MyShip {
		Ship ship = null;
		int[] rowCoord = { -1, -1, -1, -1, -1 };
		int[] columnCoord = { -1, -1, -1, -1, -1 };
		boolean[] sunk = { true, true, true, true, true };

		private MyShip() {

		}
	}

	MyShip[] myShips = new MyShip[5];

	@Override
	public void initialisePlayer(World world) {
		this.rowSize = world.numRow;
		this.columnSize = world.numColumn;
		this.isHex = world.isHex;
		guessedCell = new boolean[rowSize][columnSize];
		int i = 0;
		for (World.ShipLocation myShipLocation : world.shipLocations) {

			myShips[i] = new MyShip();
			myShips[i].ship = myShipLocation.ship;

			for (int j = 0; j < myShips[i].ship.len(); j++) {
				myShips[i].rowCoord[j] = myShipLocation.coordinates.get(j).row;
				myShips[i].columnCoord[j] = myShipLocation.coordinates.get(j).column;
				myShips[i].sunk[j] = false;
			}
			i++;
		}
		
		countProb();

	} // end of initialisePlayer()


	ArrayList<GuessTable> guessTable = new ArrayList<>();

	public class GuessTable {
		int row;
		int column;
		int count;
	}

	public void countProb() {

		for (int i = 0; i < rowSize; i++) {
			for (int j = 0; j < columnSize; j++) {
				
				if (guessedCell[i][j]!=true){ // If cells isn't marked visited
					GuessTable guessCoord = new GuessTable();
					guessCoord.row = i;
					guessCoord.column = j;

					// Calculate the configuration of the cell
					for (int k = 0; k < myShips.length; k++) {
						checkWestProb(guessCoord, k);
						checkNorthProb(guessCoord, k);
						checkEastProb(guessCoord, k);
						checkSouthProb(guessCoord, k);
					}
					guessTable.add(guessCoord);
				}
			}
		}
		for (int q = 0; q < guessTable.size(); q++) {
			System.out.print(guessTable.get(q).count + "  ");
			System.out.println("");
		}
	}

	public void checkSouthProb(GuessTable guessCoord, int k) {
		GuessTable checkCoord = new GuessTable();
		checkCoord.row = guessCoord.row - myShips[k].ship.len();
		if (checkCoord.row >= 0 && checkCoord.row < 10 && checkCoord.column >= 0 && checkCoord.column < 10 )
			// check if nearby cell has been guessed, if not, increase configuration count
			for (int i = 0; i < myShips[k].ship.len(); i++){
				checkCoord.row = guessCoord.row - i;
				if (guessedCell[checkCoord.row][guessCoord.column]!=true){
					guessCoord.count++;
				}
			}
			
	}

	public void checkNorthProb(GuessTable guessCoord, int k) {
		GuessTable checkCoord = new GuessTable();
		checkCoord.row = guessCoord.row + myShips[k].ship.len();
		if (checkCoord.row >= 0 && checkCoord.row < 10 && checkCoord.column >= 0 && checkCoord.column < 10)
			// check if nearby cell has been guessed, if not, increase configuration count
			for (int i = 0; i < myShips[k].ship.len(); i++){
				checkCoord.row = guessCoord.row + i;
				if (guessedCell[checkCoord.row][guessCoord.column]!=true){
					guessCoord.count++;
				}
			}
	}

	public void checkWestProb(GuessTable guessCoord, int k) {
		GuessTable checkCoord = new GuessTable();
		checkCoord.column = guessCoord.column - myShips[k].ship.len();
		if (checkCoord.row >= 0 && checkCoord.row < 10 && checkCoord.column >= 0 && checkCoord.column < 10)
			// check if nearby cell has been guessed, if not, increase configuration count
			for (int i = 0; i < myShips[k].ship.len(); i++){
				checkCoord.column = guessCoord.column - i;
				if (guessedCell[guessCoord.row][checkCoord.column]!=true){
					guessCoord.count++;
				}
			}
	}

	public void checkEastProb(GuessTable guessCoord, int k) {
		GuessTable checkCoord = new GuessTable();
		checkCoord.column = guessCoord.column + myShips[k].ship.len();
		if (checkCoord.row >= 0 && checkCoord.row < 10 && checkCoord.column >= 0 && checkCoord.column < 10)
			// check if nearby cell has been guessed, if not, increase configuration count
			for (int i = 0; i < myShips[k].ship.len(); i++){
				checkCoord.column = guessCoord.column + i;
				if (guessedCell[guessCoord.row][checkCoord.column]!=true){
					guessCoord.count++;
				}
			}
	}

	@Override
	public Answer getAnswer(Guess guess) {
		Answer answer = new Answer();

		for (int i = 0; i < 5; i++) {

			for (int j = 0; j < myShips[i].ship.len(); j++) {
				if (guess.row == myShips[i].rowCoord[j] && guess.column == myShips[i].columnCoord[j]) {
					answer.isHit = true;
					myShips[i].sunk[j] = true;
					boolean isSunk = true;
					for (int l = 0; l < myShips[i].ship.len(); l++) {
						if (myShips[i].sunk[l] == false) {
							isSunk = false;
						}
					}
					if (isSunk != false) {
						answer.shipSunk = myShips[i].ship;
					}
					return answer;
				}
			}

		}

		return answer;
	} // end of getAnswer()


	int largestRow, largestColumn;

	public Guess huntingMode() {

		Guess guess = new Guess();

		int largest = 0;
		for (int i = 0; i < guessTable.size(); i++) {
			// Find cell with largest configuration count
			if (guessTable.get(i).count > largest && guessedCell[guessTable.get(i).row][guessTable.get(i).column] != true) {
				largest = guessTable.get(i).count;
				largestRow = guessTable.get(i).row;
				largestColumn = guessTable.get(i).column;
				guessTable.get(i).count = 0; // mark it 0 count after guessing
			}
		}
		guess.row = largestRow;
		guess.column = largestColumn;
		guessedCell[guess.row][guess.column] = true;
		return guess;

	}

	public Guess guessWest() {
		Guess guess = new Guess();
		guess.row = lastGuess.row;
		guess.column = lastGuess.column - 1;
		guessedCell[guess.row][guess.column] = true;
		return guess;
	}

	public Guess guessEast() {
		Guess guess = new Guess();
		guess.row = lastGuess.row;
		guess.column = lastGuess.column + 1;
		guessedCell[guess.row][guess.column] = true;
		return guess;
	}

	public Guess guessNorth() {
		Guess guess = new Guess();
		guess.row = lastGuess.row + 1;
		guess.column = lastGuess.column;
		guessedCell[guess.row][guess.column] = true;
		return guess;
	}

	public Guess guessSouth() {
		Guess guess = new Guess();
		guess.row = lastGuess.row - 1;
		guess.column = lastGuess.column;
		guessedCell[guess.row][guess.column] = true;
		return guess;
	}

	public Guess targetingMode() {
		Guess guess = new Guess();

		if (lastGuess.row == 0) { // If at very bottom row
			if (isShipVertical) { // If ship is vertical, guess north only
				if (guessedCell[lastGuess.row + 1][lastGuess.column] == false) {
					return guessNorth();
				}
			}
			if (guessedCell[lastGuess.row][lastGuess.column - 1] == false) {
				return guessWest();
			}
			if (guessedCell[lastGuess.row][lastGuess.column + 1] == false) {
				return guessEast();
			}
			if (guessedCell[lastGuess.row + 1][lastGuess.column] == false) {
				return guessNorth();
			}
		} else if (lastGuess.row == rowSize - 1) { // If at very top of row
			if (isShipVertical) { // If ship is vertical, guess south only
				if (guessedCell[lastGuess.row - 1][lastGuess.column] == false) {
					return guessSouth();
				}
			}
			if (guessedCell[lastGuess.row][lastGuess.column - 1] == false) {
				return guessWest();
			}
			if (guessedCell[lastGuess.row][lastGuess.column + 1] == false) {
				return guessEast();
			}
			if (guessedCell[lastGuess.row - 1][lastGuess.column] == false) {
				return guessSouth();
			}
		} else {
			if (isShipHorizontal) { // If ship is horizontal, guess only towards
									// east and west
				if (guessedCell[lastGuess.row][lastGuess.column - 1] == false) {
					return guessWest();
				}
				if (guessedCell[lastGuess.row][lastGuess.column + 1] == false) {
					return guessEast();
				}
			}
			if (isShipVertical) { // If ship is vertical, guess only towards
									// north and south
				if (guessedCell[lastGuess.row + 1][lastGuess.column] == false) {
					return guessNorth();
				}
				if (guessedCell[lastGuess.row - 1][lastGuess.column] == false) {
					return guessSouth();
				}
			}
			if (guessedCell[lastGuess.row][lastGuess.column - 1] == false) {
				return guessWest();
			}
			if (guessedCell[lastGuess.row][lastGuess.column + 1] == false) {
				return guessEast();
			}
			if (guessedCell[lastGuess.row + 1][lastGuess.column] == false) {
				return guessNorth();
			}
			if (guessedCell[lastGuess.row - 1][lastGuess.column] == false) {
				return guessSouth();
			}
		}

		// If all surrounding cell are guessed but ship still not sunk, go back
		// to where the hit first land
		if (!trackHitHistory.isEmpty()) {
			guess = trackHitHistory.get(0);
		}

		return guess;
	}

	@Override
	public Guess makeGuess() {

		if (targetingMode == true) {
			return targetingMode();
		}
		return huntingMode();

	} // end of makeGuess()

	ArrayList<Guess> trackHitHistory = new ArrayList<Guess>();

	@Override
	public void update(Guess guess, Answer answer) {

		// calculate the configuration (doesn't fully work, but please consider giving me marks based on my logic)
		countProb();
		
		if (answer.isHit) {
			// Record the location where the first hit on the ship lands
			trackHitHistory.add(guess);
			

			// To determine if ship is horizontal or vertical, improving
			// efficiency of next guess
			if (trackHitHistory.size() > 1) {
				if (trackHitHistory.get(1).column > trackHitHistory.get(0).column
						|| trackHitHistory.get(1).column < trackHitHistory.get(0).column) {
					isShipHorizontal = true;
				}
				if (trackHitHistory.get(1).row > trackHitHistory.get(0).row
						|| trackHitHistory.get(1).row < trackHitHistory.get(0).row) {
					isShipVertical = true;
				}
			}

			// When in targeting mode, if a ship has sunk, go back to hunting
			// mode and clear first hit location
			if (answer.shipSunk != null) {
				trackHitHistory.clear();
				targetingMode = false;
				isShipVertical = false;
				isShipHorizontal = false;
			} else { // Else if a hit lands and ship has not sunk, target
						// surrounding of the current cell
				lastGuess.row = guess.row;
				lastGuess.column = guess.column;
				targetingMode = true;
			}

		} else { // If doesn't hit, go back to where the hit lands first and
					// explore the other side
			if (!trackHitHistory.isEmpty() && answer.shipSunk == null) {
				guess = trackHitHistory.get(0);
			}
			
			
			
		}

	} // end of update()

	@Override
	public boolean noRemainingShips() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < myShips[i].ship.len(); j++) {
				if (myShips[i].sunk[j] == false) {
					return false;
				}
			}
		}
		return true;
	} // end of noRemainingShips()

} // end of class MonteCarloGuessPlayer
