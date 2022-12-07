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

    /**
     * Will element-wise mulitply two matrixes. This is not the mathematical matrix multiplication.
     * @param factor: factor
     * @param vector: vector to multiply
     * @return Element-wise multiplication
     */
    public static double[] multiplyElementWise(double factor, double[] vector) {
        // element-wise multiplication
        double[] outputVector = new double[vector.length];

        for (int i = 0; i < vector.length; i++) {
            outputVector[i] = factor * vector[i];
        }

        return outputVector;
    }

    /**
     * Will element-wise mulitply two matrixes. This is not the mathematical matrix multiplication.
     * @param factor: factor
     * @param vector: vector to multiply
     * @return Element-wise multiplication
     */
    public static Vector multiplyElementWise(double factor, Vector vector) {
        // element-wise multiplication
        double[] outputVector = new double[vector.getCoords().length];

        for (int i = 0; i < vector.getCoords().length; i++) {
            outputVector[i] = factor * vector.getCoords()[i];
        }

        return new Vector(outputVector);
    }

    /**
     * Will element-wise mulitply two matrixes. This is not the mathematical matrix multiplication.
     * @param input: matrix
     * @param multiply: matrix
     * @return Element-wise multiplication
     */
    // Not really a valid mathematical multiplication but I need it
    public static Vector multiplyElementWise(double[] input, double[] multiply) {
        // element-wise multiplication
        double[] outputVector = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            outputVector[i] = input[i] * multiply[i];
        }

        return new Vector(outputVector);
    }

    public static double[] multiplyMatrices(double factor, double[] matrix) {
        double[] outputVector = new double[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            outputVector[i] = matrix[i] * factor;
        }

        return outputVector;
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

    public static Vector subtract(Vector vectorOne, Vector vectorTwo) {
        double[] result = new double[vectorOne.getCoords().length];

        for (int i = 0; i < vectorOne.getCoords().length; i++) {
            result[i] = vectorOne.getCoords()[i] - vectorTwo.getCoords()[i];
        }

        return new Vector(result);
    }

    public static double[] subtract(double[] vectorOne, double[] vectorTwo) {
        // same as vectorone + (-vectortwo)
        return sum(vectorOne, new double[]{vectorTwo[0]*-1, vectorTwo[1]*-1, vectorTwo[2]*-1, vectorTwo[3]*-1});
    }

    public static double norm(double[] vector) {
        return Math.sqrt(dot(vector, vector));
    }

    public static double[] normalize(double[] vector) {
        double[] normalized = new double[vector.length];

        double v = Math.sqrt(Utility.dot(vector, vector));
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = vector[i] / v;
        }

        return normalized;
    }

    public static Vector normalize(Vector vector) {
        double[] normalized = new double[vector.getCoords().length];

        double v = Math.sqrt(Utility.dot(vector, vector));
        for (int i = 0; i < vector.getCoords().length; i++) {
            normalized[i] = vector.getCoords()[i] / v;
        }

        return new Vector(normalized);
    }
}
