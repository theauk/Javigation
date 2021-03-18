package bfst21.view;

import bfst21.Creator;
import bfst21.Loader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.awt.*;

public class BasicDrawing extends Canvas{

    private Loader loader;
    private Affine trans = new Affine();

    public void init(Loader loader) {
        this.loader = loader;
        pan(-loader.getCreator().getMinx(),-loader.getCreator().getMiny());
        zoom(getWidth()/(loader.getCreator().getMaxx()-loader.getCreator().getMinx()), new Point2D(0,0));
    }

        void repaint () {
            GraphicsContext gc = getGraphicsContext2D();
            gc.save();
            gc.setTransform(new Affine());
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, getWidth(), getHeight());
            gc.setTransform(trans);
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.LIGHTYELLOW);
            gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

            for (var coast: loader.getCreator().getcoastlines()){
                gc.setStroke(Color.BLACK);
                coast.draw(gc);
            }
            for(var footway: loader.getCreator().getFootway()){
                footway.draw(gc);
            }
            for(var resRoad: loader.getCreator().getResidentialRoads()){
                resRoad.draw(gc);
            }
            gc.restore();
        }
    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, Point2D center) {
        trans.prependScale(factor, factor, center);
        repaint();
    }
    }
