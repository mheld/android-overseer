package com.overseer.db;

import java.util.ArrayList;
import java.util.List;

import com.overseer.models.ActivityPoint;
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
	private static final int DATABASE_VERSION = 1;

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
			onCreate(db);
		}

		/*o***********************************************************************/
		/*COORDINATES*************************************************************/
		/*o***********************************************************************/

		public Cursor fetchAllCoordinates() {
			return mDb.rawQuery("select * from "+ CoordinateColumns.TABLE+" order by "+
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
		
		public long create(ActivityPoint a){
			ContentValues initialValues = new ContentValues();
			initialValues.put(ActivityPointColumns.MAGNITUDE, a.getMagnitude());
			initialValues.put(ActivityPointColumns.CREATED_AT, System.currentTimeMillis());

			return mDb.insert(ActivityPointColumns.TABLE, null, initialValues);
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
}
