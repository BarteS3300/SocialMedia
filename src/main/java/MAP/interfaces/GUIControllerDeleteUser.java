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

public class GUIControllerDeleteUser {
    private UserService userService;

    @FXML
    private TextField username;

    @FXML
    private Button DeleteButton;

    @FXML
    private Button CancelButton;

    @FXML
    private Label error;

    public void setUserService(UserService service){
        this.userService = service;
    }

    @FXML
    protected void DeleteButton() {
        try {
            userService.removeUser(username.getText());
            userService.notifyObservers();
            Stage stage = (Stage) DeleteButton.getScene().getWindow();
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
