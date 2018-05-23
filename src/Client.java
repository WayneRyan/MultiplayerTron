import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JFrame;

public class Client extends JFrame implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;
	private BufferedReader in;
	private BufferedWriter out;
	private String username;
	private BufferedImage offscreen;
	private Graphics bg;
	private int count;
	private HashMap<String, Counter> scoreBoard;

	public Client(Socket s, String name) {
		scoreBoard = new HashMap<String, Counter>();
		count = 0;
		username = name;
		try {
			offscreen = new BufferedImage(MainClass.WIDTH+150, MainClass.HEIGHT,
					BufferedImage.TYPE_INT_RGB);
			bg = offscreen.getGraphics();
			Font f = bg.getFont().deriveFont(100f);
			bg.setFont(f);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new BufferedWriter(
					new OutputStreamWriter(s.getOutputStream()));
			out.write(username + '\n');
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setSize(MainClass.WIDTH+150, MainClass.HEIGHT+30);
		this.addKeyListener(this);
		new Thread(this).start();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		String line = "" + e.getKeyChar() + '\n';
		try {
			out.write(line.toUpperCase());
			out.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public void paint(Graphics g) {
		g.drawImage(offscreen, 0, 30, null);
	}

	@Override
	public void run() {
		boolean counting = false;
		while (true) {
			try {
				String line = in.readLine();
				String[] users = line.split(",");
				for (int i = 0; i < users.length - 1; i++) {

					String[] userParts = users[i].split(":");
					Color c = new Color(Integer.parseInt(userParts[0].trim()));
					String name = userParts[1];
					String[] coords = userParts[2].split(" ");
					boolean done = Boolean.parseBoolean(userParts[3]);
					if(!done){
						if(counting && scoreBoard.containsKey(name))scoreBoard.get(name).cnt++;
						else scoreBoard.put(name, new Counter(0,c));
					}
					int x = Integer.parseInt(coords[0]);
					int y = Integer.parseInt(coords[1]);
					bg.setColor(c);
					bg.fillRect(x, y, Player.SIZE, Player.SIZE);
				}
				// draw the scoreboard
				int y = 140;
				bg.setColor(new Color(0.1f,0.1f,0.1f));
				bg.fillRect(MainClass.WIDTH, 0, 150, MainClass.HEIGHT);
				for(String playerName : scoreBoard.keySet()){
					Counter score = scoreBoard.get(playerName);
					bg.setColor(score.myColor);
					Font old = bg.getFont();
					Font newFont = old.deriveFont(20f);
					bg.setFont(newFont);
					bg.drawString(playerName+" "+score.cnt, MainClass.WIDTH, y);
					y+=35;
					bg.setFont(old);
				}
				if (!users[users.length - 1].equals("0")) {
					counting = true;
					bg.setColor(Color.red);
					bg.drawString(users[users.length - 1], MainClass.WIDTH,
							MainClass.HEIGHT / 2);
				}

				repaint();
				// System.out.println(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
