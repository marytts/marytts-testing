/**
 * @author Atta-Ur-Rehman Shah
 */

public enum Property {

    OUTPUT_DIR("outputDir"),
    WORKERS("workers"),
    ARTICLES("articles");
    
     private final String value;
     
     private Property(String value)
     {
         this.value = value;
     }
     
     public String getValue()
     {
         return value;
     }
}
