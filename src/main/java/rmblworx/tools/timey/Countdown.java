package rmblworx.tools.timey;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import rmblworx.tools.timey.exception.NullArgumentException;
import rmblworx.tools.timey.vo.TimeDescriptor;

/*
 * Copyright 2014-2015 Christian Raue
 * MIT License http://opensource.org/licenses/mit-license.php
 */
/**
 * Ermöglicht die Zeitmessung in Form eines Countdowns.
 *
 * @author mmatthies
 */
class Countdown implements ICountdown, ApplicationContextAware {
	/**
	 * Spring-Kontext.
	 */
	private ApplicationContext context;
	/**
	 * Die genutzte Zeitmessimplementierung.
	 */
	private ICountdownTimer countdownTimer;
	/**
	 * Gibt die Maßzahl für die Zeiteinheit an.
	 *
	 * @see #timeUnit
	 */
	private final int delayPerThread;
	/**
	 * Gibt die Zeiteinheit an in welchem der Intervall die gemessene Zeit geliefert wird.
	 */
	private final TimeUnit timeUnit;

	/**
	 * Erweiterter Konstruktor. Ausschliesslich für Testzwecke.
	 * 
	 * @param countdownTimer
	 *            Referenz auf die CountdownTimer-Implementierung.
	 * @param delay
	 *            Ausführungsverzögerung des Zeitmessungs-Threads
	 * @param unit
	 *            Zeiteinheit für die zuvor angegebene Ausführungsverzögerung
	 */
	Countdown(final ICountdownTimer countdownTimer, final int delay, final TimeUnit unit) {
		this(delay, unit);
		this.countdownTimer = countdownTimer;
	}

	/**
	 * Konstruktor welcher eine Instanz dieses Receiver erzeugt.
	 *
	 * @param delay
	 *            Bestimmt den Intervall in welchem die vom Thread gemessene Zeit zurückgeliefert wird.
	 * @param unit
	 *            Maßeinheit für den Intervall.
	 */
	public Countdown(final int delay, final TimeUnit unit) {
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
		if (descriptor == null) {
			throw new NullArgumentException();
		}
		return this.countdownTimer.setCountdownTime(descriptor);
	}

	@Override
	public TimeDescriptor startCountdown() {
		this.initCountdownTimer();
		return this.countdownTimer.startCountdown(this.delayPerThread, this.timeUnit);
	}

	@Override
	public Boolean stopCountdown() {
		this.initCountdownTimer();
		return this.countdownTimer.stopCountdown();
	}
}
