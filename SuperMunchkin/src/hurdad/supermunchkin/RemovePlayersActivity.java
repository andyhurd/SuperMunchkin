package hurdad.supermunchkin;

import java.util.ArrayList;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RemovePlayersActivity extends ListActivity {
	private ArrayList<Player> players;
	private ArrayList<Player> selectedPlayers;
	private Button removeButton;
	private Button cancelButton;
	private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_players);

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
				RemovePlayersActivity.this.getListView().invalidate();
			}
        	
        };
        
        // add ListView Handler
        getListView().setOnItemClickListener(onItemClickListener);
        
        removeButton = (Button) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				// remove all selected players
				Database db = Database.getDatabase(RemovePlayersActivity.this);
				for (Player player : selectedPlayers) {
					db.deletePlayer(player);
				}
				db = null;
				
				// return to main menu
				finish();
			}
        });
        
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_super_munchkin, menu);
        return true;
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putParcelableArrayList("players", players);
		outState.putParcelableArrayList("selectedPlayers", selectedPlayers);
	}
}
