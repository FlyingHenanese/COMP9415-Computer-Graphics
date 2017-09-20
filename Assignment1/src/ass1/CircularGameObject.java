package ass1;

import com.jogamp.opengl.GL2;

/**
 * A game object that has a polygonal shape.
 *
 * This class extend GameObject to draw polygonal shapes.
 *
 * TODO: The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 */
public class CircularGameObject extends GameObject {

    private double r;
    private double[] myFillColour;
    private double[] myLineColour;

    //create an Identity Line
    public CircularGameObject(GameObject parent, double[] fillColour, double[] lineColour) {
        super(parent);
        r = 1;
        myFillColour = fillColour;
        myLineColour = lineColour;
    }
    public CircularGameObject(GameObject parent, double radius,double[] fillColour, double[] lineColour) {
        super(parent);
        r = radius;
        myFillColour = fillColour;
        myLineColour = lineColour;
    }

    public void set_radius(double radius){
        r = radius;
    }

    public double getR(){
        return r;
    }

    /**
     * Get the fill colour
     *
     * @return
     */
    public double[] getFillColour() {
        return myFillColour;
    }

    /**
     * Set the fill colour.
     *
     * Setting the colour to null means the object should not be filled.
     *
     * @param fillColour The fill colour in [r, g, b, a] form
     */
    public void setFillColour(double[] fillColour) {
        myFillColour = fillColour;
    }

    /**
     * Get the outline colour.
     *
     * @return
     */
    public double[] getLineColour() {
        return myLineColour;
    }

    /**
     * Set the outline colour.
     *
     * Setting the colour to null means the outline should not be drawn
     *
     * @param lineColour
     */
    public void setLineColour(double[] lineColour) {
        myLineColour = lineColour;
    }

    // ===========================================
    // COMPLETE THE METHODS BELOW
    // ===========================================


    /**
     * TODO: Draw the circular
     *
     * if the fill colour is non-null, fill the circle with this colour
     * if the line colour is non-null, draw the outline with this colour
     *
     */
    @Override
    public void drawSelf(GL2 gl) {
        int Npoint = 20;
        if(myFillColour!=null){
            gl.glColor4dv(myFillColour,0);
            gl.glBegin(GL2.GL_POLYGON);
            double angle;
            double interval = 2*Math.PI/Npoint;
            for(int i = 0; i<Npoint;++i){
                angle=interval*i;
                gl.glVertex2d(r*Math.cos(angle),r*Math.sin(angle));
            }
            gl.glEnd();
        }
        if(myLineColour!=null){
            gl.glColor4dv(myLineColour,0);
            gl.glBegin(GL2.GL_LINE_LOOP);
            double angle;
            double interval = 2*Math.PI/Npoint;
            for(int i = 0; i<Npoint;++i){
                angle=interval*i;
                gl.glVertex2d(r*Math.cos(angle),r*Math.sin(angle));
            }
            gl.glEnd();
        }
    }
}
