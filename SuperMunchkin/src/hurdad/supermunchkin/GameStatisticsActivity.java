package hurdad.supermunchkin;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class GameStatisticsActivity extends ListActivity {
	Game game;
	Button doneButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        
        game = getIntent().getParcelableExtra("game");
        
        getListView().addHeaderView(View.inflate(this, R.layout.player_game_stat_list_header, null));
        final PlayerStatAdapter adapter = new PlayerStatAdapter(this, game.getPlayerGames(), R.layout.player_game_stat_list_item);
        setListAdapter(adapter);
        
        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				GameStatisticsActivity.this.finish();
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_super_munchkin, menu);
        return true;
    }

	public class PlayerStatAdapter extends ArrayAdapter<PlayerGame> {
    	private int layoutResource;
    	
    	public PlayerStatAdapter(Context context, ArrayList<PlayerGame> playerGames, int layoutResource) {
    		super(context, 0, playerGames);
    		this.layoutResource = layoutResource;
    	}
        
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		View view = convertView;
    		
    		if (view == null) {
    			view = LayoutInflater.from(getContext()).inflate(this.layoutResource, null);
    		}
    		
    		// populate the View's widgets with player specific name
    		TextView playerNameTextView = (TextView) view.findViewById(R.id.playerNameTextView);
    		playerNameTextView.setText(getItem(position).getPlayer().getFirstName() + " " + getItem(position).getPlayer().getLastName());
        		
    		// populate the View's widgets with player specific level
    		TextView levelTextView = (TextView) view.findViewById(R.id.levelTextView);
    		levelTextView.setText("" + getItem(position).getLevel());
    		
    		// populate the View's widgets with player specific gear
    		TextView gearTextView = (TextView) view.findViewById(R.id.gearTextView);
    		gearTextView.setText("" + getItem(position).getGear());
    		
    		// populate the View's widgets with player specific warrior status
    		TextView warriorTextView = (TextView) view.findViewById(R.id.warriorTextView);
    		warriorTextView.setText(getItem(position).getWarrior() ? getString(R.string.true_abbr) : getString(R.string.false_abbr));
    		
    		return view;
    	}
    }
}
