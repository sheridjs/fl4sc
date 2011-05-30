/* Jay Sheridan
   4/20/2003
 */

// javac -sourcepath c:\Progra~1\jdk1.3.1_03\jiu\ flutil.java
//
// Downstairs Desktop
// javac -sourcepath c:\Progra~1\j2sdk1.4.0_01\jiu\;. flutil.java

import java.lang.Integer;
import java.lang.Math;
import java.lang.String;
import java.util.Random;
import java.util.Arrays;
import net.sourceforge.jiu.data.Gray8Image;
import net.sourceforge.jiu.data.Paletted8Image;
import net.sourceforge.jiu.data.Palette;
import net.sourceforge.jiu.color.PromoteToPaletted8;
import net.sourceforge.jiu.filters.MeanFilter;
//import flstate;

public class flutil
{
    public Random rnum = new Random();
    
    public void flutil() {
	//    rnum = new Random();
    }
    
    public void generateGrid(flstate theState) {
	// grid and H are in state.
	// H = smoothness and p = steepness
	// maxlevel can be found from state
	double delta = 1.0; // same as sigma from fract3k
	double hpow;
	int addition = 1;
	int N, stage;
	int x, y, D, d;
	int maxlevel;
	
	//rnum = new Random();
	
	maxlevel = findLevels(theState.landType);
	if (maxlevel == -1) 
	    maxlevel = 5;
	
	N = (int)Math.pow(2.0, (double)maxlevel);
	
	// Set the initial random corners.
	theState.grid[0][0] = delta * rnum.nextGaussian();
	theState.grid[0][N] = delta * rnum.nextGaussian();
	theState.grid[N][0] = delta * rnum.nextGaussian();
	theState.grid[N][N] = delta * rnum.nextGaussian();
	D = N;
	d = N >>> 1;
	hpow = Math.pow(0.5, 0.5*theState.smoothness);
	
	for (stage=1; stage<=maxlevel; stage++) {
	    // cout << "Computing level " << stage << " of " << maxlevel << "total...\r";
	    // cout.flush();
	    
	    //Going from grid type I to type II. 
	    delta *= hpow;
	    
	    //Interpolate and offset points 
	    for (x=d; x<=N-d; x+=D)
		for (y=d; y<=N-d; y+=D)
		    theState.grid[x][y] = f4(delta, theState.grid[x+d][y+d], theState.grid[x+d][y-d], theState.grid[x-d][y+d], theState.grid[x-d][y-d]);
	    
	    //Displace other points also if needed
	    if (addition != 0) {
		for (x=0; x<=N; x+=D)
		    for (y=0; y<=N; y+=D)
			theState.grid[x][y] += delta*rnum.nextGaussian();
	    }
	    
	    //Going from grid type II to type I
	    delta *= hpow;
	    
	    //Interpolate and offset boundary grid points.
	    for (x=d; x<=N-d; x+=D) {
		theState.grid[x][0] = f3(delta, theState.grid[x+d][0], theState.grid[x-d][0], theState.grid[x][d]);
		theState.grid[x][N] = f3(delta, theState.grid[x+d][N], theState.grid[x-d][N], theState.grid[x][N-d]);
		theState.grid[0][x] = f3(delta, theState.grid[0][x+d], theState.grid[0][x-d], theState.grid[d][x]);
		theState.grid[N][x] = f3(delta, theState.grid[N][x+d], theState.grid[N][x-d], theState.grid[N-d][x]);
	    }
	    
	    //Interpolate and offset interior grid points.
	    for (x=d; x<=N-d; x+=D)
		for (y=D; y<=N-d; y+=D)
		    theState.grid[x][y] = f4(delta, theState.grid[x][y+d], theState.grid[x][y-d], theState.grid[x+d][y], theState.grid[x-d][y]);
	    for (x=D; x<=N-d; x+=D)
		for (y=d; y<=N-d; y+=D)
		    theState.grid[x][y] = f4(delta, theState.grid[x][y+d], theState.grid[x][y-d], theState.grid[x+d][y], theState.grid[x-d][y]);
	    
	    //Displace other points also if needed
	    if (addition != 0) {
		for (x=0; x<=N; x+=D)
		    for (y=0; y<=N; y+=D)
			theState.grid[x][y] += delta*rnum.nextGaussian();
		for (x=d; x<=N-d; x+=D)
		    for (y=d; y<=N-d; y+=D)
			theState.grid[x][y] += delta*rnum.nextGaussian();
	    }
	    D >>>= 1;
	    d >>>= 1;
	}
	
	findGridHiLo(theState);
	// cout << endl;
    }
    
    public void findGridHiLo(flstate theState) {
	// Determines the highest and lowest values in the data grid
	int size, i, j;
	
	theState.gridHigh = theState.gridLow = theState.grid[0][0];
	size = theState.grid.length;
	
	for (i=0; i<size; i++) {
	    for (j=0; j<size; j++) {
		if (theState.grid[i][j] > theState.gridHigh)
		    theState.gridHigh = theState.grid[i][j];
		if (theState.grid[i][j] < theState.gridLow)
		    theState.gridLow = theState.grid[i][j];
	    }
	}
    }
    
    public void generateImages(flstate theState, Palette pal) {
	int size, x, y, pixvalue;
	PromoteToPaletted8 colorizer;
	
	size = theState.grid.length;
	for (y=0; y<size; y++) {
	    for (x=0; x<size; x++) {
		// force grid to user stats
		pixvalue = (int)(theState.minHeight+((theState.maxHeight-theState.minHeight)*Math.pow((theState.grid[x][y]-theState.gridLow)/(theState.gridHigh-theState.gridLow),theState.steepness)+.5));
		
/*
//rick's idea
int waterLevel = 82;
double waterFactor = 0.3, waterPow = 0.1;
if (pixvalue < waterLevel) 
{
	pixvalue = (int) (pixvalue / Math.pow((waterLevel-pixvalue) * waterFactor, waterPow));
}
*/

		// put pixels into gray image
		theState.grayImage.putSample(0, x, y, pixvalue);
	    }
	}

	// smooth terrain if needed
	if (theState.smoothing)
	    applySmoothing(theState);

	// terrace terrain if needed
	if (theState.terraced)
	    applyTerracing(theState);

	// convert grayscale to color image
	generateColorPreview(theState, pal);
    }

    public void applySmoothing(flstate theState) {
	// uses a mean filter to smooth the grayscale image
	// be sure to re-colorize the color preview image
	MeanFilter filt = new MeanFilter();
	
	filt.setArea(3,3);
	filt.setInputImage(theState.grayImage);
	filt.process();
	theState.grayImage = (Gray8Image)filt.getOutputImage();
    }

    public void applyTerracing(flstate theState) {
	// lowers odd levels of gray image to even levels
	// be sure to re-colorize the color preview image
	int size, x, y, pixvalue;

	size = theState.grid.length;
	for (y=0; y<size; y++) 
	    for (x=0; x<size; x++) {
		pixvalue = theState.grayImage.getSample(0,x,y);
		if ( (pixvalue%2 != 0) 
		     && (pixvalue <= theState.terraceHeight))
		    theState.grayImage.putSample(0,x,y, pixvalue-1);
	    }
    }
	
    public void generateColorPreview(flstate theState, Palette pal) {
	// convert grayscale to color image
	PromoteToPaletted8 colorizer;

	colorizer = new PromoteToPaletted8();
	colorizer.setInputImage(theState.grayImage);
	try {
	    colorizer.process();
	}
	catch(Exception E) {
	    System.out.println(E.toString());
	}
	theState.colorImage = (Paletted8Image)colorizer.getOutputImage();
	theState.colorImage.setPalette(pal);
    }

    public int generatePalette(Palette pal, int type) {
	int ind, waterline;
	waterline = -1;

	switch (type) {
	case 0:
	    // GrayScale
	    for (ind=0; ind<256; ind++)
		pal.put(ind,ind,ind,ind); // index is the color
	    break;
	case 1:
	    // SimCity 3000, water level = 30.5
	    waterline = 30;
	    for (ind=0; ind<256; ind++) {
		if (ind < 31) // blue water
		    pal.put(ind, 0, 0, 110+3*ind); //200);
		else if (ind < 32) // tan beach
		    pal.put(ind, 210, 200, 145);
		else if (ind < 86) { // green to yellow    
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, 0+(155-0)*(ind-32)/53,
			    33+(145-33)*(ind-32)/53,
			    0+(25-0)*(ind-32)/53);
		    /*// for use with sc4 regions
		    if (ind == 82 || ind == 83)
			pal.put(ind, 220, 0, 0); 
		    */
		}
		else if (ind < 126) // yellow to lt.brown
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, 155+(85-155)*(ind-86)/39,
			    145+(60-145)*(ind-86)/39,
			    25+(25-25)*(ind-86)/39);
		else if (ind < 216) // lt.brown to dk.brown
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, 85+(30-85)*(ind-126)/89,
			    60+(10-60)*(ind-126)/89,
			    25+(3-25)*(ind-126)/89);
		else // whites
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, ind-25, ind-25, ind-25);
	    }
	    break;
	case 2:
	    // SimCity 4, water level = 83.33
	    waterline = 83;
	    for (ind=0; ind<256; ind++) {
		if (ind < 82) // blue water
		    pal.put(ind, 0, 0, 118+ind); //200);
		else if (ind < 85) // tan beach, under water
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, 0+(210-0)*(ind-82)/3,
			    0+(200-0)*(ind-82)/3,
			    200+(145-200)*(ind-82)/3);
		else if (ind < 88) // tan beach, above water
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, 210+(0-210)*(ind-85)/3,
			    200+(33-200)*(ind-85)/3,
			    145+(0-145)*(ind-85)/3);
		else if (ind < 141) // green to yellow    
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, 0+(155-0)*(ind-88)/53,
			    33+(145-33)*(ind-88)/53,
			    0+(25-0)*(ind-88)/53);		
		else if (ind < 180) // yellow to lt.brown
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, 155+(85-155)*(ind-141)/39,
			    145+(60-145)*(ind-141)/39,
			    25+(25-25)*(ind-141)/39);
		else if (ind < 269) // lt.brown to dk.brown
		    // spills over because this is sc3k palette, but shifted.
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, 85+(30-85)*(ind-180)/89,
			    60+(10-60)*(ind-180)/89,
			    25+(3-25)*(ind-180)/89);
		else // whites <- can be ignored
		    // lo+(hi-lo)*(curr-logray)/steps
		    pal.put(ind, ind-25, ind-25, ind-25);
	    }
	    break;
	default: 
	    // GrayScale
	    for (ind=0; ind<256; ind++)
		pal.put(ind,ind,ind,ind); // index is the color
	    break;
	}
	return waterline;
    }

/*
    public Vector loadPaletteTxt(BufferedReader palFileReader) {
	// Load a set of Palettes from a file
	Vector palList = new Vector();


	
Some notes:
-If it errors anywhere, try returning a vector with a null first item.
-The second item can then be a string with the error message.
-Otherwise can possibly alternate Palette object and name String or other info.
-Takes a buffered reader as an argument instead of a file.
-File opening would be done by whatever calls this function.
	 
    }
*/

    public void resizeGrid(flstate theState, String str) {
	int levels = 0, size = 0;
	
	if (theState.landType.equalsIgnoreCase(str))
	    { return; }
	
	levels = findLevels(str);
	if (levels == -1) {
	    levels = 8;
	    str = new String("Large");
	}
	size = 1 + (1 << levels);

	theState.grid = new double[size][size];
	for(int i=0; i<size; i++) {
	    Arrays.fill(theState.grid[i], 0.0);
	}
	theState.landType = new String(str);
	theState.grayImage = new Gray8Image(size,size);
	theState.colorImage = new Paletted8Image(size,size);
    }

    public int getImageSize(flstate theState) {
	// finds image/canvas size from land type string in the state
	int levels = 0;

	levels = findLevels(theState.landType);
	if (levels == -1)
	    levels = 0;
	
	return (1 + ( 1 << levels));
    }

    // Private functions 
    private double f3(double delta, double x0, double x1, double x2){
	return (x0+x1+x2)/3 + delta*rnum.nextGaussian();
    }
    
    private double f4(double delta, double x0, double x1, double x2, double x3){
	return (x0+x1+x2+x3)/4 + delta*rnum.nextGaussian();
    }

    private int findLevels(String size) {
	//Uses land size string to return # of levels 
	//  for algorithm and image size
	//Returns -1 if error

	if (size.equalsIgnoreCase("Double Region"))
	    return 11;
	else if (size.equalsIgnoreCase("Normal Region"))
	    return 10;
	else if (size.equalsIgnoreCase("Quarter Region"))
	    return 9;
	else if (size.equalsIgnoreCase("Large City"))
	    return 8;
	else if (size.equalsIgnoreCase("Medium City"))
	    return 7;
	else if (size.equalsIgnoreCase("Small City"))
	    return 6;
	else if (size.equalsIgnoreCase("Tiny City (SC3k)"))
	    return 5;
	else
	    return -1;
    }

}

