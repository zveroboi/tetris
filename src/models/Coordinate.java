package models;

/** This class is a combination of two properties <b>X</b> and <b>Y</b>. 
 * Imagine a grid of cells that have the coordinates <b>X</b> and <b>Y</b>. 
 * In this case, field <b>X</b> corresponds to the number of row in grid, 
 * and field <b>Y</b> corresponds to the number of column in grid.
 * 
 * @author Daniil Kachur
 * @since Tetris 1.0
*/

public class Coordinate {
	/**
	 * coordinate x
	 */
	private int X;
	/**
	 * coordinate y
	 */
	private int Y;
	
	public Coordinate(){
		X = 0;
		Y = 0;
	}
	
	public Coordinate(int newX, int newY){
		X = newX;
		Y = newY;
	}
	
	/**
	 * set new point	
	 * @param newX
	 * @param newY
	 */
	
	public void setPoint(int newX, int newY){
		X = newX;
		Y = newY;
	}
	
	/**
	 * return massive contist of X and Y
	 * @return	new int[] {X,Y}
	 */
	
	public int[] getPoint(){
		return new int[]{X, Y};
	}
	
	/**
	 * setter of  X
	 * @param newX
	 */
	public void setX(int newX){
		X = newX;
	}
	/**
	 * getter of X
	 * @return X
	 */
	public int getX(){
		return X;		
	}
	/**
	 * setter of  Y
	 * @param newY
	 */
	public void setY(int newY){
		Y = newY;
	}
	
	/**
	 * getter of  Y
	 * @return Y
	 */
	public int getY(){
		return Y;		
	}
}
