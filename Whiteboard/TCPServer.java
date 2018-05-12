import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer extends Thread implements ModelListener {
	private int port;
	private ServerSocket socket;
	private ArrayList<Socket> clients;
	private Canvas canvas;
	private int id;
	
	public TCPServer(int port, Canvas canvas) {
		//creating server
		this.port = port;
		id = 0;
		clients = new ArrayList<Socket>();
		this.canvas = canvas;
		try {
			socket = new ServerSocket(port);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void modelChanged(DShapeModel model) {
		//setting id of each new shape
		if(model.getId() == 0) {
			model.setId(++id);
			canvas.getDShape().get(canvas.getDShape().size()-1).setId(id);
		}
		try {
			//encoding string and object
			String verb = model.getVerb();
			OutputStream stream = new ByteArrayOutputStream();
			XMLEncoder encoder = new XMLEncoder(stream);
			encoder.writeObject(model);
			encoder.close();
			String xmlString = stream.toString();
			//sending to each client
			for(Socket c : clients) {
				ObjectOutputStream outString = new ObjectOutputStream(c.getOutputStream());
				ObjectOutputStream out = new ObjectOutputStream(c.getOutputStream());
				try {
					outString.writeObject(verb);
					out.writeObject(xmlString); 
					out.flush();
				} catch (Exception ex) {
					ex.printStackTrace();
					clients.remove(c);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while(true) {
				//connects to client and sends all initial adds
				Socket clientSocket = null;
				clientSocket = socket.accept();
				clients.add(clientSocket);
				ArrayList<DShape> shapes = canvas.getDShape();
				id = 0;
				for(DShape s : shapes) {
					id += 1;
					s.setId(id);
					s.setVerb("add");
					OutputStream stream = new ByteArrayOutputStream();
					XMLEncoder encoder = new XMLEncoder(stream);
					encoder.writeObject(s.getModel());
					encoder.close();
					String xmlString = stream.toString();
					ObjectOutputStream outString = new ObjectOutputStream(clientSocket.getOutputStream());
					ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
					try {
						outString.writeObject("add");
						out.writeObject(xmlString); 
						out.flush();
					} catch (Exception ex) {
						ex.printStackTrace();
						clients.remove(clientSocket);
					}
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
}