package cl.femase.gestionweb.jcs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.config.PropertySetter;
import org.apache.jcs.engine.CompositeCacheAttributes;
import org.apache.jcs.engine.ElementAttributes;

public class CacheFactory {

private static final String DEFAULT_VALUES_SETTINGS_FILE =
"jcsdefaults.properties";

private static Properties defaultProperties = new Properties();

private static String defaultsFile = null;

/**Constructor*/
private CacheFactory() {

}


/**
* An auxiliary method to create JCS caches.
* This method looks for the file jcs.properties in the same
package as the
* provided Class. The cache is created by using the region
cache and
* element attributes specified in the jcs.properties file.
*
* Note that region names are global. Region names must not
contain "/" or "."
* If you ask two times for the same region, you will get a
different JCS object
* but they act over the same region, therefore elements in the
cache are accesible
* to both.
*
* The key and elements to be inserted in the cache MUST be
Serializable. This is
* expected by JCS because it can cache elements to disk or
provide them to a remote
* agent, even if the cache is just defined as in-memory cache.
*
* @param clazz - A clazz used to specify the location of the
jcs.properties file
* @param region - The region name
* @return a JCS cache object with access to the specified
region
*/
	public static JCS createCache(Class clazz, String region) {
		if( defaultProperties.isEmpty()  ) {
			String pkg = CacheFactory.class.getPackage().getName();
			pkg = pkg.replace(".".charAt(0), "/".charAt(0));

			System.out.println("package: " + pkg);

			defaultsFile = "/" + pkg + "/" + DEFAULT_VALUES_SETTINGS_FILE;

			JCS.setConfigFilename( defaultsFile );
			try {
				defaultProperties.load(
				CacheFactory.class.getResourceAsStream(DEFAULT_VALUES_SETTINGS_FILE ));
			} catch (IOException ioe) {
				throw new RuntimeException("Unable to find resource ", ioe );
			}
		}

		Properties props = new Properties();
		CompositeCacheAttributes cca = new CompositeCacheAttributes();
		ElementAttributes eca = new ElementAttributes();

		try {
			// First apply the default values from
			//jcs.default
			PropertySetter.setProperties(cca,defaultProperties, "jcs.default.cacheattributes.");
			PropertySetter.setProperties(eca,defaultProperties, "jcs.default.elementattributes.");
			// Now apply the values from a specific jcs.propeties file
			InputStream is = clazz.getResourceAsStream("jcs.properties");
			if( is != null ) {
				props.load( is );
				PropertySetter.setProperties(cca,props,	"jcs.region." + region + ".cacheattributes.");
				PropertySetter.setProperties(eca,props,	"jcs.region." + region + ".elementattributes.");
			}
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to read jcs.properties from package " + clazz.getPackage().getName() , ioe);
		}
		JCS resp = null;
		try {
			resp = JCS.getInstance(region, cca);
			resp.setDefaultElementAttributes(eca);
		} catch (CacheException e) {
			throw new RuntimeException("Unable to set initialize JCS cache for region name [" +
			region +"]",e);
		}catch (Exception ex) {
			System.err.println("Excepcion: "+ex.toString());
			System.err.println("Unable to set initialize JCS cache for region name [" +
					region +"]");
				}
		return resp;
	}
}