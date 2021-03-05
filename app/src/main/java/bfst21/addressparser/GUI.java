package bfst21.addressparser;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {

    private static TextField input;
    private static TextArea output;
    private Label title, dropDownLabel;
    private BorderPane pane;
    private Button addressButton;
    private static ComboBox<String> dropDown;
    private static ObservableList<String> dropDownList;

    private String font = "Times New Roman";
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller.setUp();
        setUp();

        pane.setTop(title);
        pane.setCenter(getCenterPane());
        primaryStage.setScene(new Scene(pane));
        primaryStage.setTitle("Advanced Address Alignment Advertiser 2.0");
        primaryStage.show();
    }

    public void setUp() {
        dropDownList = FXCollections.observableArrayList();
        input = new TextField();
        output = new TextArea();
        pane = new BorderPane();
        title = new Label("Address Alignment Advertiser");
        addressButton = new Button("Create address");
        dropDown = new ComboBox<String>();
        dropDown.setItems(dropDownList);
        dropDownLabel = new Label("Previous addresses:");

        layout();
        actions();

    }

    public static String getInput() {
        return input.getText();
    }

    public void actions() {
        addressButton.setOnAction(e -> {
            updateOutputText(Controller.createAddressClicked());
        });

        dropDown.setOnAction(e -> {
            updateOutputText(Controller.getCreatedAdressedClicked());
        });
    }

    public static void updateCreatedAddresses(String address) {
        dropDownList.add(address);       
    }
    
    private static void updateOutputText(String text) {
        output.setText(text);
    }

    public static int getCreatedAddress() {
        return dropDown.getSelectionModel().getSelectedIndex();
    }

    private void layout() {
        // fonts
        input.setFont(new Font(font, 25));
        output.setFont(new Font(font, 25));
        output.setMaxHeight(300);
        dropDownLabel.setFont(new Font(font, 15));
        title.setFont(new Font(font, 30));

        dropDown.setPromptText("Address");
        dropDown.setStyle("-fx-font: 18px \"Times New Roman\";");
        addressButton.setStyle("-fx-font: 18px \"Times New Roman\";");

        pane.setMargin(title, new Insets(20, 100, 20, 100));
    }

    private HBox getCenterPane(){
        
        var centerPane = new HBox();
        var left = new VBox();
        var right = new VBox();

        left.getChildren().addAll(input, output);
        left.setSpacing(20);
        left.setMaxWidth(500);
        
        right.getChildren().addAll(addressButton, dropDownLabel, dropDown);
        right.setMargin(addressButton, new Insets(0,0,22,0));
        right.setMinWidth(300);

        centerPane.setMargin(left, new Insets(20 ,20,20,40));
        centerPane.setMargin(right, new Insets(20 ,20,20,40));
        centerPane.setSpacing(10);
        centerPane.getChildren().addAll(left, right);
        return centerPane;
    }
}
