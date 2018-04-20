package comp1011_assigment2;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * This is the logInViewController class; its variables are defined below
 * @author Henrique
 */
public class LogInViewController implements Initializable {
    
    @FXML private TextField emailTextField;
    @FXML private PasswordField pwdField;
    @FXML private Label errorMsgLabel;
    @FXML private Button logInButton;
    
    /**
     * This method controls what happens when the log in button is pushed 
     * @param event 
     * @throws java.sql.SQLException 
     * @throws java.security.NoSuchAlgorithmException 
     * @throws java.io.IOException 
     */
    public void logInButtonPushed(ActionEvent event) throws SQLException, NoSuchAlgorithmException, IOException {
        if(!User.validEmail(emailTextField.getText())) 
            errorMsgLabel.setText("Please type your email");
        
        else {        
            if(pwdField.getText().isEmpty() )
                errorMsgLabel.setText("Please enter your password");
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        
        String userEmail = emailTextField.getText();
        
        try {
            conn = DriverManager.getConnection("jdbc:mysql://sql.computerstudi.es:3306/gc200358165", "gc200358165", "FBNs7TjT");

            String sql = "SELECT * FROM registeredUsers WHERE email = ?";
            
            ps = conn.prepareStatement(sql);

            ps.setString(1, userEmail);
            
            resultSet = ps.executeQuery();
            
            String dbPassword = null;
            byte[] salt = null;
            User user = null;
            
            while (resultSet.next()) {
                dbPassword = resultSet.getString("password");
                
                Blob blob = resultSet.getBlob("salt");
                
                int blobLength = (int) blob.length();
                salt = blob.getBytes(1, blobLength);                
                
                user = new User(resultSet.getString("email"), resultSet.getString("password"));
                
                user.setUserID(resultSet.getInt("userID"));
            }
            
            String userPW = PasswordGenerator.getSHA512Pwd(pwdField.getText(), salt);

            if (userPW.equals(dbPassword)) {
                SceneChanger sc = new SceneChanger();
                sc.changeScenes(event, "bookTableView.fxml", "Book Table View");
            }                
            else
                errorMsgLabel.setText("Password is not correct, please try again");
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }        
    }

    /**
     * This method initializes the controller class
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorMsgLabel.setText("");
        
        this.logInButton.setOnAction(event -> {
            try {
                logInButtonPushed(event);
            } 
            catch (SQLException | IOException | NoSuchAlgorithmException ex) {
                Logger.getLogger(LogInViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });       
    }   
}
