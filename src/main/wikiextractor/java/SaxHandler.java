

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author metalogicssoft
 */
public class SaxHandler extends DefaultHandler {

    private static final Logger log = Logger.getLogger(SaxHandler.class.getName());

    private Page page;
    private boolean pageStarted;
    private final Stack<String> elementStack = new Stack<>();
    private final String OUTPUT_DIR;

    /*
     * siteinfo detils
     */
    private String siteName;
    public String baseUrl;
    private final List<String> knownNamespaces;
    private String templateNamespace;
    private boolean isTemplate = false;

    /*
     * Threads details for parallel processing
     */
    private final int MAX_WORKERS = 3;
    private SaxHandlerWorker[] workers;
    private int current = 0;

    public SaxHandler(String outputDir) {
        this.knownNamespaces = new ArrayList<>();
        this.OUTPUT_DIR = outputDir;
    }

    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attributes)
            throws SAXException {
        //push element into stack
        this.elementStack.push(qName);
        //if page starts, create Page object and set pageStarted to true
        if ("page".equals(qName)) {
            this.pageStarted = true;
            this.page = new Page();
            page.setBaseUrl(baseUrl);
        }
        //if it is namespace with key = 10 for template namespace
        if ("namespace".equals(qName) && "namespaces".equals(currentElementParent())
                && attributes.getValue("key").equals("10")) {
            isTemplate = true;
        }
    }

    @Override
    public void endElement(String uri, String localName,
            String qName) throws SAXException {
        //remove openin tag from stack
        this.elementStack.pop();
        //if closing tag is of page
        if ("page".equals(qName)) {
            this.pageStarted = false;
            //add page into one of worker queue
            workers[current++].add(page);
            current = current % MAX_WORKERS;

        }
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {

        String value = new String(ch, start, length).trim();
        if (value.length() == 0) {
            return; // ignore white space
        }
        if (pageStarted) {
            //if it is title
            if ("page".equals(currentElementParent()) && "title".equals(currentElement())) {
                this.page.setTitle(value);
            } //if it is id
            else if ("page".equals(currentElementParent()) && "id".equals(currentElement())) {
                this.page.setId(value);
            } //if it is text
            else if ("text".equals(currentElement())) {
                this.page.setText(value);
            }
        } else {
            //read siteinfo 
            if ("sitename".equals(currentElement())) {
                this.siteName = value;
            } else if ("base".equals(currentElement())) {
                this.baseUrl = value.substring(0, value.lastIndexOf('/') + 1);
            } else if ("namespace".equals(currentElement()) && "namespaces".equals(currentElementParent())) {
                if (isTemplate) {
                    this.templateNamespace = value;
                    isTemplate = false;
                } else {
                    this.knownNamespaces.add(value);
                }
            }

        }
    }

    private String currentElement() {
        return this.elementStack.peek();
    }

    private String currentElementParent() {
        if (this.elementStack.size() < 2) {
            return "";
        }
        return this.elementStack.get(this.elementStack.size() - 2);
    }

    public void startWorkers() {
        log.info("Sax Handler workers are initializing ....");
        this.workers = new SaxHandlerWorker[MAX_WORKERS];
        for (int i = 0; i < MAX_WORKERS; i++) {
            this.workers[i] = new SaxHandlerWorker(this, i + 1, OUTPUT_DIR);
            this.workers[i].start();
        }
    }

    public void stopWorkers() {
        log.info("Sax Handler workers are stopping...");
        for (int i = 0; i < MAX_WORKERS; i++) {
            workers[i].stopWorker();
        }
    }

    /*
     * Getter methods - make them synchronized to avoid race conditions
     */
    public synchronized String getSiteName() {
        return siteName;
    }

    public synchronized String getBaseUrl() {
        return baseUrl;
    }

    public synchronized List<String> getKnownNamespaces() {
        return knownNamespaces;
    }

    public synchronized String getTemplateNamespace() {
        return templateNamespace;
    }
}
