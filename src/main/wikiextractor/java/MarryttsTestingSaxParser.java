
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author metalogicssoft
 */
public class MarryttsTestingSaxParser {

    private static final Logger log = Logger.getLogger(MarryttsTestingSaxParser.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //parse commandline agruments
        if (args.length < 1) {
            log.log(Level.WARNING, "Input file missing. Parser aborted");
            System.exit(0);
        }
        //load properties from file if any
        AppProperties props = new AppProperties();
        //get input file name
        final String INPUT_FILE = args[0];
        //parse the further parameters if any
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            /*
             *we expect format of property as prop=value in command line.
             * Otherwise skip it and use default values
             */
            String[] prop = arg.split("=", 2);
            if (prop.length == 2) {
                props.setProperty(prop[0], prop[1]);
            }
        }

        /*
        * Read input file to start the processing
        */
        InputStream inputStream = null;
        try {
            //log start time
            log.log(Level.INFO, "File Extraction Processing started at {0}", new DateUtils().currentDateTime());
            //create file if it is xml input file
            if (!INPUT_FILE.endsWith(".bz2")) {
                inputStream = new FileInputStream(INPUT_FILE);
            } else {
                FileInputStream fin = new FileInputStream(INPUT_FILE);
                BufferedInputStream bis = new BufferedInputStream(fin);
                inputStream = new CompressorStreamFactory().createCompressorInputStream(bis);
            }
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            //create output directory if not exists
            File dir = new File(props.getProperty(Property.OUTPUT_DIR));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //create parser
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            SaxHandler handler = new SaxHandler(props);
            //start workers
            handler.startWorkers();
            //parse file
            saxParser.parse(is, handler);
            //stop workers
            handler.stopWorkers();
            //log finish time
            log.log(Level.INFO, "File Extraction Processing finished at {0}", new DateUtils().currentDateTime());
        } catch (SAXStopException ex) {
            log.log(Level.INFO, ex.getMessage());
        } catch (CompressorException | ParserConfigurationException | SAXException | IOException ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

}
