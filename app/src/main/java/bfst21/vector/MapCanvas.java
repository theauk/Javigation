package bfst21.vector;

import java.util.List;

import bfst21.vector.osm.Node;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    private boolean trains, ferries, busses;

    public void init(Model model) {
        this.model = model;
        allBooleansTrue();
        pan(-model.minx,-model.miny);
        zoom(getWidth()/(model.maxx-model.minx), new Point2D(0,0));

    }

    void repaint() {
        var gc = getGraphicsContext2D();
        gc.save();
        gc.setTransform(new Affine());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        
        
       
        gc.setLineWidth(1/Math.sqrt(trans.determinant()));
        for (var line : model.coastLines){
            gc.setStroke(Color.LIGHTBLUE);
            gc.setLineWidth(10/Math.sqrt(trans.determinant()));
            line.draw(gc);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1/Math.sqrt(trans.determinant()));
            line.draw(gc);
            
            

            
        }
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1/Math.sqrt(trans.determinant()));
       
        
        paintWaysFill(model.water, Color.LIGHTBLUE, 1, Color.BLACK);
        paintWaysFill(model.buildings, Color.GREY, 1,Color.BLACK);
        paintWaysFill(model.grass, Color.LIGHTGREEN, 1,Color.BLACK);
        paintWaysFill(model.bridges, Color.GREY, 2, Color.GREY);

        double r1 = (1/Math.sqrt(trans.determinant()));
        double r2 = (2/Math.sqrt(trans.determinant()));
        gc.setLineDashes(r1,r2);
        paintWays(model.footway, 1, 1, Color.DARKGREEN);
        gc.setLineDashes(null);

        paintWays(model.roads, 4, 5, Color.LIGHTGREY);
        paintWays(model.primaryHighway, 5, 6, Color.LIGHTSALMON);

        if(busses){
            showBus();
        }
        if(trains){
            showTrain();
        }
        if(ferries){
            showFerry();
        }
        gc.restore();
    }

    private void paintWaysFill(List<Drawable> list, Color color, int linewidth, Color strokeColor){
        var gc = getGraphicsContext2D();
        gc.setLineWidth(linewidth/Math.sqrt(trans.determinant()));
        gc.setStroke(strokeColor);
        gc.setFill(color);
        for(Drawable var : list){
            var.draw(gc);
            gc.fill();
        }
    }

    private void paintWays(List<Drawable> list, int lineWidthinner, int linewidthouter, Color color){
        var gc = getGraphicsContext2D();
        for (Drawable var: list){
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(linewidthouter/Math.sqrt(trans.determinant()));
            var.draw(gc);

            gc.setStroke(color);
            gc.setLineWidth(lineWidthinner/Math.sqrt(trans.determinant()));
            var.draw(gc);            
        }
    }

    private void paintNodeCircles(List<Node> list, Color color, double radius, int lineWidth){
        var gc = getGraphicsContext2D();
        radius = (radius/Math.sqrt(trans.determinant()));
        for(Node node : list){            
            gc.setFill(color);            
            gc.fillOval(node.getX(), node.getY(), radius, radius);

            gc.setLineWidth(lineWidth/Math.sqrt(trans.determinant()));
            gc.setStroke(Color.BLACK);
            gc.strokeOval(node.getX(), node.getY(), radius, radius);
        }
    }

 
    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, Point2D center) {
        trans.prependScale(factor, factor, center);
        repaint();
    }

    public Point2D mouseToModelCoords(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
	}

    public void allBooleansTrue(){
        busses = true;
        trains = true;
        ferries = true;
         
     }
     private void allBooleansFalse(){
         busses= false;
         trains = false;
         ferries = false;
     }
     public void busTrue(){
         allBooleansFalse();
         busses = true;
     }
 
     public void trainTrue(){
         allBooleansFalse();
         trains = true;
     }
     public void ferriesTrue(){
         allBooleansFalse();
         ferries = true;
 
     }
 
     public void showBus(){
         for (List<Drawable> routes : model.busRoutes){
             paintWays(routes, 2,3, Color.YELLOW);
         }
         paintNodeCircles(model.busStops,Color.YELLOW , 8, 2);
 
     }
     public void showFerry(){
         paintWays(model.ferries, 2, 3, Color.rgb(0, 153, 255));
         paintNodeCircles(model.ferryStops,Color.rgb(0, 153, 255) , 8, 2);
     }
 
     public void showTrain(){
         paintWays(model.railway, 3, 4, Color.RED);
         paintNodeCircles(model.railWayStops, Color.RED, 10, 2);
     }
    
}
