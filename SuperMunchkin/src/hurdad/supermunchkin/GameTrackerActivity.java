package hurdad.supermunchkin;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class GameTrackerActivity extends Activity {
	private final int CONFIRM_END_GAME = 3;
	private Game game;
	
	private TextView playerNameTextView;
	private TextView playerStrengthTextView;
	private NumberPicker levelNumberPicker;
	private NumberPicker gearNumberPicker;
	private CheckBox warriorCheckBox;
	
	private Button nextPlayerButton;
	private Button previousPlayerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_tracker);
        
        if (savedInstanceState == null) {       
            if (getIntent().getStringExtra("callingActivity").equals("OrderPlayersActivity")) {
                
                // initialize game variables
                game = new Game();
                game.setStartDate(new Date());
                game.setEnabled(true);
                game.setPlayerTurn(0);

                // initialize playerGameVariables
                game.setPlayerGames(new ArrayList<PlayerGame>());
                ArrayList<Player> players = getIntent().getParcelableArrayListExtra("orderedPlayers");
                for (Player player : players) {
                	PlayerGame playerGame = new PlayerGame();
                	playerGame.setGear(0);
                	playerGame.setLevel(1);
                	playerGame.setWarrior(false);
                	playerGame.setPlayer(player);
                	game.getPlayerGames().add(playerGame);
                }
                players = null;
                
                // initialize game in database
                createGameDatabaseRecord();
            } else if (getIntent().getStringExtra("callingActivity").equals("ChooseUnfinishedGameActivity")) {
            	game = getIntent().getParcelableExtra("game");
            }
        } else {	// there is a savedInstanceState
        	game = savedInstanceState.getParcelable("game");
        }
 
        
        // grab handles for view objects
        playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        playerStrengthTextView = (TextView) findViewById(R.id.playerStrengthTextView);
        levelNumberPicker = (NumberPicker) findViewById(R.id.levelNumberPicker);
        gearNumberPicker = (NumberPicker) findViewById(R.id.gearNumberPicker);
        warriorCheckBox = (CheckBox) findViewById(R.id.warriorCheckBox);
        nextPlayerButton = (Button) findViewById(R.id.nextPlayerButton);
        previousPlayerButton = (Button) findViewById(R.id.previousPlayerButton);
        
        nextPlayerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	        	displayNextPlayer();
			}
        });
        previousPlayerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	        	displayPreviousPlayer();
			}
        });
        
        // place bounds on level number picker (only can be level 1-10)
        levelNumberPicker.setMinValue(1);
        levelNumberPicker.setMaxValue(10);
        levelNumberPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker levelNumberPicker, int oldVal,
					int newVal) {
				int level = levelNumberPicker.getValue();
				int gear = gearNumberPicker.getValue();
				playerStrengthTextView.setText("" + (level + gear));
				
				if (level == 10) {
			        // build the modal dialog to finish game
					AlertDialog.Builder builder = new AlertDialog.Builder(GameTrackerActivity.this);
					builder.setMessage(game.getPlayerGames().get(game.getPlayerTurn()).getPlayer().getFirstName() + " " +
									game.getPlayerGames().get(game.getPlayerTurn()).getPlayer().getLastName() + " " + getString(R.string.declare_victor))
							.setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									savePlayerState();
									saveGameFinishDate();
									Intent intent = new Intent(GameTrackerActivity.this, GameStatisticsActivity.class);
									intent.putExtra("game", game);
									startActivityForResult(intent, CONFIRM_END_GAME);
								}
								
							});
					Dialog dialog = builder.create();
					dialog.show();
				}
			}
        });
        
        // place bounds on armor number picker (never seen over 30-ish
        // in play, so 0-99 should be more than sufficient)
        gearNumberPicker.setMinValue(0);
        gearNumberPicker.setMaxValue(99);
        gearNumberPicker.setOnValueChangedListener(new OnValueChangeListener() {
			public void onValueChange(NumberPicker gearNumberPicker, int oldVal,
					int newVal) {
				int level = levelNumberPicker.getValue();
				int gear = gearNumberPicker.getValue();
				playerStrengthTextView.setText("" + (level + gear));
			}
        });
        
        // turn off circular number pick-ing
        levelNumberPicker.setWrapSelectorWheel(false);
        gearNumberPicker.setWrapSelectorWheel(false);
        
        this.displayPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_super_munchkin, menu);
        return true;
    }
    
    private void displayNextPlayer() {
    	savePlayerState();
    	if (game.getPlayerGames().size() > 0) {
	    	game.setPlayerTurn((game.getPlayerTurn() + 1) % game.getPlayerGames().size());
	    	displayPlayer();
    	}
    }
    
    private void displayPreviousPlayer() {
    	savePlayerState();
    	if (game.getPlayerGames().size() > 0) {
	    	game.setPlayerTurn((game.getPlayerTurn() - 1) % game.getPlayerGames().size());
	    	if (game.getPlayerTurn() < 0) {
	    		game.setPlayerTurn(game.getPlayerTurn() + game.getPlayerGames().size());
	    	}
	    	displayPlayer();
    	}
    }
    
    private void displayPlayer() {
    	if (game.getPlayerGames().size() > 0) {
        	int previousPlayerIndex = (game.getPlayerTurn() - 1) % game.getPlayerGames().size();
	    	if (previousPlayerIndex < 0) {
	    		previousPlayerIndex += game.getPlayerGames().size();
	    	}
        	int nextPlayerIndex = (game.getPlayerTurn() + 1) % game.getPlayerGames().size();
	    	if (nextPlayerIndex < 0) {
	    		nextPlayerIndex += game.getPlayerGames().size();
	    	}
        	
            playerNameTextView.setText(game.getPlayerGames().get(game.getPlayerTurn()).getPlayer().getFirstName() + " " + game.getPlayerGames().get(game.getPlayerTurn()).getPlayer().getLastName());
            playerStrengthTextView.setText((game.getPlayerGames().get(game.getPlayerTurn()).getLevel() + game.getPlayerGames().get(game.getPlayerTurn()).getGear()) + "");
            levelNumberPicker.setValue(game.getPlayerGames().get(game.getPlayerTurn()).getLevel());
            gearNumberPicker.setValue(game.getPlayerGames().get(game.getPlayerTurn()).getGear());
            warriorCheckBox.setChecked(game.getPlayerGames().get(game.getPlayerTurn()).getWarrior());
            
            previousPlayerButton.setText(game.getPlayerGames().get(previousPlayerIndex).getPlayer().getFirstName());
            nextPlayerButton.setText(game.getPlayerGames().get(nextPlayerIndex).getPlayer().getFirstName());
    	}
    }
    
    private void savePlayerState() {
    	game.getPlayerGames().get(game.getPlayerTurn()).setGear(gearNumberPicker.getValue());
    	game.getPlayerGames().get(game.getPlayerTurn()).setLevel(levelNumberPicker.getValue());
    	game.getPlayerGames().get(game.getPlayerTurn()).setWarrior(warriorCheckBox.isChecked());
    	
    	// update database entry for this playerGame
    	Database db = Database.getDatabase(this);
    	db.updatePlayerGame(game.getPlayerGames().get(game.getPlayerTurn()));
    	db.updateGamePlayerTurn(game);
    	db = null;
    }
    
    private void saveGameFinishDate() {
    	game.setFinishDate(new Date());
    	
    	// update database entry for this game
    	Database db = Database.getDatabase(this);
    	db.updateGameFinishDate(game);
    	db = null;
    }
    
    private void createGameDatabaseRecord() {
        Database db = Database.getDatabase(this);
        db.insertGame(game);
        db.retrieveGameId(game);
        
        if (game.getPlayerGames().size() > 0) {
            for (PlayerGame playerGame : game.getPlayerGames()) {
            	playerGame.setGameId(game.getGameId());
            	db.insertPlayerGame(playerGame);
            	db.retrievePlayerGameId(playerGame);
            }
        }
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CONFIRM_END_GAME) {
			if (resultCode == Activity.RESULT_OK){
				finish();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		savePlayerState();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("game", game);
	}
}
