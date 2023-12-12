package MAP.interfaces;

import MAP.business.ServiceException;
import MAP.business.UserService;

import MAP.domain.Friendship;
import MAP.domain.User;
import MAP.repository.RepositoryException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GUIController implements Initializable{
    private UserService service;

    @FXML
    private Button AddWindow;

    @FXML
    private Button DeleteButton;

    @FXML
    private Button UpdateWindow;

    private ObservableList<User> users = FXCollections.observableArrayList();

    private ObservableList<Friendship> friendships = FXCollections.observableArrayList();

    @FXML
    private TableView<User> tableUsers;

    @FXML
    private TableColumn<User, Long> tableIdUser;

    @FXML
    private TableColumn<User, String> tableUsername;

    @FXML
    private TableColumn<User, String> tableFirstName;

    @FXML
    private TableColumn<User, String> tableLastName;

    @FXML
    private TableView<Friendship> tableFriendships;

    @FXML
    private TableColumn<Friendship, String> tableUser1;

    @FXML
    private TableColumn<Friendship, String> tableUser2;

    @FXML
    private TableColumn<Friendship, Date> tableFriendsFrom;

    @FXML
    private Button AddFriendshipWindow;

    @FXML
    private Button DeleteFriendshipButton;

    @FXML
    private Label errorLabel;



    public void setService(UserService service){
        this.service = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        tableIdUser.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
//        tableUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
//        tableFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
//        tableLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
//        tableUsers.setItems(users);

    }

    public void loadTables(){
        tableIdUser.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableUsers.setItems(users);

        tableUser1.setCellValueFactory(friendship -> new SimpleStringProperty(service.findOneUser(((Friendship)friendship.getValue()).getId().getE1()).getUsername()));
        tableUser2.setCellValueFactory(friendship -> new SimpleStringProperty(service.findOneUser(((Friendship)friendship.getValue()).getId().getE2()).getUsername()));
        tableFriendsFrom.setCellValueFactory(new PropertyValueFactory<Friendship, Date>("friendsFrom"));
        tableFriendships.setItems(friendships);

        update();
    }

    public void update() {
        Iterable<User> usersList = service.getAll();
        users = FXCollections.observableArrayList(StreamSupport.stream(usersList.spliterator(), false).collect(Collectors.toList()));
        tableUsers.setItems(users);

        Iterable<Friendship> friendshipsList = service.getAllFriendships();
        friendships = FXCollections.observableArrayList(StreamSupport.stream(friendshipsList.spliterator(), false).collect(Collectors.toList()));
        tableFriendships.setItems(friendships);

    }

    @FXML
    protected void AddWindow(){
        errorLabel.setText("");
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerAddUser controller = fxmlLoader.getController();
            controller.setParams(service, this);


            Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
            Stage stage = new Stage();
            stage.setTitle("Add user");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            System.out.println("Error: \n" + e.getMessage());
        }
    }

    @FXML
    protected void DeleteButton() {
        if(tableUsers.getSelectionModel().getSelectedItem() != null){
            User user = tableUsers.getSelectionModel().getSelectedItem();
            try {
                errorLabel.setText("");
                service.removeUser(user.getUsername());
                update();
            } catch (RepositoryException | ServiceException e) {
                errorLabel.setText("Error: \n" + e.getMessage());
            }
        }
        else{
            errorLabel.setText("Select a user to delete!");
        }
    }
    @FXML
    protected void UpdateWindow() {
        if(tableUsers.getSelectionModel().getSelectedItem() != null){
            User user = tableUsers.getSelectionModel().getSelectedItem();
            try {
                errorLabel.setText("");

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UpdateWindow.fxml"));
                Parent root = fxmlLoader.load();

                GUIControllerUpdateUser controller = fxmlLoader.getController();
                controller.setUserService(service, this, user.getUsername());

                Scene scene = new Scene(root, root.prefWidth(-1),  root.prefHeight(-1));
                Stage stage = new Stage();
                stage.setTitle("Update user " + user.getUsername() + "!");
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                errorLabel.setText("Error: \n" + e.getMessage());
            }
        }
        else{
            errorLabel.setText("Select a user to delete!");
        }
    }

    @FXML
    protected void AddFriendshipWindow() {
        errorLabel.setText("");
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddFriendshipWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerAddFriendship controller = fxmlLoader.getController();
            controller.setParams(service, this);


            Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
            Stage stage = new Stage();
            stage.setTitle("Add friendship");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            System.out.println("Error: \n" + e.getMessage());
        }
    }

    @FXML
    protected void DeleteFriendshipButton() {
        if(tableFriendships.getSelectionModel().getSelectedItem() != null){
            Friendship friendship = tableFriendships.getSelectionModel().getSelectedItem();
            try {
                errorLabel.setText("");
                service.deleteFriendship(service.findOneUser(friendship.getId().getE1()).getUsername(), service.findOneUser(friendship.getId().getE2()).getUsername());
                update();
            } catch (RepositoryException | ServiceException e) {
                errorLabel.setText("Error: \n" + e.getMessage());
            }
        }
        else{
            errorLabel.setText("Select a friendship to delete!");
        }
    }
}