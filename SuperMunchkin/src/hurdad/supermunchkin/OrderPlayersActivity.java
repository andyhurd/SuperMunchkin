package hurdad.supermunchkin;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class OrderPlayersActivity extends ListActivity {
    private ArrayList<Player> players;
    ArrayList<Player> rawPlayers;
    private ArrayList<Player> orderedPlayers;
	private Button startGameButton;
	private Button reorderPlayersButton;
	private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_players);
        
        if (savedInstanceState == null) {
            // receive the ArrayList of selected players for this game
            players = getIntent().getParcelableArrayListExtra("selectedPlayers");
            
            // create the originally empty ArrayList of ordered players for main listView
            orderedPlayers = new ArrayList<Player>();
        	
        	// obtain a copy of the original player data to select from in the modal dialog
            rawPlayers = (ArrayList<Player>) players.clone();
            
        } else {
        	players = savedInstanceState.getParcelableArrayList("players");
        	orderedPlayers = savedInstanceState.getParcelableArrayList("orderedPlayers");
        	rawPlayers = savedInstanceState.getParcelableArrayList("rawPlayers");
        }
        
        // player adapter for main listView of ordered players
        final PlayerAdapter orderedPlayerAdapter = new PlayerAdapter(this, orderedPlayers, R.layout.ordered_player_list_item);
        setListAdapter(orderedPlayerAdapter);
        
        // set listView to not select
        getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);

        // build the modal dialog to select player order
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_next_player);
		dialog.setTitle(getString(R.string.first_player_prompt));
    	final ListView modalListView = (ListView) dialog.findViewById(R.id.listView);
    	
    	// player adapter for modal list view of unordered players
        final PlayerAdapter rawPlayerAdapter = new PlayerAdapter(this, rawPlayers, R.layout.ordered_player_list_item);
    	modalListView.setAdapter(rawPlayerAdapter);
    	
    	// set click listener for the modal listView items to be selected and added to ordered list
    	modalListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterView,
									View itemView,
									int position,
									long id) {

				// alter the modal title after the first selection
	        	if (orderedPlayerAdapter.getCount() == 0) {
	        		dialog.setTitle(getString(R.string.next_player_prompt));
	        	}
				
	        	// dismiss the modal dialog when there are no more players to order
				if (rawPlayerAdapter.getCount() == 1) {
					dialog.dismiss();
					
					// allow the user to start game once all players ordered
					startGameButton.setText(getString(R.string.start));
			        startGameButton.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							Intent data = new Intent();
							data.putExtra("callingActivity", "OrderPlayersActivity");
							data.putParcelableArrayListExtra("orderedPlayers", OrderPlayersActivity.this.getOrderedPlayers());
							setResult(Activity.RESULT_OK, data);
							finish();
						}
			        });
				}
	        	
				// remove the selected player list item from the modal
				Player player = rawPlayerAdapter.getItem(position);
				rawPlayerAdapter.remove(player);
				rawPlayerAdapter.notifyDataSetChanged();
				
				// add the selected player list item to the main listView
				orderedPlayerAdapter.add(player);
				orderedPlayerAdapter.notifyDataSetChanged();
				
				// update the corresponding listViews
				OrderPlayersActivity.this.getListView().invalidate();
				modalListView.invalidate();
			}
    		
    	});
    	
    	if (savedInstanceState == null || savedInstanceState.getBoolean("dialogShowing")) {
        	// show the initial dialog on activity start
    		dialog.show();
    	}
        
		// set click handler for Start Game button to spawn game tracker activity
        startGameButton = (Button) findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.show();
			}
        });
		
        // set click handler for reorderButton to start order process over
        reorderPlayersButton = (Button) findViewById(R.id.reorderPlayersButton);
        reorderPlayersButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				// clear and redraw modal player listView
				rawPlayerAdapter.clear();
				ArrayList<Player> rawPlayers = (ArrayList<Player>) players.clone();
				rawPlayerAdapter.addAll(rawPlayers);
				rawPlayerAdapter.notifyDataSetChanged();
				dialog.findViewById(R.id.listView).invalidate();
				
				// clear and redraw main player listView
				orderedPlayerAdapter.clear();
				orderedPlayerAdapter.notifyDataSetChanged();
				OrderPlayersActivity.this.getListView().invalidate();
				
				// set to the initial dialog title and show the modal
				dialog.setTitle(getString(R.string.first_player_prompt));
				dialog.show();
				
				// reset startGameButton
				startGameButton.setText(getString(R.string.choose_next));
		        startGameButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						dialog.show();
					}
		        });
			}
        });
    }

	public ArrayList<Player> getOrderedPlayers() {
		return orderedPlayers;
	}

	public void setOrderedPlayers(ArrayList<Player> orderedPlayers) {
		this.orderedPlayers = orderedPlayers;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if (dialog.isShowing()) {
			outState.putBoolean("dialogShowing", true);
		} else {
			outState.putBoolean("dialogShowing", false);
		}
		
		outState.putParcelableArrayList("players", players);
		outState.putParcelableArrayList("rawPlayers", rawPlayers);
		outState.putParcelableArrayList("orderedPlayers", orderedPlayers);
	}
}
