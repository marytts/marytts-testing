

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atta-Ur-Rehman Shah
 */
public class SaxHandlerWorker extends Thread {

    private static final Logger log = Logger.getLogger(SaxHandlerWorker.class.getName());

    private final DateUtils dateUtils;

    private final SaxHandler handler;

    private boolean iCanContinue = true;

    private final Queue<Page> queue = new LinkedList<>();

    private Page item;

    private final String OUTPUT_DIR;

    private final int MAX_SLEEP_TIME = 1000 * 60; //1 minute

    private final int id;

    public SaxHandlerWorker(SaxHandler handler, int id, String outputDir) {
        super("SaxHandlerWorker-" + id);
        dateUtils = new DateUtils();
        this.handler = handler;
        this.id = id;
        this.OUTPUT_DIR = outputDir;
        setDaemon(true);
    }

    @Override
    public void run() {
        log.log(Level.INFO, "Sax Handler worker-{0} is up and running.", id);
        while (iCanContinue) {
            synchronized (queue) {
                // Check for a new item from the queue
                if (queue.isEmpty()) {
                    // Sleep for it, if there is nothing to do
                    log.log(Level.INFO, "Waiting for page to process...{0}", dateUtils.currentDateTime());
                    try {
                        queue.wait(MAX_SLEEP_TIME);
                    } catch (InterruptedException e) {
                        log.log(Level.INFO, "Interrupted...{0}", dateUtils.currentDateTime());
                    }
                }
                // Take new item from the top of the queue
                item = queue.poll();
                // Null if queue is empty
                if (item == null) {
                    continue;
                }
                try {
                    //print content
                    log.log(Level.INFO, item.toString());
                    //save contents into file
                    File output = new File(OUTPUT_DIR + "/" + item.getId() + ".txt");
                    FileWriter fileWriter = new FileWriter(output.getAbsoluteFile());
                    try (BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {
                        bufferWriter.write(item.toString());
                    }
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Exception while uploading Amazon S3 file...{0}",
                            ex.getMessage());
                }
            }
        }
    }

    public void add(Page item) {
        synchronized (queue) {
            queue.add(item);
            queue.notify();
            log.log(Level.INFO, "New page added into queue for  worker-{0}...", id);
        }
    }

    public void stopWorker() {
        log.log(Level.INFO, "Stopping Sax Handler worker-{0}...", id);
        try {
            iCanContinue = false;
            this.interrupt();
            this.join();
        } catch (InterruptedException | NullPointerException e) {
            log.log(Level.SEVERE, "Exception while stopping Sax Handler worker...{0}",
                    e.getMessage());
        }
    }
}
