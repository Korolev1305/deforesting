import java.util.ArrayList;
import java.util.List;

public class LinearRegression {
    public static Double count(List<Double> x1, List<Double> y1){
        double[] x = new double[x1.size()];
        double[] y = new double[y1.size()];

        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;

        for(int i=0;i<x1.size();i++){
            x[i] = x1.get(i);
            y[i] = y1.get(i);
            sumx  += x[i];
            sumx2 += x[i] * x[i];
            sumy  += y[i];
        }
        double xbar = sumx / x1.size();
        double ybar = sumy / y1.size();

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < x1.size(); i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        // print results
        //System.out.println("y   = " + beta1 + " * x + " + beta0);

        // analyze results
        /*int df = x1.size() - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < x1.size(); i++) {
            double fit = beta1*x[i] + beta0;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        double R2    = ssr / yybar;
        double svar  = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar/x1.size() + xbar*xbar*svar1;
        System.out.println("R^2                 = " + R2);
        System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
        System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
        svar0 = svar * sumx2 / (x1.size() * xxbar);
        System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

        System.out.println("SSTO = " + yybar);
        System.out.println("SSE  = " + rss);
        System.out.println("SSR  = " + ssr);*/

        return beta1;
    }
}
