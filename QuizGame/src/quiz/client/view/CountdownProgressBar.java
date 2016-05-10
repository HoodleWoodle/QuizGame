package quiz.client.view;

import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * 
 * @author Eric
 * @version 5.05.16
 */
public class CountdownProgressBar extends JProgressBar {

	private Timer timer;
	private int counter;
	private final int delay;

	/**
	 * Creates a new CountdownProgessBar with the minimum 0, the
	 * maximum @param max, and the delay 1000.
	 * 
	 * @param min the minimum
	 * @param max the maximum
	 * @param delay the delay in milliseconds
	 */
	public CountdownProgressBar(int max) {
		this(0, max, 1000);
	}

	/**
	 * Creates a new CountdownProgessBar with the minimum @param min, the
	 * maximum @param max, and the delay 1000.
	 * 
	 * @param min the minimum
	 * @param max the maximum
	 * @param delay the delay in milliseconds
	 */
	public CountdownProgressBar(int min, int max) {
		this(min, max, 1000);
	}

	/**
	 * Creates a new CountdownProgessBar with the minimum @param min, the
	 * maximum @param max, and the @delay delay.
	 * 
	 * @param min the minimum
	 * @param max the maximum
	 * @param delay the delay in milliseconds
	 */
	public CountdownProgressBar(int min, int max, int delay) {
		super(min, max);

		this.delay = delay;
		setCounter(max);

		timer = new Timer(delay, event -> {
			counter--;
			setValue(counter);
			if (counter < min)
				timer.stop();
		});
		timer.start();
	}

	/**
	 * Returns the delay.
	 * 
	 * @return the delay in milliseconds
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Returns the Timer.
	 * 
	 * @return the Timer
	 */
	public Timer getTimer() {
		return timer;
	}

	/**
	 * Sets the value of counter.
	 * 
	 * @param counter the new counter value
	 * @throws IllegalArgumentException when @param counter > max or counter < min.
	 */
	public void setCounter(int counter) {
		if (counter > getMaximum() || counter < getMinimum())
			throw new IllegalArgumentException();

		setValue(this.counter = counter);
	}

	/**
	 * Returns the counter.
	 * 
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}
	
	/**
	 * Returns whether the countdown is over.
	 * 
	 * @return whether the countdown is over
	 */
	public boolean isOver() {
		return counter > getMinimum();
	}
}