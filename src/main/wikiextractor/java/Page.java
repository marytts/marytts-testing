

/**
 *
 * @author metalogicssoft
 */
public class Page {

    private String id;
    private String title;
    private String text;
    private String baseUrl;

    public Page() {

    }

    public Page(String id, String title, String text, String baseUrl) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.baseUrl = baseUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text += text;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String toString() {
        String str = "<doc id=\"" + id + "\" url=\"" + baseUrl + title + "\">";
        str += text;
        str += "</doc>";
        return str;
    }
}
