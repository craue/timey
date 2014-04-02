package rmblworx.tools.timey.gui;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;

import java.util.Locale;

import javafx.scene.Parent;
import javafx.stage.Stage;

import org.junit.Before;

import com.athaydes.automaton.FXApp;
import com.athaydes.automaton.FXer;
import com.athaydes.automaton.Speed;
import com.google.code.tempusfugit.concurrency.ThreadUtils;
import com.google.code.tempusfugit.temporal.Timeout;

/**
 * Basisklasse für GUI-Tests mit {@link https://github.com/renatoathaydes/Automaton}.
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
public abstract class GuiTest {

	/**
	 * Timeout beim Warten auf das Eintreten von Bedingungen.
	 */
	public static final Timeout TIMEOUT = timeout(seconds(30));

	/**
	 * Sprache für GUI-Tests.
	 */
	public static final Locale TEST_LOCALE = Locale.GERMAN;

	static {
		// Standardsprache für alle GUI-Tests setzen (wichtig z. B. als Fallback auf Travis)
		Locale.setDefault(TEST_LOCALE);
	}

	/**
	 * Fenster.
	 */
	private Stage stage;

	/**
	 * Test-Client.
	 */
	private FXer fxer;

	@Before
	public final void initClient() {
		stage = FXApp.initialize();
		final Parent rootNode = getRootNode();
		FXApp.doInFXThreadBlocking(new Runnable() {
			public void run() {
				stage.getScene().setRoot(rootNode);
			}
		});
		fxer = FXer.getUserWith(rootNode);
		fxer.waitForFxEvents();

		FXer.setDEFAULT(Speed.VERY_FAST);
	}

	/**
	 * @return Elternknoten der GUI-Elemente
	 */
	protected abstract Parent getRootNode();

	/**
	 * @return Fenster (Ist erst nach Start eines Tests verfügbar.)
	 */
	protected final Stage getStage() {
		return stage;
	}

	/**
	 * @return Test-Client (Ist erst nach Start eines Tests verfügbar.)
	 */
	protected final FXer getClient() {
		return fxer;
	}

	/**
	 * Blockiert bis alle Ereignisse im JavaFX-Thread abgearbeitet wurden.
	 */
	protected final void awaitEvents() {
		fxer.waitForFxEvents();
		ThreadUtils.sleep(millis(50));
	}

}
