public class tran_sync {

    private String name;
    private int num;

    private String type;
    tran_sync(String n){
        setName(n);
        setNum(0);

    }
    tran_sync(){

    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type=type;
    }

}
