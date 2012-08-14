/*
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.portal.people.test;

import juzu.test.protocol.standalone.AbstractStandaloneTestCase;

import org.gatein.portal.people.test.annotation.ConfigurationUnit;
import org.gatein.portal.people.test.annotation.ConfiguredBy;
import org.gatein.portal.people.test.annotation.ContainerScope;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 *
 */
@ConfiguredBy({
	@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "container/conf/configuration.xml")
})
public class WithPortalContainerTestCase extends AbstractStandaloneTestCase {
	
	private static GateInBootstrap bootstrap;
	
	@BeforeClass
	public static void before() throws Exception {
		bootstrap = new GateInBootstrap(WithPortalContainerTestCase.class);
		bootstrap.boot();
	}
	
	@AfterClass
	public static void after() {
		bootstrap.dispose();
	}
	
	@Drone
	WebDriver driver;

	@Test
	public void testDeploy() throws Exception {
		assertDeploy("people", "binding");
		driver.get(deploymentURL.toString());
		try {
			driver.findElement(By.tagName("body")).getText();
		} catch(NullPointerException e) {
			failure(e);
		}
	}
}
