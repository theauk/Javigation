package bfst21;

import bfst21.view.CanvasBounds;
import bfst21.view.MapCanvas;
import bfst21.view.Theme;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private MapData mapData;
    private Loader loader;
    private Creator creator;

    private Map<String, String> themes;
    private Point2D currentMouse;
    private Point2D lastMouse;
    private boolean viaZoomSlider = true;

    @FXML private MapCanvas mapCanvas;

    @FXML private Scene scene;
    @FXML private StackPane centerPane;
    @FXML private VBox loaderPane;

    @FXML private Label coordsLabel;
    @FXML private Label geoCoordsLabel;
    @FXML private Label nearestRoadLabel;
    @FXML private Label statusLabel;
    @FXML private Label scaleLabel;
    @FXML private Label boundsTR;
    @FXML private Label boundsBR;
    @FXML private Label boundsTL;
    @FXML private Label boundsBL;

    @FXML private ProgressIndicator loadingBar;
    @FXML private Slider zoomSlider;

    @FXML private Menu themeMenu;
    @FXML private MenuItem resetItem;
    @FXML private MenuItem zoomInItem;
    @FXML private MenuItem zoomOutItem;

    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private RadioMenuItem defaultThemeItem;
    @FXML private RadioMenuItem rTreeDebug;

    @FXML private ToggleGroup themeGroup;

    public void init() {
        mapData = new MapData();
        loader = new Loader();
        themes = new HashMap<>();
        loadThemes();
        initView();
        openFile();
    }

    private void initView() {
        mapCanvas.init(mapData, loader.loadTheme(themes.get("Default")));

        mapCanvas.widthProperty().addListener((observable, oldValue, newValue) -> setBoundsLabels());
        mapCanvas.heightProperty().addListener((observable, oldValue, newValue) -> setBoundsLabels());

        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(viaZoomSlider) zoom(newValue.intValue() - oldValue.intValue());
        });

        scaleLabel.textProperty().bind(mapCanvas.getRatio());

        themeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> setTheme(((RadioMenuItem) newValue.getToggleGroup().getSelectedToggle()).getText()));
    }

    private void loadThemes() {
        for(String file : loader.getFilesIn("/themes", ".mtheme")) {
            String themeName = Theme.parseName(file);
            themes.put(themeName, file);

            if(!file.equals("default.mtheme")) {
                RadioMenuItem item = new RadioMenuItem(themeName);
                item.setToggleGroup(themeGroup);
                themeMenu.getItems().add(item);
            }
        }
    }

    /**
     * Called when a ScrollEvent is fired. Zooms in on the MapCanvas at the Mouse's coordinates.
     *
     * @param e the ScrollEvent associated with the caller.
     */
    @FXML
    private void onScroll(ScrollEvent e) {
        zoom((e.getDeltaY() > 0), new Point2D(e.getX(), e.getY()));
    }

    /**
     * Zoom in or out on the MapCanvas via a Node.
     *
     * @param e the ActionEvent associated with the caller.
     */
    @FXML
    private void zoom(ActionEvent e) {
        if (e.getSource().equals(zoomInItem) || e.getSource().equals(zoomInButton)) zoom(true, new Point2D(mapCanvas.getWidth() / 2, mapCanvas.getHeight() / 2));
        else if (e.getSource().equals(zoomOutItem)  || e.getSource().equals(zoomOutButton)) zoom(false, new Point2D(mapCanvas.getWidth() / 2, mapCanvas.getHeight() / 2));
    }

    /**
     * Zoom in or out on the MapCanvas based on levels to be zoomed.
     *
     * @param levels the amount of zoom levels to be zoomed in or out.
     */
    private void zoom(int levels) {
        if(levels > 0) mapCanvas.zoom(true, levels);
        else if(levels < 0) mapCanvas.zoom(false, levels);
        setBoundsLabels();
    }

    /**
     * Zoom in on the mapCanvas at a specific pivot point.
     *
     * @param zoomIn to zoom in or not.
     * @param center the pivot point to zoom in at.
     */
    private void zoom(boolean zoomIn, Point2D center) {
        viaZoomSlider = false;
        mapCanvas.zoom(zoomIn, center);
        zoomSlider.setValue(mapCanvas.getZoomLevel());
        setBoundsLabels();
        viaZoomSlider = true;
    }

    @FXML
    private void onMouseDragged(MouseEvent e) {
        double dx = e.getX() - lastMouse.getX();
        double dy = e.getY() - lastMouse.getY();

        if (e.isPrimaryButtonDown()) {
            mapCanvas.setCursor(Cursor.CLOSED_HAND);
            setBoundsLabels();
            mapCanvas.pan(dx, dy);
        }

        currentMouse = new Point2D(e.getX(), e.getY());
        onMousePressed(e);
    }

    @FXML
    private void onMouseMoved(MouseEvent e) {
        mapCanvas.setCursor(Cursor.DEFAULT);
        setLabels(new Point2D(e.getX(), e.getY()));
        currentMouse = new Point2D(e.getX(), e.getY());
    }

    @FXML
    private void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
        currentMouse = new Point2D(e.getX(), e.getY());
        setLabels(new Point2D(e.getX(), e.getY()));
    }

    @FXML
    private void onMouseReleased() {
        mapCanvas.setCursor(Cursor.DEFAULT);
    }

    @FXML
    private void resetView() {
        mapCanvas.reset();
        themeGroup.selectToggle(defaultThemeItem);
        //zoomSlider.setValue(zoomSlider.getMin());
        setLabels(currentMouse);
    }

    @FXML
    private void openFile() {
        File file = showFileChooser().showOpenDialog(scene.getWindow());

        if (file != null) loadFile(file.getAbsolutePath(), file.length());
        else {
            File binaryFile = new File(getClass().getResource("/small.osm").getPath());
            String path = binaryFile.getAbsolutePath();
            path = handlePotentialSpacesInPath(path);
            loadFile(path, binaryFile.length());
        }
    }

    private String handlePotentialSpacesInPath(String path) {
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }

    @FXML
    private void exit() {
        System.exit(0);
    }

    @FXML
    private void cancelLoad() {
        if (creator != null) creator.cancel();
    }

    private void loadFile(String path, long fileSize) {
        try {
            mapData = new MapData();
            creator = new Creator(mapData, loader.load(path), fileSize);
            creator.setOnRunning(e -> {
                centerPane.setCursor(Cursor.WAIT);
                statusLabel.textProperty().bind(creator.messageProperty());
                loadingBar.progressProperty().bind(creator.progressProperty());
                disableGui(true);
                loaderPane.setVisible(true);
            });
            creator.setOnSucceeded(e -> {
                centerPane.setCursor(Cursor.DEFAULT);
                statusLabel.textProperty().unbind();
                loadingBar.progressProperty().unbind();
                disableGui(false);
                loaderPane.setVisible(false);
                mapCanvas.setMapData(mapData);
                //mapCanvas.reset();
            });
            creator.setOnCancelled(e -> {
                centerPane.setCursor(Cursor.DEFAULT);
                statusLabel.textProperty().unbind();
                loadingBar.progressProperty().unbind();
                statusLabel.setText("Cancelled.");
                disableGui(false);
            });
            creator.setOnFailed(e -> {
                centerPane.setCursor(Cursor.DEFAULT);
                statusLabel.textProperty().unbind();
                loadingBar.progressProperty().unbind();
                statusLabel.setText("Failed.");
                creator.exceptionProperty().get().printStackTrace();
                disableGui(false);
            });

            Thread creatorThread = new Thread(creator, "Creator Thread");
            creatorThread.setDaemon(true);
            creatorThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTheme(String themeName) {
        String name = themes.get(themeName);
        Theme theme = loader.loadTheme(name);
        scene.getStylesheets().clear();
        if(theme.getStylesheet() != null) scene.getStylesheets().add(theme.getStylesheet());
        mapCanvas.setTheme(theme);
    }

    private void setLabels(Point2D point) {
        try {
            Point2D coords = mapCanvas.getTransCoords(point.getX(), point.getY());
            Point2D geoCoords = mapCanvas.getGeoCoords(point.getX(), point.getY());
            setCoordsLabel((float) coords.getX(), (float) coords.getY());
            setGeoCoordsLabel((float) geoCoords.getX(), (float) geoCoords.getY());
            setNearestRoadLabel(geoCoords.getX(), geoCoords.getY());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    private void setCoordsLabel(float x, float y) {
        coordsLabel.setText("Coordinates: (" + round(x, 1) + ", " + round(y, 1) + ")");
    }

    private void setGeoCoordsLabel(float x, float y) {
        geoCoordsLabel.setText("Geo-coordinates: (" + round(x, 7) + ", " + round(y, 7) + ")");
    }

    private void setNearestRoadLabel(double x, double y) {
        nearestRoadLabel.setText("Nearest Road: " + mapData.getNearestRoad((float) x, (float) -y / 0.56f));
    }

    private void setBoundsLabels() {
        CanvasBounds bounds = mapCanvas.getBounds();
        boundsTL.setText("(" + round(bounds.getMinX(), 1) + ", " + round(bounds.getMinY(), 1) + ")");
        boundsTR.setText("(" + round(bounds.getMaxX(), 1) + ", " + round(bounds.getMinY(), 1) + ")");
        boundsBL.setText("(" + round(bounds.getMinX(), 1) + ", " + round(bounds.getMaxY(), 1) + ")");
        boundsBR.setText("(" + round(bounds.getMaxX(), 1) + ", " + round(bounds.getMaxY(), 1) + ")");
    }

    private double round(double number, int digits) {
        double scale = Math.pow(10, digits);
        return Math.round(number * scale) / scale;
    }

    private void disableGui(boolean disable) {
        zoomSlider.setDisable(disable);
        zoomInItem.setDisable(disable);
        zoomOutItem.setDisable(disable);
        resetItem.setDisable(disable);
    }

    private FileChooser showFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Open Street Map File", "*.osm", "*.zip"));

        return fileChooser;
    }

    @FXML
    private void setRTreeDebug() {
        mapData.setRTreeDebug(rTreeDebug.isSelected()); // TODO: 3/31/21 which class should it go via?
        mapCanvas.rTreeDebugMode();
    }
}
