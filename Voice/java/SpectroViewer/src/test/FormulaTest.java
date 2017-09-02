package test;

import java.util.Calendar;

/**
 * @author unicorn
 */
public class FormulaTest {
    private float rc;
    private float rs;
    private float x;
    private float y;

    public FormulaTest(float period) {
        rc = (float) Math.cos(2*Math.PI/period);
        rs = (float) Math.sin(2 * Math.PI / period);
    }

    public void rotate(float c) {
        float xn = (x-c)*rc - y*rs;
       	y = (x-c)*rs + y*rc;
       	x = xn+c;
    }

    public double a() {
        return Math.sqrt(x*x+y*y);
    }

    public static void main(String[] args) {
        Calendar.getInstance()
/*
        FormulaTest test = new FormulaTest(100);
        FormulaTest test2 = new FormulaTest(100);
        for (int i = 0; i < 10000; i++) {
//            test.rotate((float) Math.sin(i*2*Math.PI/100));
            test2.x = test.x;
            test2.y = test.y;
            test2.rotate(0);
            test.rotate((float) Math.cos(i*8*Math.PI/100)-test2.x);
            System.out.println(test.a()+" "+test.x);
        }
*/
    }
}
