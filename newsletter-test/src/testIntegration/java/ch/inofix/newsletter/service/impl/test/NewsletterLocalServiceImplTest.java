package ch.inofix.newsletter.service.impl.test;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
	
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);
	
	@Test
	public void testAddNewsletter() throws Exception {
		
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();
		
		
		
		
	}

}
