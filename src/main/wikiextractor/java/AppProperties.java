
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author metalogicssoft
 */
public class AppProperties {

    private static final Logger log = Logger.getLogger(AppProperties.class.getName());
    private final Properties props;

    public AppProperties(){
        //load properties file
        props = new Properties();
        try {
            props.load(new FileInputStream("application.properties"));
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    /*
     * get a specific property
     */
    public String getProperty(Property key) {
        return props.getProperty(key.getValue());
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }
    /*
     * overwrite/set a specific property value
     */

    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

//    public static void main(String[] args) {
//        try {
//            AppProperties pr = new AppProperties();
//            System.out.println(pr.getProperty(Property.WORKERS));
//            pr.setProperty("workers", "15");
//            pr.setProperty("input", "<folder>");
//            System.out.println(pr.getProperty(Property.WORKERS));
//            System.out.println(pr.getProperty("folder"));
//        } catch (IOException ex) {
//            Logger.getLogger(AppProperties.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
}
