package comp1011_assigment2;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author Henrique
 */
public class BookTableViewController implements Initializable {    
    
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private TableColumn<Book, Integer> yearColumn;
    @FXML private Button insertNewBookBtn;
    //@FXML private ImageView imageView;
    
    @FXML private BarChart<Book, Integer> barChart;
    @FXML private CategoryAxis genres;
    @FXML private NumberAxis numberOfBooks;
    private XYChart.Series booksPerGenreCount;

    @FXML private TextField searchField;
    ObservableList<Book> books = FXCollections.observableArrayList();
    
    /**
     * This method will change to the insertNewBookView
     * @param event
     * @throws java.io.IOException
     */
    public void insertNewBookButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "insertNewBookView.fxml", "Insert New Book");
    }

    /**
     * This method initializes the controller class
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the chart series
        booksPerGenreCount = new XYChart.Series<>();        
        
        // Add labels to the chart
        genres.setLabel("Genres");
        numberOfBooks.setLabel("Number of books");
        
        // Add data to the graphs
        try{
            getDataForGraph();
        }
        catch (SQLException e)
        {
            System.err.println(e);
        }

        barChart.getData().addAll(booksPerGenreCount);
        
        // Configure the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publishingYear"));
        
        try {
            loadBooks();
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        
        // When the insertNewBookBtn button is clicked, the setOnAction method will receive an event from the
        // EventHandler functional interface, which allows for lambda commands
        this.insertNewBookBtn.setOnAction(event -> {
            try {
                insertNewBookButtonPushed(event);
            }
            catch(IOException ex) {
                Logger.getLogger(InsertNewBookController.class.getName()).log(Level.SEVERE, null, ex);                
            }
        });
    }    

    /**
     * This method gets data from the db to create a bar graph
     * @throws SQLException 
     */
    private void getDataForGraph() throws SQLException {
        
        Connection conn=null;
        Statement statement=null;
        ResultSet resultSet=null;
        
        try {
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es:3306/gc200358165", "gc200358165", "FBNs7TjT");
        
            statement = conn.createStatement();

            String sql = "SELECT genre, COUNT(title) FROM books GROUP BY genre";            

            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                booksPerGenreCount.getData().add(new XYChart.Data(resultSet.getString(1), resultSet.getInt(2))); 
            }  
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        finally {
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
            if (resultSet != null)
                resultSet.close();
        }  
    }
    
    /**
     * This method will switch to the insertNewBookView scene when the button is pushed 
     * @param event
     * @throws java.io.IOException
     */
    public void newBookButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "insertNewBookView.fxml", "Insert new Book");
    }

    /**
     * This method populates a table with books from the db
     * @throws SQLException 
     */
    private void loadBooks() throws SQLException {        
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es:3306/gc200358165", "gc200358165", "FBNs7TjT");
            
            //Create a statement object
            statement = conn.createStatement();

            //Create the SQL query
            resultSet = statement.executeQuery("SELECT * FROM books");

            //Create contact objects from each record
            while(resultSet.next()) {
                Book book = new Book(resultSet.getString("title"), 
                                     resultSet.getString("author"), 
                                     resultSet.getString("genre"), 
                                     resultSet.getInt("publishingYear"));

                book.setBookID(resultSet.getInt("bookID"));
                //book.setImageFile(new File(resultSet.getString("imageFile")));
                
                booksTable.getItems().add(book);
            }
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        finally {
            if(conn != null)
                conn.close();
            
            if(statement != null)
                statement.close();
                
            if(resultSet != null)
                resultSet.close();
        }        
    }  
    
    /**
     * This method allows to use a TextField to search for books contained in the table 
     * @param event
     */
    public void searchBooks (KeyEvent event) {
        ObservableList completeList =  booksTable.getItems();
                
        searchField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            
            if (oldValue != null && (newValue.length() < oldValue.length())) 
                booksTable.setItems(completeList);
            
            String value = newValue.toLowerCase();
            ObservableList<Book> subentries = FXCollections.observableArrayList();

            long count = booksTable.getColumns().stream().count();
            
            for (int i = 0; i < booksTable.getItems().size(); i++) {
                for (int j = 0; j < count; j++) {
                    String entry = "" + booksTable.getColumns().get(j).getCellData(i);
                    if (entry.toLowerCase().contains(value)) {
                        subentries.add(booksTable.getItems().get(i));
                        break;
                    }
                }
            }
            booksTable.setItems(subentries);
        });
    }         
}

