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

public class GUIControllerAddUser {
    private UserService service;

    private GUIControllerAdmin guiController;

    @FXML
    private TextField username;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private Button AddButton;

    @FXML
    private Button CancelButton;

    @FXML
    private Label error;

    public void setParams(UserService service, GUIControllerAdmin controller){
        this.service = service;
        this.guiController = controller;
    }

    @FXML
    protected void AddButton() {
        try {
            service.saveUser(username.getText(), firstName.getText(), lastName.getText());
            guiController.update();
            Stage stage = (Stage) AddButton.getScene().getWindow();
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
