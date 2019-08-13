public class dynamic {

    private String name;
    private String from;
    private boolean active;
    private String to;
    private String guard;
    private String sync;
    private String assignment;
    private String template;
    private int num;
    private boolean visited;

    dynamic(){

    }
    dynamic(String n,boolean a,String f,String t,String g ,String s,String aa,String tt){
        setName(n);
        setActive(a);
        setFrom(f);
        setTo(t);
        setGuard(g);
        setSync(s);
        setAssignment(aa);
        setTemplate(tt);

    }
    public String getTemplate() {
        return template;
    }
    public void setTemplate(String template) {
        this.template = template;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getGuard() {
        return guard;
    }
    public void setGuard(String guard) {
        this.guard = guard;
    }
    public String getSync() {
        return sync;
    }
    public void setSync(String sync) {
        this.sync = sync;
    }
    public String getAssignment() {
        return assignment;
    }
    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public boolean getVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
