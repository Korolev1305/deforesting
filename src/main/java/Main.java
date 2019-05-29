import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static {
        nu.pattern.OpenCV.loadLibrary();
    }

    public static Image toBufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;

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

    public static boolean cldc(List<List<Double>> clusterPointEarly, List<List<Double>> clusterPointNow){
        boolean result = true;

        for(int i=0;i<clusterPointEarly.size();i++){
            if(!clusterPointEarly.contains(clusterPointNow.get(i))||!clusterPointNow.contains(clusterPointEarly.get(i))){
                result = false;
            }
        }

        return result;
    }

    public static boolean clusterDoNotChange(List<List<Double>> clusterPointEarly, List<List<Double>> clusterPointNow){
        if(clusterPointEarly.size()!=clusterPointNow.size()) {
            return false;
        }
        else {
            return true;
        }
//        if(clusterPointEarly.containsAll(clusterPointNow)&&clusterPointNow.containsAll(clusterPointEarly)){
//            return true;
//        } else {
//            return false;
//        }
    }

    public static void main(String[] args) {
        //Mat image = Highgui.imread("/Users/ewigkeit/Downloads/myfig244.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/Downloads/BaikaldotsWithouBorder.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/Downloads/KavkazSize.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/Downloads/KavkazSmall.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/Downloads/KavkazMedium.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/Downloads/Kavkaz2WithoutBorder.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/Downloads/BaikalSmall.png");

        //Mat image = Highgui.imread("/Users/ewigkeit/maps/2019.png");
        Mat image = Highgui.imread("/Users/ewigkeit/maps/2019-.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/maps/1985-.png");
        //Mat image = Highgui.imread("/Users/ewigkeit/maps/2019--.png");

        int variant=1;
        Gelder gelder = new Gelder();
        gelder.setImage(image);
        //List<List<Double>> allLnMeasure = new ArrayList<>();
        List<Integer> radiuses = new ArrayList<>();
        List<Double> lnRadiuses = new ArrayList<>();
        if(variant==3) {
            radiuses.addAll(Arrays.asList(1, 3, 5, 7, 9, 15, 20));
        } else {
            radiuses.addAll(Arrays.asList(1, 3, 5, 7, 9));
        }
        gelder.setRadiuses(radiuses);
        if(variant == 3) {
            lnRadiuses.addAll(Arrays.asList(Math.log(1), Math.log(3), Math.log(5), Math.log(7), Math.log(9), Math.log(15), Math.log(20)));
        } else{
            lnRadiuses.addAll(Arrays.asList(Math.log(1), Math.log(3), Math.log(5), Math.log(7), Math.log(9)));
        }
        Double maxLnMeasure = 0.0;
        double[][] gelderMatrix = new double[image.rows()][image.cols()];
        int[][] classesMatrix = new int[image.rows()][image.cols()];
        List<Double> gelderValues = new ArrayList<>();
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                List<Integer> measures = gelder.measure(i, j, 6);
                List<Double> lnMeasures = measures.stream()
                        .map(a -> a==0?0:Math.log(a))
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
        List<Double>[][] spectrsMatrix;
        System.out.println();
        if(variant==3) {
            List<Integer> radiusesCoverage = Arrays.asList(1, 2, 3);
            spectrsMatrix = gelder.getMultifractalMatrix(3, classesMatrix, 10, radiusesCoverage);
        } else {
            List<Integer> radiusesCoverage = Arrays.asList(1, 2, 3);
            spectrsMatrix = gelder.getMultifractalMatrix(5, classesMatrix, 10, radiusesCoverage);
        }

//        for (int i = 0; i < image.rows(); i ++) {
//            for (int j = 0; j < image.cols(); j ++) {
//                System.out.print("[");
//                for (int k = 0; k < spectrsMatrix[i][j].size(); k++) {
//
//
//                    System.out.printf("%6.3f", spectrsMatrix[i][j].get(k));
//
//                    System.out.print(" ");
//                }
//                System.out.print("]");
//
//            }
//            System.out.println();
//        }

        int k = 2;

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

        if(variant==1) {
            centers.add(spectrsMatrix[623][141]);
            centers.add(spectrsMatrix[425][283]);
            pointsForCenters.add(new ArrayList<>());
            pointsForCenters.add(new ArrayList<>());
        } else if(variant==2) {
            centers.add(spectrsMatrix[236][157]);
            centers.add(spectrsMatrix[20][247]);
            centers.add(spectrsMatrix[342][157]);
            centers.add(spectrsMatrix[118][20]);
            pointsForCenters.add(new ArrayList<>());
            pointsForCenters.add(new ArrayList<>());
            pointsForCenters.add(new ArrayList<>());
            pointsForCenters.add(new ArrayList<>());
        }else {
            for (int i = 0; i < k; i++) {
                for(;;){
                    Integer x = new Random().nextInt(spectrsMatrix.length);
                    Integer y = new Random().nextInt(spectrsMatrix[0].length);
                    if(!centers.contains(spectrsMatrix[x][y])){
                        centers.add(spectrsMatrix[x][y]);
                        break;
                    }
                }
                pointsForCenters.add(new ArrayList<>());
            }
        }


        for (int q = 0; q < maxIterations; q++) {

            System.out.println("Iteration: " + (q+1));

            for (int i = 0; i < spectrsMatrix.length; i ++) {
                for (int j = 0; j < spectrsMatrix[0].length; j ++) {
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

        if(variant==1) {
            long forest = 0;
            long other = 0;

            for (int i = 0; i < spectrsMatrix.length; i++) {
                for (int j = 0; j < spectrsMatrix[0].length; j++) {
                    //System.out.print(clustersMatrix[i][j] + " ");
                    if (clustersMatrix[i][j] == 1) {
                        forest++;
                    } else {
                        other++;
                    }
                }
                //System.out.println();
            }

            System.out.println();
            System.out.println("Процент соотношения лесных массивов ко всей территории: " + (double) forest / (double) (spectrsMatrix.length * spectrsMatrix[0].length) * 100 + "%");
        }

        List<List<Double>> colors = new ArrayList<>();
        for(int i=0;i<k;i++){
            List<Double> oneColor = new ArrayList<>();
            for(int j=0;j<3;j++) {
                Integer randomColor = new Random().nextInt(255);
                oneColor.add(randomColor.doubleValue());
            }
            colors.add(oneColor);
        }

        for(int i=0;i< spectrsMatrix.length;i ++){
            for(int j=0;j<spectrsMatrix[0].length;j++){
                        image.put(i,j,colors.get(clustersMatrix[i][j]-1).stream().mapToDouble(Double::doubleValue).toArray());
            }
        }

        JPanel panel = new JPanel();

        Image bufferedImage = toBufferedImage(image);
        JLabel label = new JLabel(new ImageIcon(bufferedImage));
        panel.add(label);

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Clustering result");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(panel);

        frame.pack();
        frame.setVisible(true);

    }
}