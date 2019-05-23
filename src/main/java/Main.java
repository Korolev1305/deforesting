import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.Highgui.*;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static {
        nu.pattern.OpenCV.loadLibrary();
    }
    public static void main(String[] args){
        Mat image = Highgui.imread("/Users/erzenavampilova/Desktop/study/Диплом/get picture for diplom/draft/myfig.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/maps/2019.png");
        Gelder gelder = new Gelder();
        gelder.setImage(image);
        gelder.setMaxWindowRadius(9);
        //List<List<Double>> allLnMeasure = new ArrayList<>();
        List<Integer> radiuses = new ArrayList<>();
        List<Double> lnRadiuses = new ArrayList<>();
        radiuses.addAll(Arrays.asList(1, 3, 5, 7, 9));
        lnRadiuses.addAll(Arrays.asList(Math.log(1), Math.log(3), Math.log(5), Math.log(7), Math.log(9)));
        Double maxLnMeasure = 0.0;
        double[][] gelderMatrix = new double[image.rows()][image.cols()];
        int[][] classesMatrix = new int[image.rows()][image.cols()];
        List<Double> gelderValues = new ArrayList<>();
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                List<Integer> measures = gelder.measure(i, j, 6);
                List<Double> lnMeasures = measures.stream()
                        .map(a -> Math.log(a))
                        .collect(Collectors.toList());
                //allLnMeasure.add(lnMeasures);

                gelderMatrix[i][j] = LinearRegression.count(lnRadiuses, lnMeasures);
                gelderValues.add(LinearRegression.count(lnRadiuses, lnMeasures));
            }
        }

        System.out.println();

        /*List<Double> gelderValues = allLnMeasure.stream()
                .map(lnMeasure -> LinearRegression.count(lnRadiuses,lnMeasure))
                .collect(Collectors.toList());*/

        //System.out.println(gelderValues);

        //Double minGelder = Collections.min(gelderValues);

        //Double maxGelder = Collections.max(gelderValues);

        Double minGelder = 1000.0;

        Double maxGelder = -1000.0;

        for (int i = 0; i < gelderValues.size(); i++) {

            if (gelderValues.get(i) < minGelder) {
                minGelder = gelderValues.get(i);
            }
            if (gelderValues.get(i) > maxGelder) {
                maxGelder = gelderValues.get(i);
            }
        }


        //Double maxGelderWithount2 = Collections.max(gelderValues.stream().filter(a->a!=2.0).collect(Collectors.toList()));

        gelder.setMinGelder(minGelder);
        gelder.setMaxGelder(maxGelder);

        System.out.println("min=" + minGelder + " ,max=" + maxGelder);

        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                classesMatrix[i][j] = gelder.getNumberOfGelderClass(gelderMatrix[i][j], 10);
                //System.out.print(classesMatrix[i][j]+" ");
            }
            //System.out.println();
        }

        System.out.println();
        List<Integer> radiusesCoverage = Arrays.asList(1, 3, 5);
        List<Double>[][] uuuuu = gelder.getMultifractalMatrix(25, 3, classesMatrix, 10, radiusesCoverage);

        for (int i = 0; i < image.rows(); i += 3) {
            for (int j = 0; j < image.cols(); j += 3) {
                System.out.print("[");
                for (int k = 0; k < uuuuu[i][j].size(); k++) {


                    System.out.printf("%6.3f", uuuuu[i][j].get(k));

                    System.out.print(" ");
                }
                System.out.print("]");

            }
            System.out.println();
        }



        /*JFrame frame= new JFrame("Welecome to JavaTutorial.net");
        frame.getContentPane().add(new RisuemJopu(lnRadiuses,allLnMeasure));
        frame.setSize(1000,1000);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
*/
    }
}