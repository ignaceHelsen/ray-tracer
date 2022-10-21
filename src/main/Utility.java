package main;

public class Utility {
    // TODO: add checks on lenghts/sizes
    public static double dot(Vector v1, Vector v2) {
        return v1.getX()*v2.getX() + v1.getY()*v2.getY() + v1.getZ()*v2.getZ();
    }

    public static double dot(double[] v1, double[] v2) {
        double sum = 0;

        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }

        return sum;
    }

    public static double[] multiplyMatrices(double[] input, double[][] multiply) {
        double[] outputVector = new double[input.length];

        for (int i = 0; i < multiply.length; i++) {
            double sum = 0;
            for (int j = 0; j < multiply[0].length; j++) {
                sum += multiply[i][j] * input[j];
            }
            outputVector[i] = sum;
        }

        return outputVector;
    }

    public static double[][] multiplyMatrices(double[][] input, double[][] multiply) {
        double[][] outputMatrix = new double[input.length][input[0].length];
        double[][] transposedMultiply = transpose(multiply);

        for (int i = 0; i < multiply.length; i++) {
            for (int j = 0; j < multiply[0].length; j++) {
                outputMatrix[i][j] = dot(input[i], transposedMultiply[j]);
            }
        }

        return outputMatrix;
    }

    public static double[][] transpose(double[][] matrix) {
        double[][] transposed = new double[matrix.length][matrix[0].length];

        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[0].length; j++) {
                transposed[i][j] = matrix[j][i];
            }
        }

        return transposed;
    }

    public static double[] sum(double[] vectorOne, double[] vectorTwo) {
        double[] sum = new double[vectorOne.length];

        for (int i = 0; i < vectorOne.length; i++) {
            sum[i] = vectorOne[i] + vectorTwo[i];
        }

        return sum;
    }

    public static double[] subtract(double[] vectorOne, double[] vectorTwo) {
        double[] result = new double[vectorOne.length];

        for (int i = 0; i < vectorOne.length; i++) {
            result[i] = vectorOne[i] - vectorTwo[i];
        }

        return result;
    }

    public static double norm(double[] vector) {
        return Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]);
    }
}
