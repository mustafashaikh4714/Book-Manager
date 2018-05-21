
package boookmanager.listmembers;

import boookaager.database.DatabaseHandler;
import boookmanager.addbook.AddBookController;
import boookmanager.addmember.AddmemberController;
import boookmanager.listbook.BooklistController;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author mustafa
 */
public class ListmemberController implements Initializable {
    DatabaseHandler handler;
    ObservableList<Member> list = FXCollections.observableArrayList();
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;
    @FXML
    private TextField search;
 FilteredList filter= new FilteredList(list, e->true);
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        handler= DatabaseHandler.getInstance();
        try {
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(ListmemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
        private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
    }
        private void loadData() throws SQLException {
            list.clear();
        String qu = "SELECT * FROM MEMBER";
        ResultSet rs = handler.execQuery(qu);
       while(rs.next()){
           try {
               String name= rs.getString("name");
               String id= rs.getString("id");
               String mobile= rs.getString("mobile");
               String email= rs.getString("email");
               
               
               list.addAll(new Member(name,id,mobile,email));
               
           } catch (SQLException ex) {
               Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
      tableView.getItems().setAll(list);
}

    @FXML
    private void search(MouseEvent event) {
          search.textProperty().addListener((observable, oldValue, newValue) -> {
    filter.setPredicate((Predicate<? super Member>)(Member member)->{
        
          if(newValue.isEmpty()||newValue==null)
          return true;
          
          else if(member.getId().contains(newValue))
                return true;
               
            return false;
            });
    });
        SortedList sort = new SortedList(filter);
        sort.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sort);
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
    private void hadleMemberDelete(ActionEvent event) {
        Member selectedForDeletion= tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion==null)
        {
         Alert alert= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Memeber Delete Failed");
                    alert.showAndWait();
                    return;
        }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting Member");
        alert.setContentText("Are you sure want to delete the member " + selectedForDeletion.getName() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteMember(selectedForDeletion);
            if (result) {
               Alert alert1= new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                     alert.setHeaderText(null);
                    alert.setContentText("Member Deleted Successfully");
                    alert.showAndWait();
                list.remove(selectedForDeletion);
            } else {
               Alert alert1= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Could not Delete the member");
                    alert.showAndWait();
            }
        } else {
          Alert alert1= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Member Deletion Cancelled");
                    alert.showAndWait();
        }
    }

    @FXML
    private void hadleMemberEdit(ActionEvent event) throws IOException {
        Member selectedForUpdate= tableView.getSelectionModel().getSelectedItem();
        if(selectedForUpdate==null)
        {
         Alert alert= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("No Member is selected Please select the member");
                    alert.showAndWait();
                    return;
        }
      FXMLLoader loader= new FXMLLoader(getClass().getResource("/boookmanager/addmember/addmember.fxml"));
      Parent parent=loader.load();
      AddmemberController controller=(AddmemberController) loader.getController();
      controller.passinfo(selectedForUpdate);
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setTitle("Update Member");
      stage.setScene(new Scene(parent));
      stage.show();
      
      stage.setOnCloseRequest((e)->{
            try {
                loadData();
            } catch (SQLException ex) {
                Logger.getLogger(BooklistController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @FXML
    private void handleRefresh(ActionEvent event) throws SQLException {
          loadData();
    }
     public static class Member {
    private final SimpleStringProperty name;
    private final SimpleStringProperty id;
    private final SimpleStringProperty mobile;
    private final SimpleStringProperty email;
   
    
    public Member(String name, String id, String mobile, String email) {
        this.name= new SimpleStringProperty(name);
        this.id= new SimpleStringProperty(id);
        this.mobile= new SimpleStringProperty(mobile);
        this.email= new SimpleStringProperty(email);
       
    }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }

    
 }
}
    
    
    
    
