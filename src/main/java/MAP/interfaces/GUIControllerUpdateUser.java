package MAP.interfaces;

import MAP.business.ServiceException;
import MAP.business.UserService;
import MAP.repository.RepositoryException;
import MAP.validators.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GUIControllerUpdateUser {
    private UserService userService;

    @FXML
    private TextField username;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField new_username;

    @FXML
    private TextField new_firstName;

    @FXML
    private TextField new_lastName;

    @FXML
    private Button UpdateButton;

    private Button CancelButton;

    @FXML
    private Label error;

    public void setUserService(UserService service){
        this.userService = service;
    }

    @FXML
    protected void UpdateButton(){
        try {
            userService.updateUser(username.getText(), new_username.getText(), new_firstName.getText(), new_lastName.getText());
            userService.notifyObservers();
            Stage stage = (Stage) UpdateButton.getScene().getWindow();
            stage.close();
        } catch (ValidationException | IllegalArgumentException | ServiceException | RepositoryException e) {
            error.setText("Error reading input: \n" + e.getMessage());
        }
    }

    @FXML
    protected void CancelButton(){
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        stage.close();
    }

}
