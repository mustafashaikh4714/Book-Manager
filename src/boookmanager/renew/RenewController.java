
package boookmanager.renew;

import boookaager.database.DatabaseHandler;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mustafa
 */
public class RenewController implements Initializable {
    
    DatabaseHandler handler;
    @FXML
    private JFXTextField memberId;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField mobileNo;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField bookId;
    @FXML
    private JFXTextField BookName;
    @FXML
    private JFXTextField author;
    @FXML
    private JFXTextField publisher;
    @FXML
    private JFXTextField issuetime;
    @FXML
    private JFXTextField renewCount;
    @FXML
    private JFXTextField BookIdinput;

    Boolean isReadyForSubmittion= false;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       handler=DatabaseHandler.getInstance();
      
    }    
     
       @FXML
    void resetBook(ActionEvent event) {
        memberId.setText("");
        name.setText("");
        mobileNo.setText("");
        email.setText("");
        bookId.setText("");
        author.setText("");
        publisher.setText("");
        issuetime.setText("");
        renewCount.setText("");
        BookName.setText("");
        
    }

    @FXML
    private void loadBookInfo2(ActionEvent event) throws SQLException {
        isReadyForSubmittion= false;
        String id=BookIdinput.getText();
        String qu="SELECT * FROM ISSUE WHERE bookID= '"+id+"'";
        ResultSet rs=handler.execQuery(qu);
        while(rs.next()){
         bookId.setText(id);
         memberId.setText(rs.getString("memberID"));
         Timestamp issueTime=rs.getTimestamp("issueTime");
         issuetime.setText(issueTime.toString());
         renewCount.setText(rs.getString("renew_count"));
         }
        qu="SELECT * FROM BOOK WHERE id = '"+bookId.getText()+"'";
        ResultSet r1=handler.execQuery(qu);
        while(r1.next()){
           BookName.setText(r1.getString("title"));
           author.setText(r1.getString("author"));
           publisher.setText(r1.getString("publisher"));
        }
         qu="SELECT * FROM MEMBER WHERE id = '"+memberId.getText()+"'";
         r1=handler.execQuery(qu);
         while(r1.next()){
            name.setText(r1.getString("name"));
            mobileNo.setText(r1.getString("mobile"));
            email.setText(r1.getString("email"));
        }
         isReadyForSubmittion=true;
    }

    @FXML
    private void renewBook(ActionEvent event) {
                if(!isReadyForSubmittion){
             Alert alert= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Please select book to submit");
                    alert.showAndWait();
          return;
        }
        Alert alert= new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew operation");
         alert.setHeaderText(null);
         alert.setContentText("Are you sure  want to Renew the book");
          Optional<ButtonType> response= alert.showAndWait();
        if(response.get()==ButtonType.OK){  
        String ac="UPDATE ISSUE SET issueTime=CURRENT_TIMESTAMP, renew_count=renew_count+1 WHERE bookID= '"+BookIdinput.getText() +"'";
         if(handler.execAction(ac)){
           Alert alert1= new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success");
                     alert1.setHeaderText(null);
                    alert1.setContentText("Book Has been Renewed");
                    alert1.showAndWait();
         }else{
             Alert alert1= new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Failed");
                     alert1.setHeaderText(null);
                    alert1.setContentText("Book Renew Failed");
                    alert1.showAndWait();
         }
        }else{
            Alert alert1= new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Cancelled");
                     alert.setHeaderText(null);
                    alert1.setContentText("Book Renew Cancelled");
                    alert1.showAndWait();
            
        }
    }

    @FXML
    private void submitBook(ActionEvent event) {
        
        
       
        if(!isReadyForSubmittion){
             Alert alert= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Please select book to submit");
                    alert.showAndWait();
          return;
        }else
        {
            Alert alert= new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Submittion operation");
         alert.setHeaderText(null);
         alert.setContentText("Are you sure  want to Return the book");
          Optional<ButtonType> response= alert.showAndWait();
        if(response.get()==ButtonType.OK){  
        
        String id=BookIdinput.getText();
        String ac1="DELETE FROM ISSUE WHERE bookID = '"+id+"'";
        String ac2="UPDATE BOOK SET isAvail=TRUE WHERE id='"+id+"'";
        if(handler.execAction(ac1)&&handler.execAction(ac2))
        {
           Alert alert1= new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success");
                     alert1.setHeaderText(null);
                    alert1.setContentText("Book Has been Submitted");
                    alert1.showAndWait();
        }else
        {
         Alert alert1= new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Failed");
                     alert1.setHeaderText(null);
                    alert1.setContentText("Book submittion Failed");
                    alert1.showAndWait();
        }
        }else
        {
        Alert alert1= new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Cancelled");
                     alert.setHeaderText(null);
                    alert1.setContentText("Book submittion Cancelled");
                    alert1.showAndWait();
        }
        }
        
    }
        

    @FXML
    private void homeClicked(MouseEvent event) throws IOException {
        Node source =(Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow() ;
        Parent root = FXMLLoader.load(getClass().getResource("/bookmanager/main/main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
    
}
