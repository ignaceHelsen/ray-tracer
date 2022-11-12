package main;

public class Ray implements Cloneable {
    private Vector s; // origin
    private Vector dir; // direction

    /**
     * Will also normalize the direction vector
     *
     * @param s: start point
     * @param dir: direction
     */
    public Ray(Vector s, Vector dir) {
        this.s = s;
        this.dir = dir;
    }

    public Vector getS() {
        return s;
    }

    public void setS(Vector s) {
        this.s = s;
    }

    public Vector getDir() {
        return dir;
    }

    public void setDir(Vector dir) {
        this.dir = dir;
    }

    @Override
    public Ray clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (Ray) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
