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
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import juzu.impl.fs.spi.ram.RAMFile;
import juzu.impl.fs.spi.ram.RAMFileSystem;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.gatein.portal.people.test.annotation.Component;
import org.gatein.portal.people.test.annotation.Configuration;
import org.gatein.portal.people.test.annotation.ConfigurationUnit;
import org.gatein.portal.people.test.annotation.ConfiguredBy;
import org.gatein.portal.people.test.annotation.ContainerScope;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 * @version $Id$
 * 
 */
public class GateInBootstrap
{

	private PortalContainer container;

	private final ClassLoader classLoader;

	private final Map<ContainerScope, List<URL>> configurations;

	public GateInBootstrap(Class<?> owner) throws IOException
	{
		this.classLoader = owner.getClassLoader();
		Map<ContainerScope, List<URL>> configurations = new HashMap<ContainerScope, List<URL>>();

		//
		ConfiguredBy configuredBy = owner.getAnnotation(ConfiguredBy.class);
		if (configuredBy != null)
		{
			collect(configurations, configuredBy.value());
		}

		//
		Configuration bindings = owner.getAnnotation(Configuration.class);
		if (bindings != null)
		{
			collect(configurations, bindings.value());
		}

		this.configurations = configurations;
	}

	private void collect(Map<ContainerScope, List<URL>> holder, ConfigurationUnit[] units)
	{
		List<URL> urls = null;
		for (ConfigurationUnit unit : units)
		{
			urls = holder.get(unit.scope());
			if(urls == null)
			{
				holder.put(unit.scope(), urls = new ArrayList<URL>());
			}
			
			URL url = classLoader.getResource(unit.path());
			if(url != null)
			{
				urls.add(url);
			}
		}
	}

	private void collect(Map<ContainerScope, List<URL>> holder, Component[] components) throws IOException
	{
		List<URL> urls = null;
		for(Component component : components) 
		{
			urls = holder.get(component.scope());
			if(urls == null)
			{
				holder.put(component.scope(), urls = new ArrayList<URL>());
				RAMFileSystem fs = new RAMFileSystem();
				RAMFile file = fs.addFile(fs.getRoot(), "juzu.xml").update(new StringBuilder()
					.append("<configuration>")
					.append("<component>")
					.append("<key>").append(component.key().getName()).append( "</key>")
					.append("<type>").append(component.value().getName()).append("</type>")
					.append("</component>")			
					.append("</configuration>").toString());
				urls.add(fs.getURL(file));
			}
		}
	}

	public final void boot() throws Exception
	{
		try
		{
			Field topContainerField = ExoContainerContext.class.getDeclaredField("topContainer");
			topContainerField.setAccessible(true);
			topContainerField.set(null, null);

			//
			Field singletonField = RootContainer.class.getDeclaredField("singleton_");
			singletonField.setAccessible(true);
			singletonField.set(null, null);

			Thread.currentThread().setContextClassLoader(new GateInClassLoader(configurations, classLoader));
			container = PortalContainer.getInstance();
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}

	public final void dispose()
	{
		if (container != null)
		{
			RootContainer.getInstance().stop();
			container = null;
			ExoContainerContext.setCurrentContainer(null);
		}
	}
}
