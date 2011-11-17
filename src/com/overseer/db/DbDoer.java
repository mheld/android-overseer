package com.overseer.db;


import android.content.Context;
import android.database.SQLException;

/**
 * Touches the database in an asyncTask(thunk)-like way. 
 *
 * @param <T> The type of the value to return
 */
public abstract class DbDoer<T> {
	Context context;
	protected DatabaseAdapter db;
	T ret;

	public DbDoer(Context c){
		this.context = c.getApplicationContext();
		db = new DatabaseAdapter(context);
		try{
			db.open();
			ret = perform();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			db.close();
		}

	}

	/**
	 * Performs the given operation, returning the value you want to extract. You have access to db and context.
	 * @return the db operation
	 */
	public abstract T perform();

	public T getReturnValue(){
		return ret;
	}
}
