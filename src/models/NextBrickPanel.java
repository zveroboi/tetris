package models;

/** This class defines location of brick's cells. 
 * This model contain matrix of int values which 
 * locate and value depends on type of brick (O, I, J, L, S or Z)
 * 
 * @author Daniil Kachur
 * @since Tetris 1.0
*/

public class NextBrickPanel {

	/**
	 * field 4*4 that contains data about brick
	 * @see Brick
	 */
	private int[][] brickMap;
	
	public NextBrickPanel(){
		setBrickMap(new int[][]{
				{0,0,0,0},
				{0,0,0,0},
				{0,0,0,0},
				{0,0,0,0}
		});
	}

	/**
	 * get brickMap
	 * @return
	 */
	public int[][] getBrickMap() {
		return brickMap;
	}

	/**
	 * set brickMap
	 * @param brickMap
	 */
	public void setBrickMap(int[][] brickMap) {
		this.brickMap = brickMap;
	}
	
}
