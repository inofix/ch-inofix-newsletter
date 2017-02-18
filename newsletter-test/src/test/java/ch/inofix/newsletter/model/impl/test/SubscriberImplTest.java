package ch.inofix.newsletter.model.impl.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.inofix.newsletter.model.impl.SubscriberImpl;

/**
 * 
 * @author Christian Berndt
 * @created 2017-02-18 23:25
 * @modified 2017-02-18 23:25
 * @version 1.0.0
 */
public class SubscriberImplTest {

	@Before
	public void setUp() {
		_subscriberImpl = new SubscriberImpl("Brian", "Edward", "Greenwald");
	}

	@Test
	public void testFullNameLength() {
		int length = _subscriberImpl.fullNameLength();

		Assert.assertEquals(20, length);
	}

	@Test
	public void testGetMiddleInitial() {
		String middleInitial = _subscriberImpl.getMiddleInitial();

		Assert.assertEquals("E.", middleInitial);
	}

	@Test
	public void testToString() {
		String fullName = _subscriberImpl.toString();

		Assert.assertEquals("Brian E. Greenwald", fullName);
	}

	private SubscriberImpl _subscriberImpl;

}
