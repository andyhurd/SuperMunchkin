package hurdad.supermunchkin;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	private Button newGameButton;
	private Button resumeGameButton;
	private Button statisticsButton;
	private Button removePlayersButton;
	private Button restorePlayersButton;
	private final int CHOOSE_NEW_GAME_PLAYERS = 1;
	private final int CHOOSE_UNFINISHED_GAME = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        // gain access to view buttons
        newGameButton = (Button) findViewById(R.id.newGameButton);
        resumeGameButton = (Button) findViewById(R.id.resumeGameButton);
        statisticsButton = (Button) findViewById(R.id.statisticsButton);
        removePlayersButton = (Button) findViewById(R.id.removePlayersButton);
        restorePlayersButton = (Button) findViewById(R.id.restorePlayersButton);
        
        // add click handler for new game button
        newGameButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this, ChoosePlayersActivity.class);
				startActivityForResult(intent, CHOOSE_NEW_GAME_PLAYERS);
			}
        	
        });
        
        // add click handler for resume game button
        resumeGameButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this, ChooseUnfinishedGameActivity.class);
				startActivityForResult(intent, CHOOSE_UNFINISHED_GAME);
			}
        	
        });
        
        // add click handler for statistics button
        statisticsButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(MainMenuActivity.this, StatisticsActivity.class);
				startActivity(intent);
			}
        	
        });
        
        // add click handler for remove players button
        removePlayersButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(MainMenuActivity.this, RemovePlayersActivity.class);
				startActivity(intent);
			}
        	
        });
        
        // add click handler for restore players button
        restorePlayersButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(MainMenuActivity.this, RestorePlayersActivity.class);
				startActivity(intent);
			}
        	
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_super_munchkin, menu);
        return true;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CHOOSE_NEW_GAME_PLAYERS) {
			if (resultCode == Activity.RESULT_OK) {
				Intent intent = new Intent(this, GameTrackerActivity.class);
				intent.putExtra("callingActivity", data.getStringExtra("callingActivity"));
				intent.putParcelableArrayListExtra("orderedPlayers", data.getParcelableArrayListExtra("orderedPlayers"));
				startActivity(intent);
			}
		} else if (requestCode == CHOOSE_UNFINISHED_GAME) {
			if (resultCode == Activity.RESULT_OK) {
				Intent intent = new Intent(this, GameTrackerActivity.class);
				intent.putExtra("callingActivity", data.getStringExtra("callingActivity"));
				intent.putExtra("game", data.getParcelableExtra("game"));
				startActivity(intent);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
