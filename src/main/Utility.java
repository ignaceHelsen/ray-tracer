package main;

public class Utility {
    public static double dot(Vector v1, Vector v2) {
        return v1.getX()*v2.getX() + v1.getY()*v2.getY() + v1.getZ()*v2.getZ();
    }

    public static double[] multiplyVectors(double[] input, double[][] multiply) {
        double[] outputVector = new double[input.length];

        for (int i = 0; i < multiply.length; i++) {
            int sum = 0;
            for (int j = 0; j < multiply[0].length; j++) {
                sum += multiply[i][j] * input[j];
            }
            outputVector[i] = sum;
        }

        return outputVector;
    }
}
