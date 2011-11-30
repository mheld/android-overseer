package com.overseer.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.overseer.models.ActivityPoint;
import com.overseer.models.Chunk;
import com.overseer.models.Coordinate;
import com.overseer.utils.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseAdapter {
	private SQLiteDatabase mDb;
	private Context context;
	private DatabaseHelper mDbHelper;
	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 2;

	public DatabaseAdapter(Context c){
		this.context = c.getApplicationContext();		
	};

	/*o***********************************************************************/
	/*COORDINATES*************************************************************/
	/*o***********************************************************************/
	public static final class CoordinateColumns implements BaseColumns {
		public static final String TABLE 		= "locations";
		public static final String LATITUDE 	= "latitude";
		public static final String LONGITUDE	= "longitude";
		public static final String CREATED_AT	= "created_at";
	}

	private static final String COORDINATES_CREATE = 
			maybeCreate(CoordinateColumns.TABLE) +
			"(" + 
			CoordinateColumns._ID      	+ " integer primary key autoincrement, " +
			CoordinateColumns.LATITUDE    + " text not null, " +
			CoordinateColumns.LONGITUDE	+ " text not null, " +
			CoordinateColumns.CREATED_AT	+ " text not null" +
			")";

	/*o***********************************************************************/
	/*ACTIVITYPOINTS**********************************************************/
	/*o***********************************************************************/
	
	public static final class ActivityPointColumns implements BaseColumns {
		public static final String TABLE 		= "activity_points";
		public static final String MAGNITUDE 	= "magnitude";
		public static final String CREATED_AT	= "created_at";
	}

	private static final String ACTIVITY_POINTS_CREATE = 
			maybeCreate(ActivityPointColumns.TABLE) +
			"(" + 
			ActivityPointColumns._ID      	+ " integer primary key autoincrement, " +
			ActivityPointColumns.MAGNITUDE  + " text not null, " +
			ActivityPointColumns.CREATED_AT	+ " text not null" +
			")";
	
	/*o***********************************************************************/
	/*CHUNKS******************************************************************/
	/*o***********************************************************************/
	
	public static final class ChunkColumns implements BaseColumns {
		public static final String TABLE 				= "chunks";
		public static final String FROM 				= "start";
		public static final String UNTIL				= "end";
		public static final String STRESS				= "stress_level";
		public static final String ACTIVITY_CATEGORY	= "activity_category";
		public static final String FOOD					= "food";
		public static final String CREATED_AT			= "created_at";
	}

	private static final String CHUNKS_CREATE = 
			maybeCreate(ChunkColumns.TABLE) +
			"(" + 
			ChunkColumns._ID      			+ " integer primary key autoincrement, " +
			ChunkColumns.CREATED_AT			+ " text not null, " +
			ChunkColumns.FROM				+ " text not null, " +
			ChunkColumns.UNTIL				+ " text not null, " +
			ChunkColumns.STRESS				+ " integer not null, " +
			ChunkColumns.ACTIVITY_CATEGORY	+ " text not null, " +
			ChunkColumns.FOOD				+ " text" +
			")";

	private static String maybeCreate(String table){
		return "create table if not exists " + table + " ";
	}

	/**
	 * Class represents the actual database connection 
	 */
	public class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createAllTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + CoordinateColumns.TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + ActivityPointColumns.TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + ChunkColumns.TABLE);
			onCreate(db);
		}

		/*o***********************************************************************/
		/*COORDINATES*************************************************************/
		/*o***********************************************************************/

		public Cursor fetchAllCoordinates() {
			return mDb.rawQuery("select * from "+ CoordinateColumns.TABLE+" order by "+
					CoordinateColumns.CREATED_AT+" ASC", new String[]{});
		}
		
		public Cursor fetchAllCoordinatesBetween(Date left, Date right) {
			return mDb.rawQuery("select * from "+ CoordinateColumns.TABLE+" where "+
					CoordinateColumns.CREATED_AT+" > "+ left.getTime() +" and "+
					CoordinateColumns.CREATED_AT+" < "+ right.getTime() +" order by "+
					CoordinateColumns.CREATED_AT+" ASC", new String[]{});
		}
		
		public long create(Coordinate c){
			ContentValues initialValues = new ContentValues();
			initialValues.put(CoordinateColumns.LATITUDE, c.getLatitude());
			initialValues.put(CoordinateColumns.LONGITUDE, c.getLongitude());
			initialValues.put(CoordinateColumns.CREATED_AT, System.currentTimeMillis());

			return mDb.insert(CoordinateColumns.TABLE, null, initialValues);
		}
		
		/*o***********************************************************************/
		/*ACTIVITYPOINTS**********************************************************/
		/*o***********************************************************************/
		
		public Cursor fetchAllActivityPoints() {
			return mDb.rawQuery("select * from "+ ActivityPointColumns.TABLE+" order by "+
					ActivityPointColumns.CREATED_AT+" ASC", new String[]{});
		}
		
		public Cursor fetchAllActivityPointsBetween(Date left, Date right) {
			return mDb.rawQuery("select * from "+ ActivityPointColumns.TABLE+" where "+
					ActivityPointColumns.CREATED_AT+" > "+ left.getTime() +" and "+
					ActivityPointColumns.CREATED_AT+" < "+ right.getTime() +" order by "+
					ActivityPointColumns.CREATED_AT+" ASC", new String[]{});
		}
		
		public long create(ActivityPoint a){
			ContentValues initialValues = new ContentValues();
			initialValues.put(ActivityPointColumns.MAGNITUDE, a.getMagnitude());
			initialValues.put(ActivityPointColumns.CREATED_AT, System.currentTimeMillis());

			return mDb.insert(ActivityPointColumns.TABLE, null, initialValues);
		}
		
		/*o***********************************************************************/
		/*CHUNKS******************************************************************/
		/*o***********************************************************************/
		
		public Cursor fetchAllChunks() {
			return mDb.rawQuery("select * from "+ ChunkColumns.TABLE+" order by "+
					ChunkColumns.CREATED_AT+" ASC", new String[]{});
		}
		
		public long create(Chunk c){
			ContentValues initialValues = new ContentValues();
			initialValues.put(ChunkColumns.FROM, c.getFrom().getTime());
			initialValues.put(ChunkColumns.UNTIL, c.getUntil().getTime());
			initialValues.put(ChunkColumns.ACTIVITY_CATEGORY, c.getActivityCategory());
			initialValues.put(ChunkColumns.FOOD, c.getFood());
			initialValues.put(ChunkColumns.STRESS, c.getStressLevel());
			initialValues.put(ChunkColumns.CREATED_AT, System.currentTimeMillis());

			return mDb.insert(ChunkColumns.TABLE, null, initialValues);
		}
		
	}

	public static int booleanToSQL(Boolean b){
		return (b ? 1 : 0);
	}

	public static boolean SQLToBoolean(int b){
		if(b == 1){
			return true;
		}else{
			return false;
		}
	}

	public void open() throws SQLException{
		this.mDbHelper = new DatabaseHelper(context);
		this.mDb = mDbHelper.getWritableDatabase();
		createAllTables(mDb);
	}

	public void close(){
		try{
			mDb.close();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}

	private void createAllTables(SQLiteDatabase db) throws SQLException{
		db.execSQL(COORDINATES_CREATE);
		db.execSQL(ACTIVITY_POINTS_CREATE);
		db.execSQL(CHUNKS_CREATE);
	}

	
	/*o***********************************************************************/
	/*COORDINATES*************************************************************/
	/*o***********************************************************************/
	
	public long create(Coordinate c){
		return mDbHelper.create(c);
	}
	
	public List<Coordinate> getCoordinates(){
		return getCoordinates(mDbHelper.fetchAllCoordinates());
	}
	
	public List<Coordinate> getCoordinatesBetween(Date left, Date right){
		return getCoordinates(mDbHelper.fetchAllCoordinatesBetween(left, right));
	}

	private List<Coordinate> getCoordinates(Cursor coordCursor){
		try {
			ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();

			coordCursor.moveToFirst();
			for (int i = 0; i < coordCursor.getCount(); i++) {
				coordinates.add(new Coordinate(coordCursor));
				coordCursor.moveToNext();
			}
			coordCursor.close();

			return coordinates;

		} catch (SQLException e) {
			Log.e("Exception on query", e);
			return new ArrayList<Coordinate>();
		} finally{
			coordCursor.close();
		}
	}
	
	/*o***********************************************************************/
	/*ACTIVITYPOINTS**********************************************************/
	/*o***********************************************************************/
	
	public long create(ActivityPoint a){
		return mDbHelper.create(a);
	}
	
	public List<ActivityPoint> getActivityPoints(){
		return getActivityPoints(mDbHelper.fetchAllActivityPoints());
	}
	
	public List<ActivityPoint> getActivityPointsBetween(Date left, Date right){
		return getActivityPoints(mDbHelper.fetchAllActivityPointsBetween(left, right));
	}

	private List<ActivityPoint> getActivityPoints(Cursor apCursor){
		try {
			ArrayList<ActivityPoint> points = new ArrayList<ActivityPoint>();

			apCursor.moveToFirst();
			for (int i = 0; i < apCursor.getCount(); i++) {
				points.add(new ActivityPoint(apCursor));
				apCursor.moveToNext();
			}
			apCursor.close();

			return points;

		} catch (SQLException e) {
			Log.e("Exception on query", e);
			return new ArrayList<ActivityPoint>();
		} finally{
			apCursor.close();
		}
	}
	
	/*o***********************************************************************/
	/*CHUNKS******************************************************************/
	/*o***********************************************************************/
	
	public long create(Chunk c){
		return mDbHelper.create(c);
	}
	
	public List<Chunk> getChunks(){
		return getChunks(mDbHelper.fetchAllChunks());
	}

	private List<Chunk> getChunks(Cursor chunkCursor){
		try {
			ArrayList<Chunk> chunks = new ArrayList<Chunk>();

			chunkCursor.moveToFirst();
			for (int i = 0; i < chunkCursor.getCount(); i++) {
				chunks.add(new Chunk(chunkCursor));
				chunkCursor.moveToNext();
			}
			chunkCursor.close();

			return chunks;

		} catch (SQLException e) {
			Log.e("Exception on query", e);
			return new ArrayList<Chunk>();
		} finally{
			chunkCursor.close();
		}
	}
}
