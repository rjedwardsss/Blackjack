import blackjack.game.GameStatistics;
import blackjack.ui.BlackjackFrame;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Entry point: starts the render loop on a background thread and shows the Swing UI on the EDT.
 */
public final class Main implements Runnable {

    private static final int TARGET_FPS = 100;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    private final BlackjackFrame frame;
    private final AtomicBoolean applicationRunning;
    private long lastFrameNanos = System.nanoTime();

    private Main(BlackjackFrame frame, AtomicBoolean applicationRunning) {
        this.frame = frame;
        this.applicationRunning = applicationRunning;
    }

    public static void main(String[] args) {
        GameStatistics statistics = new GameStatistics();
        AtomicBoolean applicationRunning = new AtomicBoolean(true);
        BlackjackFrame[] frameHolder = new BlackjackFrame[1];
        try {
            SwingUtilities.invokeAndWait(() ->
                    frameHolder[0] = new BlackjackFrame(statistics, applicationRunning));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException(cause);
        }
        Main loop = new Main(frameHolder[0], applicationRunning);
        new Thread(loop, "blackjack-render-loop").start();
    }

    @Override
    public void run() {
        while (applicationRunning.get()) {
            long now = System.nanoTime();
            if (now - lastFrameNanos >= NANOS_PER_SECOND / TARGET_FPS) {
                frame.tick();
                frame.repaint();
                lastFrameNanos = now;
            }
        }
    }
}
