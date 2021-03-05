package bfst21.vector;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class View {
   
    public View(Model model, Stage stage) throws IOException {
        var loader = new FXMLLoader(View.class.getResource("View.fxml"));
        Scene scene = loader.load();
        stage.setScene(scene);
        stage.setTitle("Public transport inner Copenhagen");
        Controller controller = loader.getController();
        stage.show();
        controller.init(model);}
}
