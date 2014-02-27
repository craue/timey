/*
 */

package rmblworx.tools.timey.vo;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testklasse die das Wertobjekt zum kapseln der Zeitwerte testet.
 * 
 * @author mmatthies
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-timey-context.xml"})
public class TimeDescriptorTest {

	/**
	 * Konstante fuer den erwarteten Testwert.
	 */
	private static final int EXPECTED_MILLISECONDS = 100;

	/**
	 * @throws java.lang.Exception
	 *             Exception.
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 *             Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link rmblworx.tools.timey.vo.TimeDescriptor}.
	 */
	@Test
	public final void testCorrectBehaviorOfTheVo() {
		final TimeDescriptor td = new TimeDescriptor(EXPECTED_MILLISECONDS);
		assertEquals("Test fehlgeschlagen: Millisekunden falsch!", EXPECTED_MILLISECONDS, td.getMilliSeconds());
	}
}
