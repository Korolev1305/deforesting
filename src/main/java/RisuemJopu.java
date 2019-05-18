import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

@Data
public class RisuemJopu extends JPanel {
    private List<Double> lnRadiuses;
    private List<List<Double>> lnMeasures;

    public RisuemJopu(List<Double> lnRadiuses, List<List<Double>> lnMeasures){
        this.lnMeasures = lnMeasures;
        this.lnRadiuses = lnRadiuses;
    }

    public void paint(Graphics g){
        List<Double> measure = lnMeasures.get(0);
        Graphics2D gg = (Graphics2D) g;
            for(int i=0;i<measure.size();i++){
                Ellipse2D.Double shape = new Ellipse2D.Double(lnRadiuses.get(i)*150,800-measure.get(i)*150,30,30);
                gg.draw(shape);
            }
    }
}
