/* Jay Sheridan
   4/20/2003
 */

// Compile/Run info
// javac -sourcepath c:\Progra~1\jdk1.3.1_03\jiu\ fl4sc.java
// java -cp c:\Progra~1\jdk1.3.1_03\jiu\;. fl4sc
// 
// Downstairs Desktop
// javac -sourcepath c:\Progra~1\j2sdk1.4.0_01\jiu\;. fl4sc.java
// java -cp c:\Progra~1\j2sdk1.4.0_01\jiu\;. fl4sc


// These are deffinately needed
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.Integer;
import java.lang.Double;
import java.util.Random;
import javax.swing.border.TitledBorder;
import net.sourceforge.jiu.gui.awt.ImageCanvas;
import java.awt.image.*;
import net.sourceforge.jiu.data.Palette;
import net.sourceforge.jiu.data.Paletted8Image;
import net.sourceforge.jiu.gui.awt.ImageCreator;
import net.sourceforge.jiu.gui.awt.RGBA;
import net.sourceforge.jiu.codecs.BMPCodec;
import net.sourceforge.jiu.transform.ScaleReplication;
import java.io.*;
import java.io.FileOutputStream;

// My classes
//import flstate;
//import flutil;
//import flpvline;

// These might be needed
/*
import java.awt.Toolkit;
*/

public class fl4sc implements ChangeListener, ActionListener, FocusListener
{
    static final String VERSION = new String("1.02");

    // Windows and panels
    JFrame frm_MainWindow;
    // Menu and related
    JMenuBar mub_MainMenu;
    JMenu mnu_File, mnu_Edit, mnu_Help;
    JMenuItem mui_SaveAs, mui_Exit;
    JMenuItem mui_Smoothing;
    JMenuItem mui_Readme, mui_About;
    //JSplitPane spn_MainSplit;
    JPanel pnl_MainSplit;
    JPanel pnl_MainLeft, pnl_MainRight;
    JPanel pnl_UpLeft, pnl_DownLeft, pnl_UpRight, pnl_DownRight;
    JPanel pnl_Smoothness, pnl_Steepness, pnl_MaxHeight, pnl_MinHeight;
    JPanel pnl_LandSize, pnl_Palette, pnl_Combos, pnl_Sliders, pnl_Buttons;
    JPanel pnl_PreviewGray, pnl_PreviewColor, pnl_MainCanvas;
    ScrollPane spn_MainCanvas;
    JFileChooser jfc_SaveAs;
    GridBagConstraints gbc_Sliders;
    // Parameters GUI
    // - Sliders and related
    JSlider sld_Smoothness, sld_Steepness, sld_MaxHeight, sld_MinHeight;
    JLabel lbl_Smoothness, lbl_Steepness, lbl_MaxHeight, lbl_MinHeight;
    JTextField txf_Smoothness, txf_Steepness, txf_MaxHeight, txf_MinHeight;
    // - Checkboxes and related
    JCheckBox chb_Terrace, chb_Smoothing, chb_HoldData;
    JTextField txf_Terrace;
    // - Other
    JComboBox cmb_LandSize, cmb_Palette;
    JLabel lbl_LandSize, lbl_Palette;
    JButton btn_Generate;
    JButton btn_SaveAs;
    flstate defaultState;
    flstate theState;
    flutil utilBox;
    // Images and related
    ImageCanvas cnv_PreviewGray, cnv_PreviewColor, cnv_BlankSpreader;
    ImageCanvas cnv_MainCanvas;
    flpvline pvl_PreviewLine;
    Random rnd_PreviewLine = new Random(1337);
    Palette  pal_Color;
    int palleteType = 0;
    JButton btn_View2x;

    // Temporary
    JTextField txf_Test; //+ temp feedback textfield
    
    public static void main(String[] args)
    {
	new fl4sc();
    }
    
    public fl4sc()
    {
	ImageIcon mapIcon = new ImageIcon("./mapicon.img");
	frm_MainWindow = new JFrame("Fractal Landscapes for SimCity");
	//nothing 0, hide 1, dispose 2.		
	frm_MainWindow.setDefaultCloseOperation(2);
	// set the icon
	frm_MainWindow.setIconImage(mapIcon.getImage());	
	//to actually close on exit:	   
	frm_MainWindow.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) { 
		    System.exit(0);
		}
	    });
	//setup	Windows
	makeObjects();
	doLayout();		
	
	frm_MainWindow.getContentPane().add(pnl_MainSplit);
	frm_MainWindow.setJMenuBar(mub_MainMenu);
	frm_MainWindow.pack();
	frm_MainWindow.show();
    }
    
    public void	makeObjects()
    {
	// Menus
	mub_MainMenu = new JMenuBar();
	mnu_File = new JMenu("File");
	mnu_Edit = new JMenu("Edit");
	mnu_Help = new JMenu("Help");
	mui_SaveAs = new JMenuItem("Save As");
	mui_SaveAs.setToolTipText("Save As");
	mui_SaveAs.addActionListener(this);
	mui_Exit = new JMenuItem("Exit");
	mui_Exit.setToolTipText("Exit");
	mui_Exit.addActionListener(this);
	mui_Smoothing = new JMenuItem("Smooth Map");
	mui_Smoothing.setToolTipText("Smoothing Button");
	mui_Smoothing.addActionListener(this);
	mui_Readme = new JMenuItem("View Readme");
	mui_Readme.setToolTipText("Readme");
	mui_Readme.addActionListener(this);
	mui_About = new JMenuItem("About");
	mui_About.setToolTipText("About");
	mui_About.addActionListener(this);

	// Windows and Panels

	pnl_MainSplit = new JPanel(new BorderLayout());
	pnl_MainLeft = new JPanel(new BorderLayout()); 
	pnl_MainRight = new JPanel(new BorderLayout());
	      
	pnl_UpLeft = new JPanel(new BorderLayout());
	pnl_UpLeft.setBorder(new TitledBorder("Parameters"));
	pnl_DownLeft = new JPanel(new FlowLayout());
	pnl_DownLeft.setBorder(new TitledBorder("Preview:"));
	pnl_UpRight = new JPanel(new BorderLayout());
	pnl_UpRight.setBorder(new TitledBorder("Actual Bitmap"));
	pnl_DownRight = new JPanel(new BorderLayout());
	pnl_DownRight.setBorder(new TitledBorder("Color Preview"));
	
	pnl_Sliders = new JPanel(new GridBagLayout());
	gbc_Sliders = new GridBagConstraints();
	jfc_SaveAs = new JFileChooser("./");
	jfc_SaveAs.setFileSelectionMode(JFileChooser.FILES_ONLY);

	// Slider holding panels
	pnl_Smoothness = new JPanel(new BorderLayout());
	pnl_Steepness = new JPanel(new BorderLayout());
	pnl_MaxHeight = new JPanel(new BorderLayout());
	pnl_MinHeight = new JPanel(new BorderLayout());

	// Image holding panels
	pnl_PreviewGray = new JPanel(new FlowLayout());
	pnl_PreviewColor = new JPanel(new FlowLayout());
	//+
	spn_MainCanvas = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
	spn_MainCanvas.setSize(520,520);
	pnl_MainCanvas = new JPanel(new BorderLayout());
	pnl_MainCanvas.setBorder(new TitledBorder("Current Image"));
	pnl_MainCanvas.setMinimumSize(new Dimension(257, 257));
	pnl_MainCanvas.setMaximumSize(new Dimension(257, 257));
	pnl_MainCanvas.setPreferredSize(new Dimension(257, 257));

	// Sliders and related
	// sld_Smoothness, sld_Steepness, sld_MaxHeight, sld_MinHeight;
	sld_Smoothness = new JSlider(SwingConstants.HORIZONTAL, 0, 1000, 700);
	sld_Smoothness.setToolTipText("Smoothness Slider"); 
	sld_Smoothness.addChangeListener(this);
	sld_Steepness = new JSlider(SwingConstants.HORIZONTAL, 0, 4000, 2000);
	sld_Steepness.setToolTipText("Steepness Slider");
	sld_Steepness.addChangeListener(this);
	sld_MaxHeight = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 200);
	sld_MaxHeight.setToolTipText("Maximum Height Slider");
	sld_MaxHeight.addChangeListener(this);
	sld_MinHeight = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 60);
	sld_MinHeight.setToolTipText("Minimum Height Slider");
	sld_MinHeight.addChangeListener(this);

	txf_Smoothness = new JTextField(Double.toString(sld_Smoothness.getValue()/1000.0), 4);
	txf_Smoothness.setToolTipText("Smoothness Field");
	txf_Smoothness.addActionListener(this);
	txf_Smoothness.addFocusListener(this);
	txf_Steepness = new JTextField(Double.toString(sld_Steepness.getValue()/1000.0), 4);
	txf_Steepness.setToolTipText("Steepness Field");
	txf_Steepness.addActionListener(this);
	txf_Steepness.addFocusListener(this);
	txf_MaxHeight = new JTextField(Integer.toString(sld_MaxHeight.getValue()), 4);
	txf_MaxHeight.setToolTipText("Maximum Height Field");
	txf_MaxHeight.addActionListener(this);
	txf_MaxHeight.addFocusListener(this);
	txf_MinHeight = new JTextField(Integer.toString(sld_MinHeight.getValue()), 4);
	txf_MinHeight.setToolTipText("Minimum Height Field");
	txf_MinHeight.addActionListener(this);
	txf_MinHeight.addFocusListener(this);

	lbl_Smoothness = new JLabel("Smoothness");
	lbl_Steepness = new JLabel("Steepness");
	lbl_MaxHeight = new JLabel("Maximum Height");
	lbl_MinHeight = new JLabel("Minimum Height");

	// Checkboxes and related
	// chb_Terrace, chb_Smoothing, chb_HoldData
	chb_Terrace = new JCheckBox("Terraced to height: ");
	chb_Terrace.setToolTipText("Terrace Checkbox");
	chb_Terrace.addActionListener(this); 
	chb_Smoothing = new JCheckBox("Smoothing");
	chb_Smoothing.setToolTipText("Smoothing Checkbox");
	chb_Smoothing.addActionListener(this); 
	chb_HoldData = new JCheckBox("Keep underlying terrain shape");
	chb_HoldData.setToolTipText("HoldData Checkbox");
	chb_HoldData.addActionListener(this);

	txf_Terrace = new JTextField("255", 4);
	txf_Terrace.setToolTipText("Terrace Field");
	txf_Terrace.addActionListener(this);
	txf_Terrace.addFocusListener(this);

	// Others
	// pnl/cmb_LandSize, pnl/cmb_Palette
	pnl_LandSize = new JPanel(new BorderLayout());
	cmb_LandSize = new JComboBox();
	//cmb_LandSize.addItem("Double Region");
	cmb_LandSize.addItem("Normal Region");
	cmb_LandSize.addItem("Quarter Region");
	cmb_LandSize.addItem("Large City");
	cmb_LandSize.addItem("Medium City");
	cmb_LandSize.addItem("Small City");
	cmb_LandSize.addItem("Tiny City (SC3k)");
	cmb_LandSize.setSelectedIndex(2); // Large
	lbl_LandSize = new JLabel("Terrain Size");
	defaultState = new flstate();
	theState = new flstate();
	utilBox = new flutil();

	pnl_Palette = new JPanel(new BorderLayout());
	cmb_Palette = new JComboBox();
	cmb_Palette.addItem("Grayscale");
	cmb_Palette.addItem("SimCity 3000");
	cmb_Palette.addItem("SimCity 4");
	cmb_Palette.setSelectedIndex(2); // SC4
	cmb_Palette.addActionListener(this);
	lbl_Palette = new JLabel("Palette");

	pnl_Combos = new JPanel(new BorderLayout());

	btn_Generate = new JButton("Generate");
	btn_Generate.setToolTipText("Generate");
	btn_Generate.addActionListener(this);
	btn_SaveAs = new JButton("Save As");
	btn_SaveAs.setToolTipText("Save As");
	btn_SaveAs.addActionListener(this);

	pnl_Buttons = new JPanel(new GridLayout(2,1));

	// Images
	pal_Color = new Palette(256);
	utilBox.generatePalette(pal_Color, 2); // SC4
	utilBox.generateGrid(defaultState);
	utilBox.generateImages(defaultState, pal_Color);
	theState = defaultState;
	
	//defaultState.colorImage.setPalette(pal_Color);
	//theState.colorImage.setPalette(pal_Color);

	pvl_PreviewLine = new flpvline(defaultState, 257, 256);

	cnv_PreviewGray = new ImageCanvas(null, 257, 257);
	cnv_PreviewGray.setImage(ImageCreator.convertToAwtImage(defaultState.grayImage,RGBA.DEFAULT_ALPHA));

	cnv_PreviewColor = new ImageCanvas(null, 257, 257);
	cnv_PreviewColor.setImage(ImageCreator.convertToAwtImage(defaultState.colorImage,RGBA.DEFAULT_ALPHA));

	cnv_BlankSpreader = new ImageCanvas(null, 258, 1);

	btn_View2x = new JButton("Zoom 2x");
	btn_View2x.setToolTipText("Zoom 2x");
	btn_View2x.addActionListener(this);

	// Temporary
	txf_Test = new JTextField(20);
	ToolTipManager.sharedInstance().setEnabled(false);
    }
    
    public void	doLayout()
    {
	// Menu
	mnu_File.add(mui_SaveAs);
	mnu_File.addSeparator();
	mnu_File.add(mui_Exit);
	mnu_Edit.add(mui_Smoothing);
	mnu_Help.add(mui_Readme);
	mnu_Help.add(mui_About);
	mub_MainMenu.add(mnu_File);
	mub_MainMenu.add(mnu_Edit);
	mub_MainMenu.add(mnu_Help);

	// Left Side
	pnl_Smoothness.add(lbl_Smoothness, "North");
	pnl_Smoothness.add(sld_Smoothness, "West");
	pnl_Smoothness.add(txf_Smoothness, "East");
	pnl_Steepness.add(lbl_Steepness, "North");
	pnl_Steepness.add(sld_Steepness, "West");
	pnl_Steepness.add(txf_Steepness, "East");
	pnl_MaxHeight.add(lbl_MaxHeight, "North");
	pnl_MaxHeight.add(sld_MaxHeight, "West");
	pnl_MaxHeight.add(txf_MaxHeight, "East");
	pnl_MinHeight.add(lbl_MinHeight, "North");
	pnl_MinHeight.add(sld_MinHeight, "West");
	pnl_MinHeight.add(txf_MinHeight, "East");

	pnl_LandSize.add(lbl_LandSize, "North");
	pnl_LandSize.add(cmb_LandSize, "West");
	pnl_Palette.add(lbl_Palette, "North");
	pnl_Palette.add(cmb_Palette, "West");
	pnl_Combos.add(pnl_LandSize,"West");
	pnl_Combos.add(pnl_Palette,"East");

	gbc_Sliders.gridx = 1;
	gbc_Sliders.gridy = 1;
	gbc_Sliders.gridwidth = 2;
	gbc_Sliders.gridheight = 1;
	pnl_Sliders.add(pnl_Smoothness,gbc_Sliders);
	gbc_Sliders.gridy = 2;
	pnl_Sliders.add(pnl_Steepness,gbc_Sliders);
	gbc_Sliders.gridy = 3;
	pnl_Sliders.add(pnl_MaxHeight,gbc_Sliders);
	gbc_Sliders.gridy = 4;
	pnl_Sliders.add(pnl_MinHeight,gbc_Sliders);
	// skip terracing for now
	gbc_Sliders.gridy = 6;
	gbc_Sliders.anchor = GridBagConstraints.WEST;
	pnl_Sliders.add(chb_Smoothing,gbc_Sliders);
	gbc_Sliders.gridy = 7;
	pnl_Sliders.add(chb_HoldData,gbc_Sliders);
	// do terracing now
	gbc_Sliders.gridy = 5;
	gbc_Sliders.gridwidth = 1;
	pnl_Sliders.add(chb_Terrace,gbc_Sliders);
	gbc_Sliders.gridx = 2;
	gbc_Sliders.anchor = GridBagConstraints.EAST;
	pnl_Sliders.add(txf_Terrace,gbc_Sliders);

	pnl_Buttons.add(btn_Generate);
	pnl_Buttons.add(btn_SaveAs);
	
	pnl_UpLeft.add(pnl_Combos, "North");
	pnl_UpLeft.add(pnl_Sliders, "Center");
	pnl_UpLeft.add(pnl_Buttons, "South");

	pnl_DownLeft.add(pvl_PreviewLine); 
	
	pnl_MainLeft.add(pnl_UpLeft, "North");
	pnl_MainLeft.add(pnl_DownLeft, "Center");

	//Right side
	/*
	pnl_UpRight.add(cnv_BlankSpreader, "North");
	pnl_UpRight.add(cnv_PreviewGray, "Center");
	
	pnl_DownRight.add(cnv_PreviewColor, "North");
	pnl_DownRight.add(btn_View2x, "Center");
	pnl_DownRight.add(btn_SaveAs, "South");

	pnl_MainRight.add(pnl_UpRight, "North");
	pnl_MainRight.add(pnl_DownRight, "South");
	*/
	
	//+Central canvas
	//spn_MainCanvas.setViewportView(pnl_UpRight);
	pnl_MainCanvas.add(cnv_PreviewColor, "Center");
	spn_MainCanvas.add(pnl_MainCanvas);

	pnl_MainSplit.add(pnl_MainLeft, "West");
	pnl_MainSplit.add(spn_MainCanvas, "Center");
	//pnl_MainSplit.add(pnl_MainRight, "East");
	//pnl_MainSplit.add(txf_Test, "South");
    }

    public void stateChanged(ChangeEvent e)
    {
	Object evtObj;

	evtObj = e.getSource();
	if (evtObj.equals(sld_Smoothness)) { //Smoothness Slider
	    onSmoothnessSld();
	}
	else if (evtObj.equals(sld_Steepness)) { //Steepness Slider 
	    onSteepnessSld();
	}
	else if (evtObj.equals(sld_MaxHeight)) { //Maximum Height Slider 
	    onMaxHeightSld();
	}
	else if (evtObj.equals(sld_MinHeight)) { //Minimum Height Slider 
	    onMinHeightSld();
	}
    }

    public void actionPerformed(ActionEvent e)
    {
	Object evtObj;

	evtObj = e.getSource();
	//txf_Test.setText(evtObj.toString()); //+ testing
	if (evtObj.equals(txf_Smoothness)) { //Smoothness Field
	    onSmoothnessTxf();
	}
	else if (evtObj.equals(txf_Steepness)) { //Steepness Field
	    onSteepnessTxf();
	}
	else if (evtObj.equals(txf_MaxHeight)) { //Maximum Height Field
	    onMaxHeightTxf();
	}
	else if (evtObj.equals(txf_MinHeight)) { //Minimum Height Field
	    onMinHeightTxf();
	} 
	else if (evtObj.equals(txf_Terrace)) { //Terrace 
	    onTerraceTxf();
	}
	else if (evtObj.equals(chb_Terrace)) { //Terrace Checkbox
	    onTerraceChb();
	}
	else if (evtObj.equals(chb_Smoothing)) { //Smoothing Checkbox
	    onSmoothingChb();
	}
	else if (evtObj.equals(chb_HoldData)) { //HoldData Checkbox
	    onHoldDataChb();
	}
	else if (evtObj.equals(btn_Generate)) { //Generate
	    onGenerateBtn();
	}
	else if (evtObj.equals(cmb_Palette)) { // Palette Combo Box
	    onPaletteCmb();
	}
	else if (evtObj.equals(mui_SaveAs) || 
		 evtObj.equals(btn_SaveAs)) { //Save As
	    onSaveAsBtn();
	}
	else if (evtObj.equals(btn_View2x)) { //Zoom 2x
	    onZoomBtn();
	}
	else if (evtObj.equals(mui_Readme)) { //Readme
	    onReadmeBtn();
	}
	else if (evtObj.equals(mui_About)) { //About
	    onAboutBtn();
	}
	else if (evtObj.equals(mui_Smoothing)) { //Smoothing Button
	    onSmoothingBtn();
	}
	else if (evtObj.equals(mui_Exit)) { //Exit

	    System.exit(0);
	}
    }

    public void focusGained(FocusEvent e)
    {}

    public void focusLost(FocusEvent e)
    {
	Object evtObj;

	evtObj = e.getSource();
	if (evtObj.equals(txf_Smoothness)) { //Smoothness Field
	    onSmoothnessTxf();
	}
	else if (evtObj.equals(txf_Steepness)) { //Steepness Field
	    onSteepnessTxf();
	}
	else if (evtObj.equals(txf_MaxHeight)) { //Maximum Height Field
	    onMaxHeightTxf();
	}
	else if (evtObj.equals(txf_MinHeight)) { //Minimum Height Field
	    onMinHeightTxf();
	} 
	else if (evtObj.equals(txf_Terrace)) { //Terrace 
	    onTerraceTxf();
	}
    }

    public void onSmoothnessSld()
    // callback for the Smoothness Slider
    {	
	Double value;

	//if(!chb_HoldData.isSelected()) {
	    txf_Test.setText("Smoothness Slider " + sld_Smoothness.getValue()); //+ testing
	    value = new Double( sld_Smoothness.getValue()/1000.0 );
	    txf_Smoothness.setText( value.toString() );
	    theState.smoothness = value.doubleValue();
	    drawPreviewLine();
	    //}
    }

    public void onSteepnessSld()
    // callback for the Steepness Slider
    {
	Double value;
	txf_Test.setText("Steepness Slider " + sld_Steepness.getValue()); //+ testing
	value = new Double( sld_Steepness.getValue()/1000.0 );
	txf_Steepness.setText( value.toString() );
	theState.steepness = value.doubleValue();
	drawPreviewLine();
    }

    public void onMaxHeightSld()
    // callback for the MaxHeight Slider
    {
	Integer value;
	txf_Test.setText("Max Height Slider " + sld_MaxHeight.getValue()); //+ testing
	value = new Integer( sld_MaxHeight.getValue() );
	txf_MaxHeight.setText( value.toString() );
	theState.maxHeight = value.intValue();

	if (value.intValue() < sld_MinHeight.getValue()) {
	    sld_MinHeight.setValue( value.intValue() );
	    txf_MinHeight.setText( value.toString() );
	    theState.minHeight = value.intValue();
	}
	drawPreviewLine();
    }

    public void onMinHeightSld()
    // callback for the MinHeight Slider
    {
	Integer value;
	txf_Test.setText("Min Height Slider " + sld_MinHeight.getValue()); //+ testing
	value = new Integer( sld_MinHeight.getValue() );
	txf_MinHeight.setText( value.toString() );
	theState.minHeight = value.intValue();

	if (value.intValue() > sld_MaxHeight.getValue()) {
	    sld_MaxHeight.setValue( value.intValue() );
	    txf_MaxHeight.setText( value.toString() );
	    theState.maxHeight = value.intValue();
	}
	drawPreviewLine();
    }

    public void onSmoothnessTxf()
    // callback for the Smoothness Text Field
    {
	Double value;

	txf_Test.setText(txf_Smoothness.getText()); //+ testing
	try {
	    value = Double.valueOf(txf_Smoothness.getText());
	    // check bounds
	    if ((int)(value.doubleValue()*1000) > sld_Smoothness.getMaximum()){
		value = new Double((sld_Smoothness.getMaximum()/1000.0));
		txf_Smoothness.setText(value.toString());
	    }
	    else if ((int)(value.doubleValue()*1000) < sld_Smoothness.getMinimum()) {
		value = new Double((sld_Smoothness.getMinimum()/1000.0));
		txf_Smoothness.setText(value.toString());
	    }
	    sld_Smoothness.setValue( (int)(value.doubleValue()*1000) );
	    theState.smoothness = value.doubleValue();
	}
	catch (Exception e) {
	    txf_Test.setText(e.toString()); //+ testing
	    txf_Smoothness.setText( Double.toString(defaultState.smoothness) );
	    sld_Smoothness.setValue( (int)(defaultState.smoothness*1000) );
	    theState.smoothness = defaultState.smoothness;
	}
    }

    public void onSteepnessTxf()
    // callback for the Steepness Text Field
    {
	Double value;
	txf_Test.setText(txf_Steepness.getText()); //+ testing
	try {
	    value = Double.valueOf(txf_Steepness.getText());
	    // check bounds
	    if ((int)(value.doubleValue()*1000) > sld_Steepness.getMaximum()){
		value = new Double((sld_Steepness.getMaximum()/1000.0));
		txf_Steepness.setText(value.toString());
	    }
	    else if ((int)(value.doubleValue()*1000) < sld_Steepness.getMinimum()) {
		value = new Double((sld_Steepness.getMinimum()/1000.0));
		txf_Steepness.setText(value.toString());
	    }
	    sld_Steepness.setValue( (int)(value.doubleValue()*1000) );
	    theState.steepness = value.doubleValue();
	}
	catch (Exception e) {
	    txf_Test.setText(e.toString()); //+ testing
	    txf_Steepness.setText( Double.toString(defaultState.steepness) );
	    sld_Steepness.setValue( (int)(defaultState.steepness*1000) );
	    theState.steepness = defaultState.steepness;
	}
    }

    public void onMaxHeightTxf()
    // callback for the MaxHeight Text Field
    {
	Integer value;
	txf_Test.setText(txf_MaxHeight.getText()); //+ testing
	try {
	    value = Integer.valueOf(txf_MaxHeight.getText());
	    // check bounds
	    if (value.intValue() > sld_MaxHeight.getMaximum()){
		value = new Integer(sld_MaxHeight.getMaximum());
		txf_MaxHeight.setText(value.toString());
	    }
	    else if (value.intValue() < sld_MaxHeight.getMinimum()) {
		value = new Integer((sld_MaxHeight.getMinimum()));
		txf_MaxHeight.setText(value.toString());
	    }
	    sld_MaxHeight.setValue( value.intValue() );
	    theState.maxHeight = value.intValue();

	    if (value.intValue() < sld_MinHeight.getValue()) {
		sld_MinHeight.setValue( value.intValue() );
		txf_MinHeight.setText( value.toString() );
		theState.minHeight = value.intValue();
	    }
	}
	catch (Exception e) {
	    txf_Test.setText(e.toString()); //+ testing
	    txf_MaxHeight.setText( Integer.toString(defaultState.maxHeight) );
	    sld_MaxHeight.setValue( defaultState.maxHeight );
	    theState.maxHeight = defaultState.maxHeight;

	    if ( defaultState.maxHeight < sld_MinHeight.getValue()) {
		sld_MinHeight.setValue( defaultState.maxHeight );
		txf_MinHeight.setText( Integer.toString(defaultState.maxHeight) );
		theState.minHeight = defaultState.minHeight;
	    }
	}
    }

    public void onMinHeightTxf()
    // callback for the MinHeight Text Field
    {
	Integer value;
	txf_Test.setText(txf_MinHeight.getText()); //+ testing
	try {
	    value = Integer.valueOf(txf_MinHeight.getText());
	    // check bounds
	    if (value.intValue() > sld_MinHeight.getMaximum()){
		value = new Integer(sld_MinHeight.getMaximum());
		txf_MinHeight.setText(value.toString());
	    }
	    else if (value.intValue() < sld_MinHeight.getMinimum()) {
		value = new Integer((sld_MinHeight.getMinimum()));
		txf_MinHeight.setText(value.toString());
	    }
	    sld_MinHeight.setValue( value.intValue() );
	    theState.minHeight = value.intValue();

	    if (value.intValue() > sld_MaxHeight.getValue()) {
		sld_MaxHeight.setValue( value.intValue() );
		txf_MaxHeight.setText( value.toString() );
		theState.maxHeight = value.intValue();
	    }
	}
	catch (Exception e) {
	    txf_Test.setText(e.toString()); //+ testing
	    txf_MinHeight.setText( Integer.toString(defaultState.minHeight) );
	    sld_MinHeight.setValue( defaultState.minHeight );
	    theState.minHeight = defaultState.minHeight;

	    if ( defaultState.minHeight > sld_MaxHeight.getValue()) {
		sld_MaxHeight.setValue( defaultState.minHeight );
		txf_MaxHeight.setText( Integer.toString(defaultState.minHeight) );
		theState.maxHeight = defaultState.maxHeight;
	    }
	}
    }

  public void onTerraceTxf() {
      // callback for the Terrace Text Field
      Integer value;
      txf_Test.setText(txf_Terrace.getText()); //+ testing
      try {
	value = Integer.valueOf(txf_Terrace.getText());
	    // check bounds
	    if (value.intValue() > 255){
		value = new Integer(255);
		txf_Terrace.setText(value.toString());
	    }
	    else if (value.intValue() < 0) {
		value = new Integer(0);
		txf_Terrace.setText(value.toString());
	    }
	    theState.terraceHeight = value.intValue();
	    drawPreviewLine();
	}
	catch (Exception e) {
	    txf_Test.setText(e.toString()); //+ testing
	    txf_Terrace.setText( Integer.toString(defaultState.terraceHeight) );
	    theState.terraceHeight = defaultState.terraceHeight;
	    drawPreviewLine();
	}
    }

    public void onTerraceChb() {
	// callback for the Terrace Checkbox
	theState.terraced = chb_Terrace.isSelected();
	drawPreviewLine();
    }

    public void onSmoothingChb() {
	// callback for the Smoothing Checkbox
	theState.smoothing = chb_Smoothing.isSelected();
	drawPreviewLine();
    }

    public void onHoldDataChb() {
	// callback for the HoldData Checkbox
	sld_Smoothness.setEnabled(!chb_HoldData.isSelected());
	txf_Smoothness.setEnabled(!chb_HoldData.isSelected());
	cmb_LandSize.setEnabled(!chb_HoldData.isSelected());
    }

    public void onGenerateBtn() {
	// callback for the Generate button
	int imgSize;
	Dimension imgDimension;

	txf_Test.setText("Generating Images..."); //+ Doesn't show up?
	// Set up images
        utilBox.resizeGrid(theState,(String)cmb_LandSize.getSelectedItem());
	if(!chb_HoldData.isSelected())
	    utilBox.generateGrid(theState);
	utilBox.generateImages(theState, pal_Color);
	txf_Test.setText("Done");
	
	// Display Images
	drawPreviewLine();

	// Set up canvases
	imgSize = utilBox.getImageSize(theState);
	imgDimension = new Dimension(imgSize, imgSize);
	pnl_MainCanvas.setMinimumSize(imgDimension);
	pnl_MainCanvas.setMaximumSize(imgDimension);
	pnl_MainCanvas.setPreferredSize(imgDimension);

	/*
	cnv_PreviewGray.setSize(imgDimension);
	cnv_PreviewGray.setImage(ImageCreator.convertToAwtImage(theState.grayImage,RGBA.DEFAULT_ALPHA));
	cnv_PreviewGray.repaint();
	*/
	
	cnv_PreviewColor.setSize(imgDimension);
	cnv_PreviewColor.setImage(ImageCreator.convertToAwtImage(theState.colorImage,RGBA.DEFAULT_ALPHA));
	cnv_PreviewColor.repaint();

	// Fire resize event to reset scroll pane
	frm_MainWindow.dispatchEvent(new ComponentEvent(frm_MainWindow, ComponentEvent.COMPONENT_RESIZED));
    }

    public void onPaletteCmb() {
	// callback for the Palette Combo Box
	int waterline;

	txf_Test.setText("Palette Combo Box "+cmb_Palette.getSelectedIndex());
	// Get new palette: Gray=0, SC3k=1, SC4=2
	waterline = utilBox.generatePalette(pal_Color,cmb_Palette.getSelectedIndex());
	if (waterline > 0)
	    theState.waterHeight = waterline;

	// Display Images
	drawPreviewLine();
	theState.colorImage.setPalette(pal_Color);
	cnv_PreviewColor.setImage(ImageCreator.convertToAwtImage(theState.colorImage,RGBA.DEFAULT_ALPHA));
	cnv_PreviewColor.repaint();	
    }

    public void onSaveAsBtn() {
	// callback for the Save As button
	int jfcValue, pathLength;
	String savePath, tmpPath;
	BMPCodec saveCodec = new BMPCodec();
	JOptionPane saveStatus = new JOptionPane();

	jfcValue = jfc_SaveAs.showSaveDialog(frm_MainWindow);
	if (jfcValue == JFileChooser.APPROVE_OPTION) {
	    savePath = jfc_SaveAs.getSelectedFile().getPath();
	    pathLength = savePath.length();
	    tmpPath = savePath.substring(pathLength-4, pathLength);
	    tmpPath.toLowerCase();
	    // check for .bmp ending
	    if(!tmpPath.equalsIgnoreCase(".bmp"))
		savePath = savePath + ".bmp";
	    txf_Test.setText(savePath);
	    try	{ // try saving the selected file
		saveCodec.setOutputStream(new FileOutputStream(savePath));
		saveCodec.setImage(theState.grayImage);
		saveCodec.process();
		saveStatus.showMessageDialog(frm_MainWindow, "Saved as " + savePath, "Save Successful", JOptionPane.INFORMATION_MESSAGE);
	    }
	    catch(Exception e) {
		saveStatus.showMessageDialog(frm_MainWindow, "Save Failed", "Warning:", JOptionPane.ERROR_MESSAGE);
		txf_Test.setText("Save Failed.");
	    }	
	}
    }

    public void onZoomBtn() {
	// http://java.sun.com/docs/books/tutorial/uiswing/components/dialog.html
	// callback for the Zoom button
	JOptionPane zoomDialog = new JOptionPane();
	ImageCanvas zoomCanvas;
	ScaleReplication zoomer = new ScaleReplication();
	int w, h;

	w = theState.colorImage.getWidth();
	h = theState.colorImage.getHeight();
	zoomer.setInputImage(theState.colorImage);
	zoomer.setSize(2*w, 2*h);
	try {
	    zoomer.process();
	}
	catch(Exception E) {
	    System.out.println(E.toString());
	}

	zoomCanvas = new ImageCanvas(ImageCreator.convertToAwtImage(zoomer.getOutputImage(),RGBA.DEFAULT_ALPHA), 2*w, 2*h);

	zoomDialog.showMessageDialog(frm_MainWindow, zoomCanvas, "Color Preview, 2x Zoom", JOptionPane.PLAIN_MESSAGE);
    }

    public void onReadmeBtn() {
	// callback for Readme menu button
	FileReader readmeReader;
	BufferedReader readmeBufReader;
    	JOptionPane readmeDialog = new JOptionPane();
	JScrollPane readmePanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	JTextArea readmeText = new JTextArea(30, 40);
	String readmeString, tmpString;

	readmeString = new String("");
	try { // open readme file
	    readmeReader = new FileReader("./README.txt");
	    readmeBufReader = new BufferedReader(readmeReader);

	    try { // read readme file
		while( readmeBufReader.ready() ) {
		    tmpString = readmeBufReader.readLine();
		    readmeString = readmeString + tmpString + "\n";
		}
	    }
	    catch(Exception e) {
		txf_Test.setText(e.toString()); //+ testing
	    }
	    try { // close readme files
		readmeBufReader.close();
		readmeReader.close();
	    }
	    catch(Exception e) {
		txf_Test.setText(e.toString()); //+ testing
	    }
	}
	catch(Exception e) {
	    readmeString = new String("Unable to open README.txt");
	    txf_Test.setText(e.toString()); //+ testing
	}

	readmeText.setLineWrap(true);
	readmeText.setWrapStyleWord(true);
	readmeText.setText(readmeString);
	readmeText.setCaretPosition(0);
	readmeText.setEditable(false);

	readmePanel.setViewportView(readmeText);
	readmeDialog.showMessageDialog(frm_MainWindow, readmePanel, "Readme:", JOptionPane.PLAIN_MESSAGE);
    }

    public void onAboutBtn() {
	// callback for About menu button
	JOptionPane aboutDialog = new JOptionPane();
	aboutDialog.showMessageDialog(frm_MainWindow, " Version: " + VERSION + "\n\n Send questions or comments\n to: blu_J_bird@yahoo.com\n\n http://moogle.sandwich.net", "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onSmoothingBtn() {
	// Callback for smoothing menu button
	// Smoothes current image
	utilBox.applySmoothing(theState);
	utilBox.generateColorPreview(theState, pal_Color);

	/*
	cnv_PreviewGray.setImage(ImageCreator.convertToAwtImage(theState.grayImage,RGBA.DEFAULT_ALPHA));
	cnv_PreviewGray.repaint();
	*/

	cnv_PreviewColor.setImage(ImageCreator.convertToAwtImage(theState.colorImage,RGBA.DEFAULT_ALPHA));
	cnv_PreviewColor.repaint();
    }

    public void drawPreviewLine(){
	pvl_PreviewLine.resetSeed();
	pvl_PreviewLine.updateState(theState);
	pvl_PreviewLine.repaint();
    }
    
}

/*	Previous test code
{
private	Image iii;
	
public static void main(String[] args)
{
    new fl4sc();		
}
	
public fl4sc()
    {
	Frame mainWindow = new CloseableFrame();
	Gray8Image img = new Gray8Image(20, 20);
	Image displayimg;		
	ImageCanvas can = new ImageCanvas(null,200,200);
				//Canvas can2 =	new Canvas();
	BMPCodec mycodec = new BMPCodec();
					
				// Testing an actual existing image
	Toolkit	toolkit	= Toolkit.getDefaultToolkit();
	iii = toolkit.getImage("C:\\Personal\\Andra\\andra.jpg");
	MediaTracker mediaTracker = new MediaTracker(mainWindow);
	mediaTracker.addImage(iii,0);
	try
	    {
		mediaTracker.waitForID(0);
	    }
	catch (InterruptedException ie)
	    {
		System.err.println(ie);
		System.exit(1);
	    }
					
				// this	part works
	int i, j;
	for (i=0; i<20; i++)
	    {
		for (j=0; j<20; j=j+2)
		    {
			img.putWhite(i,	j);	// default background for images seems to be black.
		    }
	    }
	img.putSample(0,10,10,100);
	can.setBackground(Color.red);
							
				// this	part works now!
	displayimg = ImageCreator.convertToAwtImage(img,RGBA.DEFAULT_ALPHA);
				//iii =	displayimg;
	can.setImage(displayimg);
				//can.repaint();
				//can.paint(displayimg.getGraphics());

				// this	part works
	try	{
	    mycodec.setOutputStream(new FileOutputStream("./TestingJIU.bmp"));
	    mycodec.setImage(img);
	    mycodec.process();
	}
	catch(Exception	e)
	    {}	
	mainWindow.setTitle("Fractal Landscapes	for SimCity");
	mainWindow.setLayout(new FlowLayout());
	mainWindow.add(can);
	mainWindow.pack();
	mainWindow.show();
    }
							 
public	void paint(Graphics	g)
    {
	g.drawImage(iii, 0, 0, null);
    }
}
*/

