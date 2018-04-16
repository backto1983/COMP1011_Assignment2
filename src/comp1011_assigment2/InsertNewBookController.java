package comp1011_assigment2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author Henrique
 */
public class InsertNewBookController implements Initializable {
    
    @FXML private TextField titleTextField;
    @FXML private TextField authorTextField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private TextField yearTextField;
    @FXML private ImageView imageView;
    @FXML private Button changeCoverBtn;
    @FXML private Label errorMsgLabel;
    @FXML private Button saveBookBtn;
    @FXML private Button cancelBtn;
    
    private File imageFile;
    private boolean imageFileChanged;
    
    /**
     * When the "Change Cover" button is pushed, a FileChooser object is launched to allow the user to browse for a
     * new image file; after a new image is picked, the view is updated
     * @param event 
     */
    public void chooseImageButtonPushed(ActionEvent event) {
        // Get the stage to open a new window 
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow(); 
        
        // Instantitate a FileChooser object 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        
        // Set a filter for .jpg and .png files
        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("Image file (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("Image file (*.png)", "*.png");
        
        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter);
        
        // Set to the user's picture directory or user home directory if not available
        String userDirectoryString = System.getProperty("user.home") + "\\Pictures";
        File userDirectory = new File(userDirectoryString);
        
        if(!userDirectory.canRead())
            userDirectory = new File(System.getProperty("user.home"));
        
        fileChooser.setInitialDirectory(userDirectory);
        
        // Open the file dialog window
        File tmpImageFile = fileChooser.showOpenDialog(stage);
        
        if(tmpImageFile != null) {            
            imageFile = tmpImageFile;
            
            // Update the ImageView with a new image
            if(imageFile.isFile()) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(imageFile);
                    Image img = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageView.setImage(img);
                    imageFileChanged = true;
                }
                catch(IOException e) {
                    System.err.println(e.getMessage()); 
                }
            } 
        }                      
    }
    
    /**
     * This method will change back to the bookTableView without adding an user; all data in the form will be lost
     * when the "Cancel" button is pushed
     * @param event
     * @throws java.io.IOException
     */
    public void cancelButtonPushed(ActionEvent event) throws IOException {
        SceneChanger sc = new SceneChanger();
        sc.changeScenes(event, "bookTableView.fxml", "Books Table");
    }
    
    /**
     * This method will read from the scene and try to create a new instance of a Book; it will also store it
     * in the database
     * @param event
     */
    public void saveBookButtonPushed(ActionEvent event) {
        try {
            Book book;
            
            if(imageFileChanged)       
                book = new Book(titleTextField.getText(), authorTextField.getText(), genreComboBox.getValue(), 
                                Integer.parseInt(yearTextField.getText()), imageFile);

            else 
                book = new Book(titleTextField.getText(), authorTextField.getText(), genreComboBox.getValue(), 
                                Integer.parseInt(yearTextField.getText())); 

            errorMsgLabel.setText(""); // Absence of error message in case object creation was successful
            
            book.insertIntoDB();
            
            SceneChanger sc = new SceneChanger();
            sc.changeScenes(event, "bookTableView.fxml", "Books Table");
            
        }
        catch(Exception e) {
            errorMsgLabel.setText(e.getMessage());
        }       
    }

    /**
     * This method initializes the controller class
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageFileChanged = false; // Initially the image has not changed; use the default
        errorMsgLabel.setText(""); // Set the error message to be empty
        
        // Setup genreComboBox
        genreComboBox.getItems().addAll(Book.getGenres());
        genreComboBox.getSelectionModel().selectFirst();
        
        // Load the default image
        try {
            imageFile = new File("./src/comp1011_assigment2/images/default.png");
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);         
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
         
        this.changeCoverBtn.setOnAction(event -> {chooseImageButtonPushed(event);});
        
        this.saveBookBtn.setOnAction(event -> {saveBookButtonPushed(event);});
        
        this.cancelBtn.setOnAction(event -> {
            try {
                cancelButtonPushed(event);
            } 
            catch (IOException ex) {
                Logger.getLogger(InsertNewBookController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }   
}
