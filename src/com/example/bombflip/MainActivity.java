package com.example.bombflip;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private int level = 0;
	//private final Context context = this;
	Gameboard gameboardValues;
	private int totalScore;
	private int currentScore;
	/*private String[] permMaskRA = {
		"*", "*", "*", "*", "*",
		"*", "*", "*", "*", "*",
		"*", "*", "*", "*", "*",
		"*", "*", "*", "*", "*",
		"*", "*", "*", "*", "*"
	};*/
	private String[] maskRA = {
		"*", "*", "*", "*", "*",
		"*", "*", "*", "*", "*",
		"*", "*", "*", "*", "*",
		"*", "*", "*", "*", "*",
		"*", "*", "*", "*", "*"
	};
	String[] allGridValues;
	
	TextView scoreText;
	GridView rowValuesGrid;
	GridView colValuesGrid;
	
	GridView gameboardGrid;
	
	SharedPreferences sharedPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//initialize saved data
		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		loadSavedData();
		
		//Get score TextView
		scoreText = (TextView) findViewById(R.id.score);
		scoreText.setText(currentScore + "");
		
		//Get GridViews
		rowValuesGrid = (GridView) findViewById(R.id.rowValuesGrid);
		colValuesGrid = (GridView) findViewById(R.id.colValuesGrid);

		gameboardGrid = (GridView) findViewById(R.id.gameboard);
		
		gameboardValues = new Gameboard(this.level);
		allGridValues = gameboardValues.getAllGridValues();

		this.initRowCol();
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, maskRA);
		gameboardGrid.setAdapter(adapter);
			
		gameboardGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
				updateScores(position);
				adapter.notifyDataSetChanged();
			}
		});

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_new_game) {
			this.promptNewGame();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Editor editor = sharedPrefs.edit();
		editor.putInt("totalScore", totalScore);
		editor.putInt("currentScore", currentScore);
		
		//Save
		editor.commit();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.loadSavedData();
	}
	
	private void loadSavedData() {
		totalScore = sharedPrefs.getInt("totalScore", 0);
		currentScore = sharedPrefs.getInt("currentScore", 0);
	}
	
	/**
	 * Initialize the TextViews specifying row/col values and zeroes
	 */
	private void initRowCol() {
		int[] rowVals = this.gameboardValues.getRowVals();
		int[] rowZeroes = this.gameboardValues.getRowZeroes();
		int[] colVals = this.gameboardValues.getColVals();
		int[] colZeroes = this.gameboardValues.getColZeroes();
		
		String[] rowData = new String[10];
		String[] colData = new String[10];
		
		//Initialize rowValues
		int valIndex = 0;
		int zeroIndex = 0;
		for (int i = 0; i < rowData.length; i++) {
			if (i % 2 == 0) {
				rowData[i] = rowVals[valIndex] + "";
				valIndex++;
			} else {
				rowData[i] = rowZeroes[zeroIndex] + "";
				zeroIndex++;
			}
		}
		
		valIndex = 0;
		zeroIndex = 0;
		//set up bottom two rows
		for (int i = 0; i < colData.length; i++) {
			if (i < 5) {
				colData[i] = colVals[valIndex] + "";
				valIndex++;
			} else {
				colData[i] = colZeroes[zeroIndex] + "";
				zeroIndex++;
			}
		}
		
		//Modify GridViews
		ArrayAdapter<String> colAdapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_list_item_1, colData);
		this.colValuesGrid.setAdapter(colAdapter);
		ArrayAdapter<String> rowAdapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_list_item_1, rowData);
		this.rowValuesGrid.setAdapter(rowAdapter);

	}
	
	/**
	 * Create an alert dialog to prompt the user to start a new game
	 */
	protected void promptNewGame() {
		//Prompt for new game
		NewGameDialogFragment newGamePrompt = new NewGameDialogFragment();
		FragmentManager fManager = getFragmentManager();
		newGamePrompt.show(fManager, "newGamePrompt");
	}
	
	/**
	 * Reset the grid mask and create a new grid
	 */
	protected void newGrid() {
		//Reset mask
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) gameboardGrid.getAdapter();
		for (int i = 0; i < maskRA.length; i++) {
			this.maskRA[i] = "*";
		}
		adapter.notifyDataSetChanged();
		//Create board
		gameboardValues = new Gameboard(this.level);
		allGridValues = gameboardValues.getAllGridValues();

		this.initRowCol();

	}
	
	/**
	 * Bulk of the work goes here
	 * @param position the position in the grid that was selected
	 */
	public void updateScores(int position) {
		//if hidden tile, do stuff, otherwise ignore
		if (!gameboardValues.flipped(position)) {
			//Get score and modify total score
			int val = gameboardValues.itemClicked(position);
			if (val == 0) {
				this.gameOver();
			}
			//update scoreText
			scoreText.setText((gameboardValues.getCurrentScore()) + "");
			//Change flipped or not
			gameboardValues.flip(position);
			maskRA[position] = allGridValues[position];
			//Check if won
			checkWin();
		}
	}
	
	/**
	 * See if you've won, increment level, make new game
	 */
	private void checkWin() {
		if (gameboardValues.won()) {
			//Toast.makeText(this, "You win!", Toast.LENGTH_LONG).show();
			//update total score
			this.totalScore = gameboardValues.getCurrentScore();
			//Increment level
			this.level++;
			//Reinitialize gameboard
			this.promptNewGame();
		}
	}
	
	/**
	 * If lost, prompt for new game, lower level
	 */
	private void gameOver() {
		this.level--;
		this.promptNewGame();
	}
	
}
