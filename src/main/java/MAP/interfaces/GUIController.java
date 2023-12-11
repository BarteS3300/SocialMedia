package MAP.interfaces;

import MAP.business.ServiceException;
import MAP.business.UserService;

import MAP.domain.User;
import MAP.observer.Observer;
import MAP.repository.RepositoryException;
import MAP.validators.ValidationException;
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
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GUIController implements Initializable, Observer {
    private UserService userService;

    @FXML
    private Button AddWindow;

    @FXML
    private Button DeleteWindow;

    @FXML
    private Button UpdateWindow;

    private ObservableList<User> users = FXCollections.observableArrayList();

    @FXML
    private TableView<User> getAll;

    @FXML
    private TableColumn<User, Long> tableID;

    @FXML
    private TableColumn<User, String> tableUsername;

    @FXML
    private TableColumn<User, String> tableFirstName;

    @FXML
    private TableColumn<User, String> tableLastName;

    public void setUserService(UserService service){
        this.userService = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableID.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        getAll.setItems(users);
    }

    @Override
    public void update() {
        Iterable<User> usersList = userService.getAll();
        users = FXCollections.observableArrayList(StreamSupport.stream(usersList.spliterator(), false).collect(Collectors.toList()));
        getAll.setItems(users);
    }

    @FXML
    protected void AddWindow(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerAddUser controller = fxmlLoader.getController();
            controller.setUserService(userService);


            Scene scene = new Scene(root, 420, 240);
            Stage stage = new Stage();
            stage.setTitle("Add user");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            System.out.println("Error: \n" + e.getMessage());
        }
    }

    @FXML
    protected void DeleteWindow() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DeleteWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerDeleteUser controller = fxmlLoader.getController();
            controller.setUserService(userService);

            Scene scene = new Scene(root, 420, 240);
            Stage stage = new Stage();
            stage.setTitle("Delete user");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            System.out.println("Error: \n" + e.getMessage());
        }
    }
    @FXML
    protected void UpdateWindow() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UpdateWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerUpdateUser controller = fxmlLoader.getController();
            controller.setUserService(userService);


            Scene scene = new Scene(root, 420, 240);
            Stage stage = new Stage();
            stage.setTitle("Update user");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            System.out.println("Error: \n" + e.getMessage());
        }
    }
}