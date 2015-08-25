package extras;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetPropertyValues {

	static String propertyValue = "";

	
	
	public static String getPropValues(String propertyName) throws IOException {
 
		InputStream inputStream = null;
		
		try {
			Properties prop = new Properties();
			String propFileName = "resources\\config.properties";
 
			inputStream =  new FileInputStream(propFileName);
 
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 
			// get the property value and print it out
			propertyValue = prop.getProperty(propertyName);
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return propertyValue;
	}
}
