/**
 */
package rmblworx.tools.timey;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import rmblworx.tools.timey.vo.TimeDescriptor;

/**
 * PatternBox: "Receiver" implementation.
 * <ul>
 * <li>knows how to perform the operations associated with carrying out a request. Any class may serve as a Receiver.</li>
 * </ul>
 * 
 * @author Dirk Ehms, <a href="http://www.patternbox.com">www.patternbox.com</a>
 * @author mmatthies
 */
class Countdown implements ICountdown, ApplicationContextAware {
	/**
	 * Anzahl der Threads die Zeit messen sollen.
	 */
	private final byte amountOfThreads;
	/**
	 * Spring-Kontext.
	 */
	private ApplicationContext context;
	/**
	 * Die genutzte Zeitmessimplementierung.
	 */
	private ICountdownTimer countdownTimer;
	/**
	 * Gibt die Maszzahl fuer die Zeiteinheit an.
	 * 
	 * @see #timeUnit
	 */
	private final int delayPerThread;
	/**
	 * Gibt die Zeiteinheit an in welchem der Intervall die gemessene Zeit geliefert wird.
	 */
	private final TimeUnit timeUnit;

	/**
	 * Konstruktor welcher eine Instanz dieses Receiver erzeugt.
	 * 
	 * @param amount
	 *            Anzahl der Zeitmessungs-Threads
	 * @param delay
	 *            Bestimmt den Intervall in welchem die vom Thread gemessene Zeit zurueckgeliefert wird.
	 * @param unit
	 *            Maszeinheit fuer den Intervall.
	 */
	public Countdown(final byte amount, final int delay, final TimeUnit unit) {
		this.amountOfThreads = amount;
		this.delayPerThread = delay;
		this.timeUnit = unit;
	}

	/**
	 * Instanziiert nach Bedarf die Timerimplementierung.
	 */
	private void initCountdownTimer() {
		if (this.countdownTimer == null) {
			this.countdownTimer = (ICountdownTimer) this.context.getBean("simpleCountdown");
		}
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public Boolean setCountdownTime(final TimeDescriptor descriptor) {
		this.initCountdownTimer();
		return this.countdownTimer.setCountdownTime(descriptor);
	}

	@Override
	public TimeDescriptor startCountdown() {
		this.initCountdownTimer();
		return this.countdownTimer.startCountdown(this.amountOfThreads, this.delayPerThread, this.timeUnit);
	}

	@Override
	public Boolean stopCountdown() {
		this.initCountdownTimer();
		return this.countdownTimer.stopCountdown();
	}
}