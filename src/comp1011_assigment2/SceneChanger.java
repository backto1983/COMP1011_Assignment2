package comp1011_assigment2;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class has a method that allows change between scenes
 * @author Henrique
 */
public class SceneChanger {
    
    /**
     * This method will accept the title of the new scene and the .fxml file name for the view and the ActionEvent
     * that triggered the change
     * @param event
     * @param viewName
     * @param title
     * @throws java.io.IOException
     */
    public void changeScenes(ActionEvent event, String viewName, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(viewName));
        Parent parent = loader.load();
        
        Scene scene = new Scene(parent);
        
        //Get the stage from the event that was passed in
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();        
    }
}
