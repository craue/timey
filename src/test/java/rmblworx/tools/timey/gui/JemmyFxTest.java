package rmblworx.tools.timey.gui;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.junit.Before;
import org.junit.Rule;

/**
 * Basisklasse für FXML-basierte GUI-Tests mit {@link https://jemmy.java.net}.
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
public abstract class JemmyFxTest {

	/**
	 * @see http://stackoverflow.com/questions/18429422/basic-junit-test-for-javafx-8/18988752#18988752
	 */
	@Rule
	public final JavaFXThreadingRule javaFxRule = new JavaFXThreadingRule();

	public static Stage currentStage;

	/**
	 * @return Name der FXML-Datei zum Laden der GUI
	 */
	protected abstract String getFxmlFilename();

	protected Parent getRootNode() {
		final GuiHelper guiHelper = new GuiHelper();
		final ResourceBundle resources = guiHelper.getResourceBundle(Locale.GERMAN);
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(getFxmlFilename()), resources);
		try {
			final Parent root = (Parent) loader.load();
			return root;
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Before
	public void replaceStage() {
		if (currentStage != null) {
			currentStage.close();
		}

		final Parent root = getRootNode();
		final Scene scene = SceneBuilder.create().root(root).build();

	    final Stage stage = new Stage();
	    currentStage = stage;
	    stage.setScene(scene);
	    stage.initStyle(StageStyle.UNDECORATED);
	    stage.show();

	    stage.toBack();
		stage.toFront();
	}

}
