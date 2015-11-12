

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.xml.sax.InputSource;

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
        if(args.length < 1)
        {
            log.log(Level.WARNING, "Input file missing. Parser aborted");
            System.exit(0);
        }
        //get input file name
        final String INPUT_FILE = args[0];
        //final String INPUT_FILE = "wikipedia2text-toparticles.xml.bz2";
        //get output directory if any 
        final String OUTPUT_DIR = args.length > 1 ? args[1] : "text";

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
            File dir = new File(OUTPUT_DIR);
            if (!dir.exists()) {
                dir.mkdir();
            }
            //create parser
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            SaxHandler handler = new SaxHandler(OUTPUT_DIR);
            //start workers
            handler.startWorkers();
            //parse file
            saxParser.parse(is, handler);
            //stop workers
            handler.stopWorkers();
            //log finish time
            log.log(Level.INFO, "File Extraction Processing finished at {0}", new DateUtils().currentDateTime());
        } catch (Exception ex) {
            Logger.getLogger(MarryttsTestingSaxParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(MarryttsTestingSaxParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
