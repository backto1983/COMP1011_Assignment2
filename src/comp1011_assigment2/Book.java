package comp1011_assigment2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * This is the Book class; its variables are defined below
 * @author Henrique
 */
public class Book {
    private int bookID, publishingYear;
    private String title, author, genre;
    private File imageFile;

    /**
     * This is one of the class constructors; it uses the default Book image
     * @param year
     * @param title
     * @param author
     * @param genre 
     */
    public Book(String title, String author, String genre, int year) {        
        setTitle(title);
        setAuthor(author);
        setGenre(genre);
        setYear(year);
        setImageFile(new File("./scr/images/default.png"));
    }
    
    /**
     * This is another constructor for the Book class; it uses a new image uploaded when the book is inserted
     * by the user
     * @param year
     * @param title
     * @param author
     * @param genre
     * @param imageFile
     * @throws IOException 
     */
    public Book(String title, String author, String genre, int year, File imageFile) throws IOException {
        this(title, author, genre, year);
        setImageFile(imageFile);
        copyImageFile();
    }

    /**
     * This method gets the bookID variable
     * @return bookID
     */
    public int getBookID() {
        return bookID;
    }

    /**
     * This method sets the bookID variable
     * @param bookID 
     */
    public void setBookID(int bookID) {
        if(bookID >= 0)        
            this.bookID = bookID;
        else
            throw new IllegalArgumentException("Book ID must be equal or greather than 0");
    }    

    /**
     * This method gets the year variable
     * @return year
     */
    public int getYear() {
        return publishingYear;
    }

    /**
     * This method sets the year variable
     * @param year 
     */
    public void setYear(int year) {
        if(year >= 1900 || year <= 2018)        
            this.publishingYear = year;
        else
            throw new IllegalArgumentException("This library only accepts books that were published since 1900");
    }

    /**
     * This method gets the title variable
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method sets the title variable
     * @param title 
     */
    public void setTitle(String title) {
        if (!title.isEmpty())
            this.title = title;
        else
            throw new IllegalArgumentException("Title cannot be empty");        
    }

    /**
     * This method gets the author variable
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * This method sets the author variable
     * @param author 
     */
    public void setAuthor(String author) {
        if (!author.isEmpty())
            this.author = author;
        else
            throw new IllegalArgumentException("Author cannot be empty");       
    }
    
    /**
     * This method will return a List of a valid book genres
     * @return genres
     */
    public static List<String> getGenres()
    {
      String[] genres = {"Adventure", "Biography", "Business", "Crime", "Fantasy" , "Health", "History", "Humour", 
          "Political", "Romance", "Sci-Fi", "Self-Help", "Sports", "Technology", "Terror"};
      return Arrays.asList(genres);
    }

    /**
     * This method gets the genre variable
     * @return genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * This method sets the genre variable
     * @param genre 
     */
    public void setGenre(String genre) {
        if(getGenres().contains(genre))        
            this.genre = genre;
        else
            throw new IllegalArgumentException("Valid genres are: " + getGenres());
    }    

    /**
     * This method gets the imageFile variable
     * @return imageFile
     */
    public File getImageFile() {
        return imageFile;
    }    

    /**
     * This method sets the imageFile variable
     * @param imageFile 
     */
    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
    
    /**
     * This method will copy the file specified to the images directory on this server and give it a unique name
     * @throws java.io.IOException
     */
    public void copyImageFile() throws IOException {
        //Create a new path to copy the image into a local directory
        Path sourcePath = imageFile.toPath();
        
        String uniqueFileName = getUniqueFileName(imageFile.getName());
        
        Path targetPath = Paths.get("./src/images/" + uniqueFileName);
        
        //Copy the file to the new directory
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        //Update the imageFile to point to the new file 
        imageFile = new File(targetPath.toString());
    }
    
    /**
     * This method will receive a String that represents a file name and return a String with a random, unique set of
     * letters prefixed to it
     * @param oldFileName
     * @return newName
     */
    public String getUniqueFileName(String oldFileName) {
        String newName;
        
        //Create a random number generator
        SecureRandom randomNumber = new SecureRandom();
        
        //Loop until we have a unique file name
        do {
            newName = "";
            
            //Generate 32 random characters
            for(int i = 1; i <= 32; i++) {
                int nextChar;
                
                do {
                    nextChar = randomNumber.nextInt(123);
                } while(!validCharacterValue(nextChar));
            
                newName = String.format("%s%c", newName, nextChar); 
            }          
            newName += oldFileName; 
        } while(!uniqueFileInDirectory(newName));
        
        return newName;
    }
    
    /**
     * This method will validate if the integer given corresponds to a valid ASCII character that could be used in a
     * file name
     * @param asciiValue
     * @return boolean
     */
    public boolean validCharacterValue(int asciiValue) {

        //0-9 = ASCII range 48 to 57
        if(asciiValue >= 48 && asciiValue <= 57)
            return true;

        //A-Z  = ASCII range 65 to 90
        if(asciiValue >= 65 && asciiValue <= 90)
            return true;

        //a-Z  = ASCII range 97 to 122
        if(asciiValue >= 97 && asciiValue <= 122)
            return true;

        return false; 
    }
    
    /**
     * This method searches the images directory and ensure that the file name is unique
     * @param fileName
     * @return boolean
     */
    public boolean uniqueFileInDirectory(String fileName) {
        File directory = new File("./src/images/");
        
        File[] directoryContents = directory.listFiles();
        
        for(File file : directoryContents) {        
        
            if(file.getName().equals(fileName))
                return false;            
        }       
        return true;      
    }
    
    /**
     * This method will write the instance of a book into the database
     * @throws java.sql.SQLException
     */
    public void insertIntoDB() throws SQLException {
        Connection conn = null;
        
        PreparedStatement preparedStatement = null;
        
        try {
            //1. Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es:3306/gc200358165", "gc200358165", "FBNs7TjT");
            //2. Create a string that holds the query with "?" as user inputs
            String sql = "INSERT INTO books (title, author, genre, publishingYear, imageFile)" + "VALUES(?,?,?,?,?)";
            //3. Prepare the query
            preparedStatement = conn.prepareStatement(sql);
            
            //4. Bind the values to the parameters
            preparedStatement.setString(1, title);  
            preparedStatement.setString(2, author); 
            preparedStatement.setString(3, genre); 
            preparedStatement.setInt(4, publishingYear);
            preparedStatement.setString(5, imageFile.getName());
            
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
