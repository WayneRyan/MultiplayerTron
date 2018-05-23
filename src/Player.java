import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class Player implements Runnable{
	
	private BufferedReader in;
	private BufferedWriter out;
	private String username;
	private int x,y,vX,vY;
	private boolean done,crashed;
	private Color myColor;
	public static final int SIZE = 10;
	

	public Player(Socket s) {
		vX = 1;
		crashed = false;
		myColor = randomColor();
		done = false;
		x = (int)(Math.random()*MainClass.WIDTH)/SIZE;
		y = (int)(Math.random()*MainClass.HEIGHT)/SIZE;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			username = in.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Thread(this).start();
	}
	
	private Color randomColor(){
		float r = (float)(Math.random()*0.75+0.25);
		float g = (float)(Math.random()*0.75+0.25);
		float b = (float)(Math.random()*0.75+0.25);
		return new Color(r,g,b);
	}
	
	public boolean isDone(){return done;}
	
	public String toString(){
		return myColor.getRGB()+":"+username + ":" + (x*SIZE) + " " + (y*SIZE) + ":" + crashed;
	}
	
	public void update(boolean[][] map){
		if(crashed || (vX==0 && vY==0))return;
		x += vX;
		y += vY;
		if(x<0 || y<0 || 
		   x>=map[0].length || y>=map.length || 
		   map[y][x]) crashed = true;
		else map[y][x] = true;
	}


	@Override
	public void run() {
		while(true){
			try {
				String command = in.readLine();
				switch (command){
				case "W" : if(vY==0){vX = 0; vY = -1;} break;
				case "A" : if(vX==0){vX = -1; vY = 0;} break;
				case "S" : if(vY==0){vX = 0; vY = 1;} break;
				case "D" : if(vX==0){vX = 1; vY = 0;} break;
				}
				System.out.println(command);
			} catch (IOException e) {
				done = true;
			}
			
		}
		
	}

	public void send(String message) {
		try {
			out.write(message + '\n');
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
