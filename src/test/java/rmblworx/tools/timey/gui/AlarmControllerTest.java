package rmblworx.tools.timey.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.categories.TestFX;
import org.loadui.testfx.utils.FXTestUtils;
import org.mockito.ArgumentMatcher;

import rmblworx.tools.timey.vo.AlarmDescriptor;

/*
 * Copyright 2014 Christian Raue
 * MIT License http://opensource.org/licenses/mit-license.php
 */
/**
 * GUI-Tests für die Alarm-Funktionalität.
 * @author Christian Raue {@literal <christian.raue@gmail.com>}
 */
@Category(TestFX.class)
public class AlarmControllerTest extends FxmlGuiControllerTest {

	/**
	 * Container für Elemente.
	 */
	private Scene scene;

	// GUI-Elemente
	private TableView<Alarm> alarmTable;

	/**
	 * {@inheritDoc}
	 */
	protected final String getFxmlFilename() {
		return "Alarm.fxml";
	}

	@Before
	@SuppressWarnings("unchecked")
	public final void setUp() {
		scene = stage.getScene();

		alarmTable = (TableView<Alarm>) scene.lookup("#alarmTable");

		// Tabelle leeren
		alarmTable.getItems().clear();
	}

	/**
	 * Testet das Löschen von Alarmen.
	 */
	@Test
	public final void testDeleteAlarm() {
		// zwei Alarme anlegen
		final ObservableList<Alarm> tableData = alarmTable.getItems();
		final LocalDateTime now = LocalDateTime.now().withNano(0);
		final Alarm alarm1 = new Alarm(now.plusSeconds(5), "alarm1");
		final Alarm alarm2 = new Alarm(now.plusSeconds(10), "alarm2");
		tableData.add(alarm1);
		tableData.add(alarm2);

		final Button alarmDeleteButton = (Button) scene.lookup("#alarmDeleteButton");

		// Zustand der Schaltflächen testen
		assertTrue(alarmDeleteButton.isVisible());
		assertTrue(alarmDeleteButton.isDisabled());

		// zweiten Alarm auswählen
		Platform.runLater(new Runnable() {
			public void run() {
				alarmTable.getSelectionModel().select(alarm2);
			}
		});
		FXTestUtils.awaitEvents();

		// Zustand der Schaltflächen testen
		assertTrue(alarmDeleteButton.isVisible());
		assertFalse(alarmDeleteButton.isDisabled());

		// Alarm löschen
		click(alarmDeleteButton);
		verify(getController().getGuiHelper().getFacade()).removeAlarm(argThat(new ArgumentMatcher<AlarmDescriptor>() {
			public boolean matches(final Object argument) {
				return ((AlarmDescriptor) argument).getAlarmtime().getMilliSeconds() == alarm2.getDateTimeInMillis();
			}
		}));

		// sicherstellen, dass zweiter Alarm gelöscht ist, erster aber nicht
		assertTrue(tableData.contains(alarm1));
		assertFalse(tableData.contains(alarm2));

		// sicherstellen, dass kein anderer Alarm ausgewählt ist
		assertNull(alarmTable.getSelectionModel().getSelectedItem());

		// Zustand der Schaltflächen testen
		assertTrue(alarmDeleteButton.isVisible());
		assertTrue(alarmDeleteButton.isDisabled());

		// ersten Alarm auswählen
		Platform.runLater(new Runnable() {
			public void run() {
				alarmTable.getSelectionModel().select(alarm1);
			}
		});
		FXTestUtils.awaitEvents();

		// Alarm löschen
		click(alarmDeleteButton);

		// sicherstellen, dass keine Alarme mehr existieren
		assertTrue(tableData.isEmpty());

		// Zustand der Schaltflächen testen
		assertTrue(alarmDeleteButton.isVisible());
		assertTrue(alarmDeleteButton.isDisabled());
	}

	/**
	 * Testet die Darstellung von Alarmen in der Tabelle.
	 */
	@Test
	public final void testAlarmTableRendering() {
		// Alarm anlegen
		final ObservableList<Alarm> tableData = alarmTable.getItems();
		final Alarm alarm1 = new Alarm(DateTimeUtil.getLocalDateTimeForString("24.12.2014 12:00:00"), "Test");
		tableData.add(alarm1);

		/*
		 * Sicherstellen, dass Zellen die korrekten Objekte enthalten.
		 * Wäre z. B. nicht der Fall, wenn der Name des Alarm-Attributs nicht mit dem Namen der Spalte übereinstimmt.
		 */
		final LocalDateTime dateTimeCellData = (LocalDateTime) alarmTable.getColumns().get(0).getCellData(0);
		assertNotNull(dateTimeCellData);
		assertEquals(alarm1.getDateTime(), dateTimeCellData);

		final String descriptionCellData = (String) alarmTable.getColumns().get(1).getCellData(0);
		assertNotNull(descriptionCellData);
		assertEquals(alarm1.getDescription(), descriptionCellData);
	}

	/**
	 * Testet das Bearbeiten eines Alarms.
	 */
	@Test
	public final void testEditAlarm() {
		final int noonHour = 12;
		final int onePmHour = 13;

		// Alarm anlegen
		final LocalDateTime noon = LocalDateTime.now()
				.plusYears(1) // +1 Jahr, um Problem mit Zeitpunkt in Vergangenheit zu vermeiden, falls Test nachmittags läuft
				.withHour(noonHour).withMinute(0).withSecond(0) // 12:00:00
				.withNano(0); // Sekundenbruchteil immer auf 0 setzen, da auch per GUI nur ganze Sekunden angegeben werden können
		final LocalDateTime onePm = noon.withHour(onePmHour); // 13:00:00 am selben Tag
		final Alarm alarm = new Alarm(noon, "alarm");
		alarmTable.getItems().add(alarm);

		final Button alarmEditButton = (Button) scene.lookup("#alarmEditButton");

		// Zustand der Schaltflächen testen
		assertTrue(alarmEditButton.isVisible());
		assertTrue(alarmEditButton.isDisabled());

		// Alarm auswählen
		Platform.runLater(new Runnable() {
			public void run() {
				alarmTable.getSelectionModel().select(alarm);
			}
		});
		FXTestUtils.awaitEvents();

		// Zustand der Schaltflächen testen
		assertTrue(alarmEditButton.isVisible());
		assertFalse(alarmEditButton.isDisabled());

		// Alarm bearbeiten
		click(alarmEditButton);

		final Scene dialogScene = ((AlarmController) getController()).getDialogStage().getScene();

		final TextField hoursTextField = (TextField) dialogScene.lookup("#hoursTextField");
		/*
		 * Der Versuch, den neuen Wert ins Textfeld per doubleClick(hoursTextField); type(String.valueOf(onePmHour)); einzugeben, würde auf
		 * Travis scheitern und der Feldinhalt würde sich nicht ändern.
		 * Selbst Fokussieren des Feldes per Platform.runLater(... hoursTextField.requestFocus(); ...) würde nicht funktionieren.
		 * Also muss der Wert direkt gesetzt (oder alternativ per Slider geändert) werden.
		 */
		Platform.runLater(new Runnable() {
			public void run() {
				hoursTextField.setText(String.valueOf(onePmHour));
			}
		});
		FXTestUtils.awaitEvents();
		// sicherstellen, dass der Wert wirklich geändert wurde
		assertEquals(String.valueOf(onePmHour), hoursTextField.getText());

		final Button alarmSaveButton = (Button) dialogScene.lookup("#alarmSaveButton");
		click(alarmSaveButton);

		// sicherstellen, dass Alarm geändert wurde
		assertEquals(onePm, alarm.getDateTime());

		// sicherstellen, dass per Fassade alter Alarm gelöscht und neuer angelegt wurde
		verify(getController().getGuiHelper().getFacade()).removeAlarm(argThat(new ArgumentMatcher<AlarmDescriptor>() {
			public boolean matches(final Object argument) {
				return ((AlarmDescriptor) argument).getAlarmtime().getMilliSeconds() == DateTimeUtil.getLocalDateTimeInMillis(noon);
			}
		}));
		verify(getController().getGuiHelper().getFacade()).setAlarm(argThat(new ArgumentMatcher<AlarmDescriptor>() {
			public boolean matches(final Object argument) {
				return ((AlarmDescriptor) argument).getAlarmtime().getMilliSeconds() == DateTimeUtil.getLocalDateTimeInMillis(onePm);
			}
		}));

		/*
		 * Sicherstellen, dass geänderter Alarm in der Tabelle korrekt angezeigt wird.
		 * Wäre z. B. nicht der Fall, wenn {@code Alarm}-Klasse keine "<Attribut>Property"-Methoden hätte,
		 * siehe http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items/24194842#24194842.
		 * TODO Funktioniert so nicht. Selbst wenn "<Attribut>Property"-Methoden fehlen, liefert dieser Code einen anderen Wert als den,
		 * der tatsächlich in der Tabelle sichtbar ist. Darstellungsfehler.
		 */
		final LocalDateTime dateTimeCellData = (LocalDateTime) alarmTable.getColumns().get(0).getCellData(0);
		assertNotNull(dateTimeCellData);
		assertEquals(onePm, dateTimeCellData);

		// sicherstellen, dass Alarm noch ausgewählt ist
		assertEquals(alarm, alarmTable.getSelectionModel().getSelectedItem());

		// Zustand der Schaltflächen testen
		assertTrue(alarmEditButton.isVisible());
		assertFalse(alarmEditButton.isDisabled());
	}

}
