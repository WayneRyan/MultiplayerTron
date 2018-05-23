import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class LockableList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;
	private Semaphore lock;
	
	public LockableList() {
		lock = new Semaphore(1);
	}
	
	public void acquire(){
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void release(){
		lock.release();
	}
	
}
