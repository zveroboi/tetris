package application;

/** This is the main class of application
 *   
 * @author Daniil Kachur
 * @since GoodDownloader 1.0
*/

import gui.GuiController;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private AnchorPane gameOverview;
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Tetris"); 
        this.primaryStage.getIcons().add(new Image("file:resources/images/tetris32.png"));
        
        FXMLLoader fxmlLoader = new FXMLLoader(GuiController.class.getResource("Gui.fxml"));
		try {
			gameOverview = (AnchorPane)fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scene scene = new Scene(gameOverview);
	    primaryStage.setScene(scene);
	    primaryStage.show();
    }
    
	public static void main(String[] args) {
		launch(args);
	}
}
