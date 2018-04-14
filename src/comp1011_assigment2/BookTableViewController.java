package comp1011_assigment2;

import java.io.File;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

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
    @FXML private ImageView imageView;
    @FXML private Button insertNewBookBtn;
    
    @FXML private BarChart<Book, Integer> barChart;
    @FXML private CategoryAxis genres;
    @FXML private NumberAxis numberOfBooks;
    private XYChart.Series booksPerGenreCount;
    
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
        getDataForGraph();
        
        barChart.getData().addAll(booksPerGenreCount);
        
        //Configure the table columns
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
                Logger.getLogger(registrationViewController.class.getName()).log(Level.SEVERE, null, ex);                
            }
        });
    }    

    private void getDataForGraph() {
        booksPerGenreCount.getData().add(new XYChart.Data("History", 2));
        booksPerGenreCount.getData().add(new XYChart.Data("Fantasy", 1));
        booksPerGenreCount.getData().add(new XYChart.Data("Crime", 3));
        booksPerGenreCount.getData().add(new XYChart.Data("Sci-Fi", 5));
        booksPerGenreCount.getData().add(new XYChart.Data("Political", 1));
        booksPerGenreCount.getData().add(new XYChart.Data("Adventure", 2));
    }
    /**
     * This method will switch to the insertNewBook scene when the button is pushed 
     * @param event
     * @throws java.io.IOException
     */
    public void newContactButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "NewContactView.fxml", "Create new Contact");
    }

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
}
