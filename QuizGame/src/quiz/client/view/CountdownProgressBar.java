package quiz.client.view;

import javax.swing.*;
import java.awt.*;

/**
 * @author Eric
 * @version 5.05.16
 */
public class CountdownProgressBar extends JProgressBar {

    private Timer timer;
    private int counter;
    private final int delay;

    /**
     * Creates a new CountdownProgessBar with the minimum 0, the maximum @param
     * max, and the delay 1000.
     *
     * @param max the maximum
     */
    public CountdownProgressBar(int max) {
        this(0, max, 1000);
    }

    /**
     * Creates a new CountdownProgressBar with the minimum @param min, the
     * maximum @param max, and the delay 1000.
     *
     * @param min the minimum
     * @param max the maximum
     */
    public CountdownProgressBar(int min, int max) {
        this(min, max, 1000);
    }

    /**
     * Creates a new CountdownProgressBar with the minimum @param min, the
     * maximum @param max, and the @delay delay.
     *
     * @param min   the minimum
     * @param max   the maximum
     * @param delay the delay in milliseconds
     */
    public CountdownProgressBar(int min, int max, int delay) {
        super(min, max);

        this.delay = delay;
        setCounter(max);
        UIManager.put("nimbusOrange", new Color(0, 255, 0));
        setForeground(new Color(0, 255, 0));

        timer = new Timer(delay, event -> {
            counter--;

            int red, green;
            double percentage = ((double) counter) / getMaximum();

            if (percentage >= 0.5) {
                green = 255;
                red = (int) ((1 - percentage) * 2 * 255);
            } else {
                red = 255;
                green = (int) (percentage * 2 * 255);
            }

            if (green  < 0) green = 0;
            setForeground(new Color(red, green, 0));
            UIManager.put("nimbusOrange", new Color(red, green, 0));
            setValue(counter);
            setString(String.valueOf(counter));

            if (counter < min)
                timer.stop();
        });
    }

    /**
     * Starts the countdown.
     */
    public void start() {
        timer.start();
    }

    /**
     * Restarts the countdown.
     * <p>
     * Starts the countdown in case it is not running so far.
     */
    public void restart() {
        if (!timer.isRunning())
            start();

        setForeground(new Color(0, 255, 0));
        timer.restart();
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
     * Returns whether the countdown is running.
     *
     * @return whether the countdown is running
     */
    public boolean isRunning() {
        return timer.isRunning();
    }
}