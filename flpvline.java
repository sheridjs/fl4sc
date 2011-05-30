/* Jay Sheridan
   4/20/2003
 */

// javac -sourcepath c:\Progra~1\jdk1.3.1_03\jiu\ flpvline.java
//
// Downstairs Desktop j2sdk1.4.0_01
// javac -sourcepath c:\Progra~1\j2sdk1.4.0_01\jiu\;. flpvline.java

import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;
//import flstate;

public class flpvline extends Canvas {

    private flstate curState;
    private Line2D.Double maxHeightLine, minHeightLine, waterLine;
    private GeneralPath mainLine;
    private int width, height;
    private static int maxlevel = 8;
    private double values[], delta[];
    private double hiVal, loVal;
    private Random rnum;
    
    public flpvline(flstate theState, int w, int h) {
    	width = w;
	height = h;
	setSize(w,h);
	rnum = new Random(121701);

	values = new double[(1<<maxlevel)+1];
	Arrays.fill(values, 0.0);
	delta = new double[maxlevel+1];
	Arrays.fill(delta, 0.0);

	updateState(theState);
    }

    public void paint(Graphics g) {
	Graphics2D g2;

	g2 = (Graphics2D)g;
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	setBackground(Color.white);
	g2.setStroke(new BasicStroke(1));
	//g2.draw(new Line2D.Double(0,0,10,20));

	g2.setColor(Color.blue);
	g2.draw(waterLine);
	g2.setColor(Color.red);
	g2.draw(minHeightLine);
	g2.draw(maxHeightLine);
	g2.setColor(Color.black);
	g2.draw(mainLine);
    }

    public void updateState(flstate theState) {
	curState = theState;

	maxHeightLine = new Line2D.Double(0,255-curState.maxHeight,width,255-curState.maxHeight);
	minHeightLine = new Line2D.Double(0,255-curState.minHeight,width,255-curState.minHeight);
	waterLine = new Line2D.Double(0,255-curState.waterHeight,width,255-curState.waterHeight);
	generateLine();
    }

    public void generateLine() {
	// Generate Brownian motion preview line
	int i, N;
	double sigma = 1.0;
	
	for(i=1; i<=maxlevel; i++) {
	    delta[i] = sigma*Math.pow(0.5, i*curState.smoothness)*Math.sqrt(1-Math.pow(2.0, 2.0*curState.smoothness-2.0));
	}
	N = 1<<maxlevel;
	values[0] = sigma*rnum.nextGaussian();
	values[N] = sigma*rnum.nextGaussian();
	doRecursion(0,N,1);
	findHiLo();
	drawLine();
    }

    public void generateWaterLine() {

    }
    
    private void doRecursion(int index0, int index2, int level) {
	int index1;
	
	index1 = (index0 + index2) >>> 1;
	values[index1] = 0.5*(values[index0]+values[index2])+delta[level]*rnum.nextGaussian();
	if(level < maxlevel){
	    doRecursion(index0, index1, level+1);
	    doRecursion(index1, index2, level+1);
	}
    }
    
    private void findHiLo() {
	int i, size;
	
	hiVal = loVal = values[0];
	size = (1<<maxlevel)+1;
	
	for(i=0; i<size; i++){
	    if (values[i] > hiVal)
		hiVal = values[i];
	    if (values[i] < loVal)
		loVal = values[i];
	}
    }
    
    private void drawLine(){
	int maxWidth, points, i;
	int yValue=0, prevVal=0, thisVal=0, nextVal=0;
	
	mainLine = new GeneralPath();

	// setup first two values
	prevVal = 255-(int)(curState.minHeight+((curState.maxHeight-curState.minHeight)*Math.pow((values[0]-loVal)/(hiVal-loVal),curState.steepness)+.5));
	thisVal = 255-(int)(curState.minHeight+((curState.maxHeight-curState.minHeight)*Math.pow((values[1]-loVal)/(hiVal-loVal),curState.steepness)+.5));

	yValue = prevVal;
	// no accounting for smoothing on first value
	// account for terracing
	if(curState.terraced) 
	    if ( (prevVal%2 !=0) && (prevVal >= 255-curState.terraceHeight))
		yValue = yValue-1;
	// move to first point
	mainLine.moveTo((float)0,(float)yValue);
	
	// find maximum width to draw
	points = (1<<maxlevel)+1;
	if(width < points)
	    maxWidth = width;
	else
	    maxWidth = points;
	
	for(i=1; i<maxWidth-1; i++) {
	    // setup i+1 values
	    nextVal = 255-(int)(curState.minHeight+((curState.maxHeight-curState.minHeight)*Math.pow((values[i+1]-loVal)/(hiVal-loVal),curState.steepness)+.5));

/*
//rick's idea
int waterLevel = 82;
double waterFactor = 1, waterPow = 1;

nextVal = -1 * (nextVal-255);
if (nextVal < waterLevel) 
{

	nextVal = 255 - (int) (nextVal \ Math.pow((waterLevel-nextVal) * waterFactor, waterPow));
}
else
	nextVal = 255 - nextVal;
*/

	    yValue = thisVal;
	    // account for smoothing
	    if (curState.smoothing){
		yValue = (int)((prevVal+thisVal+nextVal)/3.0);
	    }
	    // account for terracing
	    if (curState.terraced){
		if ( (yValue%2 !=0) && (yValue >= 255-curState.terraceHeight))
		    yValue = yValue-1;
	    }
	    // draw line to current point
	    mainLine.lineTo((float)i,(float)yValue);
	    // rotate stored values
	    prevVal = thisVal;
	    thisVal = nextVal;
	}  
	// setup final value
	yValue = nextVal;
	// no accounting for smoothing again, only terracing
	if(curState.terraced) 
	    if ( (nextVal%2 !=0) && (nextVal >= 255-curState.terraceHeight))
		yValue = yValue-1;
	// draw final point
	mainLine.lineTo((float)i,(float)yValue);

    }
    
    public void resetSeed() {
	rnum = new Random(121701);
    }
    
    public void resetSeed(long seed) {
	rnum = new Random(seed);
    }
    
}






