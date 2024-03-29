package hurdad.supermunchkin;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChoosePlayersActivity extends ListActivity {
	private ArrayList<Player> players;
	private ArrayList<Player> selectedPlayers;
	private Button doneButton;
	private Button addPlayerButton;
	private ListView listView;
	private Dialog addPlayerModalDialog;
	private final int ORDER_NEW_GAME_PLAYERS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_players);

        if (savedInstanceState == null) {
            Database db = Database.getDatabase(this);
            players = db.retrieveEnabledPlayers();
            db = null;
            selectedPlayers = new ArrayList<Player>();
        } else {
        	players = savedInstanceState.getParcelableArrayList("players");
        	selectedPlayers = savedInstanceState.getParcelableArrayList("selectedPlayers");
        }
        
        final PlayerAdapter adapter = new PlayerAdapter(this, players, selectedPlayers, R.layout.choose_player_list_item);
        setListAdapter(adapter);
        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        final OnItemClickListener onItemClickListener = new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterView,
									View itemView,
									int position,
									long id) {
				Player player = adapter.getItem(position);
				if (!selectedPlayers.contains(player)) {
					((TextView)itemView.findViewById(R.id.playerNameTextView)).setTextColor(Color.BLACK);
					selectedPlayers.add(player);
				} else {
					((TextView)itemView.findViewById(R.id.playerNameTextView)).setTextColor(getResources().getColor(R.color.mist));
					selectedPlayers.remove(player);
				}
				ChoosePlayersActivity.this.getListView().invalidate();
			}
        	
        };
        
        // add ListView Handler
        getListView().setOnItemClickListener(onItemClickListener);
        
        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(ChoosePlayersActivity.this, OrderPlayersActivity.class);
				intent.putParcelableArrayListExtra("selectedPlayers", selectedPlayers);
				startActivityForResult(intent, ORDER_NEW_GAME_PLAYERS);
			}
        });
		
		addPlayerModalDialog = new Dialog(ChoosePlayersActivity.this);
		addPlayerModalDialog.setContentView(R.layout.add_player);
		addPlayerModalDialog.setTitle("Add Player");
		
		Button addPlayerCancelButton = (Button) addPlayerModalDialog.findViewById(R.id.addPlayerCancelButton);
        addPlayerCancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				addPlayerModalDialog.dismiss();
				
				// reset edit texts
				((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerFirstNameEditText)).setText("");
				((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerLastNameEditText)).setText("");
			}
        });
        
        Button addPlayerSaveButton = (Button) addPlayerModalDialog.findViewById(R.id.addPlayerSaveButton);
        addPlayerSaveButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String firstName = ((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerFirstNameEditText)).getText().toString();
				String lastName = ((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerLastNameEditText)).getText().toString();
				
				// determine whether or not to construct player with the given attributes
				// and display toast messages if empty input
				if (firstName.equals("") && lastName.equals("")) {
					Toast.makeText(ChoosePlayersActivity.this, "You must enter a first and last name",  Toast.LENGTH_SHORT).show();
				
				} if (firstName.equals("")) {
					Toast.makeText(ChoosePlayersActivity.this, "You must enter a first name.", Toast.LENGTH_SHORT).show();
				
				} else if (lastName.equals("")) {
					Toast.makeText(ChoosePlayersActivity.this, "You must enter a last name.", Toast.LENGTH_SHORT).show();
				
				} else {
					
					// construct new player to add
					Player player = new Player();
					player.setFirstName(firstName);
					player.setLastName(lastName);
					
					// insert new player into database
					Database database = Database.getDatabase(ChoosePlayersActivity.this);
					database.insertPlayer(player);
					players = database.retrieveEnabledPlayers();
					
					// dismiss modal dialog
					addPlayerModalDialog.dismiss();
					
					// reset edit texts
					((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerFirstNameEditText)).setText("");
					((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerLastNameEditText)).setText("");
					
					// refresh the player list
					adapter.clear();
					adapter.addAll(players);
					adapter.notifyDataSetChanged();
					ChoosePlayersActivity.this.getListView().invalidate();
				}
			}
        });
        
        addPlayerButton = (Button) findViewById(R.id.addPlayerButton);
        addPlayerButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				addPlayerModalDialog.show();
			}
        });
        
        if (savedInstanceState != null && savedInstanceState.getBoolean("modalShowing")) {
        	((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerFirstNameEditText)).setText(savedInstanceState.getString("firstName"));
        	((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerLastNameEditText)).setText(savedInstanceState.getString("lastName"));
        	addPlayerModalDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_super_munchkin, menu);
        return true;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ORDER_NEW_GAME_PLAYERS) {
			if (resultCode == Activity.RESULT_OK) {
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if (addPlayerModalDialog.isShowing()) {
			outState.putBoolean("modalShowing", true);
			outState.putString("firstName", ((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerFirstNameEditText)).getText().toString());
			outState.putString("lastName", ((EditText) addPlayerModalDialog.findViewById(R.id.addPlayerLastNameEditText)).getText().toString());
		} else {
			outState.putBoolean("modalShowing", false);
		}
		outState.putParcelableArrayList("players", players);
		outState.putParcelableArrayList("selectedPlayers", selectedPlayers);
	}
}
