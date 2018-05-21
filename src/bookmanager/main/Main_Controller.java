
package bookmanager.main;

import boookaager.database.DatabaseHandler;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * FXML Controller class
 *
 * @author mustafa
 */
public class Main_Controller implements Initializable {
   private DatabaseHandler handler;
    @FXML
    private Label message;
    @FXML
    private JFXTextField bid;
    @FXML
    private JFXTextField mid;
    @FXML
    private Text bookName;
    @FXML
    private Text bAuthor;
    @FXML
    private Text bStatus;
    @FXML
    private Text mName;
    @FXML
    private Text mMobile;
    @FXML
    private AnchorPane pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       handler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void loadAddMember(ActionEvent event) throws IOException {
        loadWindow("/boookmanager/addmember/addmember.fxml","Add Member Window");
    }

    @FXML
    private void loadAddBook(ActionEvent event) throws IOException {
         loadWindow("/boookmanager/addbook/FXMLDocument.fxml","Add Book Window");
    }

    @FXML
    private void loadShowMember(ActionEvent event) throws IOException {
         loadWindow("/boookmanager/listbook/book_list.fxml","Show Book Window");
    }

    @FXML
    private void loadShowBooks(ActionEvent event) throws IOException {
         loadWindow("/boookmanager/listmembers/listmember.fxml","Show Member Window");
    }

    @FXML
    private void loadSettings(ActionEvent event) throws IOException {
      loadWindow("/bookmanger/settings/settings.fxml","Setting Window");
    }
    
    private void loadWindow(String loc, String title) throws IOException{
      AnchorPane pane=FXMLLoader.load(getClass().getResource(loc));
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setTitle(title);
      stage.setScene(new Scene(pane));
      stage.show();
    }

    @FXML
    private void laodBookInfo(ActionEvent event) throws SQLException {
        clearBookCache();
        String id = bid.getText();
        String qu= "SELECT * FROM BOOK WHERE id = '"+id +"'";
        ResultSet rs = handler.execQuery(qu);
        Boolean flag=false;
        while(rs.next()){
            String bName=rs.getString("title");
            String Author=rs.getString("author");
            Boolean Status=rs.getBoolean("isAvail");
            
             bookName.setText(bName);
             bAuthor.setText(Author);
             String status=(Status)?"Available" : "Not Available";
             bStatus.setText(status);
            flag=true;
        }
                if(!flag)
                    bookName.setText("No such book is Available");
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) throws SQLException {
        clearMemberCache();
        String Mid=mid.getText();
        String qu="SELECT * FROM MEMBER WHERE id='"+Mid +"'";
         ResultSet rs = handler.execQuery(qu);
         Boolean flag=false;
         while(rs.next()){
           String mname= rs.getString("name");
           String mmobile= rs.getString("mobile");
           
           mName.setText(mname);
           mMobile.setText(mmobile);
           flag=true;
         } 
             if(!flag)
                 mName.setText("No such member is Available");
    }
    
    void clearBookCache() {
        bookName.setText("");
        bAuthor.setText("");
        bStatus.setText("");
    }

    void clearMemberCache() {
        mName.setText("");
        mMobile.setText("");
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        
        String mID= mid.getText();
        String bID=bid.getText();
        Alert alert= new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm issue operation");
         alert.setHeaderText(null);
        alert.setContentText("Are you sure  want to issue book " + bookName.getText()+ " to \n the " +mName.getText());
        Optional<ButtonType> response= alert.showAndWait();
        if(response.get()==ButtonType.OK){                               //must specified
           String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ("     
                    + "'" + mID + "',"
                    + "'" + bID + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bID + "'";
            System.out.println(str + " and " + str2);
             if (handler.execAction(str) && handler.execAction(str2)){
                   Alert alert1= new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success");
                     alert1.setHeaderText(null);
                    alert1.setContentText("Book Issue complete");
                    alert1.showAndWait();
       
             }else
             {
             Alert alert1= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Book Issue Failed");
                    alert.showAndWait();
             }
        }else
        {
         Alert alert1= new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Cancelled");
                     alert.setHeaderText(null);
                    alert.setContentText("Book Issue Cancelled");
                    alert.showAndWait();
        }
       bookName.setText("");
        bAuthor.setText("");
        bStatus.setText("");
         mName.setText("");
        mMobile.setText("");
    }

    @FXML
    private void loadRenew(MouseEvent event) throws IOException {
         Node source =(Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow() ;
        Parent root = FXMLLoader.load(getClass().getResource("/boookmanager/renew/renew.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
       
    }
}
