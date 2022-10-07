package main.object;

import main.Ray;
import main.Vector;

public abstract class Object {
    public abstract Vector getCollision(Ray ray);
    // collision function waaraan ray meegeven: locate collision in object
    // de verschillende transformatie functies
    // implementeer voor afgeleide klasse: sphere
}
