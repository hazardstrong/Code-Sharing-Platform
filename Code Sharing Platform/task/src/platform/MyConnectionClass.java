package platform;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "snippets")
public class MyConnectionClass {
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private String id_table;
    @Column(name = "code")
    private String code;
    @Column(name = "date")
    private String date;
    @Column(name = "view")
    private int view;
    @Column(name = "time")
    private int time;
    @Column(name = "flag")
    private  int flag;

    public MyConnectionClass() {
    }

    public MyConnectionClass(String id, String code, String date, int time, int view, int flag){
        this.id_table = id;
        this.code = code;
        this.date = date;
        this.time = time;
        this.view = view;
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getId() {
        return id_table;
    }

    public void setId(String id) {
        this.id_table = id;
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
