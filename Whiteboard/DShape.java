import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class DShape {
	DShapeModel model;
	
	public DShape() {
		model = new DShapeModel(10,10,20,20,Color.GRAY);
		this.setVerb("add");
	}
	
	public DShape(int x, int y, int width, int height, Color color) {
		model = new DShapeModel(x, y, width, height, color);
		this.setVerb("add");
	}
	
	public void mimic(DShapeModel other) {
		model.mimic(other);
	}
	
	public void setRight(int right) {
		model.setRight(right);
	}
	
	public int getRight() {
		return model.getRight();
	}
	
	public void setBottom(int bottom) {
		model.setBottom(bottom);
	}
	
	public int getBottom() {
		return model.getBottom();
	}
	
	public void setX(int x) {
		model.setX(x);
	}
	
	public int getX() {
		return model.getX();
	}
	
	public void setY(int y) {
		model.setY(y);
	}
	
	public int getY() {
		return model.getY();
	}
	
	public void setWidth(int width) {
		model.setWidth(width);
	}
	
	public int getWidth() {
		return model.getWidth();
	}
	
	public void setHeight(int height) {
		model.setHeight(height);
	}
	
	public int getHeight() {
		return model.getHeight();
	}
	
	public void setColor(Color color) {
		model.setColor(color);
	}
	
	public Color getColor() {
		return model.getColor();
	}
	
	public void setKnobs(ArrayList<Point> knobs) {
		model.setKnobs(knobs);
	}
	
	public ArrayList<Point> getKnobs() {
		return model.getKnobs();
	}
	
public void updateKnobs(int x, int y, int width, int height) {
		model.updateKnobs(x, y, width, height);
	}
	
	public Point getOppositeKnob(Point selectedKnob) {
		return model.getOppositeKnob(selectedKnob);
	}
	
	public void setModel(DShapeModel newModel) {
		this.model = newModel;
	}
	
	public DShapeModel getModel() {
		return model;
	}
	
	public void setId(int id) {
		model.setId(id);
	}
	
	public int getId() {
		return model.getId();
	}
	
	public void setVerb(String verb) {
		model.setVerb(verb);
	}
	
	public String getVerb() {
		return model.getVerb();
	}
}
