import java.awt.FlowLayout;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class FrameLeakDemo2 {

	private static AtomicInteger liveFrames = new AtomicInteger(0);
	
	private static JFrame displayTestFrame(String title) {
		JFrame frm = new JFrame(title) {
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				liveFrames.decrementAndGet();
			}
			
			@Override
			public void dispose() {
				super.dispose();
				System.out.println("Disposed: "+title);
			}
		};
		
		liveFrames.incrementAndGet();
		
		frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frm.setSize(200, 200);
		frm.setLayout(new FlowLayout());

		JButton disposeAndShowBtn = new JButton("Dispose And Redisplay");
		frm.add(disposeAndShowBtn);
		disposeAndShowBtn.addActionListener(e->{
			frm.dispose();
			
			new Thread(()->{
				try {Thread.sleep(2000);} catch (InterruptedException x) {}
				
				frm.setVisible(true);
			}).start();
		});
		
		JButton disposeBtn = new JButton("Dispose Forever");
		frm.add(disposeBtn);
		disposeBtn.addActionListener(e->frm.dispose());
		
		frm.setVisible(true);
		
		return frm;
	}
	
	private static void edtMain() {
		displayTestFrame("A");
		displayTestFrame("B");
		displayTestFrame("C");
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
