package main.sdl;

import main.Material;
import main.Texture;
import main.object.Cube;
import main.object.Plane;
import main.object.Sphere;
import main.object.TaperedCylinder;
import main.transformation.Rotation;
import main.transformation.Scale;
import main.transformation.Transformation;
import main.transformation.Translation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SDL {
    public static List<Object> parse(String sourcePath) throws IOException {
        List<Object> objects = new ArrayList<>();

        // TODO: material in SDL!
        Material ruby = new Material(new double[]{0.1745, 0.01175, 0.01175}, new double[]{0.61424, 0.04136, 0.04136}, new double[]{0.727811, 0.626959, 0.626959}, new double[]{1.762, 1.770, 1.778}, 0.2, new double[]{(double)1/3, (double)1/3, (double)1/3}, 0.6);

        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));

        main.object.Object object = null;
        Transformation translation = new Transformation();
        Transformation scale = new Transformation();
        Transformation rotation = new Transformation();
        Texture texture = Texture.NONE;

        Material perfectMirror = new Material(new double[] {0, 0, 0}, new double[] {0, 0, 0}, new double[] {1, 1, 1}, new double[] {1, 1, 1}, 0.2, new double[] {(double)1/3, (double)1/3, (double)1/3}, 1);
        Material chrome = new Material(new double[]{0.25, 0.25, 0.25}, new double[]{0.4, 0.4, 0.4}, new double[]{3.1812, 3.1812, 3.1812}, new double[]{3.1812, 3.1812, 3.1812}, 0.2, new double[]{(double)1/3, (double)1/3, (double)1/3}, 0.6);
        Material gold = new Material(new double[]{0.54725, 0.4995, 0.3745}, new double[]{0.95164, 0.80648, 0.52648}, new double[]{0.928281, 0.855802, 0.666065}, new double[]{fresnelToRefr(0.989), fresnelToRefr(0.876), fresnelToRefr(0.399)}, 0.2, new double[]{(double)1/3, (double)1/3, (double)1/3}, 0.4);

        while (reader.ready()) {
            String currentLine = reader.readLine();
            if(currentLine.equals("")) continue;

            String instruction = currentLine.trim();

            // first check for object, than for transformation

            // currentline is an object
            if (instruction.equals("sphere")) {
                object = new Sphere(ruby);
            } else if (instruction.equals("cube")) {
                object = new Cube(ruby);
            } else if (instruction.equals("taperedcylinder")) {
                // TODO: get ratio from SDL!
                object = new TaperedCylinder(ruby, 0.99);
            } else if (instruction.equals("plane")) {
                object = new Plane(chrome);
            } else if (instruction.equals(instruction.toUpperCase())) {
                // textures are always written in caps
                texture = Texture.valueOf(instruction);
            } else {
                // currentline is a transformation
                double[] coords = getCoords(currentLine.trim());

                if (instruction.startsWith("translate")) {
                    translation = new Translation((int) coords[0], (int) coords[1], (int) coords[2]);
                } else if (instruction.startsWith("scale")) {
                    scale = new Scale((int) coords[0], (int) coords[1], (int) coords[2]);
                } else if (instruction.startsWith("rotate")) {
                    rotation = new Rotation().rotateX(coords[0]).rotateY(coords[1]).rotateZ(coords[2]);
                }
            }

            if (object != null) {
                object.addTransformation(scale);
                object.addTransformation(translation);
                object.addTransformation(rotation);
                object.setTexture(texture);
                if(object instanceof Plane) {
                    object.setMaterial(gold);
                }
                objects.add(object);

                // clear object and transformations again (only when object has actually been added
                object = null;
                translation = new Transformation();
                scale = new Transformation();
                rotation = new Transformation();
                texture = Texture.NONE;
            }
        }

        reader.close();

        return objects;
    }

    private static double[] getCoords(String currentLine) {
        System.out.println(currentLine);
        int indexOfFirstSpace = currentLine.indexOf(" ");
        String textWithoutTransformationWord = currentLine.substring(indexOfFirstSpace);
        int x = Integer.parseInt(new Scanner(textWithoutTransformationWord).next());

        String nextPart = currentLine.substring(indexOfFirstSpace).trim();
        // TODO: error when scaling with only one number (when scaling in all directions so we only pass one number in the sd) (: 30 instead of 30 30 30)
        String textStartingWithY = nextPart.substring(nextPart.indexOf(" ")).trim();
        int y = new Scanner(textStartingWithY).nextInt();

        String textStartingWithZ = textStartingWithY.substring(textStartingWithY.indexOf(" ")).trim();
        int z = new Scanner(textStartingWithZ).nextInt();

        return new double[]{x, y, z};
    }


    public static double fresnelToRefr(double fresnel) {
        double sqrt = Math.sqrt(fresnel);
        return (1 + sqrt) / (1 - sqrt);
    }
}
