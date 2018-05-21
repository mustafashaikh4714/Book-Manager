/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boookmanager.listbook;

import boookaager.database.DatabaseHandler;
import boookmanager.addbook.AddBookController;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author mustafa
 */
public class BooklistController implements Initializable {
    
    DatabaseHandler handler;
    ObservableList<Book> list = FXCollections.observableArrayList();
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> nameCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, Boolean> availibilityCol;
    
    FilteredList filter= new FilteredList(list, e->true);
    @FXML
    private TextField search;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        handler= DatabaseHandler.getInstance();
        try {
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(BooklistController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availibilityCol.setCellValueFactory(new PropertyValueFactory<>("availibility"));
    }

    private void loadData() throws SQLException {
        list.clear();
        String qu = "SELECT * FROM BOOK";
        ResultSet rs = handler.execQuery(qu);
       while(rs.next()){
           try {
               String titlex= rs.getString("title");
               String id= rs.getString("id");
               String author= rs.getString("author");
               String publisher= rs.getString("publisher");
               Boolean avail= rs.getBoolean("isAvail");
               
               list.addAll(new Book(titlex,id,author,publisher,avail));
               
           } catch (SQLException ex) {
               Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
      tableView.getItems().setAll(list);
}

    @FXML
    private void search(KeyEvent event) {
        search.textProperty().addListener((observable, oldValue, newValue) -> {
       filter.setPredicate((Predicate<? super Book>)(Book book)->{
        
          if(newValue.isEmpty()||newValue==null)
          return true;
          
          else if(book.getId().contains(newValue))
                return true;
               
            return false;
            });
    });
        SortedList sort = new SortedList(filter);
        sort.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sort);
        
    }

    @FXML
    private void handleBookDelete(ActionEvent event) {
        Book selectedForDeletion= tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion==null)
        {
         Alert alert= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Book Delete Failed");
                    alert.showAndWait();
                    return;
        }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting book");
        alert.setContentText("Are you sure want to delete the book " + selectedForDeletion.getTitle() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Boolean result = DatabaseHandler.getInstance().deleteBook(selectedForDeletion);
            if (result) {
               Alert alert1= new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                     alert.setHeaderText(null);
                    alert.setContentText("Book Deleted Successfully");
                    alert.showAndWait();
                list.remove(selectedForDeletion);
            } else {
               Alert alert1= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Could not Delete the book");
                    alert.showAndWait();
            }
        } else {
          Alert alert1= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Book Deletion Cancelled");
                    alert.showAndWait();
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

    @FXML
    private void handleBookUpdate(ActionEvent event) throws IOException {
        Book selectedForUpdate= tableView.getSelectionModel().getSelectedItem();
        if(selectedForUpdate==null)
        {
         Alert alert= new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                     alert.setHeaderText(null);
                    alert.setContentText("Book Update Failed");
                    alert.showAndWait();
                    return;
        }
      FXMLLoader loader= new FXMLLoader(getClass().getResource("/boookmanager/addbook/FXMLDocument.fxml"));
      Parent parent=loader.load();
      AddBookController controller=(AddBookController) loader.getController();
      controller.passObject(selectedForUpdate);
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setTitle("Update Book");
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
    

 public static class Book {
    private final SimpleStringProperty title;
    private final SimpleStringProperty id;
    private final SimpleStringProperty author;
    private final SimpleStringProperty publisher;
    private final SimpleBooleanProperty availibility;
    
    public Book(String title, String id, String au, String pub, Boolean avail) {
        this.title= new SimpleStringProperty(title);
        this.id= new SimpleStringProperty(id);
        this.author= new SimpleStringProperty(au);
        this.publisher= new SimpleStringProperty(pub);
        this.availibility= new SimpleBooleanProperty(avail);
    }

    public String getTitle() {
        return title.get();
    }

    public String getId() {
        return id.get();
    }

    public String getAuthor() {
        return author.get();
    }

    public String getPublisher() {
        return publisher.get();
    }

    public Boolean getAvailibility() {
        return availibility.get();
    }
 }
}