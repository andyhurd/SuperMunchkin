package hurdad.supermunchkin;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseUnfinishedGameActivity extends ListActivity {
	private ArrayList<Game> games;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	protected void onResume() {
		super.onResume();

        Database db = Database.getDatabase(this);
        games = db.retrieveUnfinishedGames();
        db = null;
        
        getListView().setBackgroundResource(R.color.charcoal);
        final GameAdapter adapter = new GameAdapter(this, games, R.layout.choose_game_list_item);
        setListAdapter(adapter);
        
        final OnItemClickListener onItemClickListener = new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterView,
									View itemView,
									int position,
									long id) {
				Game game = adapter.getItem(position);
				((TextView)itemView.findViewById(R.id.unfinishedGameTextView)).setTextColor(Color.BLACK);
				ChooseUnfinishedGameActivity.this.getListView().invalidate();
				
				Intent intent = new Intent();
				intent.putExtra("callingActivity", "ChooseUnfinishedGameActivity");
				intent.putExtra("game", game);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
        	
        };
        
        // add ListView Handler
        getListView().setOnItemClickListener(onItemClickListener);
	}

    @Override
	protected void onPause() {
		super.onPause();
		this.games = null;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_super_munchkin, menu);
        return true;
    }
	
	public class GameAdapter extends ArrayAdapter<Game> {
		private int layoutResource;
		
		public GameAdapter(Context context, ArrayList<Game> games, int layoutResource) {
			super(context, 0, games);
			this.layoutResource = layoutResource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = convertView;
			
			if (view == null) {
				view = LayoutInflater.from(getContext()).inflate(this.layoutResource, null);
			}
			
			// populate the View's widgets with player specific info
			TextView unfinishedGameTextView = (TextView) view.findViewById(R.id.unfinishedGameTextView);
			Game game = getItem(position);
			String gameString = game.getStartDate() + ":";
			for (PlayerGame playerGame : game.getPlayerGames()) {
				gameString += " " + playerGame.getPlayer().getFirstName();
			}
			unfinishedGameTextView.setText(gameString);
			
			return view;
		}
	}
}
