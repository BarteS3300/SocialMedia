package MAP.interfaces;

import MAP.business.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class GUIController {

    static private GUIController instance = null;
    private UserService userService;

    @FXML
    private TextField username;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField welcomeText;

    private GUIController(){
    }

    public static GUIController getInstance(){
        if(instance == null)
            instance = new GUIController();
        return instance;
    }

    public void setService(UserService userService){
        this.userService = userService;
    }

    @FXML
    protected void AddWindow(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddWindow.fxml"));
            fxmlLoader.setController(GUIController.getInstance());
            Scene scene = new Scene(fxmlLoader.load(), 420, 240);
            Stage stage = new Stage();
            stage.setTitle("User CRUD!");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            System.out.println("Error: \n" + e.getMessage());
        }
    }

    @FXML
    protected void DeleteWindow() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void UpdateWindow() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void FindOneWindow() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void GetAllWindow() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void AddButton(){
        if(username == null || firstName == null || lastName == null)
            welcomeText.setText("Error: \n" + "One or more fields are empty!");
        else
            welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void CancelButton(){
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}