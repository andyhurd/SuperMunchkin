package hurdad.supermunchkin;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class StatisticsActivity extends ListActivity {
	private ArrayList<Player> players;
	private Button doneButton;
	private final DecimalFormat df = new DecimalFormat("#.##");
	private final DecimalFormat dfPercent = new DecimalFormat("#");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        
        Database db = Database.getDatabase(this);
        players = db.retrievePlayerStats();
        db = null;

        getListView().addHeaderView(View.inflate(this, R.layout.player_stat_list_header, null));
        final PlayerStatAdapter adapter = new PlayerStatAdapter(this, players, R.layout.player_stat_list_item);
        setListAdapter(adapter);
        
        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				StatisticsActivity.this.finish();
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_super_munchkin, menu);
        return true;
    }

	public class PlayerStatAdapter extends ArrayAdapter<Player> {
    	private int layoutResource;
    	
    	public PlayerStatAdapter(Context context, ArrayList<Player> players, int layoutResource) {
    		super(context, 0, players);
    		this.layoutResource = layoutResource;
    	}
        
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		View view = convertView;
    		
    		if (view == null) {
    			view = LayoutInflater.from(getContext()).inflate(this.layoutResource, null);
    		}
    		
    		// populate the View's widgets with player specific name info
    		TextView playerNameTextView = (TextView) view.findViewById(R.id.playerNameTextView);
    		playerNameTextView.setText(getItem(position).getFirstName() + " " + getItem(position).getLastName());
    		
    		// populate the View's widgets with player specific games played
    		TextView gamesPlayedTextView = (TextView) view.findViewById(R.id.gamesPlayedTextView);
    		gamesPlayedTextView.setText("" + getItem(position).getGamesPlayed());
    		
    		// populate the View's widgets with player specific games won
    		TextView gamesWonTextView = (TextView) view.findViewById(R.id.gamesWonTextView);
    		gamesWonTextView.setText("" + getItem(position).getGamesWon());
    		
    		// populate the View's widgets with player specific win percentage
    		TextView winPercentageTextView = (TextView) view.findViewById(R.id.winPercentageTextView);
    		winPercentageTextView.setText(dfPercent.format(getItem(position).getWinPercentage()) + "%");
    		
    		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        		
        		// populate the View's widgets with player specific total levels
        		TextView totalLevelsTextView = (TextView) view.findViewById(R.id.totalLevelsTextView);
        		totalLevelsTextView.setText("" + getItem(position).getTotalLevels());
        		
        		// populate the View's widgets with player specific average level
        		TextView averageLevelTextView = (TextView) view.findViewById(R.id.averageLevelTextView);
        		averageLevelTextView.setText(df.format(getItem(position).getAverageLevel()));
        		
        		// populate the View's widgets with player specific total gear
        		TextView totalGearTextView = (TextView) view.findViewById(R.id.totalGearTextView);
        		totalGearTextView.setText("" + getItem(position).getTotalGear());
        		
        		// populate the View's widgets with player specific average gear
        		TextView averageGearTextView = (TextView) view.findViewById(R.id.averageGearTextView);
        		averageGearTextView.setText(df.format(getItem(position).getAverageGear()));
    		}
    		
    		return view;
    	}
    }
}
