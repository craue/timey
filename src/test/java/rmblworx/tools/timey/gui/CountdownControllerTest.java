package rmblworx.tools.timey.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.awt.TrayIcon;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.categories.TestFX;
import org.loadui.testfx.utils.FXTestUtils;

import rmblworx.tools.timey.ITimey;
import rmblworx.tools.timey.event.CountdownExpiredEvent;
import rmblworx.tools.timey.event.TimeyEvent;
import rmblworx.tools.timey.gui.component.TimePicker;

/*
 * Copyright 2014-2015 Christian Raue
 * MIT License http://opensource.org/licenses/mit-license.php
 */
/**
 * GUI-Tests für den Countdown.
 * @author Christian Raue {@literal <christian.raue@gmail.com>}
 */
@Category(TestFX.class)
public class CountdownControllerTest extends FxmlGuiControllerTest {

	/**
	 * Container für Elemente.
	 */
	private Scene scene;

	// GUI-Elemente
	private Label countdownTimeLabel;
	private Button countdownStartButton;
	private Button countdownStopButton;
	private TimePicker countdownTimePicker;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final String getFxmlFilename() {
		return "Countdown.fxml";
	}

	@Before
	public final void setUp() {
		scene = stage.getScene();

		countdownTimeLabel = (Label) scene.lookup("#countdownTimeLabel");
		countdownStartButton = (Button) scene.lookup("#countdownStartButton");
		countdownStopButton = (Button) scene.lookup("#countdownStopButton");
		countdownTimePicker = (TimePicker) scene.lookup("#countdownTimePicker");
	}

	@Test
	public final void testInitializedFields() throws IllegalAccessException {
		super.testFxmlInitializedFields();
	}

	/**
	 * Testet den Zustand der Schaltflächen je nach Zustand des Countdowns.
	 */
	@Test
	public final void testStartStopButtonStates() {
		final ITimey facade = getController().getGuiHelper().getFacade();

		// Zustand der Schaltflächen testen
		assertTrue(countdownStartButton.isVisible());
		assertTrue(countdownStartButton.isDisabled());

		assertFalse(countdownStopButton.isVisible());
		assertFalse(countdownStopButton.isDisabled());

		// Zeit setzen
		Platform.runLater(new Runnable() {
			public void run() {
				countdownTimePicker.setValue(DateTimeUtil.getLocalTimeForString("00:00:10"));
			}
		});
		FXTestUtils.awaitEvents();

		// Zustand der Schaltflächen testen
		assertTrue(countdownStartButton.isVisible());
		assertFalse(countdownStartButton.isDisabled());

		// Countdown starten
		click(countdownStartButton);
		verify(facade, timeout(WAIT_FOR_EVENT)).startCountdown();

		// Zustand der Schaltflächen testen
		assertFalse(countdownStartButton.isVisible());
		assertFalse(countdownStartButton.isDisabled());

		assertTrue(countdownStopButton.isVisible());
		assertFalse(countdownStopButton.isDisabled());
		assertTrue(countdownStopButton.isFocused());

		// Countdown stoppen
		click(countdownStopButton);
		verify(facade, timeout(WAIT_FOR_EVENT)).stopCountdown();

		// Zustand der Schaltflächen testen
		assertTrue(countdownStartButton.isVisible());
		assertFalse(countdownStartButton.isDisabled());
		assertTrue(countdownStartButton.isFocused());

		assertFalse(countdownStopButton.isVisible());
		assertFalse(countdownStopButton.isDisabled());

		// Zeit wieder auf 0 setzen
		Platform.runLater(new Runnable() {
			public void run() {
				countdownTimePicker.setValue(DateTimeUtil.getLocalTimeForString("00:00:00"));
			}
		});
		FXTestUtils.awaitEvents();

		// Zustand der Schaltflächen testen
		assertTrue(countdownStartButton.isVisible());
		assertTrue(countdownStartButton.isDisabled());
	}

	/**
	 * Testet die Übertragung der Zeit zwischen TimePicker und Label.
	 */
	@Test
	public final void testTimeConversionBetweenPickerAndLabel() {
		// Zeit setzen
		Platform.runLater(new Runnable() {
			public void run() {
				countdownTimePicker.setValue(DateTimeUtil.getLocalTimeForString("00:00:10"));
			}
		});
		FXTestUtils.awaitEvents();

		// Countdown starten
		click(countdownStartButton);

		// verbleibende Zeit muss angezeigt sein
		assertEquals("00:00:10", countdownTimeLabel.getText());

		assertFalse(countdownTimePicker.isVisible());
		assertTrue(countdownTimeLabel.isVisible());

		// Countdown stoppen
		click(countdownStopButton);

		// verbleibende Zeit muss stimmen
		assertEquals(10000L * DateTimeUtil.MILLI_TO_NANO, countdownTimePicker.getValue().toNanoOfDay());

		assertTrue(countdownTimePicker.isVisible());
		assertFalse(countdownTimeLabel.isVisible());
	}

	/**
	 * Testet Starten und Stoppen per Tastatur unter Berücksichtigung der korrekten Fokussierung.
	 */
	@Test
	@Ignore("Betätigen einer fokussierten Schaltfläche per Enter-Taste mit JavaFX 8 nicht mehr möglich")
	public final void testStartStopPerKeyboard() {
		final ITimey facade = getController().getGuiHelper().getFacade();

		// Sekunden-Feld fokussieren
		click(scene.lookup("#secondsTextField"));

		// Zeit auf 0 setzen
		Platform.runLater(new Runnable() {
			public void run() {
				countdownTimePicker.setValue(DateTimeUtil.getLocalTimeForString("00:00:00"));
			}
		});
		FXTestUtils.awaitEvents();

		// bei Zeit = 0 darf sich Countdown nicht starten lassen
		type(KeyCode.ENTER);
		verify(facade, never()).startCountdown();

		// Zeit setzen
		Platform.runLater(new Runnable() {
			public void run() {
				countdownTimePicker.setValue(DateTimeUtil.getLocalTimeForString("00:00:10"));
			}
		});
		FXTestUtils.awaitEvents();

		// Countdown starten
		type(KeyCode.ENTER);
		verify(facade).startCountdown();

		// Countdown stoppen
		type(KeyCode.ENTER);
		verify(facade).stopCountdown();
	}

	/**
	 * Testet die Verarbeitung eines Ereignisses.
	 */
	@Test
	public final void testHandleEvent() {
		final CountdownController controller = (CountdownController) getController();

		final MessageHelper messageHelper = mock(MessageHelper.class);
		controller.getGuiHelper().setMessageHelper(messageHelper);

		// Ereignis auslösen
		controller.handleEvent(new CountdownExpiredEvent());
		waitForThreads();

		// sicherstellen, dass Ereignis verarbeitet wird
		verify(messageHelper).showTrayMessageWithFallbackToDialog(anyString(), anyString(), isNull(TrayIcon.class),
				isA(ResourceBundle.class));
	}

	/**
	 * Testet die Verarbeitung eines unwichtigen Ereignisses.
	 */
	@Test
	public final void testIgnoreEvent() {
		final CountdownController controller = (CountdownController) getController();

		final MessageHelper messageHelper = mock(MessageHelper.class);
		controller.getGuiHelper().setMessageHelper(messageHelper);

		// unwichtiges Ereignis auslösen
		controller.handleEvent(mock(TimeyEvent.class));
		waitForThreads();

		// sicherstellen, dass Ereignis ignoriert wird
		verify(messageHelper, never()).showTrayMessageWithFallbackToDialog(anyString(), anyString(), isNull(TrayIcon.class),
				isA(ResourceBundle.class));
	}

}
