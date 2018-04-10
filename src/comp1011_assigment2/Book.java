package comp1011_assigment2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Henrique
 */
public class Book {
    private int bookID, year;
    private String title, author, genre;

    public Book(int year, String title, String author, String genre) {
        setYear(year);
        setTitle(title);
        setAuthor(author);
        setGenre(genre);
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        if(bookID >= 0)        
            this.bookID = bookID;
        else
            throw new IllegalArgumentException("Book ID must be equal or greather than 0");
    }    

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if(year >= 1900 || year <= 2018)        
            this.year = year;
        else
            throw new IllegalArgumentException("This library only accepts books that were published since 1900");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (!title.isEmpty())
            this.title = title;
        else
            throw new IllegalArgumentException("Title cannot be empty");        
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (!author.isEmpty())
            this.author = author;
        else
            throw new IllegalArgumentException("Author cannot be empty");       
    }
    
    public static List<String> getGenres()
    {
      String[] genres = {"Adventure", "Biography", "Business", "Crime", "Fantasy" , "Health", "History", "Humour", 
          "Political", "Romance", "Sci-Fi", "Self-Help", "Sports", "Technology", "Terror"};
      return Arrays.asList(genres);
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if(getGenres().contains(genre))        
            this.genre = genre;
        else
            throw new IllegalArgumentException("Valid genres are: " + getGenres());
    }
    
    public void insertIntoDB() throws SQLException {
        Connection conn = null;
        
        PreparedStatement preparedStatement = null;
        
        try {
            //1. Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es:3306/gc200358165", "gc200358165", "FBNs7TjT");
            //2. Create a string that holds the query with "?" as user inputs
            String sql = "INSERT INTO books (title, author, genre, publishingYear)" + "VALUES(?,?,?,?)";
            //3. Prepare the query
            preparedStatement = conn.prepareStatement(sql);
            
            //4. Bind the values to the parameters
            preparedStatement.setString(1, title);  
            preparedStatement.setString(2, author); 
            preparedStatement.setString(3, genre); 
            preparedStatement.setInt(4, year);
            
            preparedStatement.executeUpdate();
        }
        
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        
        finally {
            if(preparedStatement != null)
                preparedStatement.close();
            
            if(conn != null)
                conn.close();
        }
    }
}
