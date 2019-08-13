import java.util.List;
import java.util.*;

public class funcs {
    private  String template;
    private String body;

    private List<String> input;
    funcs(String template,String body){
        this.template=template;
        this.body=body;
    }
    funcs(){

    }
    public String getTemplate() {
        return template;
    }
    public void setTemplate(String template) {
        this.template = template;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body ;
    }
    public List<String> getInput() {
        return input;
    }
    public void setInput(List<String> input) {
        this.input = input ;
    }
}