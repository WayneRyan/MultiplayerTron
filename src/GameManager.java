import java.net.Socket;


public class GameManager implements Runnable {
	private LockableList<Player> clients;
	
	private boolean[][] map;
	private boolean gameStarted;
	private int time;
	
	public GameManager(){
		time = 0;
		gameStarted = false;
		map = new boolean[MainClass.HEIGHT/Player.SIZE][MainClass.WIDTH/Player.SIZE];
		System.out.println("Game manager has been created");
		clients = new LockableList<Player>();
		new Thread(this).start();
	}
	
	public void startGame(){
		time= 179;
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String message = " ";
			clients.acquire();
			for(Player p : clients){
				if(gameStarted)p.update(map);
				message += p + ",";
			}
			message = message + time/30;
			if(time==29)gameStarted=true;
			if(time>0)time--;
			for(Player p : clients)p.send(message);
			clients.release();
		}
	}
	
	public void addClient(Socket client){
		System.out.println("Manager just added client");
		Player p = new Player(client);
		clients.acquire();
		clients.add(p);
		clients.release();
		//new ServerHelper(s, clients);
	}
	

}
