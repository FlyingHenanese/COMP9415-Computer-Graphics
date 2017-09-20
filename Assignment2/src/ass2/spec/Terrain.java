package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private static double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private Camera mycamera;
    private float[] mySunlight;
    private Texture[] textures;
    private float[] black = {0,0,0,1};
    private MyTexture grass;
    private boolean night = false;
    private Avatar avatar;
    private int cameramode=0;
    private VBO_task vbo_task;
    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
        mycamera = new Camera(width,depth);
        vbo_task = new VBO_task();
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * @param x
     * @param z
     * @return
     */
    public static double altitude(double x, double z) {
        double altitude = 0;
        //In week5 ppt depth interpolation
        /**(x0,z1)      (x1,z1)
        /       ++++++++
        /       + +    +     
        /       +   +  +
        /       +     ++
        /       ++++++++
        /  (x0,z0)      (x1,z0)
        /
        */
        if(x<0 || z<0 || x>myAltitude.length||z>myAltitude[0].length){
            return altitude;
        }
        double x_float = x % 1;
        double z_float = z % 1;
        int x0 = (int) Math.floor(x);
        int z0 = (int) Math.floor(z);
        int x1 = x0<myAltitude.length-1?x0+1:x0;
        int z1 = z0<myAltitude[0].length-1?z0+1:z0;
        double altitude0 = ((1-x_float)*myAltitude[x0][z0]) + (x_float*myAltitude[x1][z0]);
        double altitude1 = ((1-x_float)*myAltitude[x0][z1]) + (x_float*myAltitude[x1][z1]);
        altitude = ((1-z_float)*altitude0)+(z_float*altitude1);
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }

    public void addAvatar(double x, double z){
        double y = altitude(x,z);
        avatar = new Avatar(x,y,z);
    }
    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }

    public void draw(GL2 gl){
        gl.glPushMatrix();//record current status
        gl.glLoadIdentity();//load identity matrix
        if(cameramode == 0){//global view
            mycamera.camera_mode = 0;//load camera by parameter
            mycamera.draw();
        }if(cameramode == 1){//first person view
            avatar.avatarcamera.camera_mode = 1;
            avatar.avatarcamera.draw();
        }
        if(cameramode == 3){//third person view
            avatar.avatarcamera.camera_mode = 3;
            avatar.avatarcamera.draw();
        }
        //light 
        if(!night){
            //Turn on OpenGl lighting
            gl.glDisable(GL2.GL_LIGHT2);
            gl.glEnable(GL2.GL_LIGHT0);

            //Light property vectors
            float lightAmb[] = {0.3f, 0.3f, 0.3f, 1.0f};
            float Spec[] = {0.3f,0.3f,0.3f,1.0f};
            float Position[] = {10, 10, 10, 1};

            //Light position
            Position[0] *= mySunlight[0];
            Position[1] *= mySunlight[1];
            Position[2] *= mySunlight[2];

            //set light properties
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmb, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, Spec, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR,Spec, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, Position, 0);
            gl.glEnable(GL2.GL_LIGHT0);// Enable particular light source.
        }else{
            
            gl.glDisable(GL2.GL_LIGHT0);
            // Turn on OpenGL lighting.
            gl.glEnable(GL2.GL_LIGHT2);

            // Light property vectors.
            float lightAmb[] = {0.0f, 0.0f, 0.0f, 1.0f};
            float Spec[] = {2.0f, 2.0f, 2.0f, 1.0f};
            float globAmb[] = {0.1f, 0.1f, 0.1f, 1.0f};

            // Light properties.
            gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, lightAmb,0);
            gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, Spec,0);
            gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, Spec,0);

            gl.glEnable(GL2.GL_LIGHT2); // Enable particular light source.
            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.
            avatar.turnOnTorch(gl);
        }
        int idx, idz;
        // these code could show how the terrain mesh generated
        // Draw the out line of the terrain
        gl.glBegin(GL2.GL_LINES);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, black, 0);
        gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_SPECULAR,black,0);
        gl.glLineWidth(1);
        for(idx = 0; idx<myAltitude.length-1;++idx){
            for(idz = 0; idz<myAltitude[0].length-1;++idz){
                //draw trangle to present curve
                gl.glVertex3d(idx, myAltitude[idx][idz], idz);
                gl.glVertex3d(idx, myAltitude[idx][idz + 1], idz + 1);

                gl.glVertex3d(idx, myAltitude[idx][idz + 1], idz + 1);
                gl.glVertex3d(idx + 1, myAltitude[idx + 1][idz], idz);

                gl.glVertex3d(idx + 1, myAltitude[idx + 1][idz], idz);
                gl.glVertex3d(idx, myAltitude[idx][idz], idz);
            }
        }
        for(idx = 0; idx<myAltitude.length-1;++idx){
            for(idz = 1; idz<myAltitude[0].length;++idz){
                //draw trangle
                gl.glVertex3d(idx, myAltitude[idx][idz], idz);
                gl.glVertex3d(idx + 1, myAltitude[idx + 1][idz], idz);

                gl.glVertex3d(idx + 1, myAltitude[idx + 1][idz], idz);
                gl.glVertex3d(idx + 1, myAltitude[idx + 1][idz - 1], idz - 1);

                gl.glVertex3d(idx + 1, myAltitude[idx + 1][idz - 1], idz - 1);
                gl.glVertex3d(idx, myAltitude[idx][idz], idz);
            }
        }
        gl.glEnd();
        //z-fighting glPolygonOffset in week 5
        gl.glEnable(GL2.GL_POLYGON_OFFSET_POINT);
        gl.glEnable(GL2.GL_POLYGON_OFFSET_LINE);
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        //pull back the polygon a little bit
        gl.glPolygonOffset(8, 8);
        // Material property in week 5
        // 3D Mesh in week4
        float DifL[] = {1.f, 1.0f, 1.f, 1.0f};
        float SpecL[] = {1.f, 1.f, 1.f, 1.f};
        float Shine[] = {50.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, DifL, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, SpecL, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, Shine, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, grass.getTextureId());
        gl.glBegin(GL2.GL_TRIANGLES);
        for(idx = 0; idx<myAltitude.length-1;++idx){
            for(idz = 0; idz<myAltitude[0].length-1;++idz){
                //set the normal
                double[] v1 = {0, myAltitude[idx][idz+1]-myAltitude[idx][idz], 1};
                double[] v2 = {1, myAltitude[idx+1][idz]-myAltitude[idx][idz+1], -1};
                double[] cross_mutiply = crossMultiply(v1,v2);
                gl.glNormal3dv(cross_mutiply,0);
                gl.glTexCoord2d(0,0);
                gl.glVertex3d(idx, myAltitude[idx][idz], idz);
                gl.glTexCoord2d(1,0);
                gl.glVertex3d(idx, myAltitude[idx][idz+1], idz+1);
                gl.glTexCoord2d(0,1);
                gl.glVertex3d(idx+1, myAltitude[idx+1][idz], idz);
            }
        }
        for(idx = 0; idx<myAltitude.length-1;++idx){
            for(idz = 1; idz<myAltitude[0].length;++idz){
                double[] v1 = {1, myAltitude[idx+1][idz]-myAltitude[idx][idz], 0};
                double[] v2 = {0, myAltitude[idx+1][idz-1]-myAltitude[idx+1][idz], -1};
                double[] cross_mutiply = crossMultiply(v1,v2);
                gl.glNormal3dv(cross_mutiply,0);
                gl.glTexCoord2d(1,0);
                gl.glVertex3d(idx, myAltitude[idx][idz], idz);
                gl.glTexCoord2d(1,1);
                gl.glVertex3d(idx+1, myAltitude[idx+1][idz], idz);
                gl.glTexCoord2d(0,1);
                gl.glVertex3d(idx+1, myAltitude[idx+1][idz-1], idz-1);
            }
        }
        gl.glEnd();

        for(Tree trees : myTrees){
            trees.draw(gl);
        }
        for(Road roads: myRoads){

            roads.Draw(gl);
        }
        avatar.draw(gl);
        vbo_task.display(gl);
        gl.glPopMatrix();
    }

    //reshape the scene for the camera
    public void reshape(GLAutoDrawable drawable,int x, int y, int width, int height){
        GL2 gl = drawable.getGL().getGL2();
        mycamera.reshape(gl, width,height);
    }

    public void init(GLAutoDrawable drawable){
        GL2 gl = drawable.getGL().getGL2();
        grass = new MyTexture(gl, "grass.bmp", "bmp", true);
        addAvatar(2,2);
        avatar.init(gl);
        vbo_task.init(gl);
        for(Tree tree : myTrees){
            tree.init(gl);
        }
        for (Road road : myRoads){
            road.init(gl);
        }
    }

    //keyboard listener for the camera and avatar
    public void key_pressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()){
            //right arrow
            case KeyEvent.VK_RIGHT:
                if(cameramode==0){
                    mycamera.rotation_y+=0.2;
                }else{
                    avatar.avatarcamera.rotation_y+=0.2;
                }
                break;
            //left arrow
            case KeyEvent.VK_LEFT:
                if(cameramode==0){
                    mycamera.rotation_y-=0.2;
                }else{
                    avatar.avatarcamera.rotation_y-=0.2;
                }
                break;
            //up arrow
            case KeyEvent.VK_UP:
                if(cameramode == 0) {
                    mycamera.spotx = (float) (mycamera.location[0] + Math.cos(mycamera.rotation_y) * 0.1);
                    mycamera.spotz = (float) (mycamera.location[2] + Math.sin(mycamera.rotation_y) * 0.1);
                    mycamera.spoty = (float) (Terrain.altitude_detect(mycamera.spotx, mycamera.spotz));
                }else{
                    avatar.moveahead();
                }
                break;
            //down arrow
            case KeyEvent.VK_DOWN:
                if(cameramode == 0) {
                    mycamera.spotx = (float) (mycamera.location[0] + Math.cos(mycamera.rotation_y) * -0.1);
                    mycamera.spotz = (float) (mycamera.location[2] + Math.sin(mycamera.rotation_y) * -0.1);
                    mycamera.spoty = (float) (Terrain.altitude_detect(mycamera.spotx, mycamera.spotz));
                }else{
                    avatar.moveback();
                }
                break;
            //no light
            case KeyEvent.VK_N:
                night = true;
                break;
            //have light
            case KeyEvent.VK_M:
                night = false;
                break;
            //first person view
            case KeyEvent.VK_1:
                cameramode = 1;
                break;
            //global view
            case KeyEvent.VK_0:
                cameramode = 0;
                break;
            //third person view
            case KeyEvent.VK_3:
                cameramode = 3;
                break;

        }
    }

    //if within the map, return altitude, else return 0
    public static double altitude_detect(double x, double z) {
        int x0 = (int) x;
        int x1 = (int) (x + 1);
        int z0 = (int) z;
        int z1 = (int) (z + 1);

        if (x0 >= 0 && x0 < myAltitude.length) {
            if (z0 >= 0 && z0 < myAltitude[x0].length) {
                if (x1 >= 0 && x1 < myAltitude.length) {
                    if (z1 >= 0 && z1 < myAltitude[x1].length) {
                        return altitude(x, z);
                    }
                }
            }
        }
        return 0;
    }

    //math method
    public static double[] crossMultiply(double[] v1,double[] v2) {
        double[] result = {
                v1[1]*v2[2] - v1[2]*v2[1],
                v1[2]*v2[0] - v1[0]*v2[2],
                v1[0]*v2[1] - v1[1]*v2[0]};
        return result;
    }
}
