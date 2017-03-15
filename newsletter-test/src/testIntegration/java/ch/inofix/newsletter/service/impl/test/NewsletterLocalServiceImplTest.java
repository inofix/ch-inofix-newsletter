package ch.inofix.newsletter.service.impl.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;

/**
 * 
 * @author Christian Berndt
 * @created 2017-02-20 23:14
 * @modified 2017-02-20 23:14
 * @version 1.0.0
 *
 */
@RunWith(Arquillian.class)
@Sync
public class NewsletterLocalServiceImplTest {
	
//	@ClassRule
//	@Rule
//	public static final AggregateTestRule aggregateTestRule =
//		new AggregateTestRule();
	
	@Test
	public void testAddNewsletter() throws Exception {
		
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();
		
		Assert.assertEquals(1, 1);
		
	}

}
