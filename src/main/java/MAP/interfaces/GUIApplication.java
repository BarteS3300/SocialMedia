package MAP.interfaces;

import MAP.business.UserService;
import MAP.repository.FriendshipDBRepository;
import MAP.repository.UserDBRepository;
import MAP.validators.FriendshipValidator;
import MAP.validators.UserValidation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        UserValidation userValidation = new UserValidation();
        FriendshipValidator friendshipValidation = new FriendshipValidator();
        //InMemoryRepository<Long, User> repo = new InMemoryRepository<>(validation);
        UserDBRepository repoUsers = new UserDBRepository(userValidation, "jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        FriendshipDBRepository repoFriendships = new FriendshipDBRepository(friendshipValidation, "jdbc:postgresql://localhost:5432/SocialNetwork_v2", "postgres", "postgres");
        UserService service = new UserService(repoUsers, repoFriendships);

        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("Login.fxml"));
        Parent root = fxmlLoader.load();

        GUIControllerLogin controller = fxmlLoader.getController();
        controller.setService(service);

        Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}