package org.test.project.api;

/**
 * The Interface Observable. Representing any observations (i.e. items, enemies)
 * that might be observed during exploration
 */
public interface Observable {

	/**
	 * Gets the observable's type. (i.e. enemy, item...etc)
	 *
	 * @return the type
	 */
	Object getType();
}
