package main.object;

import main.Ray;
import main.Vector;

import java.awt.*;

public abstract class Object {
    private Color color;
    public abstract Vector getCollision(Ray ray);
    // collision function waaraan ray meegeven: locate collision in object
    // de verschillende transformatie functies
    // implementeer voor afgeleide klasse: sphere


    public Object(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
