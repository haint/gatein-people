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

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import juzu.test.protocol.standalone.AbstractStandaloneTestCase;

import org.exoplatform.services.organization.OrganizationService;
import org.gatein.portal.people.test.annotation.Component;
import org.gatein.portal.people.test.annotation.Configuration;
import org.gatein.portal.people.test.annotation.ContainerScope;
import org.gatein.portal.people.test.service.MockOrganizationService;
import org.gatein.portal.people.test.util.HtmlParser;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 * 
 */
@Configuration({
	@Component(scope = ContainerScope.ROOT, key = OrganizationService.class, value = MockOrganizationService.class)
})
public class GateInPeopleTestCase extends AbstractStandaloneTestCase
{

	private static GateInBootstrap bootstrap;

	@BeforeClass
	public static void before() throws Exception
	{
		bootstrap = new GateInBootstrap(GateInPeopleTestCase.class);
		bootstrap.boot();
	}

	@AfterClass
	public static void after()
	{
		bootstrap.dispose();
	}
	
	@Drone
	WebDriver driver;
	
	@Test
	public void test() throws Exception
	{
		assertDeploy("gatein","people","test");
		driver.get(deploymentURL.toString());
		
		List<WebElement> anchors = driver.findElements(By.tagName("a"));
		assertEquals(5, anchors.size());

		//
		HtmlParser htmlParser = new HtmlParser();
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expression = xpath.compile("/HTML/BODY/LI");

		//
		String findUser1URL = driver.findElement(By.id("findUsers1")).getAttribute("href");
		String findUser2URL = driver.findElement(By.id("findUsers2")).getAttribute("href");
		String findGroup1URL = driver.findElement(By.id("findGroup1")).getAttribute("href");
		String findGroup2URL = driver.findElement(By.id("findGroup2")).getAttribute("href");
		String getProfileURL = driver.findElement(By.id("getProfile")).getAttribute("href");
		
		//
		driver.get(findUser1URL);
		Document doc = htmlParser.parseNonWellForm(driver.getPageSource());
		NodeList list = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
		assertEquals(10, list.getLength());
		
		String[] users = new String[] { "exo", "admin", "weblogic", "__anonim", "root", "john", "james", "mary", "marry", "demo" };		
		for(int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			assertEquals(users[i], node.getAttributes().getNamedItem("id").getNodeValue());
		}
		
		//
		driver.get(findUser2URL);
		doc = htmlParser.parseNonWellForm(driver.getPageSource());
		list = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
		assertEquals(1, list.getLength());
		assertEquals("root", list.item(0).getAttributes().getNamedItem("id").getNodeValue());
		
		//
		driver.get(findGroup1URL);
		doc = htmlParser.parseNonWellForm(driver.getPageSource());
		list = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
		assertEquals(3, list.getLength());
		
		expression = xpath.compile("DIV[1]");
		String[] groups = new String[] { "platform", "administrators", "users" };
		for(int i = 0; i < list.getLength(); i++) 
		{
			Node node = (Node)expression.evaluate(list.item(i), XPathConstants.NODE);
			assertEquals(groups[i], node.getTextContent());
		}
		
		//
		driver.get(findGroup2URL);
		doc = htmlParser.parseNonWellForm(driver.getPageSource());
		
		//
		driver.get(getProfileURL);
		doc = htmlParser.parseNonWellForm(driver.getPageSource());
		expression = xpath.compile("/HTML/BODY/DIV[@class='modal hide']/DIV[@class='modal-body']/FORM/FIELDSET/DIV[position()<3]");
		list = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
		assertEquals(2, list.getLength());
		
		expression = xpath.compile("*/INPUT");
		
		Node userNameInput = ((Node)expression.evaluate(list.item(0), XPathConstants.NODE));
		assertNotNull(userNameInput);
		assertEquals("root", userNameInput.getAttributes().getNamedItem("placeholder").getNodeValue());
		
		Node emailInput = ((Node)expression.evaluate(list.item(1), XPathConstants.NODE));
		assertNotNull(emailInput);
		assertEquals("root@mail.com", emailInput.getAttributes().getNamedItem("value").getNodeValue());
	}
}
