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
public class LineGameObject extends GameObject {


    private double x_1;
    private double y_1;
    private double x_2;
    private double y_2;
    private double[] myLineColour;

    public LineGameObject(GameObject parent, double x1, double y1, double x2, double y2, double[] lineColour) {
        super(parent);
        x_1 = x1;
        y_1 = y1;
        x_2 = x2;
        y_2 = y2;
        myLineColour = lineColour;
    }
    public LineGameObject(GameObject parent, double[] lineColour) {
        super(parent);
        //set an identity line
        x_1 = 0;
        x_2 = 1;
        y_1 = 0;
        y_2 = 0;
        myLineColour = lineColour;
    }

    public double[] get_first_point(){
        return new double[]{x_1,y_1};
    }

    public void set_first_point(double[] point){
        x_1 = point[0];
        y_1 = point[1];
    }

    public double[] get_second_point(){
        return new double[]{x_2,y_2};
    }

    public void set_second_point(double[] point){
        x_2 = point[0];
        y_2 = point[1];
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
     * TODO: Draw the LINE
     * if the line colour is non-null, draw the outline with this colour
     *
     */
    @Override
    public void drawSelf(GL2 gl) {
        if(myLineColour!=null){
            gl.glColor4dv(myLineColour,0);
            gl.glBegin(GL2.GL_LINES);
            gl.glVertex2d(x_1,y_1);
            gl.glVertex2d(x_2,y_2);
            gl.glEnd();
        }
    }
}
