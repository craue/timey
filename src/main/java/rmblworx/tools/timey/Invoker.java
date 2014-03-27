package rmblworx.tools.timey;

/**
 * Aufruferimplementierung die das uebergebene Kommando ausfuehrt.
 * 
 * @author mmatthies
 */
public class Invoker {

	/**
	 * Referenz auf die Komandoimplementierung.
	 */
	private ICommand fCommand;

	/**
	 * Default constructor.
	 */
	public Invoker() {
		super();
	}

	/**
	 * Konstruktor.
	 * 
	 * @param cmd
	 *            Referenz der auszufuehrenden Kommandoimplementierung.
	 */
	public Invoker(final ICommand cmd) {
		super();
		this.fCommand = cmd;
	}

	/**
	 * @return den durch das jeweilige Kommando definierte Rueckgabewert.
	 * @param <T>
	 *            generischer Rueckgabetyp. Was konkret geliefert wird, legt die Implementierung des Kommandos fest.
	 */
	@SuppressWarnings("unchecked")
	public final <T> T execute() {
		return (T) this.fCommand.execute();
	}

	/**
	 * Setzt die Referenz der Kommandoimplementierung.
	 * 
	 * @param cmd
	 *            Referenz auf die Kommandoimplemetierung
	 */
	public final void storeCommand(final ICommand cmd) {
		this.fCommand = cmd;
	}
}
