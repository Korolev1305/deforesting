import jdk.nashorn.internal.objects.annotations.Function;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.Highgui.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static {
        nu.pattern.OpenCV.loadLibrary();
    }

    public static double getDistance(List<Double> x, List<Double> y){
        double distance = 0.0;
        for(int i=0;i<x.size();i++){
            distance+=Math.pow((x.get(i)-y.get(i)),2);
        }
        distance = Math.sqrt(distance);
        return distance;
    }

    public static List<Double> getNewCenter(List<List<Double>> points) {
        List<Double> averageVector = new ArrayList<>();
        List<List<Double>> vector = new ArrayList<>();
        for(int i=0;i<10;i++){
            vector.add(new ArrayList<>());
        }
        points.stream().forEach(point -> {
            for(int i=0;i<10;i++){
                vector.get(i).add(point.get(i));
            }
        });
        averageVector = vector.stream().map(oneVector -> oneVector.stream().mapToDouble(Double::doubleValue).average().getAsDouble()).collect(Collectors.toList());
        double minDistance = 10000000.0;
        List<Double> newCenter = new ArrayList<>();
        for(List<Double> point : points){
            Double distance = getDistance(point,averageVector);
            if(distance<minDistance){
                newCenter = point;
                minDistance = distance;
            }
        }
        return newCenter;
    }

    public static Integer getClusterNumber(List<List<Double>> centers, List<Double> point){
        Integer minDistanceCluster = 1000000;
        Double minDistance = 1000000.0;
        for(List<Double> center : centers){
            Double distance = getDistance(point,center);
            if(distance <minDistance){
                minDistanceCluster=centers.indexOf(center)+1;
                minDistance = distance;
            }
        }
        return minDistanceCluster;
    }

    public static boolean clusterDoNotChange(List<List<Double>> clusterPointEarly, List<List<Double>> clusterPointNow){
        if(clusterPointEarly.containsAll(clusterPointNow)&&clusterPointNow.containsAll(clusterPointEarly)){
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        //Mat image = Highgui.imread("/Users/ewigkeit/Downloads/myfig244.png");
        Mat image = Highgui.imread("/Users/ewigkeit/Downloads/BaikaldotsWithouBorder.png");
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
        List<Double>[][] spectrsMatrix = gelder.getMultifractalMatrix(25, 3, classesMatrix, 10, radiusesCoverage);

        for (int i = 0; i < image.rows(); i += 3) {
            for (int j = 0; j < image.cols(); j += 3) {
                System.out.print("[");
                for (int k = 0; k < spectrsMatrix[i][j].size(); k++) {


                    System.out.printf("%6.3f", spectrsMatrix[i][j].get(k));

                    System.out.print(" ");
                }
                System.out.print("]");

            }
            System.out.println();
        }

        int k = 6;

        List<List<Double>> centers = new ArrayList<>();
        List<List<List<Double>>> pointsForCenters = new ArrayList<>();
        List<List<List<Double>>> pointsForCentersOld = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            pointsForCentersOld.add(new ArrayList<>());
        }
        List<Double> randomPoint = Arrays.asList(0.0,1.0,100.0);
        pointsForCentersOld.get(0).add(randomPoint);
        Integer maxIterations = 100;
        Integer[][] clustersMatrix = new Integer[spectrsMatrix.length][spectrsMatrix[0].length];

        for (int i = 0; i < k; i++) {
            for(;;){
                Integer x = new Random().nextInt(spectrsMatrix.length / 3);
                Integer y = new Random().nextInt(spectrsMatrix[0].length / 3);
                if(!centers.contains(spectrsMatrix[x*3][y*3])){
                    centers.add(spectrsMatrix[x * 3][y * 3]);
                    break;
                }
            }
            pointsForCenters.add(new ArrayList<>());
        }

        for (int q = 0; q < maxIterations; q++) {

            System.out.println("Iteration: " + (q+1));

            for (int i = 0; i < spectrsMatrix.length; i += 3) {
                for (int j = 0; j < spectrsMatrix[0].length; j += 3) {
                    clustersMatrix[i][j] = getClusterNumber(centers, spectrsMatrix[i][j]);
                    pointsForCenters.get(clustersMatrix[i][j] - 1).add(spectrsMatrix[i][j]);
                }
            }

            boolean flag = true;

            for(int i =0;i<pointsForCenters.size();i++){
                if(!clusterDoNotChange(pointsForCentersOld.get(i),pointsForCenters.get(i))){
                    flag = false;
                }
            }

            if(flag){
                break;
            }

            for(int i=0;i<k;i++) {
                centers.set(i,getNewCenter(pointsForCenters.get(i)));
            }

            pointsForCentersOld = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                pointsForCentersOld.add(new ArrayList<>());
            }
            Collections.copy(pointsForCentersOld, pointsForCenters);

            pointsForCenters = new ArrayList<>();

            for (int i = 0; i < k; i++) {
                pointsForCenters.add(new ArrayList<>());
            }

        }

        for (int i = 0; i < spectrsMatrix.length; i += 3) {
            for (int j = 0; j < spectrsMatrix[0].length; j += 3) {
                System.out.print(clustersMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}