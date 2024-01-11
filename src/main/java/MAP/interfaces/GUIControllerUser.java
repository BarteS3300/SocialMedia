package MAP.interfaces;

import MAP.business.UserService;
import MAP.domain.Friendship;
import MAP.domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GUIControllerUser {

    private User user;

    private UserService service;

    private ObservableList<Friendship> friends = FXCollections.observableArrayList();

    @FXML
    private Button AddFriend;

    @FXML
    private Button FriendRequest;

    @FXML
    private Button Messages;

    @FXML
    private Label hi;

    @FXML
    private ListView<Friendship> friendsList;

    void setData(User user, UserService service){
        this.user = user;
        this.service = service;
        hi.setText("Hello " + user.getFirstName() + " " + user.getLastName() + "!");
        loadList();
    }

    void loadList(){
        List<Friendship> list = StreamSupport.stream(service.getAllFriendships().spliterator(), false).filter(friendship->(friendship.getId().getE1().equals(user.getId()) || friendship.getId().getE2().equals(user.getId())) && Objects.equals(friendship.getStatus(), "accepted")).collect(Collectors.toList());
        friends.setAll(list);
        friendsList.setItems(friends);
    }

    @FXML
    void AddFriend() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("UserAddFriend.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerUserAddFriend controller = fxmlLoader.getController();
            controller.setParams(user, service, this);

            Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
            Stage stage = new Stage();
            stage.setTitle("Add friend");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void FriendRequest() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("FriendRequestWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerFriendRequest controller = fxmlLoader.getController();
            controller.setData(user, service, this);

            Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
            Stage stage = new Stage();
            stage.setTitle("Friend Requests");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void Messages() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("MessageWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerMessage controller = fxmlLoader.getController();
            controller.setParams(user, service);

            Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
            Stage stage = new Stage();
            stage.setTitle("Messages");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}