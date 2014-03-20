package rmblworx.tools.timey.gui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.scene.control.Button;

import org.jemmy.fx.SceneDock;
import org.jemmy.fx.control.LabeledDock;
import org.jemmy.timing.State;
import org.junit.Before;
import org.junit.Test;

/**
 * GUI-Tests für die Stoppuhr (JemmyFX-Variante).
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
public class StopwatchControllerJemmyTest extends JemmyFxTest {

	/**
	 * Container für Elemente.
	 */
	private SceneDock scene;

	/**
	 * {@inheritDoc}
	 */
	protected final String getFxmlFilename() {
		return "StopwatchGui.fxml";
	}

	@Before
	public final void setUp() {
		scene = new SceneDock();

		// Stoppuhr vor jedem Test zurücksetzen, um Nebeneffekte zu vermeiden.
		FacadeManager.getFacade().resetStopwatch();
	}

	/**
	 * Testet den Zustand der Schaltflächen je nach Zustand der Stoppuhr.
	 */
	@Test
	public final void testStopwatchStartStopResetButtonStates() {
		final LabeledDock stopwatchStartButtonDock = new LabeledDock(scene.asParent(), "stopwatchStartButton");
		final Button stopwatchStartButton = (Button) stopwatchStartButtonDock.wrap().getControl();

		final LabeledDock stopwatchStopButtonDock = new LabeledDock(scene.asParent(), "stopwatchStopButton");
		final Button stopwatchStopButton = (Button) stopwatchStopButtonDock.wrap().getControl();

		final LabeledDock stopwatchResetButtonDock = new LabeledDock(scene.asParent(), "stopwatchResetButton");
		final Button stopwatchResetButton = (Button) stopwatchResetButtonDock.wrap().getControl();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
//		assertTrue(stopwatchStartButton.isFocused()); // muss nicht unbedingt der Fall sein

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr starten
		stopwatchStartButtonDock.mouse().click();
		// warten bis Start-Schaltfläche nicht mehr sichtbar ist
		stopwatchStartButtonDock.wrap().waitState(new State<Boolean>() {
			public Boolean reached() {
				return !stopwatchStartButton.isVisible() ? true : null;
			}
		});

		// Zustand der Schaltflächen testen
		assertFalse(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertTrue(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());
		assertTrue(stopwatchStopButton.isFocused());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr stoppen
		stopwatchStopButtonDock.mouse().click();
		// warten bis Start-Schaltfläche wieder sichtbar ist
		stopwatchStartButtonDock.wrap().waitState(new State<Boolean>() {
			public Boolean reached() {
				return stopwatchStartButton.isVisible() ? true : null;
			}
		});

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
		assertTrue(stopwatchStartButton.isFocused());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr zurücksetzen
		stopwatchResetButtonDock.mouse().click();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
		assertTrue(stopwatchStartButton.isFocused());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());
	}

}
