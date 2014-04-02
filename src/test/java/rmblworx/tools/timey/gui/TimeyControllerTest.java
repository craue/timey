package rmblworx.tools.timey.gui;

import static org.junit.Assert.assertEquals;

import java.awt.SystemTray;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * GUI-Tests für das Hauptfenster.
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
@Category(TimeyGuiTest.class)
public class TimeyControllerTest extends FxmlGuiControllerTest {

	/**
	 * {@inheritDoc}
	 */
	protected final String getFxmlFilename() {
		return "Timey.fxml";
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void controllerLoaded() {
		((TimeyController) getController()).setStage(getStage());
	}

	/**
	 * Testet das Hinzufügen des Tray-Symbols.
	 */
	@Test
	public final void testTrayIconAdded() {
		if (!SystemTray.isSupported()) {
			// Test überspringen
			return;
		}

		assertEquals(1, SystemTray.getSystemTray().getTrayIcons().length);
	}

}
