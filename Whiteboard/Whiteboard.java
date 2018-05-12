import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;


public class Whiteboard extends JFrame {
	private JLabel addLabel;
	private JButton rect;
	private JButton oval;
	private JButton line;
	private JButton text;
	private JButton setColor;
	private JButton sendFront;
	private JButton sendBack;
	private JButton removeShape;
	private JTable shapeTable;
	private JTextField editTextEntry;
	private JButton save;
	private JButton load;
	private JButton savePNG;
	private JButton startServer;
	private JButton startClient;
	private JLabel statusString;
	private Canvas drawSection;
	private DShape selectedShape = null;
	private Point selectedKnob = null;
	private Point oppositeKnob;
	private JComboBox<Font> fontMenu;
	private ShapeTableModel model;
	private Thread serverWorker = null;
	
	public Whiteboard() {
		drawSection = new Canvas(this);
		drawSection.addMouseListener(new CustomMouseListener());
		this.setLayout(new BorderLayout());
		this.addLabel = new JLabel("Add Shapes");
		model = new ShapeTableModel(drawSection.getDShape());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//add new rectangle button
		this.rect = new JButton("Rect");
		this.rect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //initializes dshapemodel and calls addshape to initialize the dshape
            	DRect r = new DRect(10,10,30,30,Color.GRAY);
            	drawSection.addDShape(r);
            	r.setVerb("add");
            	r.getModel().attach(r);
            	r.getModel().attachCanvasListener(drawSection);
            	r.getModel().attachShapeListener(model);
            	if(serverWorker != null) r.getModel().attachServerListener((TCPServer)serverWorker);
            	selectedShape = r;
            	drawSection.setSelectedShape(r);
            }
        });
		
		//add new oval button
		this.oval = new JButton("Oval");
		this.oval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DOval o = new DOval(10,10,30,30,Color.GRAY);
            	drawSection.addDShape(o);
            	o.setVerb("add");
            	o.getModel().attach(o);
            	o.getModel().attachCanvasListener(drawSection);
            	o.getModel().attachShapeListener(model);
            	if(serverWorker != null) o.getModel().attachServerListener((TCPServer)serverWorker);
            	selectedShape = o;
            	drawSection.setSelectedShape(o);
			}
		});
		
		//add new line button
		this.line = new JButton("Line");
		this.line.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DLine l = new DLine(10,10,30,30,Color.GRAY);
            	drawSection.addDShape(l);
            	l.setVerb("add");
            	l.getModel().attach(l);
            	l.getModel().attachCanvasListener(drawSection);
            	l.getModel().attachShapeListener(model);
            	if(serverWorker != null) l.getModel().attachServerListener((TCPServer)serverWorker);
            	selectedShape = l;
            	drawSection.setSelectedShape(l);
			}
		}); 
		
		//add new text button
		this.text = new JButton("Text");
		this.text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//get text from the textbox and create a new dtext shape
				String enteredText = editTextEntry.getText();
				DText t = new DText(10, 10, 50, 20, Color.GRAY, (Font) fontMenu.getSelectedItem());
				if(enteredText != null)
					t.setText(enteredText);
				else
					t.setText("Hello");
				drawSection.addDShape(t);
				t.setVerb("add");
				t.getModel().attach(t);
				t.getModel().attachCanvasListener(drawSection);
				t.getModel().attachShapeListener(model);
				if(serverWorker != null) t.getModel().attachServerListener((TCPServer)serverWorker);
				selectedShape = t;
				drawSection.setSelectedShape(t);
			}
		});
		this.editTextEntry = new JTextField(15);
		this.editTextEntry.setText("Hello");
		this.editTextEntry.setMaximumSize(new Dimension(20, 5)); //Not sure if we really need this line
		this.fontMenu = new JComboBox<Font>();
		this.fontMenu.setEnabled(false);
		//font picker menu
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fontList = ge.getAllFonts();
		for(Font f : fontList)
			fontMenu.addItem(f);
		fontMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((DText) selectedShape).setFont((Font) fontMenu.getSelectedItem());			
			}
		});

		//color picker
		JFrame colorFrame = new JFrame("JColorChooser");
	    colorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setColor = new JButton("Set Color");
		this.setColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedShape != null) {
					Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.GRAY);
					selectedShape.setColor(newColor);
				}
			}
		});
		colorFrame.add(this.setColor);
		colorFrame.setVisible(false);
		
		//send to front button
		this.sendFront = new JButton("Send to Front");
		this.sendFront.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<DShape> shapes = drawSection.getDShape();
				if(selectedShape != null) {
					DShape shape = selectedShape;
					DShape copy = new DShape(selectedShape.getX(), selectedShape.getY(), selectedShape.getWidth(), 
                			selectedShape.getBottom(), selectedShape.getColor());
					copy.setId(selectedShape.getId());
                	copy.setVerb("front");
                	if(serverWorker != null) copy.getModel().attachServerListener((TCPServer)serverWorker);
					shapes.remove(shape);
					shapes.add(shape);
					model.fireTableDataChanged();
				}
			}
		});
		
		//send to back
		this.sendBack = new JButton("Send to Back");
		this.sendBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<DShape> shapes = drawSection.getDShape();
				if(selectedShape != null) {
					DShape shape = selectedShape;
					DShape copy = new DShape(selectedShape.getX(), selectedShape.getY(), selectedShape.getWidth(), 
                			selectedShape.getBottom(), selectedShape.getColor());
					copy.setId(selectedShape.getId());
                	copy.setVerb("back");
                	if(serverWorker != null) copy.getModel().attachServerListener((TCPServer)serverWorker);
					shapes.remove(shape);
					shapes.add(0, shape);
					model.fireTableDataChanged();
				}
			}
		});
		
		//remove shape
		this.removeShape = new JButton("Remove Shape");
        this.removeShape.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<DShape> shapes = drawSection.getDShape();
                if(selectedShape != null) {
                    for(int i = shapes.size()-1; i >= 0; i--) {
                        if(selectedShape == shapes.get(i)) {
                        	DShape copy = new DShape(selectedShape.getX(), selectedShape.getY(), selectedShape.getWidth(), 
                        			selectedShape.getBottom(), selectedShape.getColor());
                        	copy.setId(shapes.get(i).getId());
                        	copy.setVerb("remove");
                        	if(serverWorker != null) copy.getModel().attachServerListener((TCPServer)serverWorker);
                        	shapes.remove(shapes.get(i));
                            model.fireTableDataChanged();
                        }
                    }
                }
            }
        });
		
        //save to xml button - gets file name and executes function
        this.save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame saveFrame = new JFrame();
				saveFrame.setLayout(new FlowLayout());
				JPanel prompt = new JPanel();
				JLabel pathPrompt = new JLabel("Please enter the path to save the file: ");
				JTextField field = new JTextField(15);
				JButton enterButton = new JButton("Enter");
				enterButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveShapes(field.getText());
						saveFrame.setVisible(false);
					}
				});
				saveFrame.add(prompt);
				saveFrame.add(pathPrompt);
				saveFrame.add(field);
				saveFrame.add(enterButton);
				saveFrame.pack();
				saveFrame.setVisible(true);
			}
		});
		
		//load xml file button - gets name of file and tries to open it
		this.load = new JButton("Load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame loadFrame = new JFrame();
				loadFrame.setLayout(new FlowLayout());
				JPanel prompt = new JPanel();
				JLabel pathPrompt = new JLabel("Please enter the path to load the XML file from: ");
				JTextField field = new JTextField(15);
				JButton enterButton = new JButton("Enter");
				enterButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openXmlFile(field.getText());
						loadFrame.setVisible(false);
					}
				});
				loadFrame.add(prompt);
				loadFrame.add(pathPrompt);
				loadFrame.add(field);
				loadFrame.add(enterButton);
				loadFrame.pack();
				loadFrame.setVisible(true);
			}
		});
		
		//save current canvas as png file
		this.savePNG = new JButton("Save as PNG");
		savePNG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame loadFrame = new JFrame();
				loadFrame.setLayout(new FlowLayout());
				JPanel prompt = new JPanel();
				JLabel pathPrompt = new JLabel("Please enter the name of the file you wish to save as: ");
				JTextField field = new JTextField(15);
				JButton enterButton = new JButton("Enter");
				enterButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						drawSection.savePNG(field.getText());
						loadFrame.setVisible(false);
					}
				});
				loadFrame.add(prompt);
				loadFrame.add(pathPrompt);
				loadFrame.add(field);
				loadFrame.add(enterButton);
				loadFrame.pack();
				loadFrame.setVisible(true);
			}
		});
		
		//start server button
		this.startServer = new JButton("Start Server");
		this.startServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//prompts for ip and port number and creates thread from the input
                JFrame serverFrame = new JFrame();
                serverFrame.setLayout(new FlowLayout());
                JPanel prompt = new JPanel();
                JLabel pathPrompt = new JLabel("Please enter IP and port number: ");
                JTextField field = new JTextField(15);
                JButton enterButton = new JButton("Enter");
                enterButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	String input = field.getText();
                    	//start server thread
                    	try {	//no input - default server
                    		Integer portVal = 12345;
                    		if(input.isEmpty()) {
                    			serverWorker = new TCPServer(portVal, drawSection);
                    			for(DShape s : drawSection.getDShape())
                    				s.getModel().attachServerListener((TCPServer)serverWorker);
                    			serverWorker.start();
                    			startServer.setEnabled(false);
                    		}
                    		else {	//creates server rom user input
	                    		Integer port = Integer.parseInt(input);
	                    		portVal = port;
	                    		serverWorker = new TCPServer(port, drawSection);
	                    		for(DShape s : drawSection.getDShape())
                    				s.getModel().attachServerListener((TCPServer)serverWorker);
	                    		serverWorker.start();
	                    		startServer.setEnabled(false);
                    		}
                    		statusString.setText("Server mode, port: " + portVal.toString());
                    	} catch(Exception ex) {
                    		System.out.println("invalid port");
                    	}
                    	serverFrame.setVisible(false);
                    }
                });
                JButton returnButton = new JButton("Return");
                returnButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	serverFrame.setVisible(false);
                    }
                });
                serverFrame.add(prompt);
                serverFrame.add(pathPrompt);
                serverFrame.add(field);
                serverFrame.add(enterButton);
                serverFrame.add(returnButton);
                serverFrame.pack();
                serverFrame.setVisible(true);
			}
		});
		
		//start client thread
		this.startClient = new JButton("Start Client");
		this.startClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//get user input ip and port number
				JFrame clientFrame = new JFrame();
                clientFrame.setLayout(new FlowLayout());
                clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JPanel prompt = new JPanel();
                JLabel pathPrompt = new JLabel("Please enter IP and port number: ");
                JTextField field = new JTextField(15);
                JButton enterButton = new JButton("Enter");
                enterButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	String input = field.getText();
                    	Whiteboard client = new Whiteboard();
                    	client.statusString.setText("Client mode");
                    	//no input, creates client from default values
                    	if(input.isEmpty()) {
                    		Thread clientWorker = new TCPClient("127.0.0.1", 12345, client.getDrawSection());
                    		clientWorker.start();
            				client.displayWindow();
            				client.disableButtons();
                    	}
                    	//tokenize string into ip and port number, output error if invalid
                    	String[] result = input.split(":");
                    	if(result.length == 2) {
                    		String[] host = result[0].split(".");
                    		if(host.length == 4) {
                    			try {
                    				Integer port = Integer.parseInt(result[1]);
                    				Thread clientWorker = new TCPClient(result[0], Integer.parseInt(result[1]), client.getDrawSection());     		
                    				clientWorker.start();
                    				client.displayWindow();
                    				client.disableButtons();
                    			} catch(Exception ex) {
                    				drawSection.setStatusString("Client Failed to Connect");
                    			}
                    		}
                    		else drawSection.setStatusString("Client Failed to Connect");
                    	}
                    	else if(!input.isEmpty()) drawSection.setStatusString("Client Failed to Connect");
                    	clientFrame.setVisible(false);
                    }
                });
                JButton returnButton = new JButton("Return");
                returnButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	clientFrame.setVisible(false);
                    }
                });
                clientFrame.add(prompt);
                clientFrame.add(pathPrompt);
                clientFrame.add(field);
                clientFrame.add(enterButton);
                clientFrame.add(returnButton);
                clientFrame.pack();
                clientFrame.setVisible(true);
			}
		});
		
		this.statusString = new JLabel();
		//set up table
		this.shapeTable = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(shapeTable); 
        scrollPane.setPreferredSize(new Dimension(500, 150)); 
		shapeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      
		//set up the frame with all of the components
        JPanel leftScreen = new JPanel();
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel textEntry = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel textDropdown = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel setColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel networkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addPanel.add(addLabel);
        addPanel.add(rect);
		addPanel.add(oval);
		addPanel.add(line);
		addPanel.add(text);
		
		textEntry.add(new JLabel("Edit Text: "));
		textEntry.add(editTextEntry);
		textDropdown.add(fontMenu);

		setColorPanel.add(new JLabel("Set Shape Color: "));
		setColorPanel.add(setColor);
		
		editPanel.add(new JLabel("Edit Shapes: "));
		editPanel.add(sendFront);
		editPanel.add(sendBack);
		editPanel.add(removeShape);
		
		savePanel.add(new JLabel("Edit/Save Content: "));
		savePanel.add(save);
		savePanel.add(load);
		savePanel.add(savePNG);
		
		networkPanel.add(new JLabel("Networking: "));
		networkPanel.add(startServer);
		networkPanel.add(startClient);
		networkPanel.add(statusString);
		
		leftScreen.add(addPanel);
		leftScreen.add(textEntry);
		leftScreen.add(textDropdown);
		leftScreen.add(setColorPanel);
		leftScreen.add(editPanel);
		leftScreen.add(savePanel);
		leftScreen.add(networkPanel);
		leftScreen.add(scrollPane);
		leftScreen.setLayout(new BoxLayout(leftScreen, BoxLayout.Y_AXIS));
		leftScreen.setPreferredSize(new Dimension(600, 400));
		this.add(BorderLayout.WEST, leftScreen);
		this.add(BorderLayout.CENTER, drawSection);
		this.pack();
	}
	
	public void displayWindow() {
		this.setVisible(true);
	}
	
	public void hideWindow() {
		this.setVisible(false);
	}
	
	class CustomMouseListener implements MouseListener {
        public void mouseClicked(MouseEvent e) { }
        public void mousePressed(MouseEvent e) {
        	int x = e.getX();
        	int y = e.getY();
        	ArrayList<DShape> shapes = drawSection.getDShape();
        	//loops through the list of shapes and determines if a shape has been clicked
        	selectedShape = null;
        	drawSection.setSelectedShape(null);
        	fontMenu.setEnabled(false);
        	for(DShape s : shapes) {
        		if(y >= s.getY() && y <= s.getBottom() && x >= s.getX() && x <= s.getRight() ||(s instanceof DLine 
        			&& (y >= s.getY() + s.getHeight()) && y <= s.getY()) && x >= s.getX() && (x <= s.getX() + s.getWidth())) {
        			selectedShape = s;
        			drawSection.setSelectedShape(s);
        			selectedKnob = null;
        			drawSection.setSelectedKnob(null);
        			if(selectedShape instanceof DText) fontMenu.setEnabled(true);
        			ArrayList<Point> knobs = selectedShape.getKnobs();
        			for(int i = 0; i < knobs.size(); i++)
        				knobs.get(i);
        		}
        		s.setX(s.getX()); //used to call listener
        	}
        	//detects knob selection
        	if(selectedShape != null) {
        		ArrayList<Point> knobs = selectedShape.getKnobs();
        		for(Point p : knobs) {
        			if(y >= p.getY() && y <= p.getY() + 8 
        	           && x >= p.getX() && x <= p.getX() + 8) {
        	        	selectedKnob = p;
        	        	drawSection.setSelectedKnob(p);
        	        }
        		}
        		//determine knob on opposite corner of selected knob
        		oppositeKnob = selectedShape.getOppositeKnob(selectedKnob);
        		drawSection.setOppositeKnob(selectedShape.getOppositeKnob(selectedKnob));
        	}
        }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
     }
	
	public Canvas getDrawSection() {
		return drawSection;
	}
	
	public static void main(String[] args) {
		Whiteboard w = new Whiteboard();
		w.displayWindow();
	}
	
	/*
	 * Saves a class' data.
	 * @param fileName the name of the file to store the class meta data in.
	 */
	private void saveShapes(String fileName) {
		try {
	        XMLEncoder xmlOut = new XMLEncoder(
	            new BufferedOutputStream(new FileOutputStream(fileName))); 	
	        DShapeModel[] shapeList = new DShapeModel[drawSection.getDShape().size()];
	        for(int i = 0; i < drawSection.getDShape().size(); i++)
	        	shapeList[i] = drawSection.getDShape().get(i).getModel();
	        xmlOut.writeObject(shapeList);
	        xmlOut.close();
	        System.out.println("Successfully saved data");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Opens an XML file and stores the ClassData objects into the classesMap map.
	 */
	private void openXmlFile(String fileName) {
		DShapeModel[] shapeList;
		ArrayList<DShape> shapes = drawSection.getDShape();
		for(int i = shapes.size() - 1; i >= 0; i--)
			shapes.remove(i);
		try {
			XMLDecoder xmlIn = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName))); 
			shapeList = (DShapeModel[]) xmlIn.readObject();
			xmlIn.close();
			for(DShapeModel s : shapeList) {
				if(s instanceof DRectModel) {
					DRect rect = new DRect(s.getX(), s.getY(), s.getWidth(), s.getHeight(), s.getColor());
					rect.getModel().attach(rect);
	            	rect.getModel().attachCanvasListener(drawSection);
	            	rect.getModel().attachShapeListener(model);
					drawSection.getDShape().add(rect);
				}
				if(s instanceof DOvalModel) {
					DOval oval = new DOval(s.getX(), s.getY(), s.getWidth(), s.getHeight(), s.getColor());
					oval.getModel().attach(oval);
					oval.getModel().attachCanvasListener(drawSection);
					oval.getModel().attachShapeListener(model);
					drawSection.getDShape().add(oval);
				}
				if(s instanceof DLineModel) {
					DLine line = new DLine(s.getX(), s.getY(), s.getWidth(), s.getHeight(), s.getColor());
					line.getModel().attach(line);
					line.getModel().attachCanvasListener(drawSection);
					line.getModel().attachShapeListener(model);
					drawSection.getDShape().add(line);
				}
				if(s instanceof DTextModel) {
					DText text = new DText(s.getX(), s.getY(), s.getWidth(), s.getHeight(), s.getColor(),(Font) fontMenu.getSelectedItem());
					text.getModel().attach(text);
					text.getModel().attachCanvasListener(drawSection);
					text.getModel().attachShapeListener(model);
					text.setText(((DTextModel)s).getText());
					text.setX((int)((DTextModel)s).getX());
					text.setY((int)((DTextModel)s).getY());
					text.setFont(((DTextModel)s).getFont());
					text.setHeight(((DTextModel)s).getHeight());
					text.setWidth(((DTextModel)s).getWidth());
					text.setColor(((DTextModel)s).getColor());
					drawSection.getDShape().add(text);
				}
			}
			drawSection.repaint();
			System.out.println("Successfully loaded class information from " + fileName);
		}
		catch (IOException e) {
			System.out.println("Error! Invalid XML file name entered.");
		}
	}
	
	//disables all of the buttons for client
	public void disableButtons() {
		rect.setEnabled(false);
		oval.setEnabled(false);
		line.setEnabled(false);
		text.setEnabled(false);
		setColor.setEnabled(false);
		sendFront.setEnabled(false);
		sendBack.setEnabled(false);
		removeShape.setEnabled(false);
		editTextEntry.setEditable(false);
		save.setEnabled(false);
		load.setEnabled(false);
		savePNG.setEnabled(false);
		startServer.setEnabled(false);
		startClient.setEnabled(false);
	}
	
	public Thread getServerWorker() {
		return serverWorker;
	}
	
	public ShapeTableModel getModel() {
		return model;
	}
	
	public void setStatusString(String status) {
		statusString.setText(status);
	}
	
	public JComboBox<Font> getFontMenu() {
		return fontMenu;
	}
}