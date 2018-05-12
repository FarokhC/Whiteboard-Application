import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.io.Serializable;

public class DRectModel extends DShapeModel implements Serializable {
	public DRectModel() {
		super();
	}
	
	public DRectModel(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, color);
	}
	
}