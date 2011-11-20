package com.overseer.db;

import java.util.ArrayList;
import java.util.List;

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
	/*FEEDS*******************************************************************/
	/*o***********************************************************************/
	public static final class CoordinateColumns implements BaseColumns {
		public static final String TABLE 		= "locations";
		public static final String LATITUDE 	= "latitude";
		public static final String LONGITUDE	= "longitude";
		public static final String CREATED_AT	= "created_at";
		
	}

	//TODO: really shouldn't be all text columns
	private static final String COORDINATES_CREATE = 
			maybeCreate(CoordinateColumns.TABLE) +
			"(" + 
			CoordinateColumns._ID      	+ " integer primary key autoincrement, " +
			CoordinateColumns.LATITUDE    + " text not null, " +
			CoordinateColumns.LONGITUDE	+ " text not null, " +
			CoordinateColumns.CREATED_AT	+ " text not null" +
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
			onCreate(db);
		}

		/*o***********************************************************************/
		/*Coordinates*************************************************************/
		/*o***********************************************************************/

		/**
		 * Fetches all coordinates (a cursor)
		 * @return cursor that holds all coordinates
		 */
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
		}
	}

	private void createAllTables(SQLiteDatabase db) throws SQLException{
		db.execSQL(COORDINATES_CREATE);
	}

	
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
}
