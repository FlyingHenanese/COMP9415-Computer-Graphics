package ass2.spec;




import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Road {

    private List<Double> myPoints;
    private double myWidth;
    private MyTexture myTexture;
    private double step = 400;
    /**
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
    }

    /**
     * The width of the road.
     *
     * @return
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending
     * at (x3, y3). (x1, y1) and (x2, y2) are interpolated as bezier control
     * points.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);
    }

    /**
     * Get the number of segments in the curve
     *
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     *
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i * 2);
        p[1] = myPoints.get(i * 2 + 1);
        return p;
    }


    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     *
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int) Math.floor(t);
        t = t - i;

        i *= 6;

        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);

        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;

        return p;
    }

    public void Draw(GL2 gl) {
        gl.glPushMatrix();
        double y_offset = 0.03;
        double step_size = 0.001;
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());
        gl.glBegin(GL2.GL_QUAD_STRIP);
        {
            double[] point = point(0);
            double[] lastPoint = point;
            for (double t = 0; t < this.size(); t += step_size)
            {
                point = point(t);
                double[] v = { -(lastPoint[1] - point[1]), lastPoint[0] - point[0] };
                double length = Math.sqrt(Math.pow(v[0], 2) + Math.pow(v[1], 2));
                v[0] = (v[0] / length) * (myWidth / 2);
                v[1] = (v[1] / length) * (myWidth / 2);
                double y0 = y_offset + Terrain.altitude_detect(point[0] - v[0], point[1] - v[1]);
                double y1 = y_offset + Terrain.altitude_detect(point[0] + v[0], point[1] + v[1]);
                gl.glTexCoord2d(0, (t % 10)*20);
                gl.glVertex3d(point[0] - v[0], y0,point[1] - v[1]);
                gl.glTexCoord2d(1, (t % 10)*20);
                gl.glVertex3d(point[0] + v[0], y1,point[1] + v[1]);
                lastPoint = point;
            }
        }
        gl.glEnd();
        gl.glPopMatrix();
    }


    /**
     * Calculate the Bezier coefficients
     *
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {

        switch (i) {

            case 0:
                return (1 - t) * (1 - t) * (1 - t);

            case 1:
                return 3 * (1 - t) * (1 - t) * t;

            case 2:
                return 3 * (1 - t) * t * t;

            case 3:
                return t * t * t;
        }

        // this should never happen
        throw new IllegalArgumentException("" + i);
    }

    public void init(GL2 gl) {
        myTexture = new MyTexture(gl, "bricks.png", "png", true);
    }


}
