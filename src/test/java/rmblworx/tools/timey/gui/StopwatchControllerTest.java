package rmblworx.tools.timey.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import org.junit.Test;

import com.google.common.base.Predicate;

/**
 * GUI-Test für die Stoppuhr.
 * @see https://github.com/SmartBear/TestFX
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
public class StopwatchControllerTest extends JavaFxGuiTest {

	/**
	 * {@inheritDoc}
	 */
	protected final String getFxmlFilename() {
		return "StopwatchGui.fxml";
	}

	/**
	 * Testet den Zustand der Schaltflächen je nach Zustand der Stoppuhr.
	 */
	@Test
	public final void testStopwatchStartStopResetButtonStates() {
		final Scene scene = stage.getScene();

		final Button stopwatchStartButton = (Button) scene.lookup("#stopwatchStartButton");
		final Button stopwatchStopButton = (Button) scene.lookup("#stopwatchStopButton");
		final Button stopwatchResetButton = (Button) scene.lookup("#stopwatchResetButton");

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr starten
		stopwatchStartButton.fire();

		// Zustand der Schaltflächen testen
		assertFalse(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertTrue(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr stoppen
		stopwatchStopButton.fire();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr zurücksetzen
		stopwatchResetButton.fire();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());
	}

	/**
	 * Testet die Darstellung der Zeit mit und ohne Millisekunden-Anteil.
	 */
	@Test
	public final void testStopwatchTimeLabelMilliseconds() {
		final Scene scene = stage.getScene();

		final CheckBox stopwatchShowMillisecondsCheckbox = (CheckBox) scene.lookup("#stopwatchShowMillisecondsCheckbox");
		final Label stopwatchTimeLabel = (Label) scene.lookup("#stopwatchTimeLabel");

		// Ausgangszustand
		assertTrue(stopwatchShowMillisecondsCheckbox.isSelected());
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());

		// Millisekunden-Anteil ausblenden
		stopwatchShowMillisecondsCheckbox.fire();
		waitUntil(stopwatchShowMillisecondsCheckbox, new Predicate<CheckBox>() {
			public boolean apply(final CheckBox stopwatchShowMillisecondsCheckbox) {
				return !stopwatchShowMillisecondsCheckbox.isSelected();
			}
		}, 1);
		assertEquals("00:00:00", stopwatchTimeLabel.getText());

		// Millisekunden-Anteil wieder ausblenden
		stopwatchShowMillisecondsCheckbox.fire();
		waitUntil(stopwatchShowMillisecondsCheckbox, new Predicate<CheckBox>() {
			public boolean apply(final CheckBox stopwatchShowMillisecondsCheckbox) {
				return stopwatchShowMillisecondsCheckbox.isSelected();
			}
		}, 1);
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());
	}

	/**
	 * Testet die Anzeige der gemessenen Zeit.
	 */
	@Test
	public final void testStopwatchStartStopTimeMeasured() {
		final long timeToRun = 50;
		final long timeExpectedMin = 40;
		final long timeExpectedMax = 70;

		final Scene scene = stage.getScene();

		// Stoppuhr starten
		final Button stopwatchStartButton = (Button) scene.lookup("#stopwatchStartButton");
		stopwatchStartButton.fire();

		// warten
		sleep(timeToRun);

		// Stoppuhr stoppen
		final Button stopwatchStopButton = (Button) scene.lookup("#stopwatchStopButton");
		stopwatchStopButton.fire();

		// angezeigte gemessene Zeit muss im Toleranzbereich liegen
		final Label stopwatchTimeLabel = (Label) scene.lookup("#stopwatchTimeLabel");

		try {
			final long timePassed = getDateFormatter().parse(stopwatchTimeLabel.getText()).getTime();
			if (timePassed < timeExpectedMin || timePassed > timeExpectedMax) {
				fail(String.format("%d is not between %d and %d.", timePassed, timeExpectedMin, timeExpectedMax));
			}
		} catch (final ParseException e) {
			fail(e.getLocalizedMessage());
		}

		// Stoppuhr wieder starten, um zweite (additive) Messung zu berücksichtigen
		stopwatchStartButton.fire();

		// warten
		sleep(timeToRun);

		// Stoppuhr wieder stoppen
		stopwatchStopButton.fire();

		// angezeigte gemessene Zeit muss im Toleranzbereich liegen
		try {
			final long timePassed = getDateFormatter().parse(stopwatchTimeLabel.getText()).getTime();
			final long timeExpectedMinSecondRun = timeToRun + timeExpectedMin;
			final long timeExpectedMaxSecondRun = timeToRun + timeExpectedMax;
			if (timePassed < timeExpectedMinSecondRun || timePassed > timeExpectedMaxSecondRun) {
				fail(String.format("%d is not between %d and %d.", timePassed, timeExpectedMinSecondRun, timeExpectedMaxSecondRun));
			}
		} catch (final ParseException e) {
			fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Testet die Funktionalität der Zurücksetzen-Schaltfläche.
	 */
	@Test
	public final void testStopwatchReset() {
		final Scene scene = stage.getScene();

		// Stoppuhr zurücksetzen, ohne sie vorher gestartet zu haben
		final Button stopwatchResetButton = (Button) scene.lookup("#stopwatchResetButton");
		stopwatchResetButton.fire();

		// angezeigte Zeit muss zurückgesetzt sein
		final Label stopwatchTimeLabel = (Label) scene.lookup("#stopwatchTimeLabel");
		try {
			waitUntil(stopwatchTimeLabel, new Predicate<Label>() {
				public boolean apply(final Label stopwatchTimeLabel) {
					return "00:00:00.000".equals(stopwatchTimeLabel.getText());
				}
			}, 1);
		} catch (final RuntimeException e) {
			fail(stopwatchTimeLabel.getText());
		}

		// Stoppuhr starten
		final Button stopwatchStartButton = (Button) scene.lookup("#stopwatchStartButton");
		stopwatchStartButton.fire();

		// warten, bis gemessene Zeit angezeigt wird
		try {
			waitUntil(stopwatchTimeLabel, new Predicate<Label>() {
				public boolean apply(final Label stopwatchTimeLabel) {
					return !"00:00:00.000".equals(stopwatchTimeLabel.getText());
				}
			}, 1);
		} catch (final RuntimeException e) {
			fail(stopwatchTimeLabel.getText());
		}

		// Stoppuhr stoppen
		final Button stopwatchStopButton = (Button) scene.lookup("#stopwatchStopButton");
		stopwatchStopButton.fire();

		// Stoppuhr zurücksetzen
		stopwatchResetButton.fire();

		// angezeigte Zeit muss zurückgesetzt sein
		try {
			waitUntil(stopwatchTimeLabel, new Predicate<Label>() {
				public boolean apply(final Label stopwatchTimeLabel) {
					return "00:00:00.000".equals(stopwatchTimeLabel.getText());
				}
			}, 1);
		} catch (final RuntimeException e) {
			fail(stopwatchTimeLabel.getText());
		}
	}

	/**
	 * Testet die Funktionalität der Zurücksetzen-Schaltfläche während die Stoppuhr läuft.
	 */
	@Test
	public final void testStopwatchResetWhileRunning() {
		final long timeToRun = 50;
		final long timeExpectedMax = 2 * timeToRun;

		final Scene scene = stage.getScene();

		// Stoppuhr starten
		final Button stopwatchStartButton = (Button) scene.lookup("#stopwatchStartButton");
		stopwatchStartButton.fire();

		// warten
		sleep(timeToRun);

		// Stoppuhr zurücksetzen
		final Button stopwatchResetButton = (Button) scene.lookup("#stopwatchResetButton");
		stopwatchResetButton.fire();

		// warten
		sleep(timeToRun);

		// angezeigte gemessene Zeit muss im Toleranzbereich liegen
		final Label stopwatchTimeLabel = (Label) scene.lookup("#stopwatchTimeLabel");

		try {
			final long timePassed = getDateFormatter().parse(stopwatchTimeLabel.getText()).getTime();
			if (timePassed > timeExpectedMax) {
				fail(String.format("%d is not less than %d.", timePassed, timeExpectedMax));
			}
		} catch (final ParseException e) {
			fail(e.getLocalizedMessage());
		}

		// Stoppuhr stoppen
		final Button stopwatchStopButton = (Button) scene.lookup("#stopwatchStopButton");
		stopwatchStopButton.fire();
	}

	protected SimpleDateFormat getDateFormatter() {
		final SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		dateFormatter.applyPattern("HH:mm:ss.SSS");

		return dateFormatter;
	}

}