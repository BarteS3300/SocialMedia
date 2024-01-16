package MAP.interfaces;

import MAP.business.UserService;
import MAP.domain.Message;
import MAP.domain.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class GUIControllerMessage implements Initializable {

    private User user;

    private UserService service;

    private Long idUserMessages;

    private Long idMessageToReply;

    @FXML
    private Button NewMessage;

    @FXML
    private ListView<Message> messageList;

    private ObservableList<Message> messages = FXCollections.observableArrayList();

    @FXML
    private VBox viewMessagesFrom;

    @FXML
    private ListView<Message> messageWith;

    private ObservableList<Message> messagesWith = FXCollections.observableArrayList();

    @FXML
    private TextArea messageText;

    @FXML
    private Button messageButton;

    @FXML
    private Label messageToReply;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageList.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {

            @Override
            public ListCell<Message> call(ListView<Message> param) {
                ListCell<Message> cell = new ListCell<Message>() {

                    @Override
                    protected void updateItem(Message item, boolean empty) {
                        super.updateItem(item, empty);
                        String to = "";
                        if (item != null) {
                            if (item.getFrom().equals(user.getId())) {
                                for (Long id : item.getTo())
                                    to = to + service.findOneUser(id).getUsername() + ", ";
                                to = to.substring(0, to.length() - 2);
                                setText(to + "\n" + item.getData());
                            } else
                                setText(service.findOneUser(item.getFrom()).getUsername() + "\n" + item.getData());
                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });
        messageWith.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                ListCell<Message> cell = new ListCell<Message>() {

                    @Override
                    protected void updateItem(Message item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            if (item.getReply() != null) {
                                if (Objects.equals(service.findMessage(item.getReply()).getFrom(), user.getId()))
                                    setText(service.findOneUser(service.findMessage(item.getReply()).getFrom()).getUsername() + ":\n" + service.findMessage(item.getReply()).getMessage()
                                            + "\nYou replied:\n" + item.getMessage());
                                else {
                                    setText(service.findOneUser(service.findMessage(item.getReply()).getFrom()).getUsername() + ":\n" + service.findMessage(item.getReply()).getMessage()
                                            + "\n" + service.findOneUser(item.getFrom()).getUsername() + " replied:\n" + item.getMessage());
                                }
                            } else {
                                if (Objects.equals(item.getFrom(), user.getId()))
                                    setText("You:\n" + item.getMessage());
                                else {
                                    setText(service.findOneUser(item.getFrom()).getUsername() + ":\n" + item.getMessage());
                                }
                            }
                        }
                        else {
                            setText("");
                        }
                    }
                };
                ContextMenu cm = new ContextMenu();
                MenuItem reply = new MenuItem("Reply");
                reply.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        messageButton.setText("Reply");
                        messageToReply.setFont(new javafx.scene.text.Font("System", 14));
                        messageToReply.setMinHeight(50);
                        messageToReply.setText("Replying to " + service.findOneUser(cell.getItem().getFrom()).getUsername() + ":\n"+ cell.getItem().getMessage());
                        idMessageToReply = cell.getItem().getId();
                    }
                });
                MenuItem edit = new MenuItem("Edit");
                edit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        messageButton.setText("Edit");
                        messageToReply.setFont(new javafx.scene.text.Font("System", 14));
                        messageToReply.setMinHeight(50);
                        messageToReply.setText("Edit message from " + service.findOneUser(cell.getItem().getFrom()).getUsername() + " to ");
                        for(Long id : service.findMessage(cell.getItem().getId()).getTo())
                            messageToReply.setText(messageToReply.getText() + service.findOneUser(id).getUsername() + ", ");
                        messageToReply.setText(messageToReply.getText().substring(0, messageToReply.getText().length() - 2) + "\n"+ cell.getItem().getMessage());
                        idMessageToReply = cell.getItem().getId();
                    }
                });
                MenuItem delete = new MenuItem("Delete");
                cm.getItems().addAll(reply, edit, delete);
                cell.setContextMenu(cm);
                return cell;
            }
        });

        viewMessagesFrom.setVisible(false);

        messageText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {
                messageButton.setDisable(messageText.getText().isEmpty());
            }
        });
    }

    public void setParams(User user, UserService service) {
        this.user = user;
        this.service = service;
        loadList();
    }

    public void loadList() {
        messages.setAll(service.oneMessagePerUser(user));
        messageList.setItems(messages);
    }

    @FXML
    void NewMessage(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("NewMessageWindow.fxml"));
            Parent root = fxmlLoader.load();

            GUIControllerNewMessage controller = fxmlLoader.getController();
            controller.setParams(user, service, this);

            Scene scene = new Scene(root, root.prefWidth(1), root.prefHeight(1));
            Stage stage = new Stage();
            stage.setTitle("New Message");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void openMessage(MouseEvent event) {
        if (messageList.getSelectionModel().getSelectedItem() != null) {
            Message mes = messageList.getSelectionModel().getSelectedItem();
            if(Objects.equals(mes.getFrom(), user.getId()))
                idUserMessages = mes.getTo().get(0);
            else
                idUserMessages = mes.getFrom();
            messagesWith.setAll(service.getMessagesWith(user, (!Objects.equals(mes.getFrom(), user.getId()) ? mes.getFrom() : mes.getTo().get(0))));
            messageWith.setItems(messagesWith);
            if (!messagesWith.isEmpty())
                messageWith.scrollTo(messagesWith.size() - 1);
            viewMessagesFrom.setVisible(true);

        }
    }

    void updateMassages(){
        messagesWith.setAll(service.getMessagesWith(user, idUserMessages));
        messageWith.setItems(messagesWith);
        if (!messagesWith.isEmpty())
            messageWith.scrollTo(messagesWith.size() - 1);
    }

    @FXML
    void sendButton(ActionEvent event) {
        List<User> toUser = Arrays.asList(service.findOneUser(idUserMessages));
        if(Objects.equals(messageButton.getText(), "Reply")) {
            service.replyMessage(user, toUser, messageText.getText(), idMessageToReply);
            messageToReply.setText("");
            messageToReply.setMinHeight(10);
            messageToReply.setFont(new javafx.scene.text.Font("System", 10));
            messageButton.setText("Send");
            messageText.setText("");
        }
        if(Objects.equals(messageButton.getText(), "Edit")){
            service.updateMessage(idMessageToReply, messageText.getText());
            messageToReply.setText("");
            messageToReply.setMinHeight(10);
            messageToReply.setFont(new javafx.scene.text.Font("System", 10));
            messageButton.setText("Send");
            messageText.setText("");
        }
        else if(Objects.equals(messageButton.getText(), "Send")){
            service.sendMessage(user, toUser  , messageText.getText());
            updateMassages();
            messageText.setText("");
        }
    }
}
