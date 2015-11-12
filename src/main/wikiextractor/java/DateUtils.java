

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author metalogicssoft
 */
public class DateUtils {
 
    public String currentDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        return format.format(new Date());
    }
}
