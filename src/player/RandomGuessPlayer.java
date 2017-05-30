package player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import ship.Ship;
import world.World;
import world.World.ShipLocation;

/**
 * Random guess player (task A). Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class RandomGuessPlayer implements Player {

	int rowSize = 0;
	int columnSize = 0;
	boolean[][] guessedCell;
	boolean isHex;

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

	} // end of initialisePlayer()

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

	@Override
	public Guess makeGuess() {
		Random random = new Random();
		Guess guess = new Guess();
		int row;
		int column;
		// Guess a random cell that hasn't been guessed before
		do {
			row = random.nextInt(rowSize);
			if (isHex) {
				column = random.nextInt(columnSize + (rowSize + 1) / 2);
			} else {
				column = random.nextInt(columnSize);
			}

		} while (guessedCell[row][column] != false);
		guess.row = row;
		guess.column = column;
		guessedCell[row][column] = true;
		return guess;

	} // end of makeGuess()

	@Override
	public void update(Guess guess, Answer answer) {
		// To be implemented.
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

} // end of class RandomGuessPlayer
