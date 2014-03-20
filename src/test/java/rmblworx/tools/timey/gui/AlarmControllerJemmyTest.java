package rmblworx.tools.timey.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import org.jemmy.fx.SceneDock;
import org.jemmy.fx.control.LabeledDock;
import org.jemmy.fx.control.TableViewDock;
import org.jemmy.timing.State;
import org.junit.Before;
import org.junit.Test;

/**
 * GUI-Tests für die Alarm-Funktionalität (JemmyFX-Variante).
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
public class AlarmControllerJemmyTest extends JemmyFxTest {

	/**
	 * Container für Elemente.
	 */
	private SceneDock scene;

	/**
	 * {@inheritDoc}
	 */
	protected final String getFxmlFilename() {
		return "AlarmGui.fxml";
	}

	@Before
	public final void setUp() {
		scene = new SceneDock();

		// Tabelle leeren
		final TableViewDock alarmTable = new TableViewDock(scene.asParent(), "alarmTable");
		alarmTable.getItems().clear();
	}

	/**
	 * Testet das Löschen von Alarmen.
	 */
	@Test
	public void testDeleteAlarm() {
		final TableViewDock alarmTableDock = new TableViewDock(scene.asParent(), "alarmTable");
		final TableView<Alarm> alarmTable = (TableView<Alarm>) alarmTableDock.wrap().getControl();

		// zwei Alarme anlegen
		final ObservableList<Alarm> tableData = alarmTable.getItems();
		final Alarm alarm1 = new Alarm();
		final Alarm alarm2 = new Alarm();
		tableData.add(alarm1);
		tableData.add(alarm2);

		final LabeledDock alarmDeleteButtonDock = new LabeledDock(scene.asParent(), "alarmDeleteButton");
		final Button alarmDeleteButton = (Button) alarmDeleteButtonDock.wrap().getControl();

		// Zustand der Schaltflächen testen
		assertTrue(alarmDeleteButton.isVisible());
		assertTrue(alarmDeleteButton.isDisabled());

		// zweiten Alarm auswählen
		alarmTable.getSelectionModel().select(alarm2);

		// Zustand der Schaltflächen testen
		assertTrue(alarmDeleteButton.isVisible());
		assertFalse(alarmDeleteButton.isDisabled());

		// Alarm löschen
		alarmDeleteButtonDock.mouse().click();

		// warten bis Tabelle den Alarm nicht mehr enthält
		alarmTableDock.wrap().waitState(new State<Alarm>() {
			public Alarm reached() {
				return !tableData.contains(alarm2) ? alarm2 : null;
			}
		});

		// sicherstellen, dass kein anderer Alarm ausgewählt ist
		assertNull(alarmTable.getSelectionModel().getSelectedItem());

		// Zustand der Schaltflächen testen
		assertTrue(alarmDeleteButton.isVisible());
		assertTrue(alarmDeleteButton.isDisabled());

		// ersten Alarm auswählen
		alarmTable.getSelectionModel().select(alarm1);

		// Alarm löschen
		alarmDeleteButtonDock.mouse().click();

		// warten bis Tabelle den Alarm nicht mehr enthält
		alarmTableDock.wrap().waitState(new State<Alarm>() {
			public Alarm reached() {
				return !tableData.contains(alarm1) ? alarm1 : null;
			}
		});

		// Zustand der Schaltflächen testen
		assertTrue(alarmDeleteButton.isVisible());
		assertTrue(alarmDeleteButton.isDisabled());
	}

	/**
	 * Testet die Darstellung von Alarmen in der Tabelle.
	 */
	@Test
	public final void testAlarmTableRendering() {
		final TableViewDock alarmTableDock = new TableViewDock(scene.asParent(), "alarmTable");
		final TableView<Alarm> alarmTable = (TableView<Alarm>) alarmTableDock.wrap().getControl();

		// Alarm anlegen
		final ObservableList<Alarm> tableData = alarmTable.getItems();
		final Alarm alarm1 = new Alarm(DateTimeUtil.getCalendarForString("24.12.2014 12:00:00"), "Test");
		tableData.add(alarm1);

		/*
		 * Sicherstellen, dass Zellen die korrekten Objekte enthalten.
		 * Wäre z. B. nicht der Fall, wenn der Name des Alarm-Attributs nicht mit dem Namen der Spalte übereinstimmt.
		 */
		final Object dateTimeCellData = alarmTable.getColumns().get(0).getCellData(0);
		assertNotNull(dateTimeCellData);
		assertEquals(alarm1.getDateTime().getTime(), ((Calendar) dateTimeCellData).getTime());

		final Object descriptionCellData = alarmTable.getColumns().get(1).getCellData(0);
		assertNotNull(descriptionCellData);
		assertEquals(alarm1.getDescription(), (String) descriptionCellData);
	}

}
