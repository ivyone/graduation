public class locations {
    private String name;
    private boolean commited;
    private String inva;
    private  String id;
    locations(String n,boolean c){
        setName(n);
        setCommited(c);
    }
    locations(String n,boolean c,String i){
        setName(n);
        setCommited(c);
        setInva(i);
    }
    locations (){

    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isCommited() {
        return commited;
    }
    public void setCommited(boolean commited) {
        this.commited = commited;
    }
    public String getInva() {
        return inva;
    }
    public void setInva(String inva) {
        this.inva = inva;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

}
