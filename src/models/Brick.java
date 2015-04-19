package models;

/** This is the main logic class for processing bricks on <b>game Panel</b> 
 * @see GamePanel
 *  
 * @author Daniil Kachur
 * @since Tetris 1.0
*/

public class Brick{
	
//-------------FIELDS-----------------------------------------
	/**
	 * field 4*4 that contains data about brick
	 */
	protected int[][] brickMap;
	/**
	 * coordinates of all cells of brick
	 */
	private Coordinate[] points;
	/**
	 * coordinate of left top cell of brickMap on GamePanel
	 */
	private Coordinate leftTop;
	/**
	 * type of locate bric on brickMap
	 */
	private int type;
	

//-----------CONSTRUCTORS-------------------------------------
	
	public Brick(){
		setBrickMap(new int[][]{
				{0,0,0,0},
				{0,0,0,0},
				{0,0,0,0},
				{0,0,0,0}
		});
		
		points = new Coordinate[]{
				new Coordinate(0,0),
				new Coordinate(0,0),
				new Coordinate(0,0),
				new Coordinate(0,0)
		};
		
		setLeftTop(new Coordinate(0,4));
		type = -1;
	}
	
//-------------METHODS----------------------------------------
	/**
	 * getter of  brickMap
	 * @return brickMap
	 */
	public int[][] getBrickMap() {
		return brickMap;
	}
	/**
	 * setter of  brickMap
	 * @param brickMap
	 */
	public void setBrickMap(int[][] brickMap) {
		this.brickMap = brickMap;
	}	
	/**
	 * get number of color
	 * @return brickMap[i][j]
	 */
	public int getColor()
	{
		for(int i=0; i<4; i++)
			for (int j=0; j<4; j++)
				if( brickMap[i][j] !=0)
					return brickMap[i][j];
		return 0;
	}
	/**
	 * get massive of points
	 * @return points
	 */	
	public Coordinate[] getPoints(){
		return points;
	}
	/**
	 * set massive of points
	 * @param brickMap
	 */
	public void setPoints(int[][] brickMap){
		int amount = 0;
		for(int i=0; i<4; i++)
			for (int j=0; j<4; j++)
			{
				if( brickMap[i][j] == getColor())
					points[amount++].setPoint(i+leftTop.getX(), j+leftTop.getY());
				if(amount == 4)
					return;
			}
	}
	/**
	 * get leftTop
	 * @return leftTop
	 */
	public Coordinate getLeftTop() {
		return leftTop;
	}

	/**
	 * set leftTop
	 * @param leftTop
	 */
	public void setLeftTop(Coordinate leftTop) {
		this.leftTop = leftTop;
	}

	/**
	 * get type
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * set type
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
}
