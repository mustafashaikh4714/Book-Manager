
package boookmanager.addbook;

import boookaager.database.DatabaseHandler;
import boookmanager.listbook.BooklistController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mustafa
 */
public class AddBookController implements Initializable {
    
    DatabaseHandler databasehandler;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField publisher;
    @FXML
    private JFXTextField author;
    @FXML
    private ImageView home;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private AnchorPane rootPane;
    private Boolean isInEditMode=false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databasehandler= DatabaseHandler.getInstance();
        
//        try {
//            checkData();
//        } catch (SQLException ex) {
//            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
//        }
       
    }    

    @FXML
    private void homeClicked(MouseEvent event) throws IOException {
        Node source =(Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow() ;
        Parent root = FXMLLoader.load(getClass().getResource("/bookmanager/main/main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    @FXML
    private void addbook(ActionEvent event) throws SQLException {
       String bookId=id.getText();
       String bookName=name.getText();
       String bookPublisher=publisher.getText();
       String bookAuthor=author.getText();
       
       if(bookId.isEmpty()||bookName.isEmpty()||bookPublisher.isEmpty()||bookAuthor.isEmpty())
       {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Please fill All the fields");
        alert.showAndWait();
        return;                    //operation will be aborted And return to Addbook.
       
       }  
        if(isInEditMode)
        {
         handleEditOperation();
         return;
        }
//        stmt.execute("CREATE TABLE " + TABLE_NAME + "("
//                        + "	id varchar(200) primary key,\n"
//                        + "	title varchar(200),\n"
//                        + "	author varchar(200),\n"
//                        + "	publisher varchar(100),\n"
//                        + "	isAvail boolean default true"
//                        + " )");
       
       //inserting the data into the table.
       
       String qu="INSERT INTO BOOK VALUES("+
               "'"+bookId  +"',"+
               "'"+bookName  +"',"+
               "'"+bookAuthor  +"',"+
               "'"+bookPublisher  +"',"+
               ""+ true  +""+
               ")";
        System.out.println(qu);
        if(databasehandler.execAction(qu)==true)
        {
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Success");
        alert.showAndWait();
        }else
        {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Failed");
        alert.showAndWait();
        }
       
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    
    public void passObject(BooklistController.Book book)
    {
      name.setText(book.getTitle());
      id.setText(book.getId());
      publisher.setText(book.getPublisher());
      author.setText(book.getAuthor());
      id.setEditable(false);
      isInEditMode=true;
    }

        // Retrieving the information from table.
    
//    private void checkData() throws SQLException {
//       String qu = "SELECT title FROM BOOK";
//       ResultSet rs = databasehandler.execQuery(qu);
//       while(rs.next()){
//           try {
//               String titlex= rs.getString("title");
//               System.out.println(titlex);
//               
//           } catch (SQLException ex) {
//               Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
//           }
//       }
//    }

    private void handleEditOperation() throws SQLException {
        
       BooklistController.Book book=new BooklistController.Book(name.getText(),id.getText(),author.getText(),publisher.getText(),true);
       if(databasehandler.updateBook(book))
               {
                 Alert alert= new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                     alert.setHeaderText(null);
                    alert.setContentText("Book Updated Successfully");
                    alert.showAndWait();
               }else
       {
       Alert alert= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Can't Update Book");
                    alert.showAndWait();
       }
    }
    
}
