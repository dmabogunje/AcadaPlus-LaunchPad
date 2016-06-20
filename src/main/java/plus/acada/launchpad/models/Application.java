package plus.acada.launchpad.models;

public class Application implements Comparable<Application> {

    private String name;
    private String icon;
    private String url;
    private int order;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(Application application) {
        if (getOrder() == application.getOrder())
            return 0;
        else
            return getOrder() > application.getOrder() ? 1 : -1;
    }
}
