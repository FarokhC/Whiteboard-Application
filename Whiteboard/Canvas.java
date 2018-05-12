import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;

class Canvas extends JPanel implements ModelListener {
	public ArrayList<DShape> shapes;
	private DShape selectedShape = null;
	private Point selectedKnob = null;
	private Point oppositeKnob = null;
	private Whiteboard whiteboard;
	private boolean isClient;
		
	public Canvas(Whiteboard whiteboard) {
		this.whiteboard = whiteboard;
		shapes = new ArrayList<DShape>();
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(400, 400));
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(isClient) return;
				//move shape
		       	if(selectedShape != null && selectedKnob == null) {
			       	selectedShape.setX(e.getX() - (selectedShape.getWidth()/2));
			       	selectedShape.setY(e.getY() - (selectedShape.getHeight()/2));
			       	ArrayList<Point> knobs = selectedShape.getKnobs();
		        	knobs.get(0).setLocation(e.getX() - (selectedShape.getWidth()/2) - 4, e.getY() - (selectedShape.getHeight()/2) - 4);
		        	knobs.get(1).setLocation(e.getX() + (selectedShape.getWidth()/2) - 4, e.getY() - (selectedShape.getHeight()/2) - 4);
		        	knobs.get(2).setLocation(e.getX() - (selectedShape.getWidth()/2) - 4, e.getY() + (selectedShape.getHeight()/2) - 4);
		        	knobs.get(3).setLocation(e.getX() + (selectedShape.getWidth()/2) - 4, e.getY() + (selectedShape.getHeight()/2) - 4);
	        	}
	        	//resize shapes (please don't touch this code block it'll probably explode)
		       	ArrayList<Point> knobs = selectedShape.getKnobs();
		       	//handles dline resizing
	        	if(selectedShape instanceof DLine) {
	        		if(selectedKnob == knobs.get(0)) {
	        			knobs.get(0).setLocation(e.getX(), e.getY());
	        			if(e.getX() <= (int)oppositeKnob.getX()) {
		        			selectedShape.setX(e.getX() + 4);
		        			selectedShape.setWidth((int)oppositeKnob.getX() - e.getX());
		        			selectedShape.setY(e.getY());
		        			selectedShape.setHeight((int)oppositeKnob.getY() - e.getY());
	        			}
	        			else {
	        				selectedShape.setX((int)oppositeKnob.getX());
	        				selectedShape.setWidth(e.getX() - (int)oppositeKnob.getX());
	        				selectedShape.setY((int)oppositeKnob.getY());
	        				selectedShape.setHeight(e.getY() - (int)oppositeKnob.getY());
	        			}
	        		}
	        		if(selectedKnob == knobs.get(3)) {
	        			knobs.get(3).setLocation(e.getX(), e.getY());
	        			if(e.getX() >= (int)oppositeKnob.getX()) {
	        				selectedShape.setX((int)oppositeKnob.getX());
	        				selectedShape.setWidth(e.getX() - (int)oppositeKnob.getX());
	        				selectedShape.setY((int)oppositeKnob.getY());
	        				selectedShape.setHeight(e.getY() - (int)oppositeKnob.getY());
	        			}
	        			else {
	        				selectedShape.setX(e.getX());
	        				selectedShape.setWidth((int)oppositeKnob.getX() - e.getX());
	        				selectedShape.setY(e.getY());
	        				selectedShape.setHeight((int)oppositeKnob.getY() - e.getY());
	        			}
	        		}
	        	}
	        	else {	//other shapes
		        	if(selectedShape != null && selectedKnob != null) {
		        		//upper left knob resizing
		        		if(selectedKnob == knobs.get(0)) {
		        			knobs.get(0).setLocation(e.getX(), e.getY());
		        			knobs.get(1).setLocation((int)oppositeKnob.getX(), e.getY());
		        			knobs.get(2).setLocation(e.getX(), (int)oppositeKnob.getY());
		        			int x = (int)oppositeKnob.getX() - e.getX();
		        			if(x > 0) {
			        			selectedShape.setX(e.getX() + 4);
			        			selectedShape.setWidth(x);
		        			}
		        			else { //flipped x
		        				selectedShape.setX((int)oppositeKnob.getX() + 4);
			        			selectedShape.setWidth(e.getX() - (int)oppositeKnob.getX());
		        			}
		        			if(e.getY() < (int)oppositeKnob.getY()) {
			        			selectedShape.setY(e.getY() + 4);
			        			selectedShape.setHeight((int)oppositeKnob.getY() - e.getY());
		        			}
		        			else { //flipped y
		        				selectedShape.setY((int)oppositeKnob.getY() + 4);
			        			selectedShape.setHeight(e.getY() - (int)oppositeKnob.getY());
		        			}
		        		}
		        		//upper right knob resizing
		        		if(selectedKnob == knobs.get(1)) {
		        			int x = e.getX() - (int)oppositeKnob.getX();
		        			knobs.get(0).setLocation((int)oppositeKnob.getX(), e.getY());
	        				knobs.get(1).setLocation(e.getX(), e.getY());
	        				knobs.get(3).setLocation(e.getX(), (int)oppositeKnob.getY());
		        			if(x > 0) {
		        				selectedShape.setWidth(x);
		        				selectedShape.setX((int)oppositeKnob.getX() + 4);
		        			}
		        			else { //flipped
		        				selectedShape.setX(e.getX() + 4);
		        				selectedShape.setWidth((int)oppositeKnob.getX() - e.getX());
		        			}
		        			if(e.getY() < (int)oppositeKnob.getY()) {
			        			selectedShape.setY(e.getY() + 4);
			        			selectedShape.setHeight((int)oppositeKnob.getY() - e.getY());
		        			}
		        			else { //flipped
		        				selectedShape.setY((int)oppositeKnob.getY() + 4);
			        			selectedShape.setHeight(e.getY() - (int)oppositeKnob.getY());
		        			}
		        		}
		        		//lower left knob resizing
		        		if(selectedKnob == knobs.get(2)) {
		        			int x = (int)oppositeKnob.getX() - e.getX();
		        			knobs.get(0).setLocation(e.getX(), (int)oppositeKnob.getY());
		        			knobs.get(2).setLocation(e.getX(), e.getY());
		        			knobs.get(3).setLocation((int)oppositeKnob.getX(), e.getY());
		        			if(x > 0) {
			        			selectedShape.setX(e.getX() + 4);
			        			selectedShape.setWidth(x);
		        			}
		        			else {
		        				selectedShape.setX((int)oppositeKnob.getX() + 4);
			        			selectedShape.setWidth(e.getX() - (int)oppositeKnob.getX());
		        			}
		        			if(e.getY() > (int)oppositeKnob.getY()) {
		        				selectedShape.setHeight(e.getY() - (int)oppositeKnob.getY());
		        				selectedShape.setY((int)oppositeKnob.getY() + 4);
		        			}
		        			else {
		        				selectedShape.setY(e.getY() + 4);
		        				selectedShape.setHeight((int)oppositeKnob.getY() - e.getY());
		        			}
		        		}
		        		//lower right knob resizing
		        		if(selectedKnob == knobs.get(3)) {
		        			knobs.get(1).setLocation(e.getX(), (int)oppositeKnob.getY());
		        			knobs.get(2).setLocation((int)oppositeKnob.getX(), e.getY());
		        			knobs.get(3).setLocation(e.getX(), e.getY());
		        			int x = e.getX() - (int)oppositeKnob.getX();
		        			if(x > 0) {
		        				selectedShape.setWidth(x);
		        				selectedShape.setX((int)oppositeKnob.getX() + 4);
		        			}
		        			else { //flipped
		        				selectedShape.setWidth((int)oppositeKnob.getX() - e.getX());
		        				selectedShape.setX(e.getX() + 4);
		        			}
		        			if(e.getY() > (int)oppositeKnob.getY()) {
		        				selectedShape.setY((int)oppositeKnob.getY() + 4);
		        				selectedShape.setHeight(e.getY() - (int)oppositeKnob.getY());
		        			}
		        			else { //flipped
		        				selectedShape.setY(e.getY() + 4);
		        				selectedShape.setHeight((int)oppositeKnob.getY() - e.getY());
		        			}
		        		}
		        	}
	        	}
	        }
		});
	}
	
	public void setSelectedShape(DShape selectedShape) {
		this.selectedShape = selectedShape;
	}
	
	public void setSelectedKnob(Point selectedKnob) {
		this.selectedKnob = selectedKnob;
	}
	
	public void setOppositeKnob(Point oppositeKnob) {
		this.oppositeKnob = oppositeKnob;
	}
	
	public void savePNG(String fileName) {
		//saves current canvas to specified file
		selectedShape = null;
		BufferedImage image = (BufferedImage) createImage(this.getWidth(), this.getHeight());
		Graphics g = image.getGraphics();
		paintAll(g);
		g.dispose();
		File file = new File(fileName);
		try {
			javax.imageio.ImageIO.write(image, "PNG", file);
			System.out.println("created file");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	//adds a shape to the dshape list
	public void addDShape(DShape shape) {
		shapes.add(shape);
	}
	
	public ArrayList<DShape> getDShape() {
		return shapes;
	}
	
	public void removeDShape(int id) {
		shapes.remove(id);
		this.modelChanged(null);
	}
	
	public void modelChanged(DShapeModel model) {
		this.repaint();
	}
	
	public DShape createShape(DShapeModel model) {
		//creates new dshape from given dshapemodel and adds to list of dshapes
		DShape newShape = null;
		if(model instanceof DRectModel) {
			newShape = new DRect(model.getX(), model.getY(), model.getWidth(), model.getHeight(), model.getColor());
		}
		else if(model instanceof DOvalModel) {
			newShape = new DOval(model.getX(), model.getY(), model.getWidth(), model.getHeight(), model.getColor());
		}
		else if(model instanceof DLineModel) {
			newShape = new DLine(model.getX(), model.getY(), model.getWidth(), model.getHeight(), model.getColor());
		}
		else if(model instanceof DTextModel) {
			newShape = new DText(model.getX(), model.getY(), model.getWidth(), model.getHeight(), model.getColor(), ((DTextModel) model).getFont());
		}
		newShape.setId(model.getId());
		newShape.getModel().attach((ModelListener) newShape);
		newShape.getModel().attachCanvasListener(this);
		newShape.getModel().attachShapeListener(whiteboard.getModel());
		shapes.add(newShape);
		this.modelChanged(model);
		return newShape;
	}
	
	public void setClient(boolean val) {
		isClient = val;
	}
	
	public boolean getClient() {
		return isClient;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//loops through the list of dshapes and draws the shape
		for(DShape s : shapes) {
			if(s instanceof DRect) {
				g.setColor(s.getColor());
				g.fillRect(s.getX(), s.getY(), s.getWidth(), s.getHeight());
			}
			if(s instanceof DOval) {
				g.setColor(s.getColor());
				g.fillOval(s.getX(), s.getY(), s.getWidth(), s.getHeight());
			}
			if(s instanceof DLine) {
				g.setColor(s.getColor());
				g.drawLine(s.getX(), s.getY(), s.getRight(), s.getBottom());
			}
			if(s instanceof DText) {
				g.setColor(s.getColor());
				g.setFont(((DText) s).getFont());
				Font f = ((DText) s).computeFont(g);
				g.setFont(f);
				Shape clip = g.getClip();
				g.setClip(clip.getBounds().createIntersection(((DText) s).getBounds()));
				g.drawString(((DText) s).getText(), s.getX(), s.getY() + s.getHeight() - (s.getHeight()/3));
				g.setClip(clip);
			}
			if(s == selectedShape) {
				if(s instanceof DText) 
					whiteboard.getFontMenu().setEnabled(true);
				else
					whiteboard.getFontMenu().setEnabled(false);
				int i = 0;
				for(Point p : s.getKnobs()) {
					g.setColor(Color.BLACK);
					if(s instanceof DLine && (i == 1 || i == 2)) 
						g.setColor(new Color(0, 0, 0, 0));
					g.fillRect((int)p.getX(), (int)p.getY(), 8, 8);
					this.repaint();
					i++;
				}
			}
		}
	}
	
	public void setStatusString(String status) {
		whiteboard.setStatusString(status);
	}
}