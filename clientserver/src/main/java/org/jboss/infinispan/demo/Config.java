package org.jboss.infinispan.demo;

import java.io.IOException;

import javax.enterprise.inject.Produces;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.NearCacheMode;
import org.jboss.infinispan.demo.model.Task;



/**
 * This class produces configured cache objects via CDI
 *  
 * @author tqvarnst
 *
 */
public class Config {

	/**
	 * FIXME: Add a default Producer for org.infinispan.client.hotrod.RemoteCache<Long, Task> 
	 * 		  using org.infinispan.client.hotrod.configuration.ConfigurationBuilder
	 * 		  and org.infinispan.client.hotrod.RemoteCacheManager
	 * 
	 * @return org.infinispan.client.hotrod.RemoteCache<Long, Task>
	 */
	@Produces
	public RemoteCache<Long, Task> getRemoteCache() throws IOException, DataGridConfigurationException {
		/**
		ConfigurationBuilder builder = new ConfigurationBuilder(); builder.addServer().host("172.30.65.245").port(11333);
		return new RemoteCacheManager(builder.build(), true).getCache("default");
		*/
		RemoteCacheManager cacheManager = this.getCacheManager();
		 return cacheManager.getCache("default");
		}
		 
		public RemoteCacheManager getCacheManager() throws DataGridConfigurationException {
		 ConfigurationBuilder builder = new ConfigurationBuilder();
		 builder
		  .addServer()
		  .host("172.30.65.245")
		  .port(11333);
		 return new RemoteCacheManager(builder.build(), true);
		}
		public static class DataGridConfigurationException extends Exception
		{
		 private static final long serialVersionUID = -4667039447165906505L;
		 public DataGridConfigurationException(String msg) {
		        super(msg);
		    }
		 
		}
	
}
