import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class MinkowskiDimension {

    // box-counting algorithm for calculating fractal dimension
    private static Map<Double, Double> boxCounting(boolean[][] bw, int startSize, int finalSize, int step) {
        //  all received data will be putted into Map
        Map<Double, Double> result = new HashMap<>();
        // simulate the limit in the original formula by many iterations with changing the size of squares
        for (int squareSize = startSize; squareSize <= finalSize; squareSize += step) {
            int wCount = bw.length / squareSize;
            int hCount = bw[0].length / squareSize;
            // create a matrix of grid, which we will overlay on the original image
            boolean[][] filledBoxes = new boolean[wCount + (bw.length > wCount*squareSize ? 1 : 0)][hCount + (bw[0].length > hCount*squareSize ? 1 : 0)];

            // iterate the black-and-white image
            for (int x = 0; x < bw.length; x++) {
                for (int y = 0; y < bw[0].length; y++) {
                    // if we find a black pixel (aka true)
                    if (bw[x][y]) {
                        // calculate the grid cell to which the found pixel corresponds
                        // and mark it as true in the grid matrix
                        int xBox = x / squareSize;
                        int yBox = y / squareSize;
                        filledBoxes[xBox][yBox] = true;
                    }
                }
            }

            // calculate how many cells of our grid cover the fingerprint outline
            int numberOfSquares = 0;
            for (boolean[] filledBox : filledBoxes) {
                for (int j = 0; j < filledBoxes[0].length; j++) {
                    if (filledBox[j]) {
                        numberOfSquares++;
                    }
                }
            }
            // put iteration information into Map
            // we are interested in the logarithm of the cell size and the logarithm of the number of cells that cover the fingerprint
            result.put(Math.log(squareSize), Math.log(numberOfSquares));
        }
        return result;
    }

    // obtaining linear regression based on data from boxCounting method
    // The library is used to find the straight-line MLS (the method of least squares) is org.apache.commons.math3
    private static SimpleRegression getLinearRegression(BWImage img) {
        SimpleRegression regression = new SimpleRegression();
        boolean[][] bw = img.convertToBwMatrix();
        int finalSize;
        if (bw.length >= bw[0].length) {
            finalSize = bw[0].length / 5;
        } else {
            finalSize = bw.length / 5;
        }
        Map<Double, Double> boxCountingMap = boxCounting(bw, 1, finalSize, 1);
        double[][] dataForRegression = getDataForRegressionByMap(boxCountingMap);
        regression.addData(dataForRegression);
        return regression;
    }

    // obtaining data for linear regression from the output values of the boxCounting method
    private static double[][] getDataForRegressionByMap(Map<Double, Double> map) {
        double[][] dataForRegression = new double[map.size()][2];
        int i = 0;
        for (Map.Entry<Double, Double> entry : map.entrySet()) {
            dataForRegression[i][0] = entry.getKey();
            dataForRegression[i][1] = entry.getValue();
            i++;
        }
        return dataForRegression;
    }

    // the main method -- obtaining fractal dimension
    public static double getDimension(String imgPath) {
        BWImage img = new BWImage(imgPath);
        SimpleRegression regression = getLinearRegression(img);
        //  return value -- modulus of the angular coefficient of the MLS straight line
        return Math.abs(regression.getSlope());
    }
}
