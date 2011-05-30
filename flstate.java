/* Jay Sheridan
   4/20/2003
 */

// javac -sourcepath c:\Progra~1\jdk1.3.1_03\jiu\ flstate.java
//
// Downstairs Desktop
// javac -sourcepath c:\Progra~1\j2sdk1.4.0_01\jiu\ flstate.java

import java.util.Arrays;
import java.lang.String;
import net.sourceforge.jiu.data.Gray8Image;
import net.sourceforge.jiu.data.Paletted8Image;

public class flstate
{
    public double grid[][];
    public double gridHigh;
    public double gridLow;
    public String landType;
    public double smoothness;
    public double steepness;
    public int maxHeight;
    public int minHeight;
    public int waterHeight;
    public boolean terraced;
    public int terraceHeight;
    public boolean smoothing;
    public Gray8Image grayImage;
    public Paletted8Image colorImage;    
    
    public flstate()
    {
	grid = new double[257][257];
	for(int i=0; i<257; i++) {
	    Arrays.fill(grid[i], 0.0);
	}
	gridHigh = 0.0;
	gridLow = 0.0;
	grayImage = new Gray8Image(257, 257);
	colorImage = new Paletted8Image(257, 257);
	landType = new String("Large City");
	smoothness = 0.7;
	steepness = 2.0;
	maxHeight = 200;
	minHeight = 60;
	waterHeight = 83;
	terraced = false;
	terraceHeight = 255;
	smoothing = false;
    }
}




