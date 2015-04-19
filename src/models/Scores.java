package models;

/** This class contains fields and methods for storing 
 * and processing <b>score</b> and <b>high score</b>.
 * 
 * @author Daniil Kachur
 * @since Tetris 1.0
*/

public class Scores {
	
//-------------FIELDS-----------------------------------------
	/** storing score */
	private int score;
	/** storing high score */
	private int highScore;
	
//-----------CONSTRUCTORS-------------------------------------
	
	public Scores() {
	}
	
	public Scores(int score, int highScore) {
        this.score = score;
        this.highScore = highScore;
    }
	
//-------------METHODS----------------------------------------
	/**
	 * get score
	 * @return
	 */
	public int getScore() {
		return score;
	}
	/**
	 * set score
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}
	/**
	 * get highScore
	 * @return
	 */
	public int getHighScore() {
		return highScore;
	}
	/**
	 * set highScore
	 * @param highScore
	 */
	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}
	/**
	 * add value to score
	 * @param value
	 */
	public void add(int value){
        score += value;
    }

	/**
	 * reset score to 0
	 */
    public void reset() {
        score = 0;
    }
    
    /**
     * move score into highScore
     */
    public void newHighScore()
    {
    	this.highScore = score;
    }
}

	