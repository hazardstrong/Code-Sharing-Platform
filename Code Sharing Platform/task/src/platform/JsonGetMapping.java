package platform;

public class JsonGetMapping {
    private String code;
    private String date;
    private int time;
    private int views;
    public JsonGetMapping(String code, String date, int time, int views){
        this.code = code;
        this.date = date;
        this.time = time;
        this.views = views;
    }
    public JsonGetMapping(){
    }
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
