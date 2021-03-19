package bfst21.view;

import bfst21.Loader;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapCanvas extends Canvas
{
    private Loader loader;
    private Affine trans;

    public void init(Loader loader)
    {
        this.loader = loader;
        trans = new Affine();

        widthProperty().addListener(observable -> repaint());
        heightProperty().addListener(observable -> repaint());

        repaint();
    }

    public void repaint()
    {
        GraphicsContext gc = getGraphicsContext2D();
        gc.save();
        gc.setTransform(new Affine());

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());

        gc.setTransform(trans);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        //TEST LINE
        /*gc.beginPath();
        gc.moveTo(10, 10);
        gc.lineTo(20, 20);
        gc.stroke();
*/
        /*
        TO-DO:
        DRAWING ACTION -> GET ELEMENTS TO DRAW
         */
        for (var coast: loader.getCreator().getCoastlines()){
            gc.setStroke(Color.BLACK);
            coast.draw(gc);
        }
        for(var footway: loader.getCreator().getFootway()){
            footway.draw(gc);
        }
        for(var resRoad: loader.getCreator().getResidentialRoads()){
            resRoad.draw(gc);
        }
        for (var highway : loader.getCreator().getHighways()){
            highway.draw(gc);
        }
        for (var tertiary: loader.getCreator().getTertiary()){
            tertiary.draw(gc);
        }
        for (var bridge: loader.getCreator().getBridges()){
            bridge.draw(gc);
        }
        for (var road: loader.getCreator().getRoads()){
            road.draw(gc);
        }
        gc.restore();
    }

    public void zoom(double factor, Point2D center)
    {
        trans.prependScale(factor, factor, center);
        repaint();
    }

    public void pan(double dx, double dy)
    {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void reset()
    {
        trans = new Affine();
        pan(0, 0);
    }

    public Point2D getTransCoords(double x, double y) throws NonInvertibleTransformException
    {
        return trans.inverseTransform(x, y);
    }

    public Point2D getGeoCoords(double x, double y) throws NonInvertibleTransformException
    {
        Point2D geoCoords = getTransCoords(x, y);

        return new Point2D(geoCoords.getX(), -geoCoords.getY() * 0.56f);
    }
}
