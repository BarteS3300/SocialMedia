package MAP.interfaces;

import MAP.business.UserService;
import MAP.domain.Message;
import MAP.domain.User;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIControllerMessage implements Initializable {

    private User user;

    private UserService service;

    @FXML
    private Button NewMessage;

    @FXML
    private Label message;

    @FXML
    private ListView<Message> messageList;

    private ObservableList<Message> messages = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageList.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {

            @Override
            public ListCell<Message> call(ListView<Message> param){
                ListCell<Message> cell = new ListCell<Message>(){

                    @Override
                    protected void updateItem(Message item, boolean empty){
                        super.updateItem(item, empty);
                        String to = "";
                        if(item != null){
                            if(item.getFrom().equals(user.getId())) {
                                for (Long id : item.getTo())
                                    to = to + service.findOneUser(id).getUsername() + ", ";
                                to = to.substring(0,    to.length() - 2);
                                setText(to + "\n" + item.getData());
                            }
                            else
                                setText(service.findOneUser(item.getFrom()).getUsername() + "\n" + item.getData());
                        }
                        else{
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });
    }

    public void setParams(User user, UserService service){
        this.user = user;
        this.service = service;
        loadList();
    }

    public void loadList(){
        messages.setAll(service.oneMessagePerUser(user));
        messageList.setItems(messages);
    }

    @FXML
    void NewMessage(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("NewMessageWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerNewMessage controller = fxmlLoader.getController();
            controller.setParams(user, service, this);

            Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
            Stage stage = new Stage();
            stage.setTitle("New Message");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void openMessage(MouseEvent event) {
    if(messageList.getSelectionModel().getSelectedItem() != null){
            Message mes = messageList.getSelectionModel().getSelectedItem();
            message.setText("Message from " + mes.getFrom() + ":\n" + mes.getMessage() + "\n\nSent at " + mes.getData());
        }
    }

}
