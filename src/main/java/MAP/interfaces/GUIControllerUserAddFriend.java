package MAP.interfaces;

import MAP.business.UserService;
import MAP.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GUIControllerUserAddFriend {

    private User user;

    private UserService service;

    private GUIControllerUser controller;

    @FXML
    private Button AddButton;

    @FXML
    private Button CancelButton;

    @FXML
    private Label error;

    @FXML
    private TextField username;

    public void setParams(User user, UserService service, GUIControllerUser controller){
        this.user = user;
        this.service = service;
        this.controller = controller;
    }

    @FXML
    void AddButton() {
        try {
            service.requestFriendship(user.getUsername(), username.getText());
            Stage stage = (Stage) AddButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            error.setText("Error reading input: \n" + e.getMessage());
        }
    }

    @FXML
    void CancelButton() {
        Stage stage = (Stage) CancelButton.getScene().getWindow();
        stage.close();
    }

}