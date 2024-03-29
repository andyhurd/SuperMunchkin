package hurdad.supermunchkin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable {
	private int gameId;
	private int playerTurn;
	private Date startDate;
	private Date finishDate;
	private boolean enabled;
	private ArrayList<PlayerGame> playerGames;
	private DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
		public Game createFromParcel(Parcel source) {
			return new Game(source);
		}

		public Game[] newArray(int size) {
			return new Game[size];
		}
	};
	
	public Game() {};
	
	public Game(Parcel in) {
		gameId = in.readInt();
		playerTurn = in.readInt();
		String startDateString = in.readString();
		try {
			startDate = iso8601Format.parse(startDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String finishDateString = in.readString();
		if (!finishDateString.equals("null")) {
			try {
				finishDate = iso8601Format.parse(finishDateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		enabled = in.readInt() == 1 ? true : false;
		
		int arrayLength = in.readInt();
		Object[] playerGameArray = in.readParcelableArray(PlayerGame.class.getClassLoader());
		playerGames = new ArrayList<PlayerGame>();
		for (int i = 0; i < arrayLength; i++) {
			playerGames.add((PlayerGame) playerGameArray[i]);
		}
	}
	
	public int getGameId() {
		return gameId;
	}
	
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	
	public int getPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getFinishDate() {
		return finishDate;
	}
	
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public ArrayList<PlayerGame> getPlayerGames() {
		return playerGames;
	}

	public void setPlayerGames(ArrayList<PlayerGame> playerGames) {
		this.playerGames = playerGames;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(gameId);
		dest.writeInt(playerTurn);
		dest.writeString(iso8601Format.format(startDate));
		if (finishDate == null) {
			dest.writeString("null");
		} else {
			dest.writeString(iso8601Format.format(finishDate));
		}
		dest.writeInt(enabled ? 1 : 0);
		
		dest.writeInt(playerGames.size());
		dest.writeParcelableArray(playerGames.toArray(new PlayerGame[playerGames.size()]), 0);
	}
}
