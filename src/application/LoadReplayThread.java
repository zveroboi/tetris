package application;

import javafx.application.Platform;
import gui.GuiController;
public class LoadReplayThread implements Runnable {
	public Thread thread;
	private GuiController guiController;
	private String repl;
	private int i;
	private boolean running = true;
	private boolean isAlive = true;
	boolean firstBrick = true;
	private boolean brickCreated = false;
	boolean secBrick = false;
	public int SPEED_DIVIDER=3;// 12 max
	
	public LoadReplayThread(String replayLog, GuiController guiController) {
		this.guiController = guiController;
		this.repl = replayLog;
		i = 0;
		thread =  new Thread(this, "LoadReplayThread");
		thread.setDaemon(true);
		System.out.println("subthread: "+thread.getName()+" open");
		thread.start();
	}
	
	public void run(){
		int brickType = 0;
		int length = repl.length();
		System.out.println("run replay thread");
		try{			
			while(i+1 < length && isAlive){
				if(!repl.isEmpty())
				{
					if(running){
						brickCreated = false;
						brickType = repl.charAt(i+1)-48;
						switch(repl.charAt(i)){
						case 'O':
							guiController.setNextBrick(guiController.createOBrick(brickType));
							i+=2;break;
						case 'I':
							guiController.setNextBrick(guiController.createIBrick(brickType));
							i+=2;break;
						case 'T':
							guiController.setNextBrick(guiController.createTBrick(brickType));
							i+=2;break;
						case 'L':
							guiController.setNextBrick(guiController.createLBrick(brickType));
							i+=2;break;
						case 'S':
							guiController.setNextBrick(guiController.createSBrick(brickType));
							i+=2;break;
						case 'J':
							guiController.setNextBrick(guiController.createJBrick(brickType));
							i+=2;break;
						case 'Z':
							guiController.setNextBrick(guiController.createZBrick(brickType));
							i+=2;break;
						case 'd':
							Platform.runLater(() -> guiController.moveDown());
							Platform.runLater(() -> guiController.refreshScores(guiController.MOVE_DOWN_REWARD));
							i++;break;
						case 'l':
							guiController.moveLeft();
							i++;break;
						case 'r':
							guiController.moveRight();
							i++;break;
						case 'u':
							guiController.moveAround();
							i++;break;
						case 'p':
							System.out.println("pause");
							Platform.runLater(() -> guiController.pause());
							isAlive = false;
							break;
						case 'g':
							guiController.gameOver();
							break;
						default:
							int temp = i;
							while(repl.charAt(++i) != ' ');
							String buff = repl.substring(temp, i);
							guiController.DOWN_TIME/=SPEED_DIVIDER;
							Thread.sleep((int)(Integer.valueOf(buff)/SPEED_DIVIDER));
							break;
						}
						if(firstBrick){
							stop();
							System.out.println(repl);
							firstBrick = false;	
						}
						else
						{
							guiController.setCanCont();
						}
						++i;
						brickCreated = true;
					}
				}
				else{
					guiController.setLoadReplay(false);
					break;
				}
			}
		}catch(InterruptedException e){
			System.out.println("exception close replay thread");
		}
		System.out.println("close replay thread");
		guiController.setLoadReplay(false);
		//guiController.initNextBrick();
	}
	
	public void stop(){
		running = false;
		System.out.println("stop replay thread");
	}
	
	public void play(){
		running = true;
		System.out.println("continue replay thread");
	}
	
	public void join(){
		isAlive = false;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isBrickCreated() {
		return brickCreated;
	}
}
