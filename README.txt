Fractal Landscapes for SimCity
==============================
Jay Sheridan
blu_J_bird@yahoo.com

The Java runtime environment (version 1.3.1_03 or later) is REQUIRED to run this program. Go to http://java.sun.com/j2se/downloads.html to download the latest version. Click on the link for the latest version of J2SE, download the JRE (java runtime enviromnet), and install it.

To run the program: 
- Unzip the files into a directory. 
- Doubleclick on fl4sc.jar to execute the program. 

This program was made to be used with SimCity 3000 or SimCity 4, by Maxis. Check out the game at http://simcity3000unlimited.ea.com 
It uses JIU, written by Marco Schmidt. You can check it out at http://jiu.sourceforge.net

General Use:
~~~~~~~~~~~~
Run the program. You may adjust parameters for terrain generation by adjusting the sliders on the lefthand side of the window.

The Terrain Size drop-down box determines how big the generated picture will be. "Quarter Regions" are half the width and half the height (a quarter of the area) of a normal SimCity 4 region. To use anything other than a normal region in SimCity 4, you will need a special config.bmp. See http://moogle.sandwich.net/maxis.html for a description on config.bmp files. SimCity 3000 will not be able to use Normal Regions or Quarter Regions.

The "Terraced to height:" checkbox produces an effect similar to the terracing in SimCity 3000 on "easy" maps.

You may also select "Keep underlying terrain shape." This checkbox will allow you to keep the basic shape of the land but modify values (other than smoothness or terrain size).

When you have adjusted the parameters to your liking, press the "Generate" button. A new terrain will be generated. If you do not like the generated landscape, you can adjust the parameters and/or press the Generate button again. When you have a landscape that is to your liking, press the "Save As" button. Select a directory to put your file in and save it as a .bmp file. (For example, mymap.bmp) Now you need to start SimCity 3000 or SimCity 4 and load the terrain.

Importing Terrain for SC3000:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
The following information on importing bitmaps into SimCity 3000 can be found at http://www.sc3000.com/knowledge/showarticle.cfm?id=0057&openItemID=cid.26,cid.1,cid.2,cid.28

Windows: 
1. Start a new city having the correct size (see aforementioned size chart). 
2. Press Ctrl + Shift + Alt + C all at the same time. A text box pops up the upper left-hand corner of the screen. 
3. Type "load terrain <path to your bitmap>" into the textbox. For example, "load terrain c:\bitmaps\coolcity.bmp". 
4. If the file was found and was in the correct format, your terrain should load. Note that you may need to rotate the city before the lower right-hand corner map will update with your new terrain. 

Macintosh (thanks to Christopher Ison): 
1. Start a new city having the correct size (see aforementioned size chart). 
2. Press Shift + Option + Apple + C all at the same time. A text box pops up in the upper left-hand corner of the screen. 
3. Type "load terrain <folder path to your bitmap>"
4. Important: On the Mac, the path should be formatted as follows:
Folder1:folder2:Folder3:...:file.bmp. Do not use drive letters (as in "C:\..."). Problems may arise if trying to use the desktop folder. Use proper spaces and punctuation. 

Importing Terrain for SC4:
~~~~~~~~~~~~~~~~~~~~~~~~~~
1. Start a new Region.
2. Press Ctrl + Shift + Alt + R all at the same time. A file selection box pops up in the upper left-hand corner.
3. Find and select your bitmap, then press "Ok".
4. The screen should now change to the screen shown when loading any city. Be patient because the importation process can take a long time. (10 minutes or more.) When it's done it will bring you back to the region view of your new region.

Changes:
~~~~~~~~
1.0
- Initial release

1.2
- Added user interface functionality to create and display SimCity 4 sized images.
- Other minor improvements to user interface
- Added palette selection for color image
- Removed Zoom 2x button