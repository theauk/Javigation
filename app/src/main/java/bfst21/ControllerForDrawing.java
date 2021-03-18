package bfst21;

import bfst21.view.BasicDrawing;
import bfst21.view.MapCanvas;
import bfst21.view.MapSegment;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

public class ControllerForDrawing {
    private MapSegment mapSegment;
    private Point2D currentMouse;
    private Point2D lastMouse;
    private Loader osmLoader;

    @FXML
    private BasicDrawing drawing;

    public void init(Loader osmloader) throws IOException, XMLStreamException {
        this.osmLoader = osmloader;
        drawing.init(osmloader);
    }

    @FXML
    public void onScroll(ScrollEvent e) {
        double factor = Math.pow(1.01, e.getDeltaY());
        drawing.zoom(factor, new Point2D(e.getX(), e.getY()));
    }

    @FXML
    public void onMouseDragged(MouseEvent e) {
        double dx = e.getX() - lastMouse.getX();
        double dy = e.getY() - lastMouse.getY();

        drawing.setCursor(Cursor.CLOSED_HAND);

        if (e.isPrimaryButtonDown()) drawing.pan(dx, dy);

        currentMouse = new Point2D(e.getX(), e.getY());
        onMousePressed(e);
    }

    @FXML
    public void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
        currentMouse = new Point2D(e.getX(), e.getY());
    }

}
