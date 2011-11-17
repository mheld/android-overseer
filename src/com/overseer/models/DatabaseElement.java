package com.overseer.models;

import java.util.List;

import com.overseer.db.DatabaseAdapter;

/**
 * All createable objects in the database must adhere to this interface
 * Conforms to a generally REST-style interface
 *
 * @param <T> the type we're dealing with
 */
public interface DatabaseElement<T> {
	/**
	 * create this object in the database
	 * @param db an open database
	 * @return the objects of this type in the DB
	 */
	public List<T> create(DatabaseAdapter db);
	//TODO: the other methods?
}
