package pcdd;

import javax.swing.JFrame; 
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.lwjgl.util.vector.Vector3f;

import LWJGLEngine.Entity;


/**
 * Purpose : Builds and controls the user interface used in this program for selecting different parts.
 * @param panel : The panel onto which buttons and text fields are placed.
 * @param bChangeX : Button for requesting to choose a new X.
 * @param bFinish : Button for allowing the user to exit and receive a build summary.
 * @param tXDimensions : Text field for entering the product name of a(n) X.
 * @param state : Class object of RenderLoop used to control rendering.
 * @param Dimensions : 2D array of the dimensions for each part.
 * @param ImageURLWithTitle : 2D array of the name and image URL for each part.
 * @param wattage : array of ints representing the wattage required by the currently chosen GPU and CPU
 * @param cpuSocket : String representing the socket type of the currently chosen CPU
 * @param moboSocket : String representing the socket type of the currently chosen motherboard
 * @param ramDoubleDataRate : String representing the DDR type of the currently chosen RAM
 * @param moboDoubleDataRate : String representing the DDR type of the currently chosen motherboard
 */
public class GUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	JPanel panel = new JPanel();
	JButton bStartProgram = new JButton("Start Program");
	JButton bChangeCase = new JButton("Change Case");
	JButton bChangeGPU = new JButton("Change GPU");
	JButton bChangePowerSupply = new JButton("Change Power Supply");
	JButton bChangeMotherboard = new JButton("Change Motherboard");
	JButton bChangeCPU = new JButton("Change CPU");
	JButton bChangeRAM = new JButton("Change RAM");
	JButton bFinish = new JButton("Finished?");
	JTextField tCaseDimensions = new JTextField("Enter Case Name", 20);
	JTextField tGPUDimensions = new JTextField("Enter GPU Name", 20);
	JTextField tPowerSupplyDimensions = new JTextField("Enter Power Supply Name", 20);
	JTextField tMotherboardDimensions = new JTextField("Enter Motherboard Name", 20);
	JTextField tCPUDimensions = new JTextField("Enter CPU Name", 20);
	JTextField tRAMDimensions = new JTextField("Enter RAM Name", 20);
	
	private static RenderLoop state = new RenderLoop();
	private static double[][] Dimensions = new double[6][3];
	private static String [][] ImageURLWithTitle = new String[6][2];
	private static int wattage[] = new int[2];
	private static int PSwattage;
	private static String cpuSocket = "";
	private static String moboSocket = "";
	private static String ramDoubleDataRate = "";
	private static String moboDoubleDataRate = "";


	/**
	 * Purpose : Configures the GUI to run on thread 1.
	 */
	public static void main (String[] args) throws IOException {	
				
		for(int i = 0; i < Dimensions.length; i++) {
			for(int k = 0; k < Dimensions[0].length; k++)
				Dimensions[i][k] = 0;
		}
		
		Thread t1 = new Thread() {
			public void run() {
					new GUI();
			}
		};
		t1.start();
	}
	
	/**
	 * Purpose : Configures the render to run on thread 2 and sets up all the button and text field action events.
	 */
	public GUI() {
		super("PC Digital Designer");		
		setSize(400,300);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Thread t2 = new Thread() {
			/**
			 * Purpose : Creates the display, creates potential build parts, and starts the render.
			 * @param no args used
			 * @return void
			 */
			public void run() {
				state.createDisplay();
				state.createParts();
				state.render();
			}
		};
		
		bStartProgram.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Opens thread 2 to run the display, and changes the GUI panel to begin by allowing a case to be chosen
    		 * @param ActionEvent e : event of the button being clicked
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
	        		t2.start();
	        		bStartProgram.setVisible(false);
	        		panel.remove(bStartProgram);
	        		panel.add(tCaseDimensions);
            }	
        });
		
		bChangeCase.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Changes the GUI panel to allow for the user to update their case choice.
    		 * @param ActionEvent e : event of the button being clicked
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
        			panel.removeAll();
	            	panel.add(tCaseDimensions);
	            	state.removePart("case");
	            	validate();
	        		repaint();
            }	
        });
		
		bChangeGPU.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Changes the GUI panel to allow for the user to update their GPU choice.
    		 * @param ActionEvent e : event of the button being clicked
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
            		wattage[0] = 0;
            		state.removePart("PowerSupply");
            		panel.removeAll();
	            	panel.add(tGPUDimensions);
	            	panel.add(bChangeCase);
	            	panel.add(bChangeMotherboard);
	            	panel.add(bChangeCPU);
	            	panel.add(bChangeRAM);
	            	state.removePart("gpu");
	            	if(state.containsPart("gpu") && state.containsPart("cpu")) panel.add(bChangePowerSupply);
	            	validate();
	        		repaint();
            }	
        });
		
		bChangePowerSupply.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Changes the GUI panel to allow for the user to update their Power Supply choice.
    		 * @param ActionEvent e : event of the button being clicked
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
            		panel.removeAll();
	            	panel.add(tPowerSupplyDimensions);
	            	panel.add(bChangeCase);
	            	panel.add(bChangeGPU);
	            	panel.add(bChangeMotherboard);
	            	panel.add(bChangeCPU);
	            	panel.add(bChangeRAM);
	            	state.removePart("PowerSupply");
	            	validate();
	        		repaint();
            }	
        });
		
		bChangeMotherboard.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Changes the GUI panel to allow for the user to update their Motherboard choice.
    		 * @param ActionEvent e : event of the button being clicked
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
            		panel.removeAll();
	            	panel.add(tMotherboardDimensions);
	            	panel.add(bChangeCase);
	            	panel.add(bChangeGPU);
	            	panel.add(bChangeCPU);
	            	panel.add(bChangeRAM);
	            	state.removePart("motherboard");
	            	if(state.containsPart("gpu") && state.containsPart("cpu")) panel.add(bChangePowerSupply);
	            	validate();
	        		repaint();
            }	
        });
		
		bChangeCPU.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Changes the GUI panel to allow for the user to update their CPU choice.
    		 * @param ActionEvent e : event of the button being clicked
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
            		wattage[1] = 0;
            		state.removePart("PowerSupply");
            		panel.removeAll();
	            	panel.add(tCPUDimensions);
	            	panel.add(bChangeCase);
	            	panel.add(bChangeGPU);
	            	panel.add(bChangeMotherboard);
	            	panel.add(bChangeRAM);
	            	state.removePart("cpu");
	            	if(state.containsPart("gpu") && state.containsPart("cpu")) panel.add(bChangePowerSupply);
	            	validate();
	        		repaint();
            }	
        });

		bChangeRAM.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Changes the GUI panel to allow for the user to update their RAM choice.
    		 * @param ActionEvent e : event of the button being clicked
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
            		panel.removeAll();
	            	panel.add(tRAMDimensions);
	            	panel.add(bChangeCase);
	            	panel.add(bChangeGPU);
	            	panel.add(bChangeMotherboard);
	            	state.removePart("ram");
	            	if(state.containsPart("gpu") && state.containsPart("cpu")) panel.add(bChangePowerSupply);
	            	validate();
	        		repaint();
            }	
        });
		
		bFinish.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Finalizes the users choices and calls printPDF to make a build summary.
    		 * @param ActionEvent e : event of the button being clicked
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
            		printPDF();
            }	
        });
		
		tCaseDimensions.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Reads in the users case choice, gets that parts dimensions, then checks if the part produced an error,
		 				and if no error is found uses the dimensions to scale the object when adding it to the render.
    		 * @param ActionEvent e : event of the user pressing enter on their keyboard
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
        			
            	String item = tCaseDimensions.getText();
            	String[] split = getDimensionsAndImage(0, item);
            	int errorCode = setDimensions(0, split);
        		if(errorCode == 1) {
    				tCaseDimensions.setText("ERROR");
            		return;
        		}
        		else if(errorCode == 2) {
    				tCaseDimensions.setText("INVALID DIMENSIONS");
            		return;
        		}
        		
        		double temp;
        		temp = Dimensions[0][0];
        		Dimensions[0][0] = Dimensions[0][1];
        		Dimensions[0][1] = temp;
        		temp = Dimensions[0][1];
        		Dimensions[0][1] = Dimensions[0][2];
        		Dimensions[0][2] = temp;
        		if(Dimensions[0][2] < Dimensions[0][0]) {
		        	temp = Dimensions[0][2];
		        	Dimensions[0][2] = Dimensions[0][0];
		        	Dimensions[0][0] = temp;
        		}
            	state.spawnPart(0, (float)Dimensions[0][0], (float)Dimensions[0][1], (float)Dimensions[0][2]);
            	//1 2 0
        		printDim();
            	setPositions();
            	setPanel();
            }
        });
		
		tGPUDimensions.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Reads in the users GPU choice, gets that parts dimensions, specifications, and image URL, then checks if the part produced an error,
		 				and if no error is found uses the dimensions to scale the object when adding it to the render.
    		 * @param ActionEvent e : event of the user pressing enter on their keyboard
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
	    			
            	String item = tGPUDimensions.getText();
            	String[] split = getDimensionsAndImage(1, item);
            	int errorCode = setDimensions(1, split);
        		if(errorCode == 1) {
    				tGPUDimensions.setText("ERROR");
            		return;
        		}
        		else if(errorCode == 2) {
    				tGPUDimensions.setText("INVALID DIMENSIONS");
            		return;
        		}
            	state.spawnPart(1, (float)Dimensions[1][0], (float)Dimensions[1][1], (float)Dimensions[1][2]);
            	//2 1 0
            	//0 1 2
	    		printDim();
            	setPositions();
            	setPanel();
            }
        });
		
		tPowerSupplyDimensions.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Reads in the users Power Supply choice, gets that parts dimensions, specifications, and image URL, then checks if the part produced an error,
						and if no error is found uses the dimensions to scale the object when adding it to the render.
    		 * @param ActionEvent e : event of the user pressing enter on their keyboard
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
	    			
	            	String item = tPowerSupplyDimensions.getText();
	            	String[] split = getDimensionsAndImage(2, item);
	            	int errorCode = setDimensions(2, split);
	        		if(errorCode == 1) {
	    				tPowerSupplyDimensions.setText("ERROR");
	            		return;
	        		}
	        		else if(errorCode == 2) {
	    				tPowerSupplyDimensions.setText("INVALID DIMENSIONS");
	            		return;
	        		}
	        		else if(errorCode == 5) {
	    				tPowerSupplyDimensions.setText("BAD WATTAGE");
	            		return;
	        		}
	            	state.spawnPart(2, (float)Dimensions[2][0], (float)Dimensions[2][1], (float)Dimensions[2][2]);
	            	// 2 1 0
		    		printDim();
	            	setPositions();
	            	setPanel();
            }
        });
		
		tMotherboardDimensions.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Reads in the users Motherboard choice, gets that parts dimensions, specifications, and image URL, then checks if the part produced an error,
		  				and if no error is found uses the dimensions to scale the object when adding it to the render.
    		 * @param ActionEvent e : event of the user pressing enter on their keyboard
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
	    			
	            	String item = tMotherboardDimensions.getText();
	            	String[] split = getDimensionsAndImage(3, item);
	            	int errorCode = setDimensions(3, split);
	        		if(errorCode == 1) {
	    				tMotherboardDimensions.setText("ERROR");
	            		return;
	        		}
	        		else if(errorCode == 2) {
	    				tMotherboardDimensions.setText("INVALID DIMENSIONS");
	            		return;
	        		}
	        		
            		else if(errorCode == 3) {
            			tMotherboardDimensions.setText("BAD SOCKET");
	            		return;
            		}
	        		
            		else if(errorCode == 4) {
            			tMotherboardDimensions.setText("BAD DOUBLE DATA RATE");
	            		return;
            		}
	        		
	            	state.spawnPart(3, (float)Dimensions[3][0], (float)Dimensions[3][1], (float)Dimensions[3][2]);
	            	// 1 2 0
		    		printDim();
	            	setPositions();
	            	setPanel();
            }
        });
		
		tCPUDimensions.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Reads in the users CPU choice, gets that parts dimensions, specifications, and image URL, then checks if the part produced an error,
    		 * 			and if no error is found uses the dimensions to scale the object when adding it to the render.
    		 * @param ActionEvent e : event of the user pressing enter on their keyboard
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
	    			
		        	String item = tCPUDimensions.getText();
	            	String[] split = getDimensionsAndImage(4, item);
	            	int errorCode = setDimensions(4, split);
		    		if(errorCode == 1) {
						tCPUDimensions.setText("ERROR");
		        		return;
		    		}
		    		else if(errorCode == 2) {
						tCPUDimensions.setText("INVALID DIMENSIONS");
		        		return;
		    		}
		    		
		    		else if(errorCode == 3) {
		    			tCPUDimensions.setText("BAD SOCKET");
	            		return;
            		}
		            	
		        	if(Dimensions[4][0] < 2)
		        		state.spawnPart(4, (float)Dimensions[4][0], (float)Dimensions[4][1], (float)Dimensions[4][2]);
		        	else
		        		state.spawnPart(4, (float)Dimensions[4][0]/2.70933333333f, (float)Dimensions[4][1]/2.70933333333f, (float)Dimensions[4][2]/2.70933333333f);
		        	// 1 0 2
		    		printDim();
		        	setPositions();
		        	setPanel();
            }
        });
		
		tRAMDimensions.addActionListener(new ActionListener() {
            @Override
            /**
    		 * Purpose : Reads in the users RAM choice, gets that parts dimensions, specifications, and image URL, then checks if the part produced an error,
    		 * 			and if no error is found uses the dimensions to scale the object when adding it to the render.
    		 * @param ActionEvent e : event of the user pressing enter on their keyboard
    		 * @return void
    		 */
            public void actionPerformed(ActionEvent e) {
	    			
	            	String item = tRAMDimensions.getText();
	            	String[] split = getDimensionsAndImage(5, item);
	            	int errorCode = setDimensions(5, split);
            		if(errorCode == 1) {
        				tRAMDimensions.setText("ERROR");
	            		return;
            		}
            		else if(errorCode == 2) {
        				tRAMDimensions.setText("INVALID DIMENSIONS");
	            		return;
            		}
            		
            		else if(errorCode == 4) {
        				tRAMDimensions.setText("BAD DOUBLE DATA RATE");
	            		return;
            		}
	            	if (Dimensions[5][1] < .5)
	            		state.spawnPart(5, (float)Dimensions[5][0], (float)Dimensions[5][1], (float)Dimensions[5][2]);
	            	else
	            		state.spawnPart(5, (float)Dimensions[5][0]/1.34f, (float)Dimensions[5][1]/1.34f, (float)Dimensions[5][2]/2.96f);
            		printDim();
	            	setPositions();
	            	setPanel();
            }
        });
		
		
		panel.add(bStartProgram);
		add(panel);
		
		setVisible(true);
	}
	
	/**
	 * Purpose : Finds and parses the Amazon page of the user selected item to get its dimensions and specifications, then determines the main image from the Amazon page, 
	 * 			or if it can't be determined, grabs the first image from Google images
	 * @param itemtext : The product name that the user choose.
	 * @param itemID : Represents the type of part that is being searched for.
	 * @return String array containing the part's length, width and height.
	 */
	public static String[] getDimensionsAndImage(int itemID, String itemText) {
		String searchItem = "";
		for(int i = 0; i < itemText.length(); i++) {
			if(itemText.charAt(i) != ' ')
				searchItem += itemText.charAt(i);
			else
				searchItem += "%20";
		}
		Document linkList = null; 
		try {
			linkList = Jsoup.connect("http://www.google.com/search?q=" + searchItem + "%20amazon").userAgent("Mozilla").get();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Elements links = linkList.select("h3.r > a");
		String amazonLink = links.get(0).attr("href");
		amazonLink = amazonLink.substring(7, amazonLink.indexOf("&sa="));
		System.out.println("\n\nLINK TO ITEM : " + amazonLink);
		Document doc = new Document("");
		try {
			doc = Jsoup.connect(amazonLink).userAgent("Mozilla").header("Accept-Encoding", "gzip, deflate").timeout(999999).maxBodySize(0).get();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String text = doc.text();
		String dim = text.substring(text.indexOf("Product Dimensions") + 18, text.indexOf("Product Dimensions") + 45);   
		
		if (itemID == 1) {
			int powerConsumption = Integer.parseInt(text.substring(text.indexOf("Memory Speed ") + 13, text.indexOf("Memory Speed") + 17));
			if(powerConsumption > 4000) {
				wattage[0] = 200;
				System.out.println("Memory Speed: " + 1200);
			}
			else {
				calcWattage(0, (double)powerConsumption);
				System.out.println("Memory Speed: " + wattage[0]);
			}
		}
		
		else if (itemID == 2) {
			if(text.substring(0, 110).indexOf("Watt") != -1)
				PSwattage = Integer.parseInt((text.substring(text.indexOf("Watt") - 5, text.indexOf("Watt"))).trim());
			else if(text.substring(0, 110).indexOf("0W ") != -1)
				PSwattage = Integer.parseInt((text.substring(text.indexOf("0W ") - 3, text.indexOf("0W ") + 1)).trim());
			else
				PSwattage = Integer.parseInt((text.substring(text.indexOf("0W") - 3, text.indexOf("0W") + 1)).trim());
		}
		
		else if (itemID == 3) {
			moboSocket = text.substring(text.indexOf("Processor (CPU) Model Socket") + 28, text.indexOf("Processor (CPU) Model Socket") + 40);
			moboSocket = moboSocket.replaceAll("\\s","");
			if(cpuSocket.indexOf("——") != -1 ) {
				if (text.substring(0, 110).indexOf("LGA") != -1)
					moboSocket = text.substring(text.indexOf("LGA"), text.indexOf("LGA") + 7);
				else if (text.substring(0, 110).indexOf("AM4") != -1)
					moboSocket = "AM4";
				else
					moboSocket = "AM3";
			}
			else {
				String trueSock = "";
				for (int i = 0; i < moboSocket.length(); i++) {
					if(Character.isLetterOrDigit(moboSocket.charAt(i)))
						trueSock += moboSocket.charAt(i);
				}
				trueSock = trueSock.toUpperCase();
				if(trueSock.indexOf("AM") != -1) {
					trueSock = trueSock.substring(trueSock.indexOf("AM"), trueSock.indexOf("AM") + 3);
				}
				else if (trueSock.indexOf("LGA") != -1)
					trueSock = trueSock.substring(trueSock.indexOf("LGA"), trueSock.indexOf("LGA") + 7);
				moboSocket = trueSock;
			}
			moboDoubleDataRate = text.substring(text.indexOf("RAM Technology"), text.indexOf("RAM Technology") + 21);
			moboDoubleDataRate = moboDoubleDataRate.toUpperCase();
			if(moboDoubleDataRate.indexOf("DDR") != -1) 
				moboDoubleDataRate = moboDoubleDataRate.substring(moboDoubleDataRate.indexOf("DDR"), moboDoubleDataRate.indexOf("DDR") + 4);
			else if(moboDoubleDataRate.indexOf("SD") != -1) 
				moboDoubleDataRate = "DDR4";
		}
		
		else if (itemID == 4) {
			cpuSocket = text.substring(text.indexOf("Processor (CPU) Model Socket") + 28, text.indexOf("Processor (CPU) Model Socket") + 40);
			cpuSocket = cpuSocket.replaceAll("\\s","");
			if(cpuSocket.indexOf("——") != -1 ) {
				if (text.substring(0, 110).indexOf("LGA") != -1)
					cpuSocket = text.substring(text.indexOf("LGA"), text.indexOf("LGA") + 7);
				else if (text.substring(0, 110).indexOf("AM4") != -1)
					cpuSocket = "AM4";
				else
					cpuSocket = "AM3";
			}
			else {
				String trueSock = "";
				for (int i = 0; i < cpuSocket.length(); i++) {
					if(Character.isLetterOrDigit(cpuSocket.charAt(i)))
						trueSock += cpuSocket.charAt(i);
				}
				trueSock = trueSock.toUpperCase();
				if(trueSock.indexOf("AM") != -1)
					trueSock = trueSock.substring(trueSock.indexOf("AM"), trueSock.indexOf("AM") + 3);
				else if(trueSock.indexOf("LGA") != -1)
					trueSock = trueSock.substring(trueSock.indexOf("LGA"), trueSock.indexOf("LGA") + 7);
				cpuSocket = trueSock;
			}
			System.out.println("CPU Socket: " + cpuSocket);
			String temp = text.substring(text.indexOf("Summary"));
			double powerConsumption = Double.parseDouble(temp.substring(temp.indexOf("Processor") + 10, temp.indexOf("Processor") + 13));
			System.out.println("GHZ : " + powerConsumption);
			calcWattage(1, powerConsumption);
		}
		
		else if (itemID == 5) {
			ramDoubleDataRate = text.substring(text.indexOf("RAM Type"), text.indexOf("RAM Type") + 15);
			ramDoubleDataRate = ramDoubleDataRate.toUpperCase();
			if(ramDoubleDataRate.indexOf("DDR") != -1) 
				ramDoubleDataRate = ramDoubleDataRate.substring(ramDoubleDataRate.indexOf("DDR"), ramDoubleDataRate.indexOf("DDR") + 4);
			else if (ramDoubleDataRate.indexOf("SD") != -1) 
				ramDoubleDataRate = "DDR4";
		}
		
		try {
			ImageURLWithTitle[itemID][0] = getImageURL(amazonLink);
		} catch (IOException e) {
			e.printStackTrace();
		};
		
		if(ImageURLWithTitle[itemID][0].equals("")) {
			Document docImage = new Document("");
			try {
				docImage = Jsoup.connect("https://www.google.com/search?q=%20" + itemText + "%20&tbm=isch").userAgent("Mozilla").get();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Elements imageElements = docImage.getElementsByTag("img");
			ImageURLWithTitle[itemID][0] = imageElements.get(0).attr("src");
		}
		
		ImageURLWithTitle[itemID][1] = itemText;
		System.out.println("IMAGE URL : " + ImageURLWithTitle[itemID][0]);
		
    	int ind = 0;
    	String[] split = dim.split("x");
    	for (String unit : split) {
    		String temp = "";
    		unit = unit.trim();
    		for(int i = 0; i < unit.length(); i++ ) {
    			if(Character.isDigit(unit.charAt(i)) || unit.charAt(i) == '.')
    					temp += unit.charAt(i);
    			else if(unit.charAt(i) == ' ')
    				break;
    		}
    		split[ind] = temp;
    		ind++;
    	}
		System.out.println("ITEM DIMENSIONS : " + split[0] + " x " + split[1] + " x " + split[2]);
    	return split;
	}
	
	/**
	 * Purpose : Stores the dimensions of a user's chosen item, puts them in a uniform fashion (W x H x L), then determines whether the part is compatible 
	 * 			with other chosen parts
	 * @param itemID : Represents the row index of Dimensions where the dimensions should be stored.
	 * @param split : Holds the parsed Z Y and X dimensions of the user chosen item.
	 * @return An integer representing the success or reason for error of the submitted part.
	 */
    public static int setDimensions(int itemID, String[] split) {
    	int ind = 0;
    	for (int i = 0; i < split.length; i++) {
    		try {
    			Dimensions[itemID][ind] = Double.parseDouble(split[i].trim());
	    		ind++;
    		} catch (NumberFormatException e) {
    		    return 1;
    		}	    		
    	}
    	
    	
    	if(itemID == 1) {
	    	double temp;
			temp = Dimensions[1][0];
			Dimensions[1][0] = Dimensions[1][2];
			Dimensions[1][2] = temp;
			
			if(Dimensions[1][1] > Dimensions[1][0]) {
		        	temp = Dimensions[1][1];
		        	Dimensions[1][1] = Dimensions[1][0];
		        	Dimensions[1][0] = temp;
			}
    	}
    	
    	else if(itemID == 2) {
	    	double temp;
			temp = Dimensions[2][0];
			Dimensions[2][0] = Dimensions[2][2];
			Dimensions[2][2] = temp;
			
			if(Dimensions[2][2] < Dimensions[2][1]) {
	        	temp = Dimensions[2][1];
	        	Dimensions[2][1] = Dimensions[2][2];
	        	Dimensions[2][2] = temp;
	    	}
	    	if(Dimensions[2][0] < Dimensions[2][1]) {
	        	temp = Dimensions[2][2];
	        	Dimensions[2][1] = Dimensions[2][0];
	        	Dimensions[2][0] = temp;
	    	}
    	}
    	
    	else if(itemID == 3) {
    		double temp = Dimensions[3][0];
    		Dimensions[3][0] = Dimensions[3][1];
    		Dimensions[3][1] = temp;
    		temp = Dimensions[3][1];
    		Dimensions[3][1] = Dimensions[3][2];
    		Dimensions[3][2] = temp;
    		
    		if(Dimensions[3][2] < Dimensions[3][1]) {
            	temp = Dimensions[3][1];
            	Dimensions[3][1] = Dimensions[3][2];
            	Dimensions[3][2] = temp;
        	}
        	
        	if(Dimensions[3][0] > Dimensions[3][1]) {
            	temp = Dimensions[3][0];
            	Dimensions[3][0] = Dimensions[3][1];
            	Dimensions[3][1] = temp;
        	}
    	}
    	
    	else if(itemID == 4) {
	    	double temp;
			temp = Dimensions[4][0];
			Dimensions[4][0] = Dimensions[4][1];
			Dimensions[4][1] = temp;
			
	    	if(Dimensions[4][1] > Dimensions[4][0]) {
	        	temp = Dimensions[4][0];
	        	Dimensions[4][0] = Dimensions[4][1];
	        	Dimensions[4][1] = temp;
	    	}
	    	
	    	if(Dimensions[4][1] > Dimensions[4][2]) {
	        	temp = Dimensions[4][1];
	        	Dimensions[4][1] = Dimensions[4][2];
	        	Dimensions[4][2] = temp;
	    	}
	    	
    	}
    	
    	else if(itemID == 5) {
	    	double temp;
	    	if(Dimensions[5][1] > Dimensions[5][0]) {
	        	temp = Dimensions[5][0];
	        	Dimensions[5][0] = Dimensions[5][1];
	        	Dimensions[5][1] = temp;
	    	}
	    	if(Dimensions[5][2] > Dimensions[5][1]) {
	        	temp = Dimensions[5][2];
	        	Dimensions[5][2] = Dimensions[5][1];
	        	Dimensions[5][1] = temp;
	    	}
	    	
    	}
    	    	
    	return checkCompatibility(itemID);
    	
	}
	
	/**
	 * Purpose : Resets the GUI for the selection of a new item button.
	 * @param args not used
	 * @return void
	 */
	public void setPanel() {
	    	panel.removeAll();
	    	panel.add(bChangeCase);
	    	panel.add(bChangeGPU);
	    	panel.add(bChangeMotherboard);
	    	panel.add(bChangeCPU);
	    	if(state.containsPart("gpu") && state.containsPart("cpu")) panel.add(bChangePowerSupply);
	    	panel.add(bChangeRAM);
	    	if(state.getactivePartList().size() == 6)
	    		panel.add(bFinish);
	    	validate();
	    	repaint();
	}
	
	/**
	 * Purpose : Prints the dimensions of all parts currently chosen.
	 * @param args not used
	 * @return void
	 */
	public static void printDim() {
		System.out.println("\n" + "(inches) (W x H x L) / (X x Y x Z) :");
		DecimalFormat df = new DecimalFormat("#.##");
    	for(Entity part  : state.getactivePartList()) {
    		System.out.println(part.getName() + " : " + df.format(part.getScaleX()) + " x " + df.format(part.getScaleY()) + " x " + df.format(part.getScaleZ()));
    	}
    	System.out.println("\n");
	}
	/**
	 * Purpose : When the user chooses a case, this function determines the positions of all the parts so that 
	 * 			they are as close to their expected location as possible.
	 * @param args not used
	 * @return void
	 */
	public static void setPositions() {
		float x = (float)Dimensions[0][0];
		float y = (float)Dimensions[0][1];
		float z = (float)Dimensions[0][2];
		
		for(Entity part  : state.getactivePartList()) {
			if(part.getName().equals("gpu")) part.setPosition(new Vector3f ((float)(-1.5/9.2)*x, (float)(7/19.5)*y, (float)(-5/20.5)*z));
			else if(part.getName().equals("PowerSupply")) part.setPosition(new Vector3f (0, 0, (float)(-7/20.5)*z));
			else if(part.getName().equals("motherboard")) part.setPosition(new Vector3f ((float)(5.14f/9.2)*x, (float)(17/19.5)*y, (float)(1/20.5)*z));
			else if(part.getName().equals("cpu")) part.setPosition(new Vector3f ((float)(5.5f/9.2)*x, (float)(14.1f/19.5)*y, (float)(-4.2f/20.5)*z));
			else if(part.getName().equals("ram")) part.setPosition(new Vector3f ((float)(4.2/9.2)*x, (float)(14.4/19.5)*y, 0));
		}
	}
	
	/**
	 * Purpose : Calls print() to create a PDF of the build summary.
	 * @param args not used.
	 * @return void
	 */
	public static void printPDF() {
		PrintPDF print = new PrintPDF(ImageURLWithTitle);
		print.print();
		System.exit(0);
	}
	
	/**
	 * Purpose : sums together the wattage required from certain parts
	 * @param args not used.
	 * @return integer representing wattage required for PC to run
	 */
	public static int sumWattage() {
		int sum = 163;
		for (int watts  : wattage)
			sum += watts;
		return sum;
	}
	
	/**
	 * Purpose : calculates wattage needed for certain parts
	 * @param partID : represents the part for which wattage is being calculated.
	 * @param powerConsumption : the memory speed of the part which affects its power draw.
	 * @return void
	 */
	public static void calcWattage(int partID, double powerConsumption) {
		if (partID == 0) 
			wattage[0] = (int)(powerConsumption * 0.18214285714);
			
		else if (partID == 1) 
			wattage[1] = (int)(powerConsumption * 10);
	}
	
	/**
	 * Purpose : checks whether the chosen part is compatible with the other previously chosen parts
	 * @param itemID : Represents the row index of Dimensions where the dimensions should be stored.
	 * @return An integer representing the success or reason for error of the submitted part.
	 */
	public static int checkCompatibility(int itemID) {
		if(state.getactivePartList().size() != 0) {
        	for (int i = 0; i < 3; i++) {
        		if(Dimensions[itemID][i] > Dimensions[0][i]) {
        			return 2;
        		}
        	}
    	}
    	
		if (itemID == 2) {
			System.out.println("Current Wattage Needed : " + sumWattage());
			System.out.println("Power Supply Wattage : " + PSwattage);
			if (sumWattage() > PSwattage) {
				return 5;
			}
		}
    	
		else if (itemID == 3) {
			if (!cpuSocket.equals("") && !moboSocket.equals(cpuSocket)) {
				return 3;
			}
			
			if (!ramDoubleDataRate.equals("") && !moboDoubleDataRate.equals(ramDoubleDataRate)) {
				return 4;
			}
			System.out.println("Motherboard Socket Type : " + moboSocket);
			System.out.println("Motherboard DDR Type : " + moboDoubleDataRate);
		}
    	
		else if (itemID == 4) {
			if (!moboSocket.equals("") && !cpuSocket.equals(moboSocket)) {
				return 3;
			}
		}
    	
		else if (itemID == 5) {			
			if (!moboDoubleDataRate.equals("") && !ramDoubleDataRate.equals(moboDoubleDataRate)) {
				return 4;
			}
			System.out.println("RAM DDR Type : " + ramDoubleDataRate);
		}
    	
    	return 0;
	}
	
	/**
	 * Purpose : Grabs the image URL of the main image from an Amazon web page
	 * @param url  : URL for the Amazon page
	 * @return A String holding the image URL of the main image on an Amazon web page.
	 */
	public static String getImageURL(String url) throws IOException
	{
	    URL amazonLink = new URL(url);
	    URLConnection connect = amazonLink.openConnection();
	    connect.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
	    connect.connect();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
	    StringBuffer stringBuff = new StringBuffer();
	    String line;

	    while ((line = reader.readLine()) != null) {
	        if (line.trim().length() > 0)
	        	stringBuff.append(line).append("\n");
	    }
	    reader.close();
		String imageURL;
		String html = stringBuff.toString();
		Document doc = Jsoup.parse(html);
		Elements imageElements = doc.getElementsByTag("img");
		int loc = imageElements.toString().indexOf("data-old-hires=\"") + 16;
		imageURL = imageElements.toString().substring(loc, imageElements.toString().substring(loc).indexOf("\"") + loc);
		return imageURL;
	}
}