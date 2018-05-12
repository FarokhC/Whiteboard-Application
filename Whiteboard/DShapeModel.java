import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.io.Serializable;

public class DShapeModel implements Serializable {
	private int x;
	private int y;
	private int width;
	private int height;
	private int right;
	private int bottom;
	private int id;
	private Color color;
	private transient ArrayList<ModelListener> listeners;
	private transient ModelListener canvasListener;
	private transient ModelListener shapeListener;
	private transient ModelListener serverListener;
	private ArrayList<Point> knobs;
	private String verb;
	
	public DShapeModel() {
		x = 10;
		y = 10;
		width = 30;
		height = 30;
		color = Color.GRAY;
		id = 0;
		listeners = new ArrayList<ModelListener>();
		right = width + x;
		bottom = height + y;
		setVerb("add");
		knobs = new ArrayList<Point>();
		Point p1 = new Point(x - 4, y - 4);
		Point p2 = new Point(x + width - 4, y - 4);
		Point p3 = new Point(x - 4, y + height - 4);
		Point p4 = new Point(x + width - 4, y + height - 4);
		knobs.add(p1);
		knobs.add(p2);
		knobs.add(p3);
		knobs.add(p4);
	}
	
	public DShapeModel(int x, int y, int width, int height, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		id = 0;
		this.listeners = new ArrayList<ModelListener>();
		right = width + x;
		bottom = height + y;
		verb = "add";
		//starts at top left and goes clockwise
		knobs = new ArrayList<Point>();
		Point p1 = new Point(x - 4, y - 4);
		Point p2 = new Point(x + width - 4, y - 4);
		Point p3 = new Point(x - 4, y + height - 4);
		Point p4 = new Point(x + width - 4, y + height - 4);
		knobs.add(p1);
		knobs.add(p2);
		knobs.add(p3);
		knobs.add(p4);
	}
	
	public void mimic(DShapeModel other) {
		setX(other.getX());
		setY(other.getY());
		width = other.getWidth();
		height = other.getHeight();
		color = other.getColor();
	}
	
	public void attach(ModelListener l) {
		listeners.add(l);
	}
	
	public void attachCanvasListener (ModelListener l) {
		canvasListener = l;
		l.modelChanged(this);
	}
	
	public void attachShapeListener (ModelListener l) {
		shapeListener = l;
		l.modelChanged(this);
	}
	
	public void attachServerListener(ModelListener l) {
		serverListener = l;
		l.modelChanged(this);
	}
	
	public int getRight() {
		return right;
	}
	
	public void setRight(int right) {
		this.right = right;
		setVerb("changed");
		canvasListener.modelChanged(this);
	}
	
	public int getBottom() {
		return bottom;
	}
	
	public void setBottom(int bottom) {
		this.bottom = bottom;
		setVerb("changed");
		canvasListener.modelChanged(this);
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
		right = x + width;
		setVerb("changed");
		canvasListener.modelChanged(this);
		shapeListener.modelChanged(this);
		if(serverListener != null) serverListener.modelChanged(this);
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
		bottom = y + height;
		setVerb("changed");
		canvasListener.modelChanged(this);
		shapeListener.modelChanged(this);
		if(serverListener != null) serverListener.modelChanged(this);
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
		setVerb("changed");
		canvasListener.modelChanged(this);
		shapeListener.modelChanged(this);
		if(serverListener != null) serverListener.modelChanged(this);
	}
	
	public int getHeight() {
		return height;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
	
	public void setHeight(int height) {
		this.height = height;
		setVerb("changed");
		canvasListener.modelChanged(this);
		shapeListener.modelChanged(this);
		if(serverListener != null) serverListener.modelChanged(this);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.setVerb("changed");
		canvasListener.modelChanged(this);
		if(serverListener != null) serverListener.modelChanged(this);
	}
	
	public ArrayList<Point> getKnobs() {
		return knobs;
	}
	
	public void setKnobs(ArrayList<Point> knobs) {
		this.knobs = knobs;
	}
	
	public void updateKnobs(int x, int y, int width, int height) {
		knobs.get(0).setLocation(x - 4, y - 4);
		knobs.get(1).setLocation(x + width - 4, y - 4);
		knobs.get(2).setLocation(x - 4, y + height - 4);
		knobs.get(3).setLocation(x + width - 4, y + height - 4);
	}
	
	public Point getOppositeKnob(Point selectedKnob) {
		if(selectedKnob == knobs.get(0))
			return knobs.get(3);
		else if(selectedKnob == knobs.get(3))
			return knobs.get(0);
		else if(selectedKnob == knobs.get(1))
			return knobs.get(2);
		else if(selectedKnob == knobs.get(2))
			return knobs.get(1);
		else return null;
	}

	public void setVerb(String verb) {
		this.verb = verb;
		if(serverListener != null) 
			if("remove".equals(verb) || "add".equals(verb) || "front".equals(verb) || "back".equals(verb)) 
				serverListener.modelChanged(this);
	}
	
	public String getVerb() {
		return this.verb;
	}
	
	public ModelListener getServerListener() {
		return serverListener;
	}
}