import lombok.Data;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Gelder {
    private Integer maxWindowRadius;
    private Mat image;
    private Double minGelder;
    private Double maxGelder;

    private Integer getIntance(double[] rgb){
        return new Long(Math.round(rgb[0]*0.299+rgb[1]*0.587+rgb[2]*0.114)).intValue();
    }

    public List<Integer> measure( Integer x, Integer y, Integer eps){
        List<Integer> measures = new ArrayList<Integer>();
        Integer n = 1;
        Integer radius = 2*n+1;
        Double lnRadius = Math.log(radius);
        double[] rgb = image.get(x,y);
        Integer centerValue = getIntance(rgb);
        measures.add(1);
        while(radius <= maxWindowRadius) {
            Integer measure = 0;
            for(int i=x-n;i<=x+n;i++){
                for(int j=y-n;j<=y+n;j++){
                    Integer pixelIntance = 255;
                    if(i>0&&i<image.rows()&&j>0&&j<image.cols()){
                        pixelIntance = getIntance(image.get(i,j));
                    }
                    if(Math.abs(centerValue-pixelIntance)<=eps){
                        measure++;
                    }
                }
            }
            measures.add(measure);
            n++;
            radius = 2*n+1;

        }

        return measures;
    }

    public Integer getNumberOfGelderClass(Double gelderValue,Integer steps){
        Double l = maxGelder-minGelder;
        Double step = l/steps;
        Integer result=0;
        for(int i=0;i<steps;i++){
            if(gelderValue>=step*i&&gelderValue<step*(i+1)){
                result = i+1;
                break;
            }
            if(i==steps-1){
                if(gelderValue>=step*i&&gelderValue<=step*(i+1)){
                    result = i+1;
                    break;
                }
            }
        }

        return result;
    }

    public List<Double>[][] getMultifractalMatrix(Integer r, Integer step,double[][] gelderMatrix,int[][] classesMatrix,Integer steps){
        List<Double> [][] multifractalSpectrs = new List[image.rows()][image.cols()];
        Map<Integer,Integer> [][] multifractalSpectrsNumbers = new Map[image.rows()][image.cols()];
        Integer n = (r-1)/2;
        for(int i=0;i<image.rows();i+=step){
            for(int j=0;j<image.cols();j+=step){
                multifractalSpectrsNumbers[i][j]=new HashMap<>();
                for(int k=i-n;k<=i+n;k++){
                    for(int z=j-n;z<=j+n;z++){
                        Integer classNumber = steps;
                        if(k>0&&k<image.rows()&&z>0&&z<image.cols()){
                            classNumber=classesMatrix[k][z];
                        }
                        Integer count = multifractalSpectrsNumbers[i][j].getOrDefault(classNumber,0);
                        multifractalSpectrsNumbers[i][j].put(classNumber,count+1);
                    }
                }
                multifractalSpectrs[i][j] = new ArrayList<>();
                for(int c=1;c<=steps;c++){
                    Integer value = multifractalSpectrsNumbers[i][j].getOrDefault(c,0);
                    Double partOfMultifractalSpectr = 0.0;
                    if(value==1||value==0){
                        partOfMultifractalSpectr = 0.0;
                    } else {
                        partOfMultifractalSpectr = Math.log(value)/Math.log(n);
                    }
                    multifractalSpectrs[i][j].add(partOfMultifractalSpectr);
                }
            }
        }

        return multifractalSpectrs;
    }

}
