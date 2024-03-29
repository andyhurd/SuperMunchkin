package hurdad.supermunchkin;

import android.os.Parcel;
import android.os.Parcelable;

public class Player  implements Parcelable {
	private int playerId;
	private String firstName;
	private String lastName;
	private int gamesPlayed;
	private int gamesWon;
	private double winPercentage;
	private int totalLevels;
	private double averageLevel;
	private int totalGear;
	private double averageGear;
	private boolean enabled = true;
	
	public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
		public Player createFromParcel(Parcel source) {
			return new Player(source);
		}

		public Player[] newArray(int size) {
			return new Player[size];
		}
	};
	
	public Player() {};
	
	public Player(Parcel in) {
		playerId = in.readInt();
		firstName = in.readString();
		lastName = in.readString();
		gamesPlayed = in.readInt();
		gamesWon = in.readInt();
		winPercentage = in.readDouble();
		totalLevels = in.readInt();
		averageLevel = in.readDouble();
		enabled = in.readInt() == 1 ? true : false;
	}
	
	public Player(int playerId, String firstName, String lastName) {
		this.playerId = playerId;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Player(int playerId, String firstName, String lastName, int gamesPlayed,
			int gamesWon, int totalLevels, int playerPhotoURI, boolean enabled) {
		this.playerId = playerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gamesPlayed = gamesPlayed;
		this.gamesWon = gamesWon;
		this.totalLevels = totalLevels;
		this.enabled = enabled;
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public int getGamesPlayed() {
		return gamesPlayed;
	}
	
	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}
	
	public int getGamesWon() {
		return gamesWon;
	}
	
	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}
	
	public double getWinPercentage() {
		return winPercentage;
	}
	
	public void setWinPercentage(double winPercentage) {
		this.winPercentage = winPercentage;
	}
	
	public int getTotalLevels() {
		return totalLevels;
	}
	
	public void setTotalLevels(int totalLevels) {
		this.totalLevels = totalLevels;
	}
	
	public double getAverageLevel() {
		return averageLevel;
	}
	
	public void setAverageLevel(double averageLevel) {
		this.averageLevel = averageLevel;
	}

	public int getTotalGear() {
		return totalGear;
	}

	public void setTotalGear(int totalGear) {
		this.totalGear = totalGear;
	}

	public double getAverageGear() {
		return averageGear;
	}

	public void setAverageGear(double averageGear) {
		this.averageGear = averageGear;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(playerId);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeInt(gamesPlayed);
		dest.writeInt(gamesWon);
		dest.writeDouble(winPercentage);
		dest.writeInt(totalLevels);
		dest.writeDouble(averageLevel);
		dest.writeInt(enabled ? 1 :0);
	}

	@Override
	public boolean equals(Object o) {
		return ((Player)o).getPlayerId() == playerId;
	}
	
}
