package MAP.interfaces;

import MAP.business.UserService;
import MAP.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GUIControllerUser {

    private User user;

    private UserService service;

    @FXML
    private Button FriendRequest;

    @FXML
    private Button Message;

    @FXML
    private Label hi;

    void setData(User user, UserService service){
        this.user = user;
        this.service = service;
        hi.setText("Hello " + user.getFirstName() + " " + user.getLastName() + "!");
    }

    @FXML
    void FriendRequest() {

    }

    @FXML
    void Message() {

    }

}