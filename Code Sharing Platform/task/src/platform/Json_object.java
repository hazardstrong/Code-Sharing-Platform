package platform;


public class Json_object {
    private String code;
    private int time;
    private int view;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public Json_object() {

    }

    public Json_object(String code, int time, int view) {
        this.code = code;
        this.time = time;
        this.view = view;
    }
}
