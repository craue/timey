package rmblworx.tools.timey.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.utils.FXTestUtils;

import com.google.common.base.Predicate;

/**
 * GUI-Tests für die Stoppuhr.
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
public class StopwatchControllerTest extends FxmlGuiTest {

	/**
	 * Container für Elemente.
	 */
	private Scene scene;

	/**
	 * {@inheritDoc}
	 */
	protected final String getFxmlFilename() {
		return "StopwatchGui.fxml";
	}

	@Before
	public final void setUp() {
		scene = stage.getScene();

		// Stoppuhr vor jedem Test zurücksetzen, um Nebeneffekte zu vermeiden.
		FacadeManager.getFacade().resetStopwatch();
	}

	/**
	 * Testet den Zustand der Schaltflächen je nach Zustand der Stoppuhr.
	 */
	@Test
	public final void testStopwatchStartStopResetButtonStates() {
		final Button stopwatchStartButton = (Button) scene.lookup("#stopwatchStartButton");
		final Button stopwatchStopButton = (Button) scene.lookup("#stopwatchStopButton");
		final Button stopwatchResetButton = (Button) scene.lookup("#stopwatchResetButton");

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
//		assertTrue(stopwatchStartButton.isFocused()); // muss nicht unbedingt der Fall sein

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr starten
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStartButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// Zustand der Schaltflächen testen
		assertFalse(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());

		assertTrue(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());
		assertTrue(stopwatchStopButton.isFocused());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr stoppen
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStopButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// Zustand der Schaltflächen testen
		assertTrue(stopwatchStartButton.isVisible());
		assertFalse(stopwatchStartButton.isDisabled());
		assertTrue(stopwatchStartButton.isFocused());

		assertFalse(stopwatchStopButton.isVisible());
		assertFalse(stopwatchStopButton.isDisabled());

		assertTrue(stopwatchResetButton.isVisible());
		assertFalse(stopwatchResetButton.isDisabled());

		// Stoppuhr zurücksetzen
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchResetButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

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
	public final void testStopwatchTimeLabelMilliseconds() {
		final CheckBox stopwatchShowMillisecondsCheckbox = (CheckBox) scene.lookup("#stopwatchShowMillisecondsCheckbox");
		final Label stopwatchTimeLabel = (Label) scene.lookup("#stopwatchTimeLabel");

		// Ausgangszustand
		assertTrue(stopwatchShowMillisecondsCheckbox.isSelected());
		assertEquals("00:00:00.000", stopwatchTimeLabel.getText());

		// Millisekunden-Anteil ausblenden
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchShowMillisecondsCheckbox.fire();
			}
		});
		FXTestUtils.awaitEvents();
		try {
			waitUntil(stopwatchTimeLabel, new Predicate<Label>() {
				public boolean apply(final Label stopwatchTimeLabel) {
					return "00:00:00".equals(stopwatchTimeLabel.getText());
				}
			}, 1);
		} catch (final RuntimeException e) {
			fail(stopwatchTimeLabel.getText());
		}

		// Millisekunden-Anteil wieder ausblenden
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchShowMillisecondsCheckbox.fire();
			}
		});
		FXTestUtils.awaitEvents();
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
	 * Testet die Anzeige der gemessenen Zeit.
	 */
	@Test
	public final void testStopwatchStartStopTimeMeasured() {
		final long timeToRun = 50;
		final long timeExpectedMin = 40;
		final long timeExpectedMax = 200; // TODO sollte 70 sein, dauert aber mit derzeitigem Setup evtl. länger

		// Stoppuhr starten
		final Button stopwatchStartButton = (Button) scene.lookup("#stopwatchStartButton");
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStartButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// warten
		sleep(timeToRun);

		// Stoppuhr stoppen
		final Button stopwatchStopButton = (Button) scene.lookup("#stopwatchStopButton");
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStopButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// angezeigte gemessene Zeit muss im Toleranzbereich liegen
		final Label stopwatchTimeLabel = (Label) scene.lookup("#stopwatchTimeLabel");

		try {
			final long timePassed = getTimeFormatter().parse(stopwatchTimeLabel.getText()).getTime();
			if (timePassed < timeExpectedMin || timePassed > timeExpectedMax) {
				fail(String.format("%d is not between %d and %d.", timePassed, timeExpectedMin, timeExpectedMax));
			}
		} catch (final ParseException e) {
			fail(e.getLocalizedMessage());
		}

		// Stoppuhr wieder starten, um zweite (additive) Messung zu berücksichtigen
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStartButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// warten
		sleep(timeToRun);

		// Stoppuhr wieder stoppen
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStopButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// angezeigte gemessene Zeit muss im Toleranzbereich liegen
		try {
			final long timePassed = getTimeFormatter().parse(stopwatchTimeLabel.getText()).getTime();
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
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStartButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

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
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStopButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// Stoppuhr zurücksetzen
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchResetButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

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
		final long timeExpectedMax = 4 * timeToRun; // TODO sollte 2 * timeToRun sein, dauert aber mit derzeitigem Setup evtl. länger

		// Stoppuhr starten
		final Button stopwatchStartButton = (Button) scene.lookup("#stopwatchStartButton");
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStartButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// warten
		sleep(timeToRun);

		// Stoppuhr zurücksetzen
		final Button stopwatchResetButton = (Button) scene.lookup("#stopwatchResetButton");
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchResetButton.fire();
			}
		});
		FXTestUtils.awaitEvents();

		// warten
		sleep(timeToRun);

		// angezeigte gemessene Zeit muss im Toleranzbereich liegen
		final Label stopwatchTimeLabel = (Label) scene.lookup("#stopwatchTimeLabel");

		try {
			final long timePassed = getTimeFormatter().parse(stopwatchTimeLabel.getText()).getTime();
			if (timePassed > timeExpectedMax) {
				fail(String.format("%d is not less than %d.", timePassed, timeExpectedMax));
			}
		} catch (final ParseException e) {
			fail(e.getLocalizedMessage());
		}

		// Stoppuhr stoppen
		final Button stopwatchStopButton = (Button) scene.lookup("#stopwatchStopButton");
		Platform.runLater(new Runnable() {
			public void run() {
				stopwatchStopButton.fire();
			}
		});
		FXTestUtils.awaitEvents();
	}

	/**
	 * @return Formatierer für Zeit-Werte
	 */
	private SimpleDateFormat getTimeFormatter() {
		final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		return dateFormatter;
	}

}
