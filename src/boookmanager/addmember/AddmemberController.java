
package boookmanager.addmember;

import boookaager.database.DatabaseHandler;
import boookmanager.listbook.BooklistController;
import boookmanager.listmembers.ListmemberController;
import boookmanager.listmembers.ListmemberController.Member;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mustafa
 */
public class AddmemberController implements Initializable {
    DatabaseHandler handler;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField mobile;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    
    private boolean isInEditMode=false;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler= DatabaseHandler.getInstance();
    }    

    @FXML
    private void addmember(ActionEvent event) {
        
        String mName= name.getText();
        String mId= id.getText();
        String mMobile= mobile.getText();
        String mEmail= email.getText();
        
       if(mName.isEmpty()||mId.isEmpty()||mMobile.isEmpty()||mEmail.isEmpty())
       {
                 Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Please fill All the fields");
        alert.showAndWait();
        return;
       }
        if(isInEditMode)
        {
            try {
                handleUpdateMember();
            } catch (SQLException ex) {
                Logger.getLogger(AddmemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
       
       String st="INSERT INTO MEMBER VALUES("+
               "'"+mId  +"',"+
               "'"+mName  +"',"+
               "'"+mMobile  +"',"+
               "'"+mEmail  +"'"+
               ")";
       System.out.println(st);
       if(handler.execAction(st)){
                 Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Success");
        alert.showAndWait();
       }else
       {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Error Occured");
        alert.showAndWait();
       }
       
       
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    public void passinfo(ListmemberController.Member member)
    {
      id.setText(member.getId());
      name.setText(member.getName());
      mobile.setText(member.getMobile());
      email.setText(member.getEmail());
      id.setEditable(false);
      isInEditMode=true;
      
    }

    private void handleUpdateMember() throws SQLException {
        
           Member member=new ListmemberController.Member(name.getText(),id.getText(),mobile.getText(),email.getText());
       if(handler.updateMember(member))
               {
                 Alert alert= new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                     alert.setHeaderText(null);
                    alert.setContentText("Member Updated Successfully");
                    alert.showAndWait();
               }else
       {
       Alert alert= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Can't Update Member");
                    alert.showAndWait();
       }
    }
}
