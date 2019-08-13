import java.util.*;
public class func_defin {
    private String type;
    private String name;
    func_defin(String a,String b){
        setName(b);
        setType(a);
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name =name;
    }
}
