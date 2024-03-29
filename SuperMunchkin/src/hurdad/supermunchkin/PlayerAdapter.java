package hurdad.supermunchkin;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlayerAdapter extends ArrayAdapter<Player> {
	private int layoutResource;
	private ArrayList<Player> selectedPlayers;
	
	public PlayerAdapter(Context context, ArrayList<Player> players, int layoutResource) {
		super(context, 0, players);
		this.layoutResource = layoutResource;
	}
	
	public PlayerAdapter(Context context, ArrayList<Player> players, ArrayList<Player> selectedPlayers, int layoutResource) {
		super(context, 0, players);
		this.layoutResource = layoutResource;
		this.selectedPlayers = selectedPlayers;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		if (view == null) {
			view = LayoutInflater.from(getContext()).inflate(this.layoutResource, null);
		}
		
		// populate the View's widgets with player specific info
		TextView playerNameTextView = (TextView) view.findViewById(R.id.playerNameTextView);
		playerNameTextView.setText(getItem(position).getFirstName() + " " + getItem(position).getLastName());
		
		if (selectedPlayers != null) {
			if (selectedPlayers.contains(getItem(position))) {
				playerNameTextView.setTextColor(Color.BLACK);
			} else {
				playerNameTextView.setTextColor(getContext().getResources().getColor(R.color.mist));
			}
		}
		
		return view;
	}
}
