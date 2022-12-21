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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SDL {
    public static List<Object> parse(String sourcePath) throws IOException {
        List<Object> objects = new ArrayList<>();

        Map<String, Material> materials = new HashMap<String, Material>() {
            {
                put("ruby", new Material(new double[]{75, 0.01175, 1.01175}, new double[]{75.81424, 0.04136, 1.04136}, new double[]{0.827811, 0.626959, 0.626959}, new double[]{1.762, 1.770, 1.778}, 0.2, new double[]{0.05, 5, 0.001}, 0.6, 0.6, 0.6138));
                put("perfectmirror", new Material(new double[]{0, 0, 0}, new double[]{0, 0, 0}, new double[]{1, 1, 1}, new double[]{1, 1, 1}, 0.2, new double[]{0.5, 0.499, 0.0001}, 0.8, 1, 0.5220));
                put("chrome", new Material(new double[]{0.25, 0.25, 0.25}, new double[]{0.4, 0.4, 0.4}, new double[]{3.1812, 3.1812, 3.1812}, new double[]{3.1812, 3.1812, 3.1812}, 0.2, new double[]{0.4, 0.099, 0.0001}, 0.6, 0, 0));
                put("gold", new Material(new double[]{0.24725, 0.1995, 0.0745}, new double[]{0.85164, 0.70648, 0.32648}, new double[]{0.628281, 0.555802, 0.366065}, new double[]{fresnelToRefr(0.989), fresnelToRefr(0.876), fresnelToRefr(0.399)}, 0.2, new double[]{0.3, 0.399, 0.001}, 0.6, 0, 0));
                put("copper", new Material(new double[]{0.19125, 0.0735, 0.0225}, new double[]{0.7038, 0.27048, 0.0828}, new double[]{0.256777, 0.137622, 0.086014}, new double[]{fresnelToRefr(0.755), fresnelToRefr(0.49), fresnelToRefr(0.095)}, 0.2, new double[]{0.6, 0.3, 0.1}, 0.1, 0, 0));
            }
        };

        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));

        main.object.Object object = null;
        Transformation translation = new Transformation();
        Transformation scale = new Transformation();
        Transformation rotation = new Transformation();
        Texture texture = Texture.NONE;
        Material material = materials.get("ruby"); // default is ruby
        double ratio = 0; // for taperedcylinder

        while (reader.ready()) {
            String currentLine = reader.readLine();
            if (currentLine.equals("")) continue;

            String instruction = currentLine.trim().toLowerCase();

            // first check for object, then for transformation

            // currentline is an object
            if (materials.containsKey(instruction)) {
                material = materials.get(instruction);
            } else if (instruction.equals("sphere")) {
                object = new Sphere(material);
            } else if (instruction.equals("cube")) {
                object = new Cube(material);
            } else if (instruction.equals("taperedcylinder")) {
                // TODO: get ratio from SDL!
                object = new TaperedCylinder(material, 0);
            } else if (instruction.equals("plane")) {
                object = new Plane(material);
            } else if (Arrays.stream(Texture.values()).anyMatch(t -> t.toString().equalsIgnoreCase(instruction))) {
                texture = Texture.valueOf(instruction.toUpperCase());
            } else {
                // currentline is a transformation
                try {
                    double[] coords = getCoords(currentLine.trim());

                    if (instruction.startsWith("translate")) {
                        translation = new Translation((int) coords[0], (int) coords[1], (int) coords[2]);
                    } else if (instruction.startsWith("scale")) {
                        scale = new Scale((int) coords[0], (int) coords[1], (int) coords[2]);
                    } else if (instruction.startsWith("rotate")) {
                        rotation = new Rotation().rotateX(coords[0]).rotateY(coords[1]).rotateZ(coords[2]);
                    }
                } catch(Exception e) {
                    // probably the ratio of a taperedcylinder
                    ratio = Double.parseDouble(instruction);
                }
            }

            if (object != null) {
                object.addTransformation(scale);
                if (object instanceof TaperedCylinder)
                    ((TaperedCylinder) object).setRatio(ratio);
                object.addTransformation(translation);
                object.addTransformation(rotation);
                object.setTexture(texture);
                object.setMaterial(material);
                objects.add(object);

                // clear object and transformations again (only when object has actually been added
                object = null;
                translation = new Transformation();
                scale = new Transformation();
                rotation = new Transformation();
                texture = Texture.NONE;
                material = null;
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
