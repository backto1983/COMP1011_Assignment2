package comp1011_assigment2;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This is the User class; its variables are defined below
 * @author Henrique
 */
public class User {
    private int userID;
    private String email, password;
    private byte[] salt;
    
    /**
     * This is the class constructors
     * @param email
     * @param password
     * @throws NoSuchAlgorithmException 
     */
    public User(String email, String password) throws NoSuchAlgorithmException {
        setEmail(email);
        salt = PasswordGenerator.getSalt();
        this.password = PasswordGenerator.getSHA512Pwd(password, salt);        
    }

    /**
     * This method gets the userID variable
     * @return userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * This method sets the userID variable
     * @param userID 
     */
    public void setUserID(int userID) {
        if(userID >= 0)
            this.userID = userID;
        else
            throw new IllegalArgumentException("User ID must be equal or greather than 0");
    }

    /**
     * This method gets the email variable
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method sets the email variable
     * @param email 
     */
    public void setEmail(String email) {        
        if(validEmail(email))
            this.email = email;
    }    
    
    /**
     * 
     * @param email
     * @return 
     */
    public static boolean validEmail(String email) {
        // In this regex, some restrictions were used to both username and domain parts of the email address:
        // 1) A-Z, a-z, dash(-), dot(.) and underscore(_) characters are allowed
        // 2) 0-9 digits are allowed 
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    
    /**
     * This method will write the instance of a user into the database
     * @throws java.sql.SQLException
     */
    public void insertIntoDB() throws SQLException {
        Connection conn = null;
        
        PreparedStatement preparedStatement = null;
        
        try {
            //1. Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es:3306/gc200358165", "gc200358165", "FBNs7TjT");
            //2. Create a string that holds the query with "?" as user inputs
            String sql = "INSERT INTO registeredUsers (email, password, salt)" + "VALUES(?,?,?)";
            //3. Prepare the query
            preparedStatement = conn.prepareStatement(sql);
            
            //4. Bind the values to the parameters
            preparedStatement.setString(1, email);  
            preparedStatement.setString(2, password); 
            preparedStatement.setBlob(3, new javax.sql.rowset.serial.SerialBlob(salt));
            
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
