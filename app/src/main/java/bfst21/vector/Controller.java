package bfst21.vector;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class Controller {
	private Model model;
    private Point2D lastMouse;

    @FXML
    private MapCanvas canvas;

    public void init(Model model) {
        this.model = model;
        canvas.init(model);
	}

    @FXML
    private void onScroll(ScrollEvent e) {
        double factor = Math.pow(1.01, e.getDeltaY());
        canvas.zoom(factor, new Point2D(e.getX(), e.getY()));
    }

    @FXML
    private void onMouseDragged(MouseEvent e) {
        double dx = e.getX() - lastMouse.getX();
        double dy = e.getY() - lastMouse.getY();
        if (e.isPrimaryButtonDown()) {
            canvas.pan(dx, dy);
        }
        onMousePressed(e);
    }

    @FXML
    private void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
    }

    @FXML
    private void showOnlyBus() {
        canvas.busTrue();
        canvas.repaint();
    }

    @FXML
    private void showOnlyFerries() {
        canvas.ferriesTrue();
        canvas.repaint();
    }

    @FXML
    private void showOnlyTrains() {
        canvas.trainTrue();
        canvas.repaint();
        
    }

    @FXML
    private void showAll() {
        canvas.allBooleansTrue();
        canvas.repaint();
    }
}
