package mx.com.rolcar.inventarios.util;

import com.atcloud.util.Numero;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Configuracion {
    static Logger log = Logger.getLogger(Configuracion.class.getName());
    private static Configuracion singleton = null;
    private Properties properties = null;
    private static String PROPERTIES_FILE = null;

    private Configuracion() throws Exception {
        properties = new Properties();
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if(stream==null) {
            File file = new File(PROPERTIES_FILE);
            if(!file.exists())
                throw new Exception("Can't find resource ["+PROPERTIES_FILE+"]");
            log.info("Path Absoluto ["+file.getAbsolutePath()+"]");
            stream = new FileInputStream(file);
        }
        properties.load(stream);
    }

    public static Configuracion getInstance() {
        if (singleton == null) {
            try {
                PROPERTIES_FILE = System.getProperty("PROPERTIES");
                if(PROPERTIES_FILE == null)
                    PROPERTIES_FILE = System.getProperty("ecommerce.home")+"/config/mumuso.properties";
                log.info("Archivo de Propiedades ["+PROPERTIES_FILE+"]");
                singleton = new Configuracion();
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

        }
        return singleton;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    public boolean getBooleanProperty(String name) {
        return Boolean.parseBoolean(properties.getProperty(name));
    }

    public int getIntProperty(String name) {
        return Numero.getIntFromString(properties.getProperty(name));
    }
}
