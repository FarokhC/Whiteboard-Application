import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class ShapeTableModel extends AbstractTableModel implements ModelListener{
	private static final String[] columnNames = {"X", "Y", "WIDTH", "HEIGHT"};
	private ArrayList<DShape> models;
	
	public ShapeTableModel(ArrayList<DShape> models) {
		super();
		this.models = models;
	}
	
	public String getColumnName(int columnIndex) {
	    return columnNames[columnIndex];
	}
	
	@Override
	public int getRowCount() {
		return models.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		//return value at column
		switch(column) {
			case 0: return Integer.valueOf(models.get(row).getX());
			case 1: return Integer.valueOf(models.get(row).getY());
			case 2: return models.get(row) instanceof DLine ? Integer.valueOf(models.get(row).getRight()) 
					- Integer.valueOf(models.get(row).getX()) : Integer.valueOf(models.get(row).getWidth());
			case 3: return models.get(row) instanceof DLine ? Integer.valueOf(models.get(row).getBottom()) 
					- Integer.valueOf(models.get(row).getY()) : Integer.valueOf(models.get(row).getHeight());
		}
		return null;
	}
	
	public void modelChanged(DShapeModel model) {
		//update table when a change happens
		int i = 0;
		for(DShape m : models) {
			if(m.getModel() == model)
				fireTableRowsUpdated(i, i);
			i++;
		}
		fireTableDataChanged();
	}
}
