/*
 * CRITTERS Point.java
 * EE422C Project 5 submission by
 * Replace <...> with your actual data.
 * <Sanjay Gorur>
 * <sg52879>
 * <17805>
 * Keshav Narasimhan
 * kn9558
 * 17805
 * Fall 2021
 */

package assignment5;

public class Point implements Comparable<Point> {
	private int x_coord;
	private int y_coord;
	
	public Point(int x, int y) {
		x_coord = x;
		y_coord = y;
	}
	
	/**
	 * toString() returns a String representation of this Point's coordinates
	 * @return String representation of ordered pair
	 */
	public String toString()
	{
		String point = "[" + x_coord  + "," + y_coord + "]";
		return point;
	}

	/**
	 * compareTo() compares the Points in order for the TreeMap in Critter.java to order Critters by location on the grid
	 * orders by y coordinate, and then if needed, x coordinate
	 * 
	 * @return Integer value that symbolizes whether this Point's location is greater/less than or equal to Point other's location
	 */
	@Override
	public int compareTo(Point other) {
		// TODO Auto-generated method stub
		if (this.y_coord < other.y_coord) {
			return -1;
		} else if (this.y_coord > other.y_coord) {
			return 1;
		} else {
			if (this.x_coord < other.x_coord) {
				return -1;
			} else if (this.x_coord > other.x_coord) {
				return 1;
			} else {
				return 0;
			}
		}
		
	}

	/**
	 * equals() method checks to see if this Point is equal to object parameter
	 * if obj is a Point, checks to see if the two share the same x and y coordinates
	 * 
	 * @param obj - Object that this is being compared to
	 * @return boolean value which determines equality
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Point) {
			Point other = (Point) obj;
			if (other.getX() == this.getX() && other.getY() == this.getY()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Getter Method to return Point's x-coordinate
	 * @return x_coord
	 */
	public int getX() {
		return this.x_coord;
	}
	
	/**
	 * Getter Method to return Point's y-coordinate
	 * 
	 * @return y_coord
	 */
	public int getY() {
		return this.y_coord;
	}
	
	
}
