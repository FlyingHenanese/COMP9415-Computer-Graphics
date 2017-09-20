package ass2.spec;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Tree
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos; //position array
    private MyTexture trunk;//trunk element
    private MyTexture leaf;//leaf element

    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }

    public double[] getPosition() {
        return myPos;
    }

    public void draw(GL2 gl){
        gl.glPushMatrix();//record current status
        gl.glTranslated(myPos[0], myPos[1], myPos[2]);//move the tree to a particular position
        float DifL[] = {1.f, 1.0f, 1.f, 1.0f};//enveronment colour and scatter colour
        float SpecL[] = {1.f, 1.f, 1.f, 1.f};//specular colour
        float Shine[] = {50.0f};//reflective index
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, DifL, 0);//set enveronment colour and scatter colour of meterials
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, SpecL, 0);//set specular colour of meterials
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, Shine, 0);//set reflective index of meterials
        //draw the trunk
        GLUT glut = new GLUT();

        gl.glEnable(GL2.GL_TEXTURE_GEN_S);//enable s texture coordinate generation
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);//enable t texture coordinate generation
        gl.glBindTexture(GL2.GL_TEXTURE_2D, trunk.getTextureId());//set 2d texture
        gl.glFrontFace(GL2.GL_CW);//set the clockwise sureface is front face
        gl.glRotatef(90,1,0,0);
        gl.glTranslated(0,0,-0.8);
        glut.glutSolidCylinder(0.1,0.8,15,15);// draw a soild cylinder
        gl.glDisable(GL2.GL_TEXTURE_GEN_S);//take current s
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);//take current t

        //draw leaf
        // Material property vectors.
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);//enable s texture coordinate generation
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);//enable t texture coordinate generation
        gl.glBindTexture(GL2.GL_TEXTURE_2D,leaf.getTextureId());//set 2d texture for leaf
        gl.glFrontFace(GL2.GL_CW);//set the clockwise surface is the front face
        glut.glutSolidSphere(0.4, 15, 15);//draw a soild cylinder
        gl.glDisable(GL2.GL_TEXTURE_GEN_S);//take current s
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);//take current t
        gl.glPopMatrix();//move back to the previous status
    }
    //initialize tree
    public void init(GL2 gl){
        leaf = new MyTexture(gl, "grass.bmp","bmp",true);
        trunk = new MyTexture(gl, "rock.bmp","bmp",true);

    }
}
