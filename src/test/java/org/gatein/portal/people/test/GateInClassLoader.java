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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.gatein.portal.people.test.annotation.ContainerScope;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 * 
 */
public class GateInClassLoader extends ClassLoader
{

	private final Map<ContainerScope, List<URL>> configurations;

	public GateInClassLoader(Map<ContainerScope, List<URL>> configurations, ClassLoader parent)
	{
		super(parent);
		this.configurations = configurations;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException
	{
		if ("conf/configuration.xml".equals(name))
		{
			ArrayList<URL> list = Collections.list(super.getResources(name));
			List<URL> urls = configurations.get(ContainerScope.ROOT);
			if (urls != null)
			{
				list.addAll(urls);
			}
			return Collections.enumeration(list);
		}
		else if ("conf/portal/configuration.xml".equals(name))
		{
			ArrayList<URL> list = Collections.list(super.getResources(name));
			List<URL> urls = configurations.get(ContainerScope.PORTAL);
			if (urls != null)
			{
				list.addAll(urls);
			}
			return Collections.enumeration(list);
		}
		else
		{
			return super.getResources(name);
		}
	}
}
