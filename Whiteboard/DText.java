import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class DText extends DShape implements ModelListener {
	private	DTextModel model;
	private String text = "Hello";
	private Font font = new Font("Dialog", Font.BOLD, 24);
	
	double size = 1.0;
	
	public DText() {
        super();
        model = new DTextModel(10, 10, 50, 20, Color.GRAY);
    }
	
	public Rectangle getBounds() {
		return model.getBounds();
	}
	
	public DText(int x, int y, int width, int height, Color color, Font f) {
		super(x, y, width, height, color);
        model = new DTextModel(10, 10, 50, 20, Color.GRAY);
		model.setText(text);
		font = f;
		model.setFont(font);
	}
	
	public Font computeFont(Graphics g) {
		double size = 1.0;
		double lastSize = 1.0;
		Font f = new Font(this.model.getFontName(), 0, (int) size);
		
		while(size < this.model.getHeight()) {
			lastSize = size;
			size = (size * 1.10) + 1;
			f = new Font(g.getFont().getFontName(), 0, (int) size);
		}
		return new Font(g.getFont().getFontName(), 0, (int) lastSize);
	}
	
	public void draw(Graphics g) {
		
	}

	public DTextModel getModel() {
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
	
	public void setFont(Font f) {
		model.setFont(f);
		if(getModel().getServerListener() != null) {
			getModel().getServerListener().modelChanged(this.getModel());
		}
	}
	
	public void setText(String text) {
		model.setText(text);
	}

	public String getText() {
		return model.getText();
	}

	public Font getFont() {
		return model.getFont();
	}
	
	public void setSize(int size) {
		model.setSize(size);
	}
	
	public double getSize() {
		return model.getSize();
	}
}