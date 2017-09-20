package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;


public class Camera {
    public double view_radius;
    public double rotation_x;
    public double rotation_y;
    public int fovy;
    public double spotx;
    public double spoty;
    public double spotz;
    public float[] location = new float[3];
    public int camera_mode;

    //global camera
    public Camera(int width, int depth){
        view_radius = 0.01;//set radius that could be saw
        spotx = 0;
        spotz = 0;
        rotation_x = 0;//abandon
        rotation_y = 0;//camera angle at y axis
        change_location(spotx,Terrain.altitude_detect(spotx,spotz),spotz);//update x, y, z position of global camera with current altitude, the height is 1.7 unit above the ground
        fovy = 60;//initialize field of view angle in y axis
    }
    //avatar camera
    public Camera(double x, double y, double z){
        view_radius = 1;//set radius 
        spotx = x;
        spoty = y;
        spotz = z;
        rotation_x = 0;//abandon, always 0
        rotation_y = 0;//camera angle at y axis
    }

    //reshape the scene for the camera
    public void reshape(GL2 gl, int width, int height){
        gl.glMatrixMode(GL2.GL_PROJECTION);//modify matrix of projection
        gl.glLoadIdentity();//load identity matrix
        double aspect;
        aspect = width/height;//get scale
        GLU glu = new GLU();
        glu.gluPerspective(fovy,aspect,1,200);//set perspective projection matrix with angle, aspect scale, the closest and farest from z axis
        gl.glMatrixMode(GL2.GL_MODELVIEW);//modify matrix of modelview
    }

    public void draw(){
        GLU glu = new GLU();
        double l_x;
        double l_z;
        double l_y;
        //camera_mode equals 1 means first person view, 2, 0 means global view, otherwise is third person view
        if(camera_mode == 1||camera_mode==0) {
            //x, y, z mean the position of the stuff that the camera is looking at
            l_x = spotx - Math.cos(rotation_y) * view_radius;
            l_z = spotz - Math.sin(rotation_y) * view_radius;
            l_y = spoty;
            glu.gluLookAt(l_x, l_y + 1.7, l_z, spotx, spoty + 1.7, spotz, 0, 1, 0);//set 'head' always point to (0,1,0)
        }
        else{
            //camera position at world in x, y, z
            l_x = spotx - Math.cos(rotation_y) * view_radius;
            l_z = spotz - Math.sin(rotation_y) * view_radius;
            l_y = Terrain.altitude_detect(l_x,l_z)+1.5;

            glu.gluLookAt(l_x,l_y,l_z,spotx,spoty+1,spotz,0,1,0);
        }
        change_location(l_x,l_y,l_z);
    }

    public void change_location(double x, double y, double z){
        location[0] = (float) x;
        location[1] = (float) y;
        location[2] = (float) z;
    }

    public void move_location(double x, double y, double z){
        spotx = x;
        spoty = y;
        spotz = z;

    }
}
