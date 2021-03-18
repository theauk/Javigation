package bfst21.view;

import bfst21.ControllerForDrawing;
import bfst21.Loader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class ViewDrawing {
    public ViewDrawing(Loader loader, Stage stage) throws IOException, XMLStreamException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/drawing.fxml"));
        Scene scene = fxmlLoader.load();
        stage.setScene(scene);
        stage.setTitle("Working Title...");

        ControllerForDrawing controller = fxmlLoader.getController();
        stage.show();
        controller.init(loader);
    }
}
