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

public class GUIControllerAddFriendship {
    private UserService service;

    private GUIController guiController;

    @FXML
    private TextField username1;

    @FXML
    private TextField username2;

    @FXML
    private Button AddButton;

    @FXML
    private Button CancelButton;

    @FXML
    private Label error;

    public void setParams(UserService service, GUIController controller){
        this.service = service;
        this.guiController = controller;
    }

    @FXML
    protected void AddButton() {
        try {
            service.addFriendship(username1.getText(), username2.getText());
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
