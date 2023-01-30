package main.sdl;

import main.Material;
import main.object.Cube;
import main.object.Object;
import main.object.Plane;
import main.object.Sphere;
import main.object.TaperedCylinder;
import main.texture.Checkerboard;
import main.texture.Texture;
import main.texture.TextureEnum;
import main.texture.Wood;
import main.transformation.Rotation;
import main.transformation.Scale;
import main.transformation.Transformation;
import main.transformation.Translation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SDL {
    /**
     * Will parse all objects from a file.
     * @param sourcePath: The file to read.
     * @param materials: The materials that will be looked for.
     * @param skipLines: Number of lines to skip before reading the objects. Mostly used for settings.
     * @return: List of materials found in the file.
     * @throws IOException
     */
    public static List<Object> parseObjects(String sourcePath, List<Material> materials, int skipLines) throws IOException {
        List<Object> objects = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));

        Object object = null;
        Transformation translation = new Transformation();
        Transformation scale = new Transformation();
        Transformation rotation = new Transformation();
        Texture texture = null;
        Material material = materials.stream().filter(m -> m.getName().equalsIgnoreCase("ruby")).findAny().orElse(null); // default is ruby
        double ratio = 0; // for taperedcylinder

        int lines = 0;

        while (reader.ready()) {
            String currentLine = reader.readLine();
            if (lines < skipLines) {
                lines++;
                continue;
            }

            lines++;

            if (currentLine.equals("") || currentLine.startsWith("#")) continue;

            String instruction = currentLine.trim();

            // first check for object, then for transformation

            // currentline is an object
            if (materials.stream().map(Material::getName).toList().contains(instruction)) {
                material = materials.stream().filter(m -> m.getName().equalsIgnoreCase(instruction)).findAny().orElse(null);
            } else if (instruction.equalsIgnoreCase("sphere")) {
                object = new Sphere(material);
            } else if (instruction.equalsIgnoreCase("cube")) {
                object = new Cube(material);
            } else if (instruction.equalsIgnoreCase("taperedcylinder")) {
                // TODO: get ratio from SDL!
                object = new TaperedCylinder(material, 0);
            } else if (instruction.equalsIgnoreCase("plane")) {
                object = new Plane(material);
            } else if (Arrays.stream(TextureEnum.values()).anyMatch(t -> t.toString().equalsIgnoreCase(instruction)) && instruction.equals(instruction.toUpperCase())) {
                switch (TextureEnum.valueOf(instruction.toUpperCase())) {
                    case CHECKERBOARD -> texture = new Checkerboard();
                    case WOOD -> texture = new Wood();
                }
            } else {
                // currentline is a transformation
                try {
                    double[] coords = getValues(currentLine.trim());

                    if (instruction.startsWith("translate")) {
                        translation = new Translation((int) coords[0], (int) coords[1], (int) coords[2]);
                    } else if (instruction.startsWith("scale")) {
                        scale = new Scale((int) coords[0], (int) coords[1], (int) coords[2]);
                    } else if (instruction.startsWith("rotate")) {
                        rotation = new Rotation(coords[0], coords[1], coords[2]);
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
                texture = null;
                material = null;
            }
        }

        reader.close();

        return objects;
    }

    public static List<Material> parseMaterial(String sourcePath, int skipLines) throws IOException {
        List<Material> materials = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));
        Material currentMaterial = new Material();

        int lines = 0;
        while (reader.ready()) {
            String currentLine = reader.readLine();
            if (lines < skipLines) {
                lines++;
                continue;
            }

            lines++;


            // in this loop we will cover one material. Therefore, a material in the sdl should be strictly structured
            if (currentLine.equals("") || currentLine.startsWith("#")) continue;

            String text = currentLine.trim().toLowerCase();
            currentMaterial.setName(text);

            currentMaterial.setAmbient(getValues(reader.readLine().trim()));
            currentMaterial.setDiffuse(getValues(reader.readLine().trim()));
            currentMaterial.setSpecular(getValues(reader.readLine().trim()));
            currentMaterial.setRefractionIndex(getValues(reader.readLine()));

            String roughness = reader.readLine().trim();
            currentMaterial.setRoughness(Double.parseDouble(roughness.substring(roughness.indexOf(" "))));

            currentMaterial.setkDistribution(getValues(reader.readLine().trim()));

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

    /**
     * Will get the next three values from a string.
     * @param currentLine: The text to scan.
     * @return: Three values from the text.
     */
    private static double[] getValues(String currentLine) {
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

    public static Settings parseSettings(String sourcePath, int skipLines) throws IOException {
        Settings settings = new Settings();

        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));

        int lines = 0;
        while (reader.ready()) {
            String currentLine = reader.readLine();
            if (lines > 6) break;

            if (lines < skipLines) {
                lines++;
                continue;
            }

            lines++;

            // in this loop we will cover one material. Therefore, a material in the sdl should be strictly structured
            if (currentLine.equals("") || currentLine.startsWith("#")) continue;

            String text = currentLine.trim().toLowerCase();
            String instruction = text.substring(0, text.indexOf(" ")); // first word (= everything before the first space)

            double value = 0;
            boolean trueOrFalse = false;

            try {
                // let's assume the value is a double, if exception: it's a boolean
                value = Double.parseDouble(text.substring(text.indexOf(" "))); // the value of the text (= everything after the first space)
            } catch (NumberFormatException nfe) {
                trueOrFalse = Boolean.parseBoolean(text.substring(text.indexOf(" ")).trim());
            }

            switch (instruction) {
                case "lightsourcefactor":
                    settings.setLightsourceFactor(value);
                case "epsilon":
                    settings.setEpsilon(value);
                case "maxrecurselevel":
                    settings.setMaxRecurseLevel((int) value);
                case "dw":
                    settings.setDw(value);
                case "shadowsenabled":
                    settings.setShadowsEnabled(trueOrFalse);
                case "reflection":
                    settings.setReflection(trueOrFalse);
                case "refraction":
                    settings.setRefraction(trueOrFalse);
            }
        }

        return settings;
    }
}