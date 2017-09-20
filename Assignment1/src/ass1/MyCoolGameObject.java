package ass1;


public class MyCoolGameObject extends GameObject{
    /**
     * Public constructor for creating GameObjects, connected to a parent (possibly the ROOT).
     * <p>
     * New objects are created at the same location, orientation and scale as the parent.
     *
     * @param parent
     */
    public MyCoolGameObject(){
        //auto set parent as root
        this(GameObject.ROOT);

    }
    public MyCoolGameObject(GameObject parent) {
        super(parent);
        double black[] = {0,0,0,0};
        double white[] = {1, 1, 1, 1};
        double points[] = {0, 0, 1, 1, 0, 1};
        PolygonalGameObject triangle = new PolygonalGameObject(this, points, null, white);
        triangle.rotate(-45);
        triangle.translate(-0.5,-0.5);
        CircularGameObject round = new CircularGameObject(triangle, 0.25, white, white);
        round.translate(0.42,0.08);
        CircularGameObject round1 = new CircularGameObject(round, 0.25, white, white);
        round1.translate(0.5,0.5);
        CircularGameObject round3 = new CircularGameObject(round,0.125, black, white);
        round3.translate(0.09,-0.09);
        CircularGameObject round4 = new CircularGameObject(round1,0.125, black, white);
        round4.translate(0.09,-0.09);
    }
}
