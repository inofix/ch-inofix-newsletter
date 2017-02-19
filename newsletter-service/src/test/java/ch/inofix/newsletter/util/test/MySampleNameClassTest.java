package ch.inofix.newsletter.util.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.inofix.newsletter.util.MySampleNameClass;

public class MySampleNameClassTest {

	@Before
	public void setUp() {
		_mySampleNameClass = new MySampleNameClass("Brian", "Edward", "Greenwald");
	}

	@Test
	public void testFullNameLength() {
		int length = _mySampleNameClass.fullNameLength();

		Assert.assertEquals(20, length);
	}

	@Test
	public void testGetMiddleInitial() {
		String middleInitial = _mySampleNameClass.getMiddleInitial();

		Assert.assertEquals("E.", middleInitial);
	}

	@Test
	public void testToString() {
		String fullName = _mySampleNameClass.toString();

		Assert.assertEquals("Brian E. Greenwald", fullName);
	}

	private MySampleNameClass _mySampleNameClass;

}
