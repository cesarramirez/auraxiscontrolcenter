package com.cesarandres.ps2link.module;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cesarandres.ps2link.soe.content.CharacterProfile;
import com.cesarandres.ps2link.soe.content.Faction;
import com.cesarandres.ps2link.soe.content.Member;
import com.cesarandres.ps2link.soe.content.Outfit;
import com.cesarandres.ps2link.soe.content.World;
import com.cesarandres.ps2link.soe.content.backlog.Times;
import com.cesarandres.ps2link.soe.content.character.BattleRank;
import com.cesarandres.ps2link.soe.content.character.Certs;
import com.cesarandres.ps2link.soe.content.character.Name;
import com.cesarandres.ps2link.soe.content.world.Name_Multi;

/**
 * Class that retrieves information from the SQLiteManager and convert it into
 * objects that can be used by other classes.
 * 
 * @author Cesar
 * 
 */
public class ObjectDataSource {

	private SQLiteDatabase database;
	private SQLiteManager dbHelper;
	private String[] allColumnsWorlds = { SQLiteManager.WORLDS_COLUMN_ID,
			SQLiteManager.WORLDS_COLUMN_NAME,
			SQLiteManager.WORLDS_COLUMN_STATE};
	
	private String[] allColumnsFactions = { SQLiteManager.FACTIONS_COLUMN_ID,
			SQLiteManager.FACTIONS_COLUMN_NAME,
			SQLiteManager.FACTIONS_COLUMN_CODE,
			SQLiteManager.FACTIONS_COLUMN_ICON};
	
	private String[] allColumnsCharacters = { SQLiteManager.CHARACTERS_COLUMN_ID,
			SQLiteManager.CHARACTERS_COLUMN_NAME_FIRST,
			SQLiteManager.CHARACTERS_COLUMN_NAME_FIRST_LOWER,
			SQLiteManager.CHARACTERS_COLUMN_ACTIVE_PROFILE_ID,
			SQLiteManager.CHARACTERS_COLUMN_CURRENT_POINTS,
			SQLiteManager.CHARACTERS_COLUMN_PERCENTAGE_TO_NEXT_CERT,
			SQLiteManager.CHARACTERS_COLUMN_PERCENTAGE_TO_NEXT_RANK,
			SQLiteManager.CHARACTERS_COLUMN_RANK_VALUE,
			SQLiteManager.CHARACTERS_COLUMN_LAST_LOGIN,
			SQLiteManager.CHARACTERS_COLUMN_MINUTES_PLAYED,
			SQLiteManager.CHARACTERS_COLUMN_FACTION_ID,
			SQLiteManager.CHARACTERS_COLUMN_WORLD_ID};
	
	private String[] allColumnsMembers = { SQLiteManager.MEMBERS_COLUMN_ID,
			SQLiteManager.MEMBERS_COLUMN_MEMBER_SINCE,
			SQLiteManager.MEMBERS_COLUMN_RANK,
			SQLiteManager.MEMBERS_COLUMN_ORDINAL,
			SQLiteManager.MEMBERS_COLUMN_OUTFIT_ID,
			SQLiteManager.MEMBERS_COLUMN_ONLINE_STATUS};
	
	private String[] allColumnsOutfit = { SQLiteManager.OUTFIT_COLUMN_ID,
			SQLiteManager.OUTFIT_COLUMN_NAME,
			SQLiteManager.OUTFIT_COLUMN_LEADER_CHARACTER_ID,
			SQLiteManager.OUTFIT_COLUMN_MEMBER_COUNT,
			SQLiteManager.OUTFIT_COLUMN_TIME_CREATED,
			SQLiteManager.OUTFIT_COLUMN_WORDL_ID,
			SQLiteManager.OUTFIT_COLUMN_FACTION_ID};

	/**
	 * Constructor that requires a reference to the current context.
	 * 
	 * @param context
	 *            reference to the calling activity.
	 */
	public ObjectDataSource(Context context) {
		dbHelper = new SQLiteManager(context);
	}

	/**
	 * Open the database and get it ready to retrieve information.
	 * 
	 * @throws SQLException
	 *             if there is an error while opening the database.
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Close the database.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Drops all the tables and creates them again.
	 */
	public void reset() {
		dbHelper.onUpgrade(database, SQLiteManager.DATABASE_VERSION, SQLiteManager.DATABASE_VERSION);
	}
	
	public boolean insertCharacter(CharacterProfile character) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.CHARACTERS_COLUMN_ID, character.getId());
		values.put(SQLiteManager.CHARACTERS_COLUMN_NAME_FIRST, character.getName().getFirst());
		values.put(SQLiteManager.CHARACTERS_COLUMN_NAME_FIRST_LOWER, character.getName().getFirst_lower());
		values.put(SQLiteManager.CHARACTERS_COLUMN_ACTIVE_PROFILE_ID, character.getActive_profile_id());
		values.put(SQLiteManager.CHARACTERS_COLUMN_CURRENT_POINTS, character.getCerts().getCurrentpoints());
		values.put(SQLiteManager.CHARACTERS_COLUMN_PERCENTAGE_TO_NEXT_CERT, character.getCerts().getPercentagetonext());
		values.put(SQLiteManager.CHARACTERS_COLUMN_RANK_VALUE, character.getBattle_rank().getValue());
		values.put(SQLiteManager.CHARACTERS_COLUMN_PERCENTAGE_TO_NEXT_RANK, character.getBattle_rank().getPercent_to_next());
		values.put(SQLiteManager.CHARACTERS_COLUMN_LAST_LOGIN, character.getTimes().getLast_login());
		values.put(SQLiteManager.CHARACTERS_COLUMN_MINUTES_PLAYED, character.getTimes().getMinutes_played());
		values.put(SQLiteManager.CHARACTERS_COLUMN_FACTION_ID, character.getFaction_id());
		values.put(SQLiteManager.CHARACTERS_COLUMN_WORLD_ID, character.getWorld_id());
		long insertId = database.insert(SQLiteManager.TABLE_CHARACTERS_NAME, null, values);
		return (insertId != -1);
	}

	public void deleteCharacter(CharacterProfile character) {
		String id = character.getId();
		database.delete(SQLiteManager.TABLE_CHARACTERS_NAME, SQLiteManager.CHARACTERS_COLUMN_ID + " = " + id, null);
	}

	private CharacterProfile cursorToCharacterProfile(Cursor cursor) {
		CharacterProfile character = new CharacterProfile();
		character.setId(cursor.getString(0));
		Name name = new Name();
		name.setFirst(cursor.getString(1));
		name.setFirst_lower(cursor.getString(2));
		character.setName(name);
		
		character.setActive_profile_id(cursor.getString(3));
		Certs certs = new Certs();
		certs.setCurrentpoints(cursor.getString(4));
		certs.setPercentagetonext(cursor.getString(5));
		character.setCerts(certs);
		
		BattleRank br = new BattleRank();
		br.setValue(cursor.getInt(6));
		br.setPercent_to_next(cursor.getInt(7));
		character.setBattle_rank(br);

		Times times = new Times();
		times.setLast_login(cursor.getString(8));
		times.setMinutes_played(cursor.getString(9));
		character.setTimes(times);
		
		character.setFaction_id(cursor.getString(10));
		character.setWorld_id(cursor.getString(11));
		
		return character;
	}
	
	public boolean insertFaction(Faction faction) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.FACTIONS_COLUMN_ID, faction.getId());
		values.put(SQLiteManager.FACTIONS_COLUMN_NAME, faction.getName().getEn());
		values.put(SQLiteManager.FACTIONS_COLUMN_CODE, faction.getCode());
		values.put(SQLiteManager.FACTIONS_COLUMN_ICON, faction.getIcon());
		long insertId = database.insert(SQLiteManager.TABLE_CHARACTERS_NAME, null, values);
		return (insertId != -1);
	}

	public void deleteFaction(Faction faction) {
		String id = faction.getId();
		database.delete(SQLiteManager.TABLE_FACTIONS_NAME, SQLiteManager.FACTIONS_COLUMN_ID + " = " + id, null);
	}

	private Faction cursorToFaction(Cursor cursor) {
		Faction faction = new Faction();
		faction.setId(cursor.getString(0));
		Name_Multi name = new Name_Multi();
		name.setEn(cursor.getString(1));
		faction.setName(name);
		
		faction.setCode(cursor.getString(2));
		faction.setIcon(cursor.getString(3));
		return faction;
	}
	
	public boolean insertMember(Member member) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.MEMBERS_COLUMN_ID, member.getCharacter_id());
		values.put(SQLiteManager.MEMBERS_COLUMN_MEMBER_SINCE, member.getMember_since());
		values.put(SQLiteManager.MEMBERS_COLUMN_ONLINE_STATUS, member.getOnline_status());
		values.put(SQLiteManager.MEMBERS_COLUMN_RANK, member.getRank());
		values.put(SQLiteManager.MEMBERS_COLUMN_ORDINAL, member.getRank_ordinal());
		long insertId = database.insert(SQLiteManager.TABLE_MEMBERS_NAME, null, values);
		return (insertId != -1);
	}

	public void deleteMember(Member member) {
		String id = member.getCharacter_id();
		database.delete(SQLiteManager.TABLE_MEMBERS_NAME, SQLiteManager.MEMBERS_COLUMN_ID + " = " + id, null);
	}

	private Member cursorToMember(Cursor cursor) {
		Member member = new Member();
		member.setCharacter_id(cursor.getString(0));
		member.setMember_since(cursor.getString(1));
		member.setOnline_status(cursor.getString(2));
		member.setRank(cursor.getString(3));
		member.setRank_ordinal(cursor.getString(4));
		return member;
	}
	
	public boolean insertOutfit(Outfit outfit) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.OUTFIT_COLUMN_ID, outfit.getId());
		values.put(SQLiteManager.OUTFIT_COLUMN_NAME, outfit.getName());
		values.put(SQLiteManager.OUTFIT_COLUMN_ALIAS, outfit.getAlias());
		values.put(SQLiteManager.OUTFIT_COLUMN_LEADER_CHARACTER_ID, outfit.getLeader_character_id());
		values.put(SQLiteManager.OUTFIT_COLUMN_MEMBER_COUNT, outfit.getMember_count());
		values.put(SQLiteManager.OUTFIT_COLUMN_TIME_CREATED, outfit.getLeader_character_id());
		long insertId = database.insert(SQLiteManager.TABLE_MEMBERS_NAME, null, values);
		return (insertId != -1);
	}

	public void deleteOutfit(Outfit outfit) {
		String id = outfit.getId();
		database.delete(SQLiteManager.TABLE_OUTFITS_NAME, SQLiteManager.OUTFIT_COLUMN_ID + " = " + id, null);
	}

	private Outfit cursorToOutfit(Cursor cursor) {
		Outfit outfit = new Outfit();
		outfit.setId(cursor.getString(0));
		outfit.setName(cursor.getString(1));
		outfit.setAlias(cursor.getString(2));
		outfit.setLeader_character_id(cursor.getString(3));
		outfit.setTime_created(cursor.getString(4));
		outfit.setWorld_id(cursor.getString(5));
		outfit.setFaction_id(cursor.getString(6));
		return outfit;
	}
	
	public boolean insertWorld(World world) {
		ContentValues values = new ContentValues();
		values.put(SQLiteManager.WORLDS_COLUMN_NAME, world.getName().getEn());
		values.put(SQLiteManager.WORLDS_COLUMN_ID, world.getWorld_id());
		values.put(SQLiteManager.WORLDS_COLUMN_STATE, world.getState());
		long insertId = database.insert(SQLiteManager.TABLE_WORLDS_NAME, null, values);
		return (insertId != -1);
	}

	public void deleteWorld(World world) {
		String id = world.getWorld_id();
		database.delete(SQLiteManager.TABLE_WORLDS_NAME, SQLiteManager.OUTFIT_COLUMN_ID + " = " + id, null);
	}

	private World cursorToWorld(Cursor cursor) {
		World world = new World();
		
		world.setWorld_id(cursor.getString(0));
		world.setState(cursor.getString(1));
		
		Name_Multi name = new Name_Multi();
		name.setEn(cursor.getString(2));
		world.setName(name);
		
		
		return world;
	}
	
}