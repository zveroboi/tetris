package models;

/** This is the main logic class for processing all data about bricks and game
 *   
 * @author Daniil Kachur
 * @since Tetris 1.0
*/

public class GamePanel {
	
//-------------FIELDS-----------------------------------------
	/**
	 * massive of data that apearance game
	 */
	private int[][] gamePanel;
	/**
	 * coordinate y of leftTop
	 * @see Brick
	 */
	private int centerPoint;
	/**
	 * amount of rows for delete
	 */
	private int amountOfRows;
	
	public final int MOVE_BRICK_AROUND = 0;
	public final int MOVE_BRICK_DOWN = 1;
	public final int MOVE_BRICK_LEFT = 2;
	public final int MOVE_BRICK_RIGHT = 3;
	

//-----------CONSTRUCTORS-------------------------------------
	
	public GamePanel(){
		setGamePanel(new int[23][12]);
		
		for (int j=22; j<23; j++)
			for(int i=0; i<12; i++)
				gamePanel[j][i]=8;
		
		for(int i=0; i<22; i++)
		{
			gamePanel[i][0]=8;
			gamePanel[i][11]=8;
		}
		centerPoint = 4;
		amountOfRows = 0;
			
	}
	
//-------------METHODS----------------------------------------	
	/**
	 * get amount of delete rows
	 * @return amountOfRows
	 */
	public int getAmountOfRows() {
		return amountOfRows;
	}

	/**
	 * set amount of delete rows
	 * @param amountOfRows
	 */
	public void setAmountOfRows(int amountOfRows) {
		this.amountOfRows = amountOfRows;
	}
	
	/**
	 * move 0 in every cells on game panel
	 */
	public void clear(){
		for (int i = 1; i < 11; i++) 
            for (int j = 0; j < 22; j++)
            	gamePanel[j][i]=0;
	}
	/**
	 * set 99 in cell of rows that will offset
	 */
	public boolean clearRows()
	{
		boolean needClear = false;
		int count = 0;
		amountOfRows = 0;
		for (int j=21; j>0; j--)
		{
			count = 0;
			for(int i=1; i<11; i++)
				if(gamePanel[j][i] !=0)
					count++;
			if(count == 10)
			{
				amountOfRows++;
				for(int i=1; i<11; i++)
					gamePanel[j][i] = 99;	
				needClear = true;
			}
		}	
		
		return needClear;		
	}
	/**
	 * offset clear rows
	 */
	public void Offset(){
		int count;
		int copyAmountOfRows = amountOfRows;
		for (int j=21; j>0; j--)
		{
			if(copyAmountOfRows == 0)
				break;
			count = 0;
			for(int i=1; i<11; i++)
				if(gamePanel[j][i] == 99)
					count++;
			if(count == 10)
			{
				copyAmountOfRows--;
				for(int k = j; k>1; k--)
					for(int i=1; i<11; i++)
						gamePanel[k][i] = gamePanel[k - 1][i];
				j++;
			}
		}	
	}
	/**
	 * add brick to panel
	 * @param brick
	 * @return
	 */
	public boolean addBrick(Brick brick){
		int amount = 0;
		
		for (int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				if(gamePanel[i][j+centerPoint] != 0)
					return false;
		
		for (int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				if(brick.getBrickMap()[i][j]!=0)
				{
					gamePanel[i][j+centerPoint]=brick.getBrickMap()[i][j];
					brick.getPoints()[amount].setX(i);
					brick.getPoints()[amount].setY(j+centerPoint);
					amount++;
				}
		return true;
	}
	/**
	 * add brick to pannel on lefTop position
	 * @param brick
	 * @param leftTop
	 */
	public void addBrick(Brick brick, Coordinate leftTop){
		int amount = 0;
		
		for (int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				if(brick.getBrickMap()[i][j]!=0)
				{
					gamePanel[i+leftTop.getX()][j+leftTop.getY()]=brick.getBrickMap()[i][j];
					brick.getPoints()[amount].setX(i+leftTop.getX());
					brick.getPoints()[amount].setY(j+leftTop.getY());
					amount++;
				}
	}
	/**
	 * get game panel
	 * @return
	 */
	public int[][] getGamePanel() {
		return gamePanel;
	}
	/**
	 * set game panel
	 * @param gamePanel
	 */
	public void setGamePanel(int[][] gamePanel) {
		this.gamePanel = gamePanel;
	}
	
	//--------------MANIPULATORS-------------------
	/**
	 * move brick around
	 * @param brick
	 * @return boolean
	 */
	private boolean moveAround(Brick brick)
	{
		int freePoints = 0;
		int x, y;
		
		int[][] next = new int[4][4];
		int[][] save = new int[4][4];
		
		save = brick.getBrickMap();
		for(int i=0; i<4; i++)
		{
			x=brick.getPoints()[i].getX();
			y=brick.getPoints()[i].getY();
			if(gamePanel[x][y] == brick.getColor()) 
				gamePanel[x][y] = 0;			
		}
				
		for(int i=0; i<4; i++)
			for (int j=0; j<4; j++)
				next[j][i]=brick.getBrickMap()[3-i][j];	
		
		brick.setBrickMap(next);// повернули фигурку
		brick.setPoints(brick.getBrickMap());
		
		for(int i=0; i<4; i++)
			if(gamePanel[brick.getPoints()[i].getX()][brick.getPoints()[i].getY()] == 0)
				freePoints++;
			
		if(freePoints == 4)	
		{
			for (int i=0; i<4; i++)
			{
				brick.getPoints()[i].setX(brick.getPoints()[i].getX()+brick.getLeftTop().getX());
				brick.getPoints()[i].setY(brick.getPoints()[i].getY()+brick.getLeftTop().getY());
				addBrick(brick, brick.getLeftTop());
			}
			addBrick(brick, brick.getLeftTop());
		}
		else
		{
			brick.setBrickMap(save);
			brick.setPoints(save);
			addBrick(brick, brick.getLeftTop());
			return false;
		}
		return true;
	}
	
	/**
	 * move brick any type
	 * @param brick
	 * @param moveType
	 * @return boolean
	 */
	public boolean moveBrick(Brick brick, int moveType)
	{
		int x, y;
		int incX = 0;
		int incY = 0;
		int freeCells = 0;
		
		switch(moveType){
		case 0:
			return moveAround(brick);
		case 1:
			incX = 1;
			break;
		case 2:
			incY = -1;
			break;
		case 3:
			incY = 1;
			break;
		}
		
		for(int i=0; i<4; i++)
		{
			x=brick.getPoints()[i].getX();
			y=brick.getPoints()[i].getY();
			gamePanel[x][y] = 0;
		}		
		
		for(int i=0; i<4; i++)
		{
			if(gamePanel[brick.getPoints()[i].getX()+incX][brick.getPoints()[i].getY()+incY] == 0)
				freeCells++;
		}
		
		if(freeCells == 4)
		{		
			for (int i=0; i<4; i++)
			{
				brick.getPoints()[i].setY(brick.getPoints()[i].getY()+incY);
				brick.getPoints()[i].setX(brick.getPoints()[i].getX()+incX);
				gamePanel[brick.getPoints()[i].getX()][brick.getPoints()[i].getY()] = brick.getColor();
			}
			brick.setLeftTop(new Coordinate(brick.getLeftTop().getX() + incX ,brick.getLeftTop().getY() + incY));
			return true;
		}
		else
		{
			addBrick(brick, brick.getLeftTop());
			return false;
		}
	}
}