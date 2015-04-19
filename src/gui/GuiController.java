package gui;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Shadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import application.LoadReplayThread;
import application.RefreshViewThread;
import models.*;

public class GuiController
{	
	//---------FIELDS---------------------------------------------
	private final double ARC_VALUE_DIVIDER = 2.15;
	
	private final int TIME_DIVIDER = 10;
	
	private final int REWARD_DIVIDER = 10;
	
	private final int MOVE_DOWN_REWARD_DIVIDER = 5;
	
	private final int NEED_LEVELS_FOR_NEW_COLOR = 5;
	
	private final int ROWS_TO_LEVEL = 5; //for coorect work need ROWS_TO_LEVEL >= 4
		
	//private final int DELETE_ROWS_DELAY = 500;	//задержка, чтобы новый блок появился на milisecs позже, чем уберутся белые слои
	
	//private final int KEY_CODE_DOWN = 40; //для emulatePressKey()
	
	//private final int KEY_CODE_UP = 38;
	
	//private final int KEY_CODE_LEFT = 37;
	
	//private final int KEY_CODE_RIGHT = 39;

	//----------------------------
    
	@FXML
	private AnchorPane mainLayout;
	
	@FXML
	private Text scoreValue;
	
	@FXML
	private Text highScoreValue;
		
	@FXML
	private Text levelValue;
		
	@FXML
	private Text rowsValue;
	
    @FXML
    private ToggleButton pauseButton;
    
    @FXML
    private Button newGameButton;
       
    @FXML
    private Text info;
    
    @FXML
    private GridPane gamePanel;
        
    @FXML
    private GridPane nextBrickPanel;
    
    @FXML
    private Rectangle gpBackground;
    
    @FXML
    private Rectangle nbpBackground;
    
    @FXML 
    private GridPane mainGrid;
    
    @FXML
    private Label gameOverLabel;
    
    @FXML
    private Label pauseLabel;
    
    //----------------
    
    private int level;
    
    private int rowsCounter;
    
    private Brick nextBrick;
    
    private Brick curBrick;
    
    private GamePanel gpModel;
    
    private NextBrickPanel nbpModel;
    
	private Scores scores;
    
    //-----------------

    private Rectangle[][] gameRectMap;
    
    private Rectangle[][] nextBrickgameRectMap;
    
    //-------------------------
    
    private Timeline timeline;
        
    private boolean isGameOver = false;
    
    private boolean isPause = false;
        
    private boolean needOffset = false;	// индикатор, кот выставляется в true, когда появляются
    									// слои квадратиков, которые нужно сместить 
    									// (англ. offset - смещение)
    
    private boolean canClickKeyWhenNeedOffset = false;	// во время задержки DELETE_ROWS_DELAY не даст 
    													// нажимать клавиши, иначе во время задержки 
    													// можно будет двигать фигурки, но не видеть их
    													// и после задержки вас будет ждать СЮРПРИЗ =)
    
    private KeyFrame Down;
    private KeyFrame AutoPlay; 
   
    private final int GRID_OFFSET_Y = 1; // смещение в индексах по столбцам gamePanel по отношению 
    								   // к gpModel т.к. в модели за кубики отвечают
    								   // столбцы с 1 по 10 а в gamePanel с 0 по 9
    
    private final int GRID_OFFSET_X = 2;
    
	public int DOWN_TIME = 770;
	
	private int ROW_REWARD = 10;
	
	public int MOVE_DOWN_REWARD = 1;
    
    private boolean isAutoPlay = false;
    
    private boolean isGame = false;
    
    RefreshViewThread rvt;
    
    LoadReplayThread lrt;
    
    private String replayLog = "";

	private boolean isLoadReplay = false;
    
	private Brick buffBrick;
	long time = System.currentTimeMillis();
	private boolean canCont = false;;
	
//----------------CONSTRUCTORS--------------------------   
    public GuiController(){
    	int t = DOWN_TIME;
		int r = ROW_REWARD;
		for (int i=0; i<50; i++)
		{
			System.out.println("level: "+Integer.toString(i));
			System.out.println("down : "+Integer.toString(t));
			System.out.println("reward: "+Integer.toString(r)+"\n");
			t -=t/TIME_DIVIDER;
			r += r/REWARD_DIVIDER;
		}
		
		Thread thread = Thread.currentThread();
		System.out.println("Current thread:" + thread);
		thread.setName("MainThread");
		System.out.println("New name:"+ thread);
		
    }
    
    protected void finalize(){
	}
//----------------METHODS-----------------------------------------------------------------------    
    
    //--------------INITIALIZATION------------------------
    
    @FXML
    private void initialize() {
    	
 	    initGameView();
 	    initGameModel(); 	
 	    
 	    mainLayout.setFocusTraversable(true);
    	mainLayout.requestFocus();
    	mainLayout.setOnKeyPressed(new EventHandler<KeyEvent>(){
			 @Override
			 public void handle(KeyEvent keyEvent){
				
				 if (!isPause && !isGameOver && !canClickKeyWhenNeedOffset) {
					 if(keyEvent.getCode() == KeyCode.LEFT)
					 {	
						 long nextTime = System.currentTimeMillis();
					     long duraton = nextTime - time;
					     time = nextTime;
						 replayLog += Long.toString(duraton)+" ";
						 replayLog += "l ";
						
						 moveLeft();
					 }
					 if(keyEvent.getCode() == KeyCode.RIGHT)
					 {
						 long nextTime = System.currentTimeMillis();
					     long duraton = nextTime - time;
					     time = nextTime;
						 replayLog += Long.toString(duraton)+" ";
						 replayLog += "r ";
						 moveRight();
					 }
					 if(keyEvent.getCode() == KeyCode.UP)
					 {
						 long nextTime = System.currentTimeMillis();
					     long duraton = nextTime - time;
					     time = nextTime;
						 replayLog += Long.toString(duraton)+" ";
						 replayLog += "u ";
						 moveAround();
					 }
					 if(keyEvent.getCode() == KeyCode.DOWN)
					 { 
						 moveDown();
						 refreshScores(MOVE_DOWN_REWARD);
					 }
					 mainLayout.requestFocus();
				 }
			 }
		});	
    			 
		newGameButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	       @Override
	        public void handle(MouseEvent mouseEvent) {
	    	   newGame();
	        }
	    });
		
		pauseButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	if(isGame)
            		pause();
            }
		});
		
		gamePanel.widthProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
			
		    	double newWidth = newSceneWidth.doubleValue()/10 - 1;
		    	for (int i = 0; i < 10; i++) 
		    		for (int j = 0; j < 20; j++){
		    			gameRectMap[j][i].setWidth(newWidth);
		    			gameRectMap[j][i].setArcWidth(newWidth/ARC_VALUE_DIVIDER);
		    		}
		    	
		    	gpBackground.setWidth(gamePanel.getWidth()+1);
    			nbpBackground.setWidth(nextBrickPanel.getWidth()+1);
		    }
		});
		
		gamePanel.heightProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		    	
		    	double newHeigh = newSceneHeight.doubleValue()/20 - 1;
		        for (int i = 0; i < 10; i++) 
		    		for (int j = 0; j < 20; j++){
		    			gameRectMap[j][i].setHeight(newHeigh);
		    			gameRectMap[j][i].setArcHeight(newHeigh/ARC_VALUE_DIVIDER);
		    		}
		        
		        gpBackground.setHeight(gamePanel.getHeight()+1);
		    }
		});

		nextBrickPanel.widthProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
			
		    	double newWidth = newSceneWidth.doubleValue()/4 - 1;
		    	for (int i = 0; i < 4; i++) 
		    		for (int j = 0; j < 4; j++){
		    			nextBrickgameRectMap[j][i].setWidth(newWidth);
		    			nextBrickgameRectMap[j][i].setArcWidth(newWidth/ARC_VALUE_DIVIDER);
		    		}
		    	
    			nbpBackground.setWidth(nextBrickPanel.getWidth()+1);
		    }
		});
			
		nextBrickPanel.heightProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		    	
		    	double newHeigh = newSceneHeight.doubleValue()/4 - 1;
		        for (int i = 0; i < 4; i++) 
		    		for (int j = 0; j < 4; j++){
		    			nextBrickgameRectMap[j][i].setHeight(newHeigh);
		    			nextBrickgameRectMap[j][i].setArcHeight(newHeigh/ARC_VALUE_DIVIDER);
		    		}
		        
    			nbpBackground.setHeight(nextBrickPanel.getHeight()+1);
		    }
		});
				
    }
    /**
     * refresh scores
     * @param reward
     */
    public void refreshScores(int reward){
    	 scores.add(reward);
    	 scoreValue.setText(Integer.toString(scores.getScore()));
		 if(scores.getScore()>scores.getHighScore())
		 {
			 scores.setHighScore(scores.getScore());
			 highScoreValue.setText(Integer.toString(scores.getScore()));
		 }
    }
    
    //--------------REPLAYS------------------------
    /**
     * save replay to file
     */
    public void saveReplay(){
    	saveToFile(replayLog+"\n", "resources/replays/last.txt");
    	saveHighScore();
    }
    
    /**
     * load replay from file
     */
    public void loadReplay(){
    	replayLog = new String(readFromFile("resources/replays/last.txt"));
    	lrt = new LoadReplayThread(replayLog, this);
    	isLoadReplay = true;
    	newGame();
    }
    
   
    //-------------------------------------------------
    /**
     * new game
     */
	public void newGame(){ 	
		DOWN_TIME = 700;
		level = 0;
		timeline.stop();
    	if(isAutoPlay){
			 timeline.stop();
			 timeline.getKeyFrames().clear();		// надо обновить фреймы, ведь в autoPlayMode тоже уровни тикали

		     timeline.getKeyFrames().add(Down);
		     timeline.play();
			 isAutoPlay = false;
			 Shadow effect = new Shadow(BlurType.GAUSSIAN, Color.web("#4d3982"), 0);
			 effect.setHeight(0);
			 effect.setWidth(0);
   		 gpBackground.setEffect(effect);
		 }
    	 isGame = true;
 	     time = System.currentTimeMillis();
		 initNewGame();
		 mainLayout.requestFocus();
    }
    
	/**
	 * auto game
	 */
    public void autoGame(){
    	 
	   	 newGame();
	   	 isAutoPlay = true;
	   	 timeline.getKeyFrames().add(AutoPlay);
	   	 
	   	 if(isAutoPlay)
	   	 {
	   		 Shadow effect = new Shadow(BlurType.GAUSSIAN, Color.CADETBLUE, 10);
	   		 effect.setHeight(50);
	   		 effect.setWidth(50);
	   		 gpBackground.setEffect(effect);
	   	 }
	   	 mainLayout.requestFocus();
    }
    
    /**
     * quit
     */
    public void quit(){
    	System.exit(0);
    }

    /**
     * initialize game view
     */
    private void initGameView() {
    	
    	Down = new KeyFrame(Duration.millis(DOWN_TIME), ae -> moveDown());
        AutoPlay = new KeyFrame(Duration.millis(DOWN_TIME), ae -> autoPlay());
    	
    	gameRectMap = new Rectangle[20][10];
    	
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                Rectangle rectangle = new Rectangle();
                gameRectMap[j][i] = rectangle;
                GridPane.setHalignment(rectangle, HPos.CENTER);
                GridPane.setValignment(rectangle, VPos.CENTER);               
            }
        }
        
        nextBrickgameRectMap = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle();
                nextBrickgameRectMap[i][j] = rectangle;
                rectangle.setWidth(10);
                rectangle.setHeight(10);
                GridPane.setHalignment(rectangle, HPos.CENTER);
                GridPane.setValignment(rectangle, VPos.CENTER);
            }
        }
        
        
	    curBrick = nextBrick;
	    nextBrick = generateRandomBrick(); // сгенерили случайное число, через switch return новый brick
	    showinitNextBrick();
	    
    	info.setText("Use arrow keys\n"
 	    		+ "(←, ↑, →, ↓)\n"
 	    		+ "for movement  and rotating.\n"
 	    		+ "N - start new  game.\n"
 	    		+ "P - play/pause.\n"
 	    		+ "A - auto game.\n"
 	    		+ "L - load replay.\n"
 	    		+ "S - save.");
    	
    	levelValue.setFill(getFillColor(1));
    	rowsValue.setFill(getFillColor(1));
    	
    	timeline = new Timeline();
     	timeline.getKeyFrames().add(Down);
    	timeline.setCycleCount(Timeline.INDEFINITE);
    }
    /**
     * initialize game models
     */
    private void initGameModel()
    {
    	nbpModel = new NextBrickPanel();
    	gpModel = new GamePanel();
    	
    	level = 1;
    	level = Integer.valueOf(levelValue.getText());
    	
    	scores = new Scores(0, Integer.valueOf(loadHighScore()));
		scoreValue.setText(Integer.toString(scores.getScore()));
		highScoreValue.setText(Integer.toString(scores.getHighScore()));
    }
    
    //-------------INIT NEXT BRICK---------------------
    /**
     * initialize next brick
     */
    void initNextBrick(){
    	if(needOffset){
    		timeline.stop(); // запаузили timeline, иначе новая фигурка будет опускаться пока будет задержка от sleep()
    		try {
    			Thread.sleep(1);
				Thread.sleep(DOWN_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // DELETE_ROWS_DELAY
    		canClickKeyWhenNeedOffset = false;
    		
    		gpModel.Offset();
    		needOffset = false;
    		
    		
    		MOVE_DOWN_REWARD += MOVE_DOWN_REWARD/MOVE_DOWN_REWARD_DIVIDER;
    		ROW_REWARD += ROW_REWARD/REWARD_DIVIDER;
    		
    		refreshScores(ROW_REWARD);
			
			rowsCounter += gpModel.getAmountOfRows();
			rowsValue.setText(Integer.toString(rowsCounter));
			rowsValue.setFill(levelValue.getFill());
			
			if(rowsCounter >= level * ROWS_TO_LEVEL)
			{
				DOWN_TIME = DOWN_TIME - DOWN_TIME/TIME_DIVIDER;
				
				timeline.getKeyFrames().clear();
				
				level++;
				if(level % NEED_LEVELS_FOR_NEW_COLOR == 0){
					levelValue.setFill(getFillColor(level/NEED_LEVELS_FOR_NEW_COLOR + 1));
					rowsValue.setFill(getFillColor(level/NEED_LEVELS_FOR_NEW_COLOR + 1));
				}
				levelValue.setText(Integer.toString(level));
				Down = new KeyFrame(Duration.millis(DOWN_TIME), ae -> moveDown());
				AutoPlay = new KeyFrame(Duration.millis(DOWN_TIME), ae -> autoPlay());
				
				timeline.getKeyFrames().add(Down);}
			
			if(isAutoPlay)
			{
				timeline.getKeyFrames().clear();
				timeline.getKeyFrames().add(AutoPlay);
				timeline.getKeyFrames().add(Down);
			}
	    }
    	
    	if(!isLoadReplay){
    		curBrick = nextBrick;
    		timeline.play();
	    	nextBrick = generateRandomBrick(); // сгенерили случайное число, через switch return новый brick
	    	}
    	else
    	{	
    		
    		curBrick = buffBrick;
    		if(lrt.isAlive() && !lrt.isRunning())
    		{
    			curBrick = nextBrick;
    			lrt.play();
    		}   
    		while(!canCont)
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		buffBrick = nextBrick;
    	}
    	
    	clearNBPView();
    	showinitNextBrick();  // изменили цвета в nextBrickgameRectMap и добавили это в nextBrickPanel
    	nbpModel.setBrickMap(nextBrick.getBrickMap()); // сохранили в модель nextBrick
	
    	if(!gpModel.addBrick(curBrick))
    		gameOver();
    	
    }
    
    public void setCanCont()
    {
    	canCont = true;
    }
    
    //--------------<NEW GAME>--------------------   
    /**
     * initialize new game
     */
    private void initNewGame()
    {
    	System.nanoTime();
    	if(isPause){
    		isPause = false;
    		pauseLabel.setVisible(false);
    		pauseButton.setText("PAUSE");
    	}
    	if(isGame)
    		rvt = new RefreshViewThread(this); 
    	else
	       	rvt.play();
    	level = 1;
    	levelValue.setText(Integer.toString(level));
    	rowsCounter = 0;
    	rowsValue.setText(Integer.toString(rowsCounter));
    	
    	clearGPModel();	// обновили данные gamePanel
    	gamePanel.getChildren().clear();	// обновили поле вывода gamePanel
    	
    	initNextBrick(); 
    	
    	scores.setScore(0);
    	scoreValue.setText(Integer.toString(scores.getScore()));
    	
		isGameOver = false;
		gameOverLabel.setVisible(false);
    }
        
    //---------PAUSE------------------
    /**
     * pause
     */
	public void pause(){
    	if (isGame)
    	{
	    	pauseButton.selectedProperty().setValue(!isPause);
	        
	        if(pauseButton.selectedProperty().getValue())
	        {
		         pauseButton.setText("PLAY");
		       	 timeline.pause();
		       	 isPause = true;
		       	 replayLog += "p ";
		       	 pauseLabel.setVisible(true);
		       	 rvt.stop();
	        }
	        else
	        {
	        	 pauseButton.setText("PAUSE");
		       	 isPause = false;
		         pauseLabel.setVisible(false);
		       	 timeline.play(); 
		       	 rvt.play();
	        }
    	}

        mainLayout.requestFocus();
    }
    
    //----------GAME OVER----------------
    /**
     * game over
     */
    public void gameOver(){
    	replayLog += "g ";
    	isGameOver = true;
    	isGame = false;
    	rowsCounter = 0;    	
    	timeline.getKeyFrames().clear();
    	Down = new KeyFrame(Duration.millis(DOWN_TIME), ae -> moveDown());
    	timeline.getKeyFrames().add(Down);
    	timeline.stop();
    	gameOverLabel.setVisible(true);
    	timeline.pause();
		rvt.join();
    }
    
    //-------------------------------------------
    /**
     * generate random brick
     * @return new brick
     */
    private Brick generateRandomBrick()
    {    	
    	Brick returnBrick;
    	switch(new Random().nextInt(7)){
	    	 case 0:
	    		 replayLog+="O";
	    		 returnBrick = createOBrick(-1);
	    		 break;
	    	 case 1:
	    		 replayLog+="L";
	    		 returnBrick = createLBrick(-1);
	    		 break;
	         case 2:
	        	 replayLog+="J";
	        	 returnBrick = createJBrick(-1);
	        	 break;
	         case 3:
	        	 replayLog+="I";
	        	 returnBrick = createIBrick(-1);
	        	 break;
	         case 4:
	        	 replayLog+="T";
	        	 returnBrick = createTBrick(-1);
	        	 break;
	         case 5:
	        	 replayLog+="S";
	        	 returnBrick = createSBrick(-1);
	        	 break;
	         case 6:
	        	 replayLog+="Z";
	        	 returnBrick = createZBrick(-1);
	        	 break;
	         default:
	        	 returnBrick = new Brick();
	        	 break;
    	}
    	replayLog+= returnBrick.getType()+" ";
    	return returnBrick;
    }
    
    /**
     * get fill color
     * @param i
     * @see Brick.type
     * @return
     */
    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 1:
                returnPaint = Color.web("#ff2c2c");//red
                break;
            case 2:
            	returnPaint = Color.web("#ff742d");//orange
                break;
            case 3:
            	returnPaint = Color.web("#f3ff2d");//yellow
                break;
            case 4:
            	returnPaint = Color.web("#92ff2d");//green
                break;
            case 5:
            	returnPaint = Color.web("#2dffdb");//ltblue
                break;
            case 6:
            	returnPaint = Color.web("#2d3cff");//blue
                break;
            case 7:
            	returnPaint = Color.web("#cf2dff");//violet
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }
    private void showinitNextBrick(){
    	
    	for (int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				if(nextBrick.getBrickMap()[i][j]!=0 )
				{
					nextBrickgameRectMap[i][j].setFill(getFillColor(nextBrick.getBrickMap()[i][j]));
					nextBrickPanel.add(nextBrickgameRectMap[i][j], j, i); 
				}
    }
    private void clearNBPView(){
    	nextBrickPanel.getChildren().clear();
    }       
    private void clearGPModel(){	
        gpModel.clear();
    }
    synchronized public void refreshGamePanel(){
    	gamePanel.getChildren().clear();
    	
    	for (int i=0; i<20; i++)
			for(int j=0; j<10; j++)
				if(gpModel.getGamePanel()[i+GRID_OFFSET_X][j+GRID_OFFSET_Y]!=0)
				{
					gameRectMap[i][j].setFill(getFillColor(gpModel.getGamePanel()[i+GRID_OFFSET_X][j+GRID_OFFSET_Y]));
					gamePanel.add(gameRectMap[i][j], j, i ); 
				}
    } 
    
    //------------BRICKS-------------------------------
    
    
    //-------------------------------------------------
    
    synchronized public void moveDown(){
    	if(!isGameOver){	
    		if(!isLoadReplay)
    		{
    			long nextTime = System.currentTimeMillis();
			    long duraton = nextTime - time;
			    time = nextTime;
				replayLog += Long.toString(duraton)+" ";
				replayLog += "d ";
    		}
    		
    		if(needOffset)			
    		{						// timeline вызвает функцию когда needOffset == true
    			initNextBrick();		// в этой функции если needOffset == true обновляем картинку и ставим needOffset в  false
    			return; 	// выходим из функции т.к. нам нужен следующий вызов от timeline
    		}
    		if(!gpModel.moveBrick(curBrick,gpModel.MOVE_BRICK_DOWN))
		    {
		    	if(needOffset = gpModel.clearRows()) // нужно будет сдвигать кубики, needOffset теперь true
		    	{
		    										// в данных белые квадратики помечены значением 99
		    										// вывели белые слои, теперь нужно их убрать из модели и обновить картинку
		    		canClickKeyWhenNeedOffset = true;	// запрещаем нажимать на кнопки
		    		return;					// поэтому выходим из функции и timeline заново в нее зайдет через 1 сек	
		    	}
				initNextBrick();
		    }		    
    	}
    }
    synchronized public void moveRight(){
    	gpModel.moveBrick(curBrick,gpModel.MOVE_BRICK_RIGHT);
    }
    synchronized public void moveLeft(){
    	gpModel.moveBrick(curBrick,gpModel.MOVE_BRICK_LEFT);
    }
    synchronized public void moveAround(){
    	gpModel.moveBrick(curBrick,gpModel.MOVE_BRICK_AROUND);
    }
    
    //----------SAVE HIGH SCORE-----------------
    
    
    //-------------------------------------------------
    
    
    private void autoPlay(){
    	
	    switch(new Random().nextInt(4)){
		   	 case 0:
		   		 moveDown();
		  	   	 refreshScores(MOVE_DOWN_REWARD);
		   		 break;		
		   	 case 1:
		   		 moveLeft();
		   		 break;
		     case 2:
		    	 moveRight();
		       	 break;
		     case 3:
		    	 moveAround();
		       	 break;
		}
    }

    //-------------------------------------------------
    
    public void saveHighScore(){
    	saveToFile(highScoreValue.getText(), "resources/highScore.txt");
    }
    
    public String loadHighScore(){
		return readFromFile("resources/highScore.txt");
    }
    
    static String readFromFile(String filepath) {
    	String result = new String("");
            try(FileReader reader = new FileReader(filepath))
            {
            	int c;
                while((c=reader.read())!=-1){
                    result += (char)c;
                } 
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }   
        return result;      
    }
 
    public void saveToFile(String text, String filepath) {
    	try(FileWriter writer = new FileWriter(filepath, false)){
    		writer.write(text);
    		writer.close();
    	}catch(IOException ex){
    		System.out.println(ex.getMessage());
    	} 
    }
    
    //----------AUTO PLAY-----------------------
   
    //-------------------------------------------------
    
    public Brick createIBrick(int type){
    	Brick IBrick = new Brick();
    	int random = new Random().nextInt(4);
    	if(type<0)
    	IBrick.setType(random);
    	else
    	{
    		IBrick.setType(type);
    		random = type;
    	}
    	switch(random){
		case 0:
			IBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{4,4,4,4},
					{0,0,0,0},
					{0,0,0,0}
			}); 
			break;
		case 1:
			IBrick.setBrickMap(new int[][]{
					{0,4,0,0},
					{0,4,0,0},
					{0,4,0,0},
					{0,4,0,0}
			}); 
			break;
		case 2:
			IBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,0,0,0},
					{4,4,4,4},
					{0,0,0,0}
			}); 
			break;
		case 3:
			IBrick.setBrickMap(new int[][]{
					{0,0,4,0},
					{0,0,4,0},
					{0,0,4,0},
					{0,0,4,0}
			}); 
			break;
    	}
    	return IBrick;
    }
    public Brick createJBrick(int type){
    	Brick JBrick = new Brick();
    	int random = new Random().nextInt(4);
    	if(type<0)
    	JBrick.setType(random);
    	else
    	{
    		JBrick.setType(type);
    		random = type;
    	}
    	switch(random){
		case 0:
			JBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,0,3,0},
					{0,0,3,0},
					{0,3,3,0}
			}); 
			break;
		case 1:
			JBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{3,0,0,0},
					{3,3,3,0},
					{0,0,0,0}
			}); 
			break;
		case 2:
			JBrick.setBrickMap(new int[][]{
					{0,3,3,0},
					{0,3,0,0},
					{0,3,0,0},
					{0,0,0,0}
			}); 
			break;
		case 3:
			JBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,3,3,3},
					{0,0,0,3},
					{0,0,0,0}
			}); 
			break;
		}		
    	
    	return JBrick;
    }
    public Brick createLBrick(int type){
    	Brick LBrick = new Brick();
    	int random = new Random().nextInt(4);
    	if(type<0)
    	LBrick.setType(random);
    	else
    	{
    		LBrick.setType(type);
    		random = type;
    	}
    	switch(random){
		case 0:
			LBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,2,0,0},
					{0,2,0,0},
					{0,2,2,0}
			}); 
			break;
		case 1:
			LBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{2,2,2,0},
					{2,0,0,0},
					{0,0,0,0}
			}); 
			break;
		case 2:
			LBrick.setBrickMap(new int[][]{
					{0,2,2,0},
					{0,0,2,0},
					{0,0,2,0},
					{0,0,0,0}
			}); 
			break;
		case 3:
			LBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,0,0,2},
					{0,2,2,2},
					{0,0,0,0}
			}); 
			break;
		}
    	
    	return LBrick;
    }
    public Brick createOBrick(int type){
    	Brick OBrick = new Brick();
    	int random = new Random().nextInt(4);
    	if(type<0)
    		OBrick.setType(random);
    	else
    	{
    		OBrick.setType(type);
    		random = type;
    	}
    	OBrick.setBrickMap(new int[][]{
				{0,0,0,0},
				{0,1,1,0},
				{0,1,1,0},
				{0,0,0,0}
		}); 
    	
    	return OBrick;
    }
    public Brick createSBrick(int type){
    	Brick SBrick = new Brick();
    	int random = new Random().nextInt(4);
    	if(type<0)
    		SBrick.setType(random);
    	else
    	{
    		SBrick.setType(type);
    		random = type;
    	}
    	switch(random){
		case 0:
			SBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,6,6,0},
					{6,6,0,0},
					{0,0,0,0}
			}); 
			break;
		case 1:
			SBrick.setBrickMap(new int[][]{
					{0,6,0,0},
					{0,6,6,0},
					{0,0,6,0},
					{0,0,0,0}
			}); 
			break;
		case 2:
			SBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,0,6,6},
					{0,6,6,0},
					{0,0,0,0}
			}); 
			break;
		case 3:
			SBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,6,0,0},
					{0,6,6,0},
					{0,0,6,0}
			}); 
			break;
    	}
    	
    	return SBrick;
    }
    public Brick createTBrick(int type){
    	Brick TBrick = new Brick();
    	int random = new Random().nextInt(4);
    	if(type<0)
    		TBrick.setType(random);
    	else
    	{
    		TBrick.setType(type);
    		random = type;
    	}
    	switch(random){
		case 0:
			TBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{5,5,5,0},
					{0,5,0,0},
					{0,0,0,0}
			});
			break;
		case 1:
			TBrick.setBrickMap(new int[][]{
					{0,0,5,0},
					{0,5,5,0},
					{0,0,5,0},
					{0,0,0,0}
			}); 
			break;
		case 2:
			TBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,0,5,0},
					{0,5,5,5},
					{0,0,0,0}
			}); 
			break;
		case 3:
			TBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,5,0,0},
					{0,5,5,0},
					{0,5,0,0}
			}); 
			break;
    	} 
    	
    	return TBrick;
    }
    public Brick createZBrick(int type){
    	Brick ZBrick = new Brick();
    	int random = new Random().nextInt(4);
    	if(type<0)
    		ZBrick.setType(random);
    	else
    	{
    		ZBrick.setType(type);
    		random = type;
    	}
    	switch(random){
		case 0:
			ZBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,7,7,0},
					{0,0,7,7},
					{0,0,0,0}
			}); 
		case 1:
			ZBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{0,0,7,0},
					{0,7,7,0},
					{0,7,0,0}
			}); 
			break;
		case 2:
			ZBrick.setBrickMap(new int[][]{
					{0,0,0,0},
					{7,7,0,0},
					{0,7,7,0},
					{0,0,0,0}
			}); 
			break;
		case 3:
			ZBrick.setBrickMap(new int[][]{
					{0,0,7,0},
					{0,7,7,0},
					{0,7,0,0},
					{0,0,0,0}
			}); 
			break;
    	}
    	
    	return ZBrick;
    }
    
    //-------------------------------------------------
        
	public boolean isLoadReplay() {
		return isLoadReplay;
	}

	public void setLoadReplay(boolean isLoadReplay) {
		this.isLoadReplay = isLoadReplay;
	}

	public void setNextBrick(Brick nextBrick) {
		this.nextBrick = nextBrick;
	}
	
//	public void setCurBrick(Brick curBrick) {
//		this.curBrick = curBrick;
//	}
}