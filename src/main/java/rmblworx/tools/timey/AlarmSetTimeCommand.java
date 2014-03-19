package rmblworx.tools.timey;

import rmblworx.tools.timey.vo.TimeDescriptor;

/**
 * PatternBox: "ConcreteCommand" implementation.
 * <ul>
 * <li>defines a binding between a Receiver object and an action.</li>
 * <li>implements Execute by invoking the corresponding operation(s) on Receiver.</li>
 * </ul>
 * 
 * @author Dirk Ehms, <a href="http://www.patternbox.com">www.patternbox.com</a>
 * @author mmatthies
 */
public class AlarmSetTimeCommand implements ICommand {
	/** stores the Receiver instance of the ConcreteCommand. */
	private final IAlarm fReceiver;
	/**
	 * Beschreibung des Alarmzeitpunktes.
	 */
	private final TimeDescriptor td;

	/**
	 * Erweiterter Konstruktor.
	 * @param receiver Empfaengerimplmentierung.
	 * @param td Beschreibung des Alarmzeitpunktes.
	 */
	public AlarmSetTimeCommand(final IAlarm receiver, final TimeDescriptor td) {
		super();
		this.fReceiver = receiver;
		this.td = td;
	}

	/**
	 * This method executes the command by invoking the corresponding method
	 * of the Receiver instance.
	 * 
	 */
	@Override
	public <T> T execute() {
		this.fReceiver.setAlarmtimestamp(this.td);

		return (T) this.td;
	}

}