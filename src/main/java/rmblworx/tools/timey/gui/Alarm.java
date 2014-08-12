package rmblworx.tools.timey.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.joda.time.LocalDateTime;

/**
 * Alarm-VO für die GUI.
 * 
 * @author Christian Raue <christian.raue@gmail.com>
 * @copyright 2014 Christian Raue
 * @license http://opensource.org/licenses/mit-license.php MIT License
 */
public class Alarm implements Comparable<Alarm> {

	private final BooleanProperty enabled;
	private final ObjectProperty<LocalDateTime> dateTime;
	private final StringProperty description;
	private final StringProperty sound;

	/**
	 * Initialisiert den Alarm mit der aktuellen Systemzeit.
	 */
	public Alarm() {
		this(LocalDateTime.now().millisOfSecond().setCopy(0), null);
	}

	public Alarm(final LocalDateTime dateTime, final String description) {
		this(dateTime, description, null, true);
	}

	public Alarm(final LocalDateTime dateTime, final String description, final boolean enabled) {
		this(dateTime, description, null, enabled);
	}

	public Alarm(final LocalDateTime dateTime, final String description, final String sound, final boolean enabled) {
		this.enabled = new SimpleBooleanProperty(enabled);
		this.dateTime = new SimpleObjectProperty<>(dateTime);
		this.description = new SimpleStringProperty(description);
		this.sound = new SimpleStringProperty(sound);
	}

	public void setEnabled(final boolean enabled) {
		this.enabled.set(enabled);
	}

	public boolean isEnabled() {
		return enabled.get();
	}

	public void setDateTime(final LocalDateTime dateTime) {
		this.dateTime.set(dateTime);
	}

	public LocalDateTime getDateTime() {
		return dateTime.get();
	}

	/**
	 * @return UTC-basierter Datum/Zeit-Wert in ms
	 */
	public long getDateTimeInMillis() {
		return dateTime.get().toDateTime().getMillis();
	}

	public void setDescription(final String description) {
		this.description.set(description);
	}

	public String getDescription() {
		return description.get();
	}

	public void setSound(final String sound) {
		this.sound.set(sound);
	}

	public String getSound() {
		return sound.get();
	}

	public int compareTo(final Alarm other) {
		// nach Datum/Zeit sortieren, älteste zuerst
		return getDateTime().toDateTime().getMillis() > other.getDateTime().toDateTime().getMillis() ? 1 : -1;
	}

	/*
	 * Die "<Attribut>Property"-Methoden sorgen dafür, dass die Änderungen beim Bearbeiten eines Alarms korrekt in der Alarm-Tabelle
	 * dargestellt werden. Siehe http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items/24194842#24194842.
	 */
	public BooleanProperty enabledProperty() {
		return enabled;
	}

	public ObjectProperty<LocalDateTime> dateTimeProperty() {
		return dateTime;
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public StringProperty soundProperty() {
		return sound;
	}

}
