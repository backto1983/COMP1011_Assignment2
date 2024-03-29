package comp1011_assigment2;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Consumer;
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
 * This is the registrationViewController class; its variables are defined below
 * @author Henrique
 */
public class registrationViewController implements Initializable {
    
    @FXML private TextField emailTextField;
    @FXML private PasswordField pwdField;
    @FXML private PasswordField confirmPwdField;
    @FXML private Label errorMsgLabel;
    @FXML private Button submitBtn;
    @FXML private Button registeredBtn;

    /**
     * This method controls what happens when the submit button is pushed 
     * @param event
     * @throws SQLException
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */
    public void submitButtonPushed(ActionEvent event) throws SQLException, IOException, NoSuchAlgorithmException {
        
        if(!User.validEmail(emailTextField.getText())) {
            //errorMsgLabel.setText("Email must match the pattern email@email.com");            
            
            // Use the errorMsgLabel to show an error message using Consumer, which takes an argument but do not 
            // return anything    
            Consumer<String> display = (errorMsg) -> errorMsgLabel.setText(errorMsg);
            display.accept("Email must match the pattern email@email.com");
        }
        else {        
            if(pwdField.getText().isEmpty() )
                errorMsgLabel.setText("Please choose a password");
            else if(validPassword()) {   
                try {
                    User user = new User(emailTextField.getText(), pwdField.getText());

                    errorMsgLabel.setText(""); //Absence of error message in case object creation was successful

                    user.insertIntoDB();

                    SceneChanger sc = new SceneChanger();
                    sc.changeScenes(event, "logInView.fxml", "Log In"); 

                }
                catch(Exception e) {
                    errorMsgLabel.setText(e.getMessage());
                }
            }
        }
    }
    
    /**
     * This method defines requirements for a password to be considered valid
     * @return boolean
     */
    public boolean validPassword() {
        if (pwdField.getText().length() < 7) {
            //errorMsgLabel.setText("Passwords must have 7 characters or more");            
            Consumer<String> display = (errorMsg) -> errorMsgLabel.setText(errorMsg);
            display.accept("Passwords must have 7 characters or more");
            
            return false;
        }        
        else if(!pwdField.getText().equals(confirmPwdField.getText())) {
            errorMsgLabel.setText("Passwords must match");
            return false;
        }
        else 
            if (pwdField.getText().equals(confirmPwdField.getText())) {
                return true;
            }
            else           
                return false;      
    }
    
    /**
     * This method will change to the logInView without adding a new user
     * @param event
     * @throws java.io.IOException
     */
    public void registeredButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "logInView.fxml", "Log In");
    }
    
    /**
     * This method initializes the controller class
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorMsgLabel.setText("");
        this.submitBtn.setOnAction(event -> {
            try {
                submitButtonPushed(event);
            } 
            catch (SQLException | IOException | NoSuchAlgorithmException ex) {
                Logger.getLogger(registrationViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        this.registeredBtn.setOnAction(event -> {
            try {
                registeredButtonPushed(event);
            }
            catch(IOException ex) {
                Logger.getLogger(registrationViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });        
    }        
}
