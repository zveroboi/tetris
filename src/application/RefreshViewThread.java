package application;

import gui.GuiController;
import javafx.application.Platform;
public class RefreshViewThread implements Runnable {
	public Thread thread;
	private GuiController guiController;
	boolean running = true;
	boolean isAlive = true;
	
	public RefreshViewThread(GuiController guiController) {
		this.guiController = guiController;
		thread =  new Thread(this, "RefreshViewThread");
		System.out.println("subthread: "+thread.getName()+" open");
		thread.setDaemon(true);
		thread.start();
	}
	
	public void run(){
		System.out.println("run view thread");
		try{
			while(isAlive){
				if(running){
					Platform.runLater(() -> guiController.refreshGamePanel());
					Thread.sleep(1);
				}
				else{
					Thread.sleep(1);
					guiController.saveHighScore();
					guiController.saveReplay();
				}
			}
			guiController.saveHighScore();
			guiController.saveReplay();
		}catch(InterruptedException e){
			System.out.println("exception close view thread");
		}
		System.out.println("close view thread");
	}
	
	public void stop(){
		running = false;
	}
	
	public void play(){
		running = true;
	}
	
	public void join(){
		isAlive = false;
	}
}
