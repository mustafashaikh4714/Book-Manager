
package boookmanager.login;

import bookmanger.settings.Preferences;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;
//boookmanager.login.LoginController path
/**
 * FXML Controller class
 *
 * @author mustafa
 */
public class LoginController implements Initializable {
    @FXML
    private JFXTextField username;
    @FXML
    private JFXButton loginButton;
    @FXML
    private JFXPasswordField password;
    Preferences preference;
    @FXML
    private AnchorPane pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference=Preferences.getPreferences();
    }    

    @FXML
    private void loginButtonClicked(ActionEvent event) throws IOException {
        String uname=username.getText();
        String pword=DigestUtils.shaHex(password.getText());
        
        if(uname.equals(preference.getUsername())&& pword.equals(preference.getPassword()))
        {
           closestage();
           loadMain();
        }else
        {
          Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Invalid Content");
        alert.showAndWait();
        }
    }

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        System.exit(0);
    }

    private void closestage() {
        ((Stage)username.getScene().getWindow()).close();
    }
      void loadMain() throws IOException{
      AnchorPane pane=FXMLLoader.load(getClass().getResource("/bookmanager/main/main.fxml"));
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setTitle("Main Window");
      stage.setScene(new Scene(pane));
      stage.show();
    }
}
