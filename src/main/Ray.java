package main;

public class Ray {
    private Vector s; // origin
    private Vector dir; // direction

    /**
     * Will also normalize the direction vector
     *
     * @param s
     * @param dir
     */
    public Ray(Vector s, Vector dir) {
        this.s = s;
        this.dir = dir;

        // normalize c
        this.dir = new Vector(Utility.normalize(dir.getCoords()));

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
}
