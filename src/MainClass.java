import java.awt.Color;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class MainClass extends JFrame implements Runnable, ActionListener, TextListener{

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	private static final long serialVersionUID = 1L;
	private ServerSocket server;
	private boolean done;
	private JButton startServer,startGame;
	private TextField username, servername;
	private GameManager gManager;
	public MainClass() {
		done = true;
		this.setLayout(null);
		this.setSize(400, 150);
		this.setResizable(false);
		this.getContentPane().setBackground(Color.BLACK);
		this.getContentPane().setForeground(Color.green);
		
		startServer = new JButton("Start Server");
		startServer.setBounds(10, 10, 150, 25);
		startServer.addActionListener(this);
		startServer.setEnabled(false);
		startServer.setBackground(Color.BLACK);
		startServer.setForeground(Color.GREEN);
		this.add(startServer);
		
		startGame = new JButton("Start Game");
		startGame.setBounds(210, 10, 150, 25);
		startGame.addActionListener(this);
		startGame.setEnabled(false);
		startGame.setBackground(Color.BLACK);
		startGame.setForeground(Color.GREEN);
		this.add(startGame);

		
		
		username = new TextField();
		username.setBounds(120, 50, 200, 25);
		username.addTextListener(this);
		username.setBackground(Color.black);
		username.setForeground(Color.green);
		this.add(username);
		
		JLabel nameLabel = new JLabel("Username: ");
		nameLabel.setForeground(Color.green);
		nameLabel.setBounds(10, 50, 100, 25);
		this.add(nameLabel);
		
		servername = new TextField();
		servername.setBounds(120, 90, 200, 25);
		servername.addActionListener(this);
		servername.setBackground(Color.black);
		servername.setForeground(Color.green);
		servername.setEnabled(false);
		this.add(servername);
		
		JLabel serverLabel = new JLabel("Server name: ");
		serverLabel.setForeground(Color.green);
		serverLabel.setBounds(10,  90,  100,  25);
		this.add(serverLabel);
	}
	public static void main(String[] args) throws Exception {
		MainClass mc = new MainClass();
		mc.setDefaultCloseOperation(MainClass.EXIT_ON_CLOSE);
		mc.setVisible(true);
	}
	//Server
	@Override
	public void run() {
		while (!done) {
			try {
				System.out.println("Server listening for connections");
				gManager.addClient(server.accept());
				System.out.println("Server just added a client");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startServer) {
			done = false;
			try {
				server = new ServerSocket(4444);
				gManager = new GameManager();
				new Thread(this).start();
				Client c = new Client(new Socket("localhost", 4444), username.getText().trim());
				c.setVisible(true);
			} catch (IOException ex) {
				if (ex instanceof BindException)JOptionPane.showMessageDialog(null, "Server initialization error: "+ex.getMessage());
				else ex.printStackTrace();
			}
			startGame.setEnabled(true);
		}
		if (e.getSource() == servername) {
			try {
				Client c = new Client(new Socket(servername.getText().trim(), 4444), username.getText().trim());
				c.setVisible(true);
			} catch (Exception ex) {
				if (ex instanceof UnknownHostException) {
					JOptionPane.showMessageDialog(null, "Unknown host");
				}
			}
		}
		if(e.getSource()==startGame){
			gManager.startGame();
			startGame.setEnabled(false);
		}
		startServer.setEnabled(false);
		servername.setEnabled(false);
		username.setText("");
	}
	@Override
	public void textValueChanged(TextEvent arg0) {
		if (username.getText().trim().length() == 0) {
			servername.setEnabled(false);
			startServer.setEnabled(false);
		} else {
			servername.setEnabled(true);
			startServer.setEnabled(done);
		}
		
	}

}
