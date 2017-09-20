package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

public class Avatar {
    private double[] myPos;//position array
    private MyTexture kitten;//texture
    public Camera avatarcamera;//camera
    public Avatar(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
        avatarcamera = new Camera(x,y,z);
    }

    public double[] getPosition() {
        return myPos;
    }

    public void draw(GL2 gl){
        gl.glPushMatrix();//record current status
        gl.glTranslated(myPos[0], myPos[1], myPos[2]);//move to a position

        //set color for trunk
        float DifL[] = {1.f, 1.0f, 1.f, 1.0f};//enveronment colour and scatter colour
        float SpecL[] = {1.f, 1.f, 1.f, 1.f};//specular colour
        float Shine[] = {50.0f};//reflective index
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, DifL, 0);//set enveronment colour and scatter colour of meterials
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, SpecL, 0);//set specular colour of meterials
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, Shine, 0);//set reflective index of meterials
        GLUT glut = new GLUT();
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);//enable s texture coordinate generation
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);//enable t texture coordinate generation
        gl.glBindTexture(GL2.GL_TEXTURE_2D, kitten.getTextureId());//set 2d texture
//        gl.glFrontFace(GL2.GL_CW);//set front face with clockwise
        gl.glTranslated(0,0.3,0);
        glut.glutSolidTeapot(0.3);// draw a soild teapot
        gl.glDisable(GL2.GL_TEXTURE_GEN_S);//take current s
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);//take current t
    }

    public void moveahead(){
        myPos[0]+=Math.cos(this.avatarcamera.rotation_y) * 0.1;//compute x(move only 0.1 of the length unit at matrix)
        myPos[2]+=Math.sin(this.avatarcamera.rotation_y) * 0.1;//compute z
        myPos[1]=Terrain.altitude_detect(myPos[0],myPos[2]);//get altitude y from current terrain
        System.out.println("current location: "+ myPos[0]+" "+myPos[1]+" "+myPos[2]);
        avatarcamera.move_location(myPos[0],myPos[1],myPos[2]);//move avatar camera at current position
    }

    public void moveback(){
        myPos[0]+=Math.cos(this.avatarcamera.rotation_y) * -0.1;//compute x
        myPos[2]+=Math.sin(this.avatarcamera.rotation_y) * -0.1;//compute z
        myPos[1]=Terrain.altitude_detect(myPos[0],myPos[2]);//get altitude y from current terrain
        avatarcamera.move_location(myPos[0],myPos[1],myPos[2]);//move avatar camera at current position
    }

    public void turnOnTorch(GL2 gl) {
        gl.glPushMatrix();{
            float lightPos[] = {(float) (myPos[0]), (float) (myPos[1]+1.7), (float) (myPos[2]), 1.0f}; // Spotlight position.
            gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, lightPos,0);
            float[] spotDirection = new float[3];
            spotDirection[0] = (float) Math.cos(avatarcamera.rotation_y);
            spotDirection[2] = (float) Math.sin(avatarcamera.rotation_y);
            spotDirection[1] = (float) -0.3;
            gl.glLightf(GL2.GL_LIGHT2, GL2.GL_SPOT_CUTOFF, 60);
            gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPOT_DIRECTION, spotDirection,0);
            gl.glLightf(GL2.GL_LIGHT2, GL2.GL_SPOT_EXPONENT, 90);

        }gl.glPopMatrix();

    }

    public void init(GL2 gl){
        kitten = new MyTexture(gl, "kittens.jpg", "jpg",true);
    }
}
