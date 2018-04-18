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
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
        try {
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
        
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es:3306/gc200358165", "gc200358165", "FBNs7TjT");
        
            statement = conn.createStatement();

            String sql = "SELECT genre, COUNT(title) FROM books GROUP BY genre";            

            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) 
                booksPerGenreCount.getData().add(new XYChart.Data(resultSet.getString(1), resultSet.getInt(2))); 
             
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

            //Create book objects from each record
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
     */
    public void searchBooks() {        
        books = booksTable.getItems();   
        
        booksTable.getItems().stream()
             // sorted() do I need to sort?
            .filter((Book title) -> title.getTitle().contains(searchField.getText())) // or .equals()
            .filter((Book author) -> author.getAuthor().contains(searchField.getText()))
            .filter((Book genre) -> genre.getGenre().contains(searchField.getText()))                
            .forEach(Book -> books.add(Book)); 
        
            //.forEach(Book -> booksTable.setItems(books)); 
            
        /*
        books = booksTable.getItems();
        
        // The code below was adapted from the following source http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
                
        // Wrap the ObservableList (books) in a FilteredList, so all books are initially displayed
        FilteredList<Book> filteredList = new FilteredList<>(books, b -> true);
        
        // Use the searchField to filter results using Predicate, which takes a book as an argument and returns a 
        // boolean; if it is true, display filtered books based on title, author and genre, if there is no match, 
        // return false (table should not show any results)       
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(book -> {
                // If filter text is empty, display all books
                if (newValue == null || newValue.isEmpty()) 
                    return true;                
                
                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) 
                    return true; // Filter matches title
                
                else if (book.getAuthor().toLowerCase().contains(lowerCaseFilter)) 
                    return true; // Filter matches author 
                
                else if (book.getGenre().toLowerCase().contains(lowerCaseFilter)) 
                    return true; // Filter matches genre
                
                return false;
            });
        });
        // Wrap the FilteredList in a SortedList
        SortedList<Book> sortedList = new SortedList<>(filteredList);

        // Bind the SortedList comparator to the TableView comparator
        sortedList.comparatorProperty().bind(booksTable.comparatorProperty());

        // Add sorted and filtered data to the table
        booksTable.setItems(sortedList);

        
        /*
        arrayList.stream()
                 .peek(name -> System.out.printf("%nname before filter: %s", name))
                 .filter(name -> name.substring(0, 1).equals("J"))
                 .peek(name -> System.out.printf(" name after filter: %s%n", name))
                 .forEach(name -> System.out.println());        
        */
    }         
}

