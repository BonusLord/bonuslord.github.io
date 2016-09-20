import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class FrameLeakDemo {

	private static AtomicInteger liveFrames = new AtomicInteger(0);
	
	private static JFrame displayTestFrame() {
		JFrame frm = new JFrame("Leak Demo") {
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				liveFrames.decrementAndGet();
			}
		};
		
		liveFrames.incrementAndGet();
		
		frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frm.setSize(100, 100);

		frm.setVisible(true);
		
		return frm;
	}
	
	private static void edtMain() {
		displayTestFrame();
		displayTestFrame();
		displayTestFrame();
	}
	
	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeLater(()->edtMain());
		
		while (true) {
			Thread.sleep(1000);
			
			System.gc();
			System.out.println("JFrames remaining: "+liveFrames.get());
		}
	}
}
