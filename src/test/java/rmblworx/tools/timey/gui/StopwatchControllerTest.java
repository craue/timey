package rmblworx.tools.timey.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.categories.TestFX;
import org.loadui.testfx.utils.FXTestUtils;

import rmblworx.tools.timey.vo.TimeDescriptor;

/**
 * GUI-Tests für die Stoppuhr.
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
@Category(TestFX.class)
public class StopwatchControllerTest extends FxmlGuiControllerTest {

	/**
	 * Container für Elemente.
	 */
	private Scene scene;

	// GUI-Elemente
	private Label stopwatchTimeLabel;
	private Button stopwatchStartButton;
	private Button stopwatchStopButton;
	private ToggleButton stopwatchSplitTimeButton;
	private Button stopwatchResetButton;

	/**
	 * {@inheritDoc}
	 */
	protected final String getFxmlFilename() {
		return "Stopwatch.fxml";
	}

	@Before
	public final void setUp() {
		scene = stage.getScene();

		stopwatchTimeLabel = (Label) scene.lookup("#stopwatchTimeLabel");
		stopwatchStartButton = (Button) scene.lookup("#stopwatchStartButton");
		stopwatchStopButton = (Button) scene.lookup("#stopwatchStopButton");
		stopwatchSplitTimeButton = (ToggleButton) scene.lookup("#stopwatchSplitTimeButton");
		stopwatchResetButton = (Button) scene.lookup("#stopwatchResetButton");
	}

	/**
	 * Testet den Zustand der Schaltflächen je nach Zustand der Stoppuhr.
	 */
	@Test
	public final void testStopwatchButtonStates() {
		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchSplitTimeButton.isVisible());
		assertTrue(stopwatchSplitTimeButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr starten
		click(stopwatchStartButton);
		verify(getController().getGuiHelper().getFacade()).startStopwatch();

		// Zustand der Schaltflächen testen
		assertFalse(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertTrue(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());
		assertTrue(stopwatchStopButton.isFocused());

		assertTrue(stopwatchSplitTimeButton.isVisible());
		assertFalse(stopwatchSplitTimeButton.isDisabled());
		assertFalse(stopwatchSplitTimeButton.isSelected());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Zwischenzeit aktivieren
		click(stopwatchSplitTimeButton);
		verify(getController().getGuiHelper().getFacade()).toggleTimeModeInStopwatch();

		// Zustand der Schaltflächen testen
		assertFalse(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertTrue(stopwatchStopButton.isVisible());
		assertTrue(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchSplitTimeButton.isVisible());
		assertFalse(stopwatchSplitTimeButton.isDisabled());
		assertTrue(stopwatchSplitTimeButton.isSelected());
		assertTrue(stopwatchSplitTimeButton.isFocused());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Zwischenzeit deaktivieren
		click(stopwatchSplitTimeButton);
		verify(getController().getGuiHelper().getFacade(), times(2)).toggleTimeModeInStopwatch(); // zweiter Aufruf

		// Zustand der Schaltflächen testen
		assertFalse(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertTrue(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchSplitTimeButton.isVisible());
		assertFalse(stopwatchSplitTimeButton.isDisabled());
		assertFalse(stopwatchSplitTimeButton.isSelected());
		assertTrue(stopwatchSplitTimeButton.isFocused());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr stoppen
		click(stopwatchStopButton);
		verify(getController().getGuiHelper().getFacade()).stopStopwatch();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
		assertTrue(stopwatchStartButton.isFocused());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchSplitTimeButton.isVisible());
		assertTrue(stopwatchSplitTimeButton.isDisabled());
		assertFalse(stopwatchSplitTimeButton.isSelected());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr zurücksetzen
		click(stopwatchResetButton);
		verify(getController().getGuiHelper().getFacade()).resetStopwatch();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
		assertTrue(stopwatchStartButton.isFocused());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchSplitTimeButton.isVisible());
		assertTrue(stopwatchSplitTimeButton.isDisabled());
		assertFalse(stopwatchSplitTimeButton.isSelected());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());
	}

	/**
	 * Testet die Darstellung der Zeit mit und ohne Millisekunden-Anteil.
	 */
	@Test
	public final void testStopwatchTimeLabelMilliseconds() {
		final CheckBox stopwatchShowMillisecondsCheckbox = (CheckBox) scene.lookup("#stopwatchShowMillisecondsCheckbox");

		// Ausgangszustand
		assertTrue(stopwatchShowMillisecondsCheckbox.isSelected());
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());

		// Millisekunden-Anteil ausblenden
		stopwatchShowMillisecondsCheckbox.fire();
		FXTestUtils.awaitEvents();
		assertEquals("00:00:00", stopwatchTimeLabel.getText());

		// Millisekunden-Anteil wieder einblenden
		stopwatchShowMillisecondsCheckbox.fire();
		FXTestUtils.awaitEvents();
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());
	}

	/**
	 * Testet die Anzeige der gemessenen Zeit.
	 */
	@Test
	public final void testStopwatchStartStopTimeMeasured() {
		// Stoppuhr starten
		when(getController().getGuiHelper().getFacade().startStopwatch()).thenReturn(new TimeDescriptor(50));
		stopwatchStartButton.fire();
		FXTestUtils.awaitEvents();

		// Stoppuhr stoppen
		stopwatchStopButton.fire();
		FXTestUtils.awaitEvents();

		// gemessene Zeit muss angezeigt sein
		assertEquals("00:00:00.050", stopwatchTimeLabel.getText());

		// Stoppuhr wieder starten, um zweite (additive) Messung zu berücksichtigen
		when(getController().getGuiHelper().getFacade().startStopwatch()).thenReturn(new TimeDescriptor(200));
		stopwatchStartButton.fire();
		FXTestUtils.awaitEvents();

		// Stoppuhr wieder stoppen
		stopwatchStopButton.fire();
		FXTestUtils.awaitEvents();

		// gemessene Zeit muss angezeigt sein
		assertEquals("00:00:00.200", stopwatchTimeLabel.getText());
	}

	/**
	 * Testet die Funktionalität der Zurücksetzen-Schaltfläche.
	 */
	@Test
	public final void testStopwatchReset() {
		// Stoppuhr zurücksetzen, ohne sie vorher gestartet zu haben
		stopwatchResetButton.fire();
		FXTestUtils.awaitEvents();
		verify(getController().getGuiHelper().getFacade()).resetStopwatch();

		// angezeigte Zeit muss zurückgesetzt sein
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());

		// Stoppuhr starten
		when(getController().getGuiHelper().getFacade().startStopwatch()).thenReturn(new TimeDescriptor(9876));
		stopwatchStartButton.fire();
		FXTestUtils.awaitEvents();

		// gemessene Zeit muss angezeigt sein
		assertEquals("00:00:09.876", stopwatchTimeLabel.getText());

		// Stoppuhr stoppen
		stopwatchStopButton.fire();
		FXTestUtils.awaitEvents();

		// Stoppuhr zurücksetzen
		stopwatchResetButton.fire();
		FXTestUtils.awaitEvents();

		// angezeigte Zeit muss zurückgesetzt sein
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());
	}

	/**
	 * Testet die Funktionalität der Zurücksetzen-Schaltfläche während die Stoppuhr läuft.
	 */
	@Test
	public final void testStopwatchResetWhileRunning() {
		// Stoppuhr starten
		when(getController().getGuiHelper().getFacade().startStopwatch()).thenReturn(new TimeDescriptor(50));
		stopwatchStartButton.fire();
		FXTestUtils.awaitEvents();
		verify(getController().getGuiHelper().getFacade()).startStopwatch();

		// Stoppuhr zurücksetzen
		stopwatchResetButton.fire();
		FXTestUtils.awaitEvents();
		verify(getController().getGuiHelper().getFacade()).resetStopwatch();

		// gemessene Zeit muss angezeigt sein
		assertEquals("00:00:00.050", stopwatchTimeLabel.getText());

		// Stoppuhr stoppen
		stopwatchStopButton.fire();
		FXTestUtils.awaitEvents();
		verify(getController().getGuiHelper().getFacade()).stopStopwatch();
	}

	/**
	 * Testet die Funktionalität der Zurücksetzen-Schaltfläche während Zwischenzeit aktiv ist.
	 */
	@Test
	public final void testStopwatchResetWhileRunningWithSplitTime() {
		// Stoppuhr starten
		stopwatchStartButton.fire();
		FXTestUtils.awaitEvents();
		verify(getController().getGuiHelper().getFacade()).startStopwatch();

		// Zwischenzeit aktivieren
		stopwatchSplitTimeButton.fire();
		FXTestUtils.awaitEvents();
		verify(getController().getGuiHelper().getFacade()).toggleTimeModeInStopwatch();

		// Stoppuhr zurücksetzen
		stopwatchResetButton.fire();
		FXTestUtils.awaitEvents();
		verify(getController().getGuiHelper().getFacade()).resetStopwatch();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchSplitTimeButton.isVisible());
		assertFalse(stopwatchSplitTimeButton.isDisabled());
		assertFalse(stopwatchSplitTimeButton.isSelected());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr stoppen
		stopwatchStopButton.fire();
		FXTestUtils.awaitEvents();
		verify(getController().getGuiHelper().getFacade()).stopStopwatch();
	}

}
