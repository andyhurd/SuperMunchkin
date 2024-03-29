package hurdad.supermunchkin;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerGame implements Parcelable {
	private int playerGameId;
	private Player player;
	private int gameId;
	private int level;
	private int gear;
	private boolean warrior;
	
	public static final Parcelable.Creator<PlayerGame> CREATOR = new Parcelable.Creator<PlayerGame>() {
		public PlayerGame createFromParcel(Parcel source) {
			return new PlayerGame(source);
		}

		public PlayerGame[] newArray(int size) {
			return new PlayerGame[size];
		}
	};
	
	public PlayerGame() {};
	
	public PlayerGame(Parcel in) {
		playerGameId = in.readInt();
		player = in.readParcelable(Player.class.getClassLoader());
		gameId = in.readInt();
		level = in.readInt();
		gear = in.readInt();
		warrior = in.readInt() == 1 ? true : false;
	}
	
	public int getPlayerGameId() {
		return playerGameId;
	}

	public void setPlayerGameId(int playerGameId) {
		this.playerGameId = playerGameId;
	}

	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getGear() {
		return gear;
	}
	
	public void setGear(int gear) {
		this.gear = gear;
	}
	
	public boolean getWarrior() {
		return warrior;
	}
	
	public void setWarrior(boolean warrior) {
		this.warrior = warrior;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(playerGameId);
		dest.writeParcelable(player, 0);
		dest.writeInt(gameId);
		dest.writeInt(level);
		dest.writeInt(gear);
		dest.writeInt(warrior ? 1 : 0);
	}
	
}
