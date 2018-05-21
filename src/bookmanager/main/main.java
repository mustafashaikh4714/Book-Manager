
package bookmanager.main;

import boookaager.database.DatabaseHandler;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author mustafa
 */
public class main extends Application {
    
    @Override
    public void start(Stage mystage) throws Exception {
        
    Parent root = FXMLLoader.load(getClass().getResource("/boookmanager/login/login.fxml"));
        
        Scene scene = new Scene(root);
      
        mystage.setScene(scene);
        mystage.show();
      
         new Thread(() -> {
            DatabaseHandler.getInstance();
        }).start();
    }

   
    public static void main(String[] args) {
        launch(args);
    }
    
}
