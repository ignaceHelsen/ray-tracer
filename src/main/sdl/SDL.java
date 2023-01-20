package main.sdl;

import main.Material;
import main.Texture;
import main.object.Cube;
import main.object.Object;
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
import java.util.stream.Collectors;

public class SDL {
    public static List<Object> parseObjects(String sourcePath, List<Material> materials) throws IOException {
        List<Object> objects = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));

        Object object = null;
        Transformation translation = new Transformation();
        Transformation scale = new Transformation();
        Transformation rotation = new Transformation();
        Texture texture = Texture.NONE;
        Material material = materials.stream().filter(m -> m.getName().equalsIgnoreCase("ruby")).findAny().orElse(null); // default is ruby
        double ratio = 0; // for taperedcylinder

        while (reader.ready()) {
            String currentLine = reader.readLine();
            if (currentLine.equals("") || currentLine.startsWith("#")) continue;

            String instruction = currentLine.trim().toLowerCase();

            // first check for object, then for transformation

            // currentline is an object
            if (materials.stream().map(Material::getName).toList().contains(instruction)) {
                material = materials.stream().filter(m -> m.getName().equalsIgnoreCase(instruction)).findAny().orElse(null);
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

                } catch (Exception e) {
                    // probably the ratio of a taperedcylinder
                    ratio = Double.parseDouble(instruction);
                }
            }

            if (object != null) {
                object.addTransformation(scale);
                object.addTransformation(rotation);
                object.addTransformation(translation);
                if (object instanceof TaperedCylinder)
                    ((TaperedCylinder) object).setRatio(ratio);
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

    public static List<Material> parseMaterial(String sourcePath) throws IOException {
        List<Material> materials = new ArrayList<>();
        String[] candidateMaterials = new String[]{"ruby", "copper", "gold", "mirror", "chrome"};

        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));
        Material currentMaterial = new Material();

        while (reader.ready()) {
            // in this loop we will cover one material. Therefore, a material in the sdl should be strictly structured
            String currentLine = reader.readLine();
            if (currentLine.equals("") || currentLine.startsWith("#")) continue;

            String text = currentLine.trim().toLowerCase();

            if (Arrays.asList(candidateMaterials).contains(text)) {
                currentMaterial.setName(text);
            }

            currentMaterial.setAmbient(getCoords(reader.readLine().trim()));
            currentMaterial.setDiffuse(getCoords(reader.readLine().trim()));
            currentMaterial.setSpecular(getCoords(reader.readLine().trim()));
            currentMaterial.setRefractionIndex(getCoords(reader.readLine()));

            String roughness = reader.readLine().trim();
            currentMaterial.setRoughness(Double.parseDouble(roughness.substring(roughness.indexOf(" "))));

            currentMaterial.setkDistribution(getCoords(reader.readLine().trim()));

            String shininess = reader.readLine().trim();
            currentMaterial.setShininess(Double.parseDouble(shininess.substring(shininess.indexOf(" "))));

            String transparency = reader.readLine().trim();
            currentMaterial.setTransparency(Double.parseDouble(transparency.substring(transparency.indexOf(" "))));

            String lightspeed = reader.readLine().trim();
            currentMaterial.setFractionOfSpeedOfLight(Double.parseDouble(lightspeed.substring(lightspeed.indexOf(" "))));

            materials.add(currentMaterial);
            // now off to next material
            currentMaterial = new Material();
        }

        return materials;
    }

    private static double[] getCoords(String currentLine) {
        System.out.println(currentLine);
        int indexOfFirstSpace = currentLine.indexOf(" ");
        String textWithoutTransformationWord = currentLine.substring(indexOfFirstSpace);
        double x = Double.parseDouble(new Scanner(textWithoutTransformationWord).useLocale(Locale.US).next());

        String nextPart = currentLine.substring(indexOfFirstSpace).trim();
        // TODO: error when scaling with only one number (when scaling in all directions so we only pass one number in the sd) (: 30 instead of 30 30 30)
        String textStartingWithY = nextPart.substring(nextPart.indexOf(" ")).trim();
        double y = new Scanner(textStartingWithY).useLocale(Locale.US).nextDouble();

        String textStartingWithZ = textStartingWithY.substring(textStartingWithY.indexOf(" ")).trim();
        double z = Double.parseDouble(textStartingWithZ);

        return new double[]{x, y, z};
    }


    public static double fresnelToRefr(double fresnel) {
        double sqrt = Math.sqrt(fresnel);
        return (1 + sqrt) / (1 - sqrt);
    }
}