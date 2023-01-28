package main;

import java.util.ArrayList;
import java.util.List;

public class RenderSettings {
    public static double[][] aaFourMap = {{-0.5, -0.5}, {0.5, -0.5}, {-0.5, 0.5}, {0.5, 0.5}};
    public static double[][] aaNineMap = {{-0.5, -0.5}, {0, -0.5}, {0.5, -0.5},
                                          {-0.5, 0}, {0, 0}, {0.5, 0},
                                          {-0.5, 0.5}, {0, 0.5}, {0.5, 0.5}};

    public static List<int[]> getSpiralOrder(int columns, int rows) {
        int i, k = 0, l = 0;
        int m = rows;
        int n = columns;

        // total number of
        // elements in matrix
        int total = m * n;

        List<int[]> spiralFormIndices = new ArrayList<>();

        /* k - starting row index
        m - ending row index
        l - starting column index
        n - ending column index
        i - iterator */

        // initialize the count
        int cnt = 0;

        while (k < m && l < n) {
            if (cnt == total)
                break;

            // Print the first column
            // from the remaining columns
            for (i = k; i < m; ++i) {
                spiralFormIndices.add(new int[]{i, l});
                cnt++;
            }
            l++;

            if (cnt == total)
                break;

            // Print the last row from
            // the remaining rows
            for (i = l; i < n; ++i) {
                spiralFormIndices.add(new int[]{m - 1, i});
                cnt++;
            }
            m--;

            if (cnt == total)
                break;

            // Print the last column
            // from the remaining columns
            if (k < m) {
                for (i = m - 1; i >= k; --i) {
                    spiralFormIndices.add(new int[]{i, n - 1});
                    cnt++;
                }
                n--;
            }

            if (cnt == total)
                break;

            // Print the first row
            // from the remaining rows
            if (l < n) {
                for (i = n - 1; i >= l; --i) {
                    spiralFormIndices.add(new int[] {k, i});
                    cnt++;
                }
                k++;
            }
        }

        return spiralFormIndices;
    }
}
