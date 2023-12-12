package MAP.interfaces;


import MAP.business.ServiceException;
import MAP.business.UserService;
import MAP.domain.User;
import MAP.repository.FriendshipDBRepository;
import MAP.repository.RepositoryException;
import MAP.repository.UserDBRepository;
import MAP.validators.FriendshipValidator;
import MAP.validators.UserValidation;
import MAP.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.AuthProvider;
import java.util.Objects;

public class GUIControllerLogin {

    private UserService service;

    @FXML
    private Button LoginButton;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label error;

    void setService(UserService service){
        this.service = service;
    }

    @FXML
    protected void LoginButton() {
        try{
            User user = service.login(username.getText(), password.getText());
            Stage stage = (Stage) LoginButton.getScene().getWindow();
            stage.close();

            if(Objects.equals(user.getRole(), "admin")){
                FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("GUI.fxml"));
                Parent root = fxmlLoader.load();

                GUIControllerAdmin controller = fxmlLoader.getController();
                controller.setService(service);
                controller.loadTables();

                Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
                stage.setTitle("User CRUD!");
                stage.setScene(scene);
                stage.show();
            }

            if(Objects.equals(user.getRole(), "user")){
                FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("GUIUser.fxml"));
                Parent root = fxmlLoader.load();

                GUIControllerUser controller = fxmlLoader.getController();
                controller.setData(user, service);
//                controller.loadTables();

                Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
                stage.setTitle(user.getUsername());
                stage.setScene(scene);
                stage.show();
            }
        } catch (ValidationException | IllegalArgumentException | ServiceException | RepositoryException e) {
            error.setText("Error reading input: \n" + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
