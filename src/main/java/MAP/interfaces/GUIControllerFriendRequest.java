package MAP.interfaces;

import MAP.business.ServiceException;
import MAP.business.UserService;
import MAP.domain.Friendship;
import MAP.domain.User;
import MAP.repository.RepositoryException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.stream.Collectors;

public class GUIControllerFriendRequest {

    private User user;

    private UserService service;

    private GUIControllerUser controller;

    @FXML
    private Button AcceptButton;

    @FXML
    private Button CloseButton;

    @FXML
    private Button RefuseButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ListView<Friendship> friendRequestList;

    private ObservableList<Friendship> friendRequests = FXCollections.observableArrayList();

    void setData(User user, UserService service, GUIControllerUser guiController){
        this.user = user;
        this.service = service;
        this.controller = guiController;
        loadList();
    }

    void loadList(){
        List<Friendship> list = service.getFriendRequests().stream()
                .filter(x->x.getId().getE2().equals(user.getId()))
                .collect(Collectors.toList());
        friendRequests.setAll(list);
        friendRequestList.setItems(friendRequests);
    }


    @FXML
    void AcceptButton() {
        if(friendRequestList.getSelectionModel().getSelectedItem() != null){
            Friendship friendship = friendRequestList.getSelectionModel().getSelectedItem();
            try {
                errorLabel.setText("");
                service.acceptFriendship(service.findOneUser(friendship.getId().getE1()).getUsername(), service.findOneUser(friendship.getId().getE2()).getUsername());
                controller.loadList();
                loadList();
            } catch (RepositoryException | ServiceException e) {
                errorLabel.setText("Error: \n" + e.getMessage());
                System.out.println(e.getMessage());
            }
        }
        else{
            errorLabel.setText("Select a friendship to delete!");
        }
    }

    @FXML
    protected void RefuseButton() {

    }

    @FXML
    protected void CloseButton() {

    }
}
