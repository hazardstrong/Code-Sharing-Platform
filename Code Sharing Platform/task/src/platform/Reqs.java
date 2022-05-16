package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@Component
public class Reqs {
    @Autowired
            private MyRepository myobj;
    int flag;
    @ResponseBody
    @PostMapping(value = "/api/code/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object postCode(@RequestBody Json_object snip) {
        LocalDate today = LocalDate.now();
        LocalTime n1 = LocalTime.now();
        LocalTime now = n1.withNano(0);
        String date = today + " " + now;
        String code = snip.getCode();
        int time = snip.getTime();
        int view = snip.getView();

        if (time == 0 && view == 0){
            flag = 0;
        }else if (time != 0 && view == 0){
            flag = 1;
        }else if (time == 0 && view != 0){
            flag = 2;
        }else if (time != 0 && view != 0){
            flag = 3;
        }
        //Creating UUID:
        UUID uuid = UUID.randomUUID();
        String UUIDString = uuid.toString();
        myobj.save(new MyConnectionClass(UUIDString, code, date, time, view, flag));
        return "{ \"id\" : "+"\""+UUIDString+"\" }";
    }

    @GetMapping(value = "/code/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public Object getHTML(@PathVariable String id, Model model) {
        if (myobj.existsById(id) == false){
            return "NotFound";
        }else {
            Optional<MyConnectionClass> opt = myobj.findById(id);
            String db_code = opt.map(this::getMyCode).orElse(null);
            String db_date = opt.map(this::getMyDate).orElse(null);

            int db_seconds = opt.map(this::getMySeconds).orElse(null);
            int db_views = opt.map(this::getMyviews).orElse(null);
            int db_flag = opt.map(this::getMyflag).orElse(null);
            LocalDateTime now = LocalDateTime.now().withNano(0);
            String[] s = db_date.split(" ");
            LocalDateTime code_time = LocalDateTime.parse(s[0]+"T"+s[1]);
            int time_diff = (int) ChronoUnit.SECONDS.between(code_time, now);
            model.addAttribute("date_html", db_date);
            model.addAttribute("code", db_code);
            if (db_flag == 1 && time_diff < db_seconds){
                model.addAttribute("seconds", db_seconds - time_diff);
                return "codeWithTimeLimit";
            }else if (db_flag == 1 && time_diff > db_seconds){
                myobj.deleteById(id);
                return "NotFound";
            }else if (db_flag == 2 && db_views != 1){
                db_views--;
                myobj.save(new MyConnectionClass(id, db_code, db_date, db_seconds, db_views, db_flag));
                model.addAttribute("views", db_views);
                return "codeWithViewsLimit";
            } else if (db_flag == 2 && db_views == 1) {
                db_views--;
                myobj.deleteById(id);
                model.addAttribute("views", db_views);
                return "codeWithViewsLimit";
            } else if (db_flag == 3 && db_views != 1 && time_diff < db_seconds) {
                db_views--;
                myobj.save(new MyConnectionClass(id, db_code, db_date, db_seconds, db_views, db_flag));
                model.addAttribute("seconds", db_seconds-time_diff);
                model.addAttribute("views", db_views);
                return "code";
            } else if (db_flag == 3 && db_views == 1 && time_diff < db_seconds) {
                db_views--;
                myobj.deleteById(id);
                model.addAttribute("seconds", db_seconds-time_diff);
                model.addAttribute("views", db_views);
                return "code";
            } else if (db_flag == 3 && time_diff > db_seconds) {
                myobj.deleteById(id);
                return "NotFound";
            } else if (db_views == 0 && db_seconds == 0){
                return "UnLimited";
            }else {
                return "NotFound";
            }
        }
    }

    @ResponseBody
    @GetMapping(value = "/api/code/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object apiCode(JsonGetMapping getjson, @PathVariable String id) {
        if (myobj.existsById(id) == false){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }else {
            Optional<MyConnectionClass> opt = myobj.findById(id);
            String db_code = opt.map(this::getMyCode).orElse(null);
            String db_date = opt.map(this::getMyDate).orElse(null);
            int db_seconds = opt.map(this::getMySeconds).orElse(null);
            int db_views = opt.map(this::getMyviews).orElse(null);
            int db_flag = opt.map(this::getMyflag).orElse(null);

            LocalDateTime now = LocalDateTime.now().withNano(0);
            String[] s = db_date.split(" ");
            LocalDateTime code_time = LocalDateTime.parse(s[0]+"T"+s[1]);
            int time_diff = (int) ChronoUnit.SECONDS.between(code_time, now);

            getjson.setCode(db_code);
            getjson.setDate(db_date);
            if (db_flag == 1 && db_seconds > time_diff){
                getjson.setTime(db_seconds - time_diff);
                getjson.setViews(db_views);
                return getjson;
            }else if (db_flag == 1 && db_seconds < time_diff){
                myobj.deleteById(id);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }else if (db_flag == 2 && db_views != 0){
                myobj.save(new MyConnectionClass(id, db_code, db_date, db_seconds, db_views-1, db_flag));
                getjson.setViews(db_views-1);
                getjson.setTime(db_seconds);
                return getjson;
            }else if (db_flag == 2 && db_views == 0){
                myobj.deleteById(id);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }else if (db_flag == 3 && db_views != 0 && db_seconds > time_diff){
                myobj.save(new MyConnectionClass(id, db_code, db_date, db_seconds, db_views-1, db_flag));
                getjson.setViews(db_views-1);
                getjson.setTime(db_seconds);
                return getjson;
            }else if (db_flag == 3 && db_views == 0 && db_seconds > time_diff){
                myobj.deleteById(id);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }else if (db_flag == 3 && db_seconds < time_diff){
                myobj.deleteById(id);
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }else if (db_flag == 0){
                getjson.setTime(0);
                getjson.setViews(0);
                return getjson;
            }else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        }

    }

    //-----------------------------------------------------------------------------
    private int getMyviews(MyConnectionClass myConnectionClass) {
        return myConnectionClass.getView();
    }
    private int getMySeconds(MyConnectionClass myConnectionClass) {
        return myConnectionClass.getTime();
    }
    private String getMyDate(MyConnectionClass myConnectionClass) {
        return myConnectionClass.getDate();
    }

    private String getMyCode(MyConnectionClass myConnectionClass) {
        return myConnectionClass.getCode();
    }
    private int getMyflag(MyConnectionClass myConnectionClass) {
        return myConnectionClass.getFlag();
    }
//-------------------------------------------------------------------------------

    @GetMapping(value = "/code/latest", produces = MediaType.TEXT_HTML_VALUE)
    public Object getLatestHtml(Model model) {
        //This endpoint shows the last 10 snippets
        List<LatestHTML> codelist = new ArrayList<>();
        Iterable<MyConnectionClass> latest = myobj.findAll();
        Collections.reverse((List<?>) latest);
        int count =1;
        for (MyConnectionClass k: latest){
            if (k.getView() == 0 && k.getTime() == 0 && count <= 10){
                codelist.add(new LatestHTML(k.getCode(), k.getDate()));
                count++;
            }
        }
        model.addAttribute("codes", codelist);
        return "latest";
    }
    @ResponseBody
    @GetMapping(value = "/api/code/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getLatestApi(){
        //The last 10 snippets:
        List<JsonGetMapping> api_list = new ArrayList<>();
        Iterable<MyConnectionClass> latest = myobj.findAll();
        Collections.reverse((List<?>) latest);
        int count =1;
        for (MyConnectionClass k: latest){
            if (k.getView() == 0 && k.getTime() == 0 && count <= 10){
                api_list.add(new JsonGetMapping(k.getCode(), k.getDate(), 0, 0));
                count++;
            }
        }
        return api_list;
    }

    @GetMapping(value = "/code/new", produces = MediaType.TEXT_HTML_VALUE)
    public Object getNew() {
        return "new";
    }
}