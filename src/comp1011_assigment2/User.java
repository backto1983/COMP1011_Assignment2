package comp1011_assigment2;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Henrique
 */
public class User {
    private int userID;
    private String email, password;
    private byte[] salt;
    
    public User(String email, String password) throws NoSuchAlgorithmException {
        setEmail(email);
        this.password = PasswordGenerator.getSHA512Pwd(password, salt);
        salt = PasswordGenerator.getSalt();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        if(userID >= 0)
            this.userID = userID;
        else
            throw new IllegalArgumentException("User ID must be equal or greather than 0");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        // In this regex, some restrictions were used to both username and domain parts of the email address:
        // 1) A-Z, a-z, dash(-), dot(.) and underscore(_) characters allowed
        // 2) 0-9 digits allowed 
        if(email.matches("^[A-Z0-9+_.-]+@[A-Z0-9.-]+$"))
            this.email = email;       
        else
            throw new IllegalArgumentException("Email must be in the pattern email@email.com");       
    }
    
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
            preparedStatement.setString(1, password);  
            preparedStatement.setString(2, email); 
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
