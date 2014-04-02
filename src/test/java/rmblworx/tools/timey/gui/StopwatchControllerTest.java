package rmblworx.tools.timey.gui;

import static com.athaydes.automaton.assertion.AutomatonMatcher.hasText;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeoutException;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import rmblworx.tools.timey.vo.TimeDescriptor;

import com.athaydes.automaton.FXer;
import com.google.code.tempusfugit.temporal.Condition;

/**
 * GUI-Tests für die Stoppuhr.
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
@Category(TimeyGuiTest.class)
public class StopwatchControllerTest extends FxmlGuiControllerTest {

	/**
	 * Test-Client.
	 */
	private FXer fxer;

	/**
	 * {@inheritDoc}
	 */
	protected final String getFxmlFilename() {
		return "Stopwatch.fxml";
	}

	@Before
	public final void setUp() {
		fxer = getClient();
	}

	/**
	 * Testet den Zustand der Schaltflächen je nach Zustand der Stoppuhr.
	 */
	@Test
	public final void testStopwatchStartStopResetButtonStates() {
		final Button stopwatchStartButton = (Button) fxer.getAt("#stopwatchStartButton");
		final Button stopwatchStopButton = (Button) fxer.getAt("#stopwatchStopButton");
		final Button stopwatchResetButton = (Button) fxer.getAt("#stopwatchResetButton");

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr starten
		stopwatchStartButton.fire();
		fxer.waitForFxEvents();
		verify(getController().getGuiHelper().getFacade()).startStopwatch();

		// Zustand der Schaltflächen testen
		assertFalse(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertTrue(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());
		assertTrue(stopwatchStopButton.isFocused());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr stoppen
		stopwatchStopButton.fire();
		fxer.waitForFxEvents();
		verify(getController().getGuiHelper().getFacade()).stopStopwatch();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
		assertTrue(stopwatchStartButton.isFocused());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr zurücksetzen
		stopwatchResetButton.fire();
		fxer.waitForFxEvents();
		verify(getController().getGuiHelper().getFacade()).resetStopwatch();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
		assertTrue(stopwatchStartButton.isFocused());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());
	}

	/**
	 * Testet die Darstellung der Zeit mit und ohne Millisekunden-Anteil.
	 */
	@Test
	public final void testStopwatchTimeLabelMilliseconds() throws InterruptedException, TimeoutException {
		final CheckBox stopwatchShowMillisecondsCheckbox = (CheckBox) fxer.getAt("#stopwatchShowMillisecondsCheckbox");
		final Label stopwatchTimeLabel = (Label) fxer.getAt("#stopwatchTimeLabel");

		// Ausgangszustand
		assertTrue(stopwatchShowMillisecondsCheckbox.isSelected());
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());

		// Millisekunden-Anteil ausblenden
		stopwatchShowMillisecondsCheckbox.fire();
		fxer.waitForFxEvents();
		assertEquals("00:00:00", stopwatchTimeLabel.getText());

		// Millisekunden-Anteil wieder einblenden
		stopwatchShowMillisecondsCheckbox.fire();
		fxer.waitForFxEvents();
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());
	}

	/**
	 * Testet die Anzeige der gemessenen Zeit.
	 */
	@Test
	public final void testStopwatchStartStopTimeMeasured() throws InterruptedException, TimeoutException {
		final Button stopwatchStartButton = (Button) fxer.getAt("#stopwatchStartButton");
		final Button stopwatchStopButton = (Button) fxer.getAt("#stopwatchStopButton");
		final Label stopwatchTimeLabel = (Label) fxer.getAt("#stopwatchTimeLabel");

		// Stoppuhr starten
		when(getController().getGuiHelper().getFacade().startStopwatch()).thenReturn(new TimeDescriptor(50));
		stopwatchStartButton.fire();
		fxer.waitForFxEvents();

		// Stoppuhr stoppen
		stopwatchStopButton.fire();
//		awaitEvents();

		// gemessene Zeit muss angezeigt sein
//		assertThat(stopwatchTimeLabel, hasText("00:00:00.050"));
//		assertEquals("00:00:00.050", stopwatchTimeLabel.getText());
		waitOrTimeout(new Condition() {
			public boolean isSatisfied() {
				return "00:00:00.050".equals(stopwatchTimeLabel.getText());
			}
		}, TIMEOUT);

		// Stoppuhr wieder starten, um zweite (additive) Messung zu berücksichtigen
		when(getController().getGuiHelper().getFacade().startStopwatch()).thenReturn(new TimeDescriptor(200));
		stopwatchStartButton.fire();
		fxer.waitForFxEvents();

		// Stoppuhr wieder stoppen
		stopwatchStopButton.fire();
		awaitEvents();

		// gemessene Zeit muss angezeigt sein
		assertEquals("00:00:00.200", stopwatchTimeLabel.getText());
	}

	/**
	 * Testet die Funktionalität der Zurücksetzen-Schaltfläche.
	 */
	@Test
	public final void testStopwatchReset() throws InterruptedException, TimeoutException {
		final Button stopwatchStartButton = (Button) fxer.getAt("#stopwatchStartButton");
		final Button stopwatchStopButton = (Button) fxer.getAt("#stopwatchStopButton");
		final Button stopwatchResetButton = (Button) fxer.getAt("#stopwatchResetButton");
		final Label stopwatchTimeLabel = (Label) fxer.getAt("#stopwatchTimeLabel");

		// Stoppuhr zurücksetzen, ohne sie vorher gestartet zu haben
		stopwatchResetButton.fire();
		fxer.waitForFxEvents();
		verify(getController().getGuiHelper().getFacade()).resetStopwatch();

		// angezeigte Zeit muss zurückgesetzt sein
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());

		// Stoppuhr starten
		when(getController().getGuiHelper().getFacade().startStopwatch()).thenReturn(new TimeDescriptor(9876));
		stopwatchStartButton.fire();
		awaitEvents();

		// gemessene Zeit muss angezeigt sein
		assertEquals("00:00:09.876", stopwatchTimeLabel.getText());

		// Stoppuhr stoppen
		stopwatchStopButton.fire();
		fxer.waitForFxEvents();

		// Stoppuhr zurücksetzen
		stopwatchResetButton.fire();
		fxer.waitForFxEvents();

		// angezeigte Zeit muss zurückgesetzt sein
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());
	}

	/**
	 * Testet die Funktionalität der Zurücksetzen-Schaltfläche während die Stoppuhr läuft.
	 */
	@Test
	public final void testStopwatchResetWhileRunning() {
		final Button stopwatchStartButton = (Button) fxer.getAt("#stopwatchStartButton");
		final Button stopwatchStopButton = (Button) fxer.getAt("#stopwatchStopButton");
		final Button stopwatchResetButton = (Button) fxer.getAt("#stopwatchResetButton");
		final Label stopwatchTimeLabel = (Label) fxer.getAt("#stopwatchTimeLabel");

		// Stoppuhr starten
		when(getController().getGuiHelper().getFacade().startStopwatch()).thenReturn(new TimeDescriptor(50));
		stopwatchStartButton.fire();
		fxer.waitForFxEvents();
		verify(getController().getGuiHelper().getFacade()).startStopwatch();

		// Stoppuhr zurücksetzen
		stopwatchResetButton.fire();
		fxer.waitForFxEvents();
		verify(getController().getGuiHelper().getFacade()).resetStopwatch();

		// gemessene Zeit muss angezeigt sein
		assertEquals("00:00:00.050", stopwatchTimeLabel.getText());

		// Stoppuhr stoppen
		stopwatchStopButton.fire();
		fxer.waitForFxEvents();
		verify(getController().getGuiHelper().getFacade()).stopStopwatch();
	}

}
