package MAP.interfaces;

import MAP.business.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("GUI.fxml"));
        fxmlLoader.setController(GUIController.getInstance());
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("User CRUD!");
        stage.setScene(scene);
        stage.show();
    }

    public static void launch(String[] args) {
        launch();
    }
}