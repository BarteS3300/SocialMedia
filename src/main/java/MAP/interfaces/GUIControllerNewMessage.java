package MAP.interfaces;

import MAP.business.UserService;
import MAP.domain.Tuple;
import MAP.domain.User;
import com.dlsc.formsfx.view.controls.SimpleCheckBoxControl;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.SocketOption;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GUIControllerNewMessage implements Initializable {

    private User user;

    private UserService service;

    private GUIControllerMessage controller;

    @FXML
    private Button Send;

    @FXML
    private TextArea messageLabel;

    @FXML
    private Label to;

    private ArrayList<User> toUsers = new ArrayList<>();

    private ObservableList<Tuple<User, CheckBox>> usersCheck = FXCollections.observableArrayList();

    @FXML
    private TableView<Tuple<User, CheckBox>> usersTable;


    @FXML
    private TableColumn<Tuple<User, CheckBox>, String> usernameColumn;

    @FXML
    private TableColumn<Tuple<User, CheckBox>, CheckBox> checkboxColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameColumn.setCellValueFactory(cell -> new SimpleStringProperty(((User)((Tuple<User, CheckBox>)cell.getValue()).getE1()).getUsername()));
        checkboxColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(((Tuple<User, CheckBox>)cell.getValue()).getE2()));
//        checkboxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkboxColumn));
        usersTable.setItems(usersCheck);

    }

    void setParams(User user, UserService service, GUIControllerMessage controller){
        this.user = user;
        this.service = service;
        this.controller = controller;
        updateList();

    }

    public void updateList(){
        usersCheck = FXCollections.observableArrayList(service.getAllCheck());
        usersTable.setItems(usersCheck);
    }

    @FXML
    void Send() {
        service.sendMessage(user, toUsers, messageLabel.getText());
        Stage stage = (Stage) Send.getScene().getWindow();
        stage.close();
    }

    @FXML
    void toUser(){
        if(usersTable.getSelectionModel().getSelectedItem() != null){
            Tuple<User, CheckBox> row = usersTable.getSelectionModel().getSelectedItem();
            if( !row.getE2().isSelected()){
                row.getE2().setSelected(true);
                usersTable.refresh();
                to.setText(to.getText() + ((User)row.getE1()).getUsername() + " ");
                toUsers.add(((User)row.getE1()));
            }
            else{
                row.getE2().setSelected(false);
                usersTable.refresh();
                to.setText(to.getText().replace(((User)row.getE1()).getUsername() + " ", ""));
                toUsers.remove(((User)row.getE1()));
            }
        }
    }
}