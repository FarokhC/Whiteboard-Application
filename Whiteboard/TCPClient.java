import java.beans.XMLDecoder;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class TCPClient extends Thread {
	private String host;
	private int port;
	private Socket socket;
	private Canvas canvas;
	
	public TCPClient(String host, int port, Canvas canvas) {
		this.host = host;
		this.port = port;
		this.canvas = canvas;
		canvas.setClient(true);
	}
	
	@Override
	public void run() {
		try {
			this.socket = new Socket(host, port);
			while(true) {
				//gets encoded shape from server
				ObjectInputStream inString = new ObjectInputStream(socket.getInputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				String verb = (String) inString.readObject();	//this is for the action string
				String xmlString = (String) in.readObject();
				XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
				DShapeModel shape = (DShapeModel) decoder.readObject();
				//determine what action must be done
				if("add".equals(verb)) {
					DShape createdShape = canvas.createShape(shape);
					if(shape instanceof DTextModel) {
						((DText)createdShape).setText(((DTextModel)shape).getText());
						((DText)createdShape).setX((int)((DTextModel)shape).getX());
						((DText)createdShape).setY((int)((DTextModel)shape).getY());
						((DText)createdShape).setFont(((DTextModel)shape).getFont());
						((DText)createdShape).setHeight(((DTextModel)shape).getHeight());
						((DText)createdShape).setWidth(((DTextModel)shape).getWidth());
						((DText)createdShape).setColor(((DTextModel)shape).getColor());
					}
				}
				//remove shape
				else if("remove".equals(verb)) {
					int id = shape.getId();
					for(int i = 0; i < canvas.getDShape().size(); i++) 
						if(canvas.getDShape().get(i).getId() == id) 
							canvas.removeDShape(i);
				}
				//move to front
				else if("front".equals(verb)) {
					ArrayList<DShape> shapes = canvas.getDShape();
					DShape moved = null;
					for(int i = 0; i < canvas.getDShape().size(); i++) {
						if(canvas.getDShape().get(i).getId() == shape.getId()) {
							moved = canvas.getDShape().get(i);
						}
					}
					shapes.remove(moved);
					shapes.add(moved);
					canvas.modelChanged(moved.getModel());
				}
				//move to back
				else if("back".equals(verb)) {
					ArrayList<DShape> shapes = canvas.getDShape();
					DShape moved = null;
					for(int i = 0; i < canvas.getDShape().size(); i++) 
						if(canvas.getDShape().get(i).getId() == shape.getId()) 
							moved = canvas.getDShape().get(i);
					shapes.remove(moved);
					shapes.add(0, moved);
					canvas.modelChanged(moved.getModel());
				}
				//general shape change
				else if("changed".equals(verb)) {
					DShape changedShape = null;
					for(int i = 0; i < canvas.getDShape().size(); i++)
						if(canvas.getDShape().get(i).getId() == shape.getId()) 
							changedShape = canvas.getDShape().get(i);
					changedShape.setX(shape.getX());
					changedShape.setY(shape.getY());
					changedShape.setWidth(shape.getWidth());
					changedShape.setHeight(shape.getHeight());
					changedShape.updateKnobs(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
					changedShape.setColor(shape.getColor());
					changedShape.setId(shape.getId());
					if(shape instanceof DTextModel) {
						((DText)changedShape).setFont(((DTextModel)shape).getFont());
					}
				}
			}
		} catch(IOException io) {
			canvas.setStatusString("Client Failed to Connect");
			io.printStackTrace();
		} catch(ClassNotFoundException ce) {
			ce.printStackTrace();
		}
	}
}