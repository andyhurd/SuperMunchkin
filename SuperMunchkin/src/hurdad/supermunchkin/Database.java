package hurdad.supermunchkin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {
	
	// database constants
	private static final String DATABASE_NAME = "super_munchkin.db";
	private static final int DATABASE_VERSION = 1;
	
	// player table constants
	private static final String TABLE_PLAYER = "player";
	private static final String COLUMN_PLAYER_ID = "player_id";
	private static final String COLUMN_PLAYER_FIRST_NAME = "player_first_name";
	private static final String COLUMN_PLAYER_LAST_NAME = "player_last_name";
	private static final String COLUMN_PLAYER_ENABLED = "player_enabled";
	
	// game table constants
	private static final String TABLE_GAME = "game";
	private static final String COLUMN_GAME_ID = "game_id";
	private static final String COLUMN_GAME_PLAYER_TURN = "game_player_turn";
	private static final String COLUMN_GAME_START_DATE = "game_start_date";
	private static final String COLUMN_GAME_FINISH_DATE = "game_finish_date";
	private static final String COLUMN_GAME_ENABLED = "game_enabled";
	
	// player_game table constants
	private static final String TABLE_PLAYER_GAME = "player_game";
	private static final String COLUMN_PLAYER_GAME_ID = "player_game_id";
	private static final String COLUMN_PLAYER_GAME_PLAYER_ID = "player_game_player_id";
	private static final String COLUMN_PLAYER_GAME_GAME_ID = "player_game_game_id";
	private static final String COLUMN_PLAYER_GAME_LEVEL = "player_game_level";
	private static final String COLUMN_PLAYER_GAME_GEAR = "player_game_gear";
	private static final String COLUMN_PLAYER_GAME_WARRIOR = "player_game_warrior";
	
	// DDL scripts
	private static final String TABLE_PLAYER_DDL =
				"CREATE TABLE " + TABLE_PLAYER + " ( " +
					COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COLUMN_PLAYER_FIRST_NAME + " TEXT NOT NULL, " +
					COLUMN_PLAYER_LAST_NAME + " TEXT NOT NULL, " +
					COLUMN_PLAYER_ENABLED + " INTEGER NOT NULL, " +
					"CHECK(" + COLUMN_PLAYER_ENABLED + " = 0 " +
							"OR " + COLUMN_PLAYER_ENABLED + " = 1));";
	
	private static final String TABLE_GAME_DDL =
				"CREATE TABLE " + TABLE_GAME + " ( " +
					COLUMN_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_GAME_PLAYER_TURN + " INTEGER NOT NULL, " +
					COLUMN_GAME_START_DATE + " TEXT NOT NULL, " +
					COLUMN_GAME_FINISH_DATE + " TEXT, " +
					COLUMN_GAME_ENABLED + " INTEGER NOT NULL, " +
					"CHECK(" + COLUMN_GAME_ENABLED + " = 0 " +
							"OR " + COLUMN_GAME_ENABLED + " = 1));";
	
	private static final String TABLE_PLAYER_GAME_DDL =
				"CREATE TABLE " + TABLE_PLAYER_GAME + " ( " +
					COLUMN_PLAYER_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					COLUMN_PLAYER_GAME_PLAYER_ID + " INTEGER NOT NULL, " +
					COLUMN_PLAYER_GAME_GAME_ID + " INTEGER NOT NULL, " +
					COLUMN_PLAYER_GAME_LEVEL + " NUMERIC(2) NOT NULL, " +
					COLUMN_PLAYER_GAME_GEAR + " NUMERIC(2) NOT NULL, " +
					COLUMN_PLAYER_GAME_WARRIOR + " NUMERIC(1) NOT NULL, " +
					"FOREIGN KEY(" + COLUMN_PLAYER_GAME_PLAYER_ID + ") REFERENCES " +
						TABLE_PLAYER + "(" + COLUMN_PLAYER_ID + "), " +
					"FOREIGN KEY(" + COLUMN_PLAYER_GAME_GAME_ID + ") REFERENCES " +
						TABLE_GAME + "(" + COLUMN_GAME_ID + "), " +
					"CHECK(" + COLUMN_PLAYER_GAME_LEVEL + " > 0 " +
						"AND " + COLUMN_PLAYER_GAME_LEVEL + " <= 10 " +
						"AND " + COLUMN_PLAYER_GAME_GEAR + " >= 0 " +
						"AND " + COLUMN_PLAYER_GAME_GEAR + " <= 99 " +
						"AND (" + COLUMN_PLAYER_GAME_WARRIOR + " = 0 " +
							"OR " + COLUMN_PLAYER_GAME_WARRIOR + " = 1)));";
	
	private static final String RETRIEVE_UNFINISHED_PLAYER_GAMES =
				"SELECT " +
					COLUMN_GAME_ID + ", " +
					COLUMN_GAME_PLAYER_TURN + ", " +
					COLUMN_GAME_START_DATE + ", " +
					COLUMN_PLAYER_GAME_ID + ", " +
					COLUMN_PLAYER_GAME_LEVEL + ", " +
					COLUMN_PLAYER_GAME_GEAR + ", " +
					COLUMN_PLAYER_GAME_WARRIOR + ", " +
					COLUMN_PLAYER_ID + ", " +
					COLUMN_PLAYER_FIRST_NAME + ", " +
					COLUMN_PLAYER_LAST_NAME + " " +
				"FROM " +
					TABLE_GAME + " " +
					"JOIN " + TABLE_PLAYER_GAME + " ON " + TABLE_PLAYER_GAME + "." +
						COLUMN_PLAYER_GAME_GAME_ID + " = " + TABLE_GAME + "." + COLUMN_GAME_ID + " " +
					"JOIN " + TABLE_PLAYER + " ON " + TABLE_PLAYER + "." + COLUMN_PLAYER_ID + " = " +
						TABLE_PLAYER_GAME + "." + COLUMN_PLAYER_GAME_PLAYER_ID + " " +
				"WHERE " +
					COLUMN_GAME_FINISH_DATE + " IS NULL " +
				"AND " +
					COLUMN_GAME_ENABLED + " = 1 " +
				"ORDER BY " + COLUMN_GAME_ID + " DESC, " + COLUMN_PLAYER_GAME_ID + ";";
	
	private static final String RETRIEVE_PLAYER_STATS =
				"SELECT " +
					COLUMN_PLAYER_ID + ", " +
					COLUMN_PLAYER_FIRST_NAME + ", " +
					COLUMN_PLAYER_LAST_NAME + ", " +
					"SUM(" + COLUMN_PLAYER_GAME_LEVEL + "), " +
					"SUM(" + COLUMN_PLAYER_GAME_GEAR + "), " +
					"COUNT(" + COLUMN_GAME_ID + "), " +
					"sub.wins " +
				"FROM " +
					TABLE_PLAYER + " " +
					"JOIN " + TABLE_PLAYER_GAME + " ON " + TABLE_PLAYER_GAME + "." +
						COLUMN_PLAYER_GAME_PLAYER_ID + " = " + TABLE_PLAYER + "." + COLUMN_PLAYER_ID + " " +
					"JOIN " + TABLE_GAME + " ON " + TABLE_PLAYER_GAME + "." +
						COLUMN_PLAYER_GAME_GAME_ID + " = " + TABLE_GAME + "." + COLUMN_GAME_ID + " " +
					"LEFT JOIN (SELECT " +
								COLUMN_PLAYER_GAME_PLAYER_ID + ", " +
								"COUNT(" + COLUMN_PLAYER_GAME_ID + ") AS wins " +
							"FROM " + 
								TABLE_PLAYER_GAME + " " +
							"WHERE " +
								COLUMN_PLAYER_GAME_LEVEL + " = 10 " +
							"GROUP BY " +
								COLUMN_PLAYER_GAME_PLAYER_ID + ") AS sub ON sub." + 
									COLUMN_PLAYER_GAME_PLAYER_ID + " = " + TABLE_PLAYER + "." +
									COLUMN_PLAYER_ID + " " +
				"WHERE " +
					COLUMN_PLAYER_ENABLED + " = 1 AND " + COLUMN_GAME_ENABLED + " = 1 " +
				"GROUP BY " +
					COLUMN_PLAYER_ID + ", " +
					COLUMN_PLAYER_FIRST_NAME + ", " +
					COLUMN_PLAYER_LAST_NAME + ", " +
					"sub.wins " + 
				"ORDER BY " +
					COLUMN_PLAYER_FIRST_NAME + ";";
	
	// singleton database
	private static Database singleton = null;
	private SQLiteDatabase database;
	private DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private Database(Context context) {
		OpenHelper openHelper = new OpenHelper(context);
		this.database = openHelper.getWritableDatabase();
	}
	
	public static Database getDatabase(Context context) {
		if (singleton == null) {
			singleton = new Database(context);
		}
			
		return singleton;
	}
	
	public void close() {
		this.database.close();
	}
	
	public ArrayList<Player> retrievePlayerStats() {
		ArrayList<Player> players = new ArrayList<Player>();

		Cursor cursor = database.rawQuery(RETRIEVE_PLAYER_STATS, null);

		Player player;
		while (cursor.moveToNext()) {
			player = new Player();
			player.setPlayerId(cursor.getInt(0));
			player.setFirstName(cursor.getString(1));
			player.setLastName(cursor.getString(2));
			player.setTotalLevels(cursor.getInt(3));
			player.setTotalGear(cursor.getInt(4));
			player.setGamesPlayed(cursor.getInt(5));
			player.setGamesWon(cursor.getInt(6));
			
			player.setAverageLevel(((double) player.getTotalLevels()) / player.getGamesPlayed());
			player.setAverageGear(((double) player.getTotalGear()) / player.getGamesPlayed());
			player.setWinPercentage(100 * ((double) player.getGamesWon()) / player.getGamesPlayed());
			players.add(player);
		}
		
		return players;
	}
	
	public ArrayList<Player> retrieveAllPlayers() {
		return retrievePlayers(null);
	}
	
	public ArrayList<Player> retrieveEnabledPlayers() {
		return retrievePlayers(COLUMN_PLAYER_ENABLED + " = 1");
	}
	
	public ArrayList<Player> retrieveDeletedPlayers() {
		return retrievePlayers(COLUMN_PLAYER_ENABLED + " = 0");
	}
	
	public ArrayList<Player> retrievePlayers(String whereClause) {
		ArrayList<Player> players = new ArrayList<Player>();
		Player player;
		
		Cursor cursor = database.query(TABLE_PLAYER, new String[] {COLUMN_PLAYER_ID, COLUMN_PLAYER_FIRST_NAME,
				COLUMN_PLAYER_LAST_NAME, COLUMN_PLAYER_ENABLED}, whereClause, null, null, null, COLUMN_PLAYER_FIRST_NAME);
		while (cursor.moveToNext()) {
			player = new Player();
			player.setPlayerId(cursor.getInt(0));
			player.setFirstName(cursor.getString(1));
			player.setLastName(cursor.getString(2));
			player.setEnabled(cursor.getInt(3) == 1 ? true : false);
			players.add(player);
		}
		return players;
	}
	
	public void insertPlayer(Player player) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PLAYER_FIRST_NAME, player.getFirstName());
		values.put(COLUMN_PLAYER_LAST_NAME, player.getLastName());
		values.put(COLUMN_PLAYER_ENABLED, (player.getEnabled() ? 1 : 0));
		database.insert(TABLE_PLAYER, null, values);
	}
	
	public void retrieveGameId(Game game) {
		Cursor cursor = database.query(TABLE_GAME, new String[] {COLUMN_GAME_ID}, COLUMN_GAME_START_DATE + " = '" +
				iso8601Format.format(game.getStartDate()) + "'", null, null, null, null);
		while (cursor.moveToNext()) {
			game.setGameId(cursor.getInt(0));
		}
	}
	
	public void insertGame(Game game) {
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_GAME_PLAYER_TURN, game.getPlayerTurn());
		values.put(COLUMN_GAME_START_DATE, iso8601Format.format(game.getStartDate()));
		values.put(COLUMN_GAME_ENABLED, (game.getEnabled() ? 1 : 0));
		
		if (game.getFinishDate() != null) {
			values.put(COLUMN_GAME_FINISH_DATE, iso8601Format.format(game.getFinishDate()));
		} else {
			values.putNull(COLUMN_GAME_FINISH_DATE);
		}
		
		database.insert(TABLE_GAME, null, values);
	}
	
	public void retrievePlayerGameId(PlayerGame playerGame) {
		Cursor cursor = database.query(TABLE_PLAYER_GAME, new String[] {COLUMN_PLAYER_GAME_ID}, COLUMN_PLAYER_GAME_PLAYER_ID + " = " +
				playerGame.getPlayer().getPlayerId() + " AND " + COLUMN_PLAYER_GAME_GAME_ID + " = " + playerGame.getGameId(),
				null, null, null, null);
		while (cursor.moveToNext()) {
			playerGame.setPlayerGameId(cursor.getInt(0));
		}
	}
	
	public void insertPlayerGame(PlayerGame playerGame) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PLAYER_GAME_PLAYER_ID, playerGame.getPlayer().getPlayerId());
		values.put(COLUMN_PLAYER_GAME_GAME_ID, playerGame.getGameId());
		values.put(COLUMN_PLAYER_GAME_LEVEL, playerGame.getLevel());
		values.put(COLUMN_PLAYER_GAME_GEAR, playerGame.getLevel());
		values.put(COLUMN_PLAYER_GAME_WARRIOR, (playerGame.getWarrior() ? 1 : 0));
		database.insert(TABLE_PLAYER_GAME, null, values);
	}
	
	public void updatePlayerGame(PlayerGame playerGame) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PLAYER_GAME_LEVEL, playerGame.getLevel());
		values.put(COLUMN_PLAYER_GAME_GEAR, playerGame.getGear());
		values.put(COLUMN_PLAYER_GAME_WARRIOR, playerGame.getWarrior() ? 1 : 0);
		database.update(TABLE_PLAYER_GAME, values, COLUMN_PLAYER_GAME_ID + " = " + playerGame.getPlayerGameId(), null);
	}
	
	public void updateGamePlayerTurn(Game game) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_GAME_PLAYER_TURN, game.getPlayerTurn());
		database.update(TABLE_GAME, values, COLUMN_GAME_ID + " = " + game.getGameId(), null);
	}
	
	public void updateGameFinishDate(Game game) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_GAME_FINISH_DATE, iso8601Format.format(game.getFinishDate()));
		database.update(TABLE_GAME, values, COLUMN_GAME_ID + " = " + game.getGameId(), null);
	}
	
	public ArrayList<Game> retrieveUnfinishedGames() {
		
		Map<Integer, Game> unfinishedGames = new HashMap<Integer, Game>();
		Map<Integer, Player> players = new HashMap<Integer, Player>();
		
		Game game;
		PlayerGame playerGame;
		Player player;
		
		int gameId;
		int playerId;
		
		Cursor cursor = database.rawQuery(RETRIEVE_UNFINISHED_PLAYER_GAMES, null);
		
		while (cursor.moveToNext()) {
			gameId = cursor.getInt(0);
			if (!unfinishedGames.containsKey(gameId)) {
				game = new Game();
				game.setGameId(gameId);
				game.setPlayerTurn(cursor.getInt(1));
				try {
					game.setStartDate(iso8601Format.parse(cursor.getString(2)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				game.setEnabled(true);
				game.setPlayerGames(new ArrayList<PlayerGame>());
				unfinishedGames.put(gameId, game);
			} else {
				game = unfinishedGames.get(gameId);
			}
			
			playerGame = new PlayerGame();
			playerGame.setPlayerGameId(cursor.getInt(3));
			playerGame.setGameId(game.getGameId());
			playerGame.setLevel(cursor.getInt(4));
			playerGame.setGear(cursor.getInt(5));
			int warriorInt = cursor.getInt(6);
			playerGame.setWarrior(warriorInt == 1 ? true : false);
			
			playerId = cursor.getInt(7);
			if (!players.containsKey(playerId)) {
				player = new Player();
				player.setPlayerId(playerId);
				player.setFirstName(cursor.getString(8));
				player.setLastName(cursor.getString(9));
				players.put(playerId, player);
			} else {
				player = players.get(playerId);
			}
			
			playerGame.setPlayer(player);
			game.getPlayerGames().add(playerGame);
		}

		return new ArrayList<Game>(unfinishedGames.values());
	}
	
	public void deletePlayer(Player player) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PLAYER_ENABLED, 0);
		database.update(TABLE_PLAYER, values, COLUMN_PLAYER_ID + " = " + player.getPlayerId(), null);
	}
	
	public void enablePlayer(Player player) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_PLAYER_ENABLED, 1);
		database.update(TABLE_PLAYER, values, COLUMN_PLAYER_ID + " = " + player.getPlayerId(), null);
	}
	
	class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_PLAYER_DDL);
			db.execSQL(TABLE_GAME_DDL);
			db.execSQL(TABLE_PLAYER_GAME_DDL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
		
	}

}
