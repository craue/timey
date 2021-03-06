package rmblworx.tools.timey.persistence.dao;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import rmblworx.tools.timey.vo.AlarmDescriptor;
import rmblworx.tools.timey.vo.TimeDescriptor;

/*
 * Copyright 2014-2015 Christian Raue
 * MIT License http://opensource.org/licenses/mit-license.php
 */
/**
 * Testet die Klasse AlarmDao auf korrektes Exception-Handling bei Datenbankproblemen. Das heißt, das von der Klasse
 * erwartet wird, das sie keine Exceptions wirft - stattdessen lediglich einen Logeintrag erzeugt.
 *
 * @author mmatthies
 */
public class AlarmDaoTest {
	/**
	 * Zeitpunkt für den TimeDescriptor.
	 */
	private static final int ZEITWERT = 1000;

	/**
	 * AlarmDescriptor.
	 */
	private AlarmDescriptor alarmDescriptor;
	/**
	 * Zu testende Implementierung.
	 */
	private AlarmDao dao;
	/**
	 * Gemockter Logger um den Log nicht unnötig zuzumüllen.
	 */
	@Mock
	private Logger logger;
	/**
	 * SessionFactory. Explizites setzen auf null da Persistence für diese Tests nicht vorhanden sein soll.
	 */
	private final SessionFactory sessionFactory = null;
	/**
	 * TimeDescriptor.
	 */
	private TimeDescriptor timeDescriptor;

	/**
	 * @throws java.lang.Exception
	 *             wenn eine Ausnahme auftritt.
	 */
	@Before
	public final void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.timeDescriptor = new TimeDescriptor(ZEITWERT);
		this.alarmDescriptor = new AlarmDescriptor(this.timeDescriptor, true, "leer", "/bla", this.timeDescriptor);
		this.dao = new AlarmDao(this.sessionFactory, this.logger);
	}

	/**
	 * @throws java.lang.Exception
	 *             wenn eine Ausnahme auftritt.
	 */
	@After
	public final void tearDown() throws Exception {
		this.dao = null;
		this.timeDescriptor = null;
		this.alarmDescriptor = null;
	}

	/**
	 * Test method for{@link AlarmDao#createAlarm(AlarmDescriptor)}.
	 */
	@Test
	public final void testCreateAlarm() {
		this.dao.createAlarm(this.alarmDescriptor);
	}

	/**
	 * Test method for{@link AlarmDao#deleteAlarm(AlarmDescriptor)}.
	 */
	@Test()
	public final void testDeleteAlarm() {
		this.dao.deleteAlarm(this.alarmDescriptor);
	}

	/**
	 * Test method for {@link AlarmDao#findAll()}.
	 */
	@Test
	public final void testFindAll() {
		this.dao.findAll();
	}

	/**
	 * Test method for{@link AlarmDao#isActivated(AlarmDescriptor)}.
	 */
	@Test
	public final void testIsActivated() {
		this.dao.isActivated(this.alarmDescriptor);
	}

	/**
	 * Test method for {@link AlarmDao#setIsActivated(AlarmDescriptor, Boolean)}.
	 */
	@Test
	public final void testSetIsActivated() {
		this.dao.setIsActivated(this.alarmDescriptor, true);
	}

}
