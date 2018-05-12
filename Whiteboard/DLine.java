import java.awt.Color;
import java.awt.Graphics;

public class DLine extends DShape implements ModelListener{
	DLineModel model;
	
	public DLine() {
		super();
		model = new DLineModel(10,10,20,20,Color.GRAY);
	}
	
	public DLine(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, color);
		model = new DLineModel(x, y, width, height, color);
	}
	
	public void draw(Graphics g) {
		
	}
	
	public DLineModel getModel() {
        return model;
    }
	
	public void modelChanged(DShapeModel model) {
        setX(model.getX());
        setY(model.getY());
        setColor(model.getColor());
        setHeight(model.getHeight());
        setWidth(model.getWidth());
    }
	
	public int getRight() {
		return model.getRight();
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
	
}
