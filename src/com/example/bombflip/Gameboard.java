package com.example.bombflip;

import java.util.Random;

public class Gameboard {
	
	/* Constants */
	private static final int SIZE = 5;
	
	/* Scores */
	private int currentScore;
	public boolean youWin;

	private int[][] grid;		//The 2d array which stores the values of the grid

	Random randy;				//Used to randomly select values for grid
	int level;					//The user's current level, used for choosing what values to use for grid
	private int[] colVals;		//The sum of the values of each column
	private int[] rowVals;		//The sum of the values of each row
	private int[] colZeroes;	//The number of zeroes in each column
	private int[] rowZeroes;	//The number of zeroes in each row
	
	private int[] allGridValues;//1d Array of size SIZE*SIZE that has all values of the grid
	private int counter;		//Keeps track of how many values have been added to allGridValues

	private boolean[] flipped;
	private boolean[] flag0;
	private boolean[] flag1;
	private boolean[] flag2;
	private boolean[] flag3;
	
	private int winningScore;	//The multiple of all the 2 and 3 values to check when player has won
	
	/**
	 * Constructor for a gameboard object
	 */
	public Gameboard(int level) {
		randy = new Random();
		this.level = level;
		this.counter = 0;
		this.currentScore = 0;
		this.winningScore = 1;
		this.colVals = new int[SIZE];
		this.rowVals = new int[SIZE];
		this.colZeroes = new int[SIZE];
		this.rowZeroes = new int[SIZE];
		this.grid = new int[SIZE][SIZE];
		
		this.allGridValues = new int[SIZE*SIZE];
		this.flipped = new boolean[SIZE*SIZE];
		this.flag0 = new boolean[SIZE*SIZE];
		this.flag1 = new boolean[SIZE*SIZE];
		this.flag2 = new boolean[SIZE*SIZE];
		this.flag3 = new boolean[SIZE*SIZE];

		this.youWin = false;
		this.initGrid();
	}
	
	/**
	 * Initializes each element of the grid to either 0, 1, 2, or 3
	 */
	private void initGrid() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				int val = randy.nextInt(100);
				//distribute values (based on level)
				//The higher the level, the higher the number of 3's and 0's
				if (val <= 15) {
					val = 0;
				} else if (val <= 25) {
					val = 3;
					winningScore *= val;
				} else if (val <= 40) {
					val = 2;
					winningScore *= val;
				} else {
					val = 1;
				}
				grid[i][j] = val;
				allGridValues[counter] = val;
				counter++;
				//if zero, add to row and column of zero arrays
				if (val == 0) {
					colZeroes[j] += 1;
					rowZeroes[i] += 1;
				}
				//add to row and column of value arrays
				colVals[j] += val;
				rowVals[i] += val;
			}
		}
		
		for (int i = 0; i < SIZE*SIZE; i++) {
			flipped[i] = false;
			flag0[0] = false;
			flag1[0] = false;
			flag2[0] = false;
			flag3[0] = false;
		}
	}
	
	/**
	 * Casts all values in allGridValues array to String
	 * @return the String array of all grid values
	 */
	public String[] getAllGridValues() {
		String[] stringGrid = new String[SIZE*SIZE];
		for (int i = 0; i < SIZE*SIZE; i++) {
			stringGrid[i] = allGridValues[i] + " ";
		}
		return stringGrid;
	}
	
	/**
	 * Pass the position in the grid that was clicked, then get the score and do calculations to it
	 * @param position
	 * @return the value of the tile clicked
	 */
	public int itemClicked(int position) {
		int val = allGridValues[position];
		if (val == 0) {
			gameOver();
		} else {
			if (this.currentScore == 0) {
				this.currentScore = val;
			} else {
				this.currentScore *= val;
			}
			if (this.currentScore == this.winningScore) {
				youWin = true;
			}
		}
		return val;
	}
	
	/**
	 * Cannot flip back to false
	 * @param position
	 */
	public void flip(int position) {
		flipped[position] = true;
	}
	
	public boolean flipped(int position) {
		if (flipped[position]) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/*** Flag Methods ***/
	
	public void flag0(int position) {
		if (!flag0[position]) {
			flag0[position] = true;
		} else {
			flag0[position] = false;
		}
	}
	
	public void flag1(int position) {
		if (!flag1[position]) {
			flag1[position] = true;
		} else {
			flag1[position] = false;
		}
	}
	
	public void flag2(int position) {
		if (!flag2[position]) {
			flag2[position] = true;
		} else {
			flag2[position] = false;
		}
	}
	
	public void flag3(int position) {
		if (!flag3[position]) {
			flag3[position] = true;
		} else {
			flag3[position] = false;
		}
	}
	
	public boolean won() {
		return this.youWin;
	}

	private void gameOver() {
		currentScore = 0;
	}

	/***Accessor methods***/
	
	public int[][] getGrid() {
		return grid;
	}
	
	public int[] getColVals() {
		return colVals;
	}
	
	public int[] getRowVals() {
		return rowVals;
	}
	
	public int[] getColZeroes() {
		return colZeroes;
	}
	
	public int[] getRowZeroes() {
		return rowZeroes;
	}
	
	public int getCurrentScore() {
		return currentScore;
	}
	
}
