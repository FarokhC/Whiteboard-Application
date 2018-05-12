import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

public class DTextModel extends DShapeModel {
	private String text;
	private Font font;
	private int size;
	
	public DTextModel() {
		super(10, 10, 50, 20, Color.GRAY);
	}
	public DTextModel(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, color);
	}
	
	public Rectangle getBounds() {
		return super.getBounds();
	}
	
	public void setFont(Font f) {
		font = f;
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getFontName() {
		return font.getFontName();
	}
}