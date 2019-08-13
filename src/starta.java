import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import  java.lang.*;
import  java.util.*;


public class starta extends JFrame implements ActionListener {
    private static final long serialVersionUID = -1189035634361220261L;
    private String infile;
    private String outfile;

    JFrame mainframe;
    JPanel panel;
    //创建相关的Label标签
    JLabel infilepath_label = new JLabel("Source File(XML):");
    JLabel outfilepath_label = new JLabel("Destination Folder:");

    JTextField infilepath_textfield = new JTextField(20);
    JTextField outfilepath_textfield = new JTextField(20);
    //创建滚动条以及输出文本域
    JScrollPane jscrollPane;
    JTextArea outtext_textarea = new JTextArea();
    //创建按钮
    JButton infilepath_button = new JButton("...");
    JButton outfilepath_button = new JButton("...");

    JButton start_button = new JButton("Submit");


    private static List<dynamic> info;       //存储动态表内容
    private static List<locations> loc;      //存储静态表locations内容
    private static List<funcs> fun;          //存储静态表local_variables内容

    private static String tran;
    private static  StringBuilder sb;
    private static String str_for_mapping;
    private static boolean is_revert;
    private static boolean is_whilea;
    private static List<func_defin > definList=new ArrayList<>();


    @Override
    public void show(){
        mainframe = new JFrame("Code Generator Based On UPPAAL");
        // Setting the width and height of frame
        mainframe.setSize(575, 550);
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.setResizable(false);//固定窗体大小

        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width/2; // 获取屏幕的宽
        int screenHeight = screenSize.height/2; // 获取屏幕的高
        int height = mainframe.getHeight(); //获取窗口高度
        int width = mainframe.getWidth(); //获取窗口宽度
        mainframe.setLocation(screenWidth-width/2, screenHeight-height/2);//将窗口设置到屏幕的中部
        //窗体居中，c是Component类的父窗口
        //mainframe.setLocationRelativeTo(c);
        Image myimage=kit.getImage("resourse/hxlogo.gif"); //由tool获取图像
        mainframe.setIconImage(myimage);
        initPanel();//初始化面板
        mainframe.add(panel);
        mainframe.setVisible(true);
    }
    /* 创建面板，这个类似于 HTML 的 div 标签
     * 我们可以创建多个面板并在 JFrame 中指定位置
     * 面板中我们可以添加文本字段，按钮及其他组件。
     */
    public void initPanel(){
        this.panel = new JPanel();
        panel.setLayout(null);
        //this.panel = new JPanel(new GridLayout(3,2)); //创建3行3列的容器
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        infilepath_label.setBounds(10,20,120,25);
        infilepath_textfield.setBounds(120,20,400,25);
        infilepath_button.setBounds(520,20, 30, 25);
        this.panel.add(infilepath_label);
        this.panel.add(infilepath_textfield);
        this.panel.add(infilepath_button);

        outfilepath_label.setBounds(10,50,120,25);
        outfilepath_textfield.setBounds(120,50,400,25);
        outfilepath_button.setBounds(520,50, 30, 25);
        this.panel.add(outfilepath_label);
        this.panel.add(outfilepath_textfield);
        this.panel.add(outfilepath_button);


        start_button.setBounds(10,120,80,25);
        this.panel.add(start_button);

        outtext_textarea.setEditable(false);
        outtext_textarea.setFont(new Font("标楷体", Font.BOLD, 16));
        jscrollPane = new JScrollPane(outtext_textarea);
        jscrollPane.setBounds(10,170, 550, 330);
        this.panel.add(jscrollPane);
        //增加动作监听
        infilepath_button.addActionListener(this);
        outfilepath_button.addActionListener(this);

        start_button.addActionListener(this);
    }
    /**
     * 单击动作触发方法
     * @param event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        // TODO Auto-generated method stub
        System.out.println(event.getActionCommand());
        if(event.getSource() == start_button){
            //确认对话框弹出
            int result = JOptionPane.showConfirmDialog(null, "是否开始转换?", "确认", 0);//YES_NO_OPTION
            if (result == 1) {//是：0，否：1，取消：2
                return;
            }
            System.out.println(infilepath_textfield.getText());
            if (infilepath_textfield.getText().equals("") || outfilepath_textfield.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "路径不能为空", "提示", 2);//弹出提示对话框，warning
                return;
            }else{
                outtext_textarea.setText("");
                infile = infilepath_textfield.getText();
                outfile = outfilepath_textfield.getText();
                start_ana(infile,outfile);
                outtext_textarea.setText("模型解析完成！\n成功生成目标文件！\n");



            }
        }else{
            //判断三个选择按钮并对应操作
            if(event.getSource() == infilepath_button) {
                File file = openChoseWindow(JFileChooser.FILES_ONLY);
                if(file == null)
                    return;
                infilepath_textfield.setText(file.getAbsolutePath());
                outfilepath_textfield.setText(file.getParent()+"/out.sol");

            }else if(event.getSource() == outfilepath_button){
                File file = openChoseWindow(JFileChooser.DIRECTORIES_ONLY);
                if(file == null)
                    return;
                outfilepath_textfield.setText(file.getAbsolutePath()+"/out.sol");
            }
        }
    }
    /**
     * 打开选择文件窗口并返回文件
     * @param type
     * @return
     */
    public File openChoseWindow(int type){
        JFileChooser jfc=new JFileChooser();
        jfc.setFileSelectionMode(type);//选择的文件类型(文件夹or文件)
        jfc.showDialog(new JLabel(), "选择");
        File file=jfc.getSelectedFile();
        return file;
    }
    public void windowClosed(WindowEvent arg0) {
        System.exit(0);
    }
    public void windowClosing(WindowEvent arg0) {
        System.exit(0);
    }


    public String getOutfile() {
        return outfile;
    }

    public void setOutfile(String outfile) {
        this.outfile = outfile;
    }
    public  String getInfile() {
        return infile;
    }

    public void setInfile(String infile) {
        this.infile = infile;
    }

public static void start_ana(String in,String out){

    //String infile=getInfile();
    //String outfile=getOutfile();
    Model_dom4j mo=new Model_dom4j();
          mo.xml(in);   //之后改回来
         // List<locations> lo=new ArrayList<>();
    str_for_mapping=mo.Get_str_to_delete();
    //获得数据库里的内容
    info=connecttrans();
    //info=mo.Get_dyna();
    tran=mo.Get_decla_str();
    loc=connectloc();
    //loc=mo.Get_loclist();
    //fun=connectfunc();
    fun=mo.Get_decla_template();
    List<dynamic> mid=new ArrayList();
    sb=new StringBuilder();
    //先写入规格和变量
        sb.append("pragma solidity ^0.5.1;\n");
        sb.append("contract Ballot{\n");
    String sa=null;
    tran_sync ss;
    String sf=new String();
    dynamic sd;
    int i=0,cnt=0;

     sb.append(tran);



        for(i=0;i<info.size();i++) {
        sd=info.get(i);
        if(sd.isActive()) {
            sd.setNum(cnt);
            mid.add(sd);
            cnt++;

        }
    }
    //模型分析开始

    //StringBuilder st = new StringBuilder();
    String std="";
    String guarda="";
    String syncc="";
    List<Integer> sides=new ArrayList<Integer>();
    //std="function S0()"+" public{\n";

    for(i=0;i<cnt;i++) {   //测试后改回来

        sd = mid.get(i);
        String name=sd.getTemplate();
        funcs fun_get=new funcs();
        std="function "+name+"(";
        for(int j=0;j<fun.size();j++){
             fun_get=fun.get(j);
           if(fun_get.getTemplate().equals(name)){
               for(int jj=0;jj<fun_get.getInput().size();jj++){
                   if(jj==fun_get.getInput().size()-1)
                       std+=fun_get.getInput().get(jj);
                   else
                       std+=fun_get.getInput().get(jj)+",";
               }
               std+=") public {\n";
               sb.append(std);
               break;
           }
        }
        definList.clear();
        String[]  b,c;
        for(int j=0;j<fun.size();j++){
            fun_get=fun.get(j);
            if(fun_get.getTemplate().equals(name)) {

                sf = fun_get.getBody();
                b=sf.split("\n");
                for(int ii=0;ii<b.length;ii++) {
                    String type="",dname="";
                    c = b[ii].split(" ");
                    if(c.length<=1) continue;
                        dname = c[c.length - 1].substring(0, c[c.length - 1].length() - 1);
                        for (int jj = 0; jj < c.length - 1; jj++) {
                            type += c[jj] + " ";
                        }
                        func_defin ff = new func_defin(type, dname);
                        definList.add(ff);
                    }

                //sb.append(sf);
                break;
            }
        }
        //sb.append(std);

       analyse(sd,1);
        analyse_next(sd);
        sb.append("}\n");

    }

sb.append("}\n");

    //sb.append(st.toString());
    //System.out.println(sb.toString());




    sb=opt_code(sb.toString());

    //写文件开始

    File filea=new File(out);
        if (!filea.exists()) {
        try {
            filea.createNewFile();
            FileWriter printWriter =new FileWriter(filea,false);
            printWriter.write(sb.toString());
            printWriter.close();
            System.out.println("Done");
        } catch (IOException e) {
            System.out.println("创建新文件时出现了错误。。。");
            e.printStackTrace();
        }

    }else {
        System.out.println(filea.getAbsolutePath());
        System.out.println(filea.getName());
        System.out.println(filea.length());
        try {
            FileWriter printWritera =new FileWriter(filea,true);
            printWritera.write(sb.toString());
            printWritera.close();
            System.out.println("Done");
        }catch (IOException e) {
            System.out.println("写入新文件时出现了错误。。。");
            e.printStackTrace();
        }
    }
}


public static  boolean is_while(dynamic sa){
        String to=sa.getTo();
        if(to.contains("while"))
            return true;
        return false;



}
//private static String  from;
public static void handle_while(dynamic a,String from){
        String to;


        dynamic sa=new dynamic();
        List<Integer> sides=reset(a.getTo());
        for(int i=0;i<sides.size();i++){
            sa=info.get(sides.get(i));
            sa.setVisited(true);
            to=sa.getTo();
            if(from.equals(to))
                break;
            if(to.contains("Start"))
                return;
            /* String guard=sa.getGuard();
              boolean emp=(guard==null);
            if((guard.charAt(0)!='!'&&!emp)||emp){
            */
                if(sides.size()==1)
                analyse(sa, 1);
                else {
                    if (i == 0)
                        analyse(sa, 2);
                    else if (i == sides.size() - 1)
                        analyse(sa, 4);
                    else
                        analyse(sa, 3);
                }


            handle_while(sa,from);
            if(have_branch) {
                sb.append("}\n");
                have_branch=false;
            }
        }

}

    public static void analyse_next(dynamic a){
        if(a.getVisited())
            return;
        a.setVisited(true);
        List<Integer> sides=new ArrayList<Integer>();
        sides=reset(a.getTo());
        dynamic sda=new dynamic();
        for(int i=0;i<sides.size();i++){    //在这里设置while
            sda=info.get(sides.get(i));
            if(sda.getTo().contains("Start"))
                return;
            if(is_while(sda)){
               String st=handle_assign(sda.getGuard());
               String std="while("+st+"){\n";
                sb.append(std);
                is_whilea=true;
                String from=sda.getFrom();
             handle_while(sda,from);
             sb.append("}\n");
            }else {
                if(is_whilea){
                    is_whilea=false;
                    analyse_next(sda);
                }else {
                    if (sides.size() == 1)
                        analyse(sda, 1);
                    else {
                        if (i == 0)
                            analyse(sda, 2);
                        else if (i == sides.size() - 1)

                            analyse(sda, 4);


                        else
                            analyse(sda, 3);
                    }
                }
            }
            analyse_next(sda);
            if(have_branch) {
                sb.append("}\n");
                have_branch=false;
            }

        }

    }
    private static  boolean have_branch;
    public static void  analyse(dynamic sd,int i) {
        String guarda = null;
        String syncc = null;
        String std = null;
        guarda = handle_assign(sd.getGuard());
        syncc = sd.getSync();
        String is_g = "";
        is_g = get_inva(sd.getFrom());
        boolean inva_ex = !(is_g == null);
        boolean guard_ex = !(guarda == null);
        boolean syncc_ex = !(syncc == null);
        String differ_type=new String();

        if (inva_ex || guard_ex ) {

            if(i==1){
                std = "for(,,,){\n";
                sb.append(std);
                differ_type="if(";
            }
            if(i==2){
                differ_type="if(";
            }
            if(i==3)
                differ_type="else if(";
            if(i==1||i==2||i==3) {
                have_branch=true;
                if (inva_ex && guard_ex) {
                    std = differ_type + get_inva(sd.getFrom()) + "&&" + guarda + "){\n";
                    sb.append(std);

                } else if (guard_ex) {
                    std = differ_type + guarda + "){\n";
                    sb.append(std);
                } else if (inva_ex) {
                    std = differ_type + get_inva(sd.getFrom()) + "){\n";
                    sb.append(std);
                }

                    if (sd.getTo().contains("revert")) {
                        sb.append("revert();\n");
                        is_revert=true;
                    } else {
                        String hsa = handle_assign(sd.getAssignment());
                        if (hsa != null) {
                            std = hsa + ";\n";
                            sb.append(std);
                        }

                    }

                if (i == 1)
                    sb.append("}\n"); //test
            }else{
                if (inva_ex) {
                    have_branch=true;
                    std = "if(" + get_inva(sd.getFrom()) + "){\n";
                    sb.append(std);
                }else {
                    if(!is_revert) {
                        have_branch=true;
                        std = "else{\n";
                        sb.append(std);
                    }else
                        is_revert=false;
                }
                    if (sd.getTo().contains("revert")) {
                        is_revert=true;
                        sb.append("revert();\n");
                    } else {
                        String hsa = handle_assign(sd.getAssignment());
                        if (hsa != null) {
                            std = hsa + ";\n";
                            sb.append(std);
                        }

                    }

            }
        }else {
            String hsa = handle_assign(sd.getAssignment());

            if (hsa != null) {
                int spot=hsa.indexOf("=");
                String cc="";
                if(spot!=-1) {
                    cc = hsa.substring(0, spot);
                    for(int j=0;j<definList.size();j++){
                        func_defin ff=definList.get(j);
                        if(cc.equals(ff.getName())){
                            hsa=ff.getType()+" "+hsa;
                            break;
                        }
                    }
                }

                std =  hsa + ";\n";

                sb.append(std);
            }


        }


    }




    public  static String handle_assign(String as){   //delete assignment里的：
        //String as=a.getAssignment();

        if (as!=null){
            int spot=as.indexOf(str_for_mapping);
            char sa='a';
            //if(!str_for_mapping.equals("")&&spot!=-1)
                //sa=as.charAt(spot+str_for_mapping.length());
            while ((spot!=-1&&!str_for_mapping.equals(""))||spot+str_for_mapping.length()==as.length()) {
                //String str_delete="\\"+str_to_delete;
                // c = c.replaceFirst(str_delete, "");
                if(spot+str_for_mapping.length()==as.length())
                    as=as.substring(0,spot);
                else if(as.charAt(spot+str_for_mapping.length())=='.')
                as=delete_str_for_mapping(spot,as);
                else
                    break;
                spot=as.indexOf(str_for_mapping);
            }
            if(as.contains("this"))
                as=as.replace("this","msg.sender");
            if(as.contains("address_0"))
                as=as.replace("address_0","address(0)");


        }
        return as;
    }

    public static String  delete_str_for_mapping(int spot,String cs){
        String d=new String();
        d=cs.substring(0,spot);
        d+=cs.substring(spot+str_for_mapping.length());
        return d;

    }

    //当前点是否为紧急点
    public static boolean is_commited(String a) {
        for(int i=0;i<loc.size();i++) {
            locations ll=loc.get(i);
            if(a.equals(ll.getName())&&ll.isCommited())
                return true;
        }
        return false;

    }
    //获取点的inva
    public static String get_inva(String a) {
        for(int i=0;i<loc.size();i++) {
            locations ll=loc.get(i);
            if(a.equals(ll.getName()))
                return ll.getInva();
        }
        return null;

    }

    //将以此点为起点的边的active设为true
    public static List<Integer> reset(String a) {    //假设只有两条边
        dynamic sa;
        List<Integer> si=new ArrayList();
        String sb=null;
        for(int i=0;i<info.size();i++) {
            sa=info.get(i);
            sb=sa.getFrom();
            if(sb.equals(a)&&!sa.getVisited()) {
                sa.setActive(true);
                si.add(i);
            }

        }

            String guard;
            if(si.size()>1) {
                sa = info.get(si.get(0));
                if (sa.getGuard() != null) {
                    guard = sa.getGuard();
                    if (guard.charAt(0) == '!') {
                        int temp1, temp2;
                        temp1 = si.get(0);
                        temp2 = si.get(1);
                        si.clear();
                        si.add(temp2);
                        si.add(temp1);    //交换位置


                    }
                }
            }

        return si;
    }
    //是否有可执行的下一条边
    public static boolean next_tran(dynamic a) {
        dynamic sa;
        String fr=a.getTo();
        for(int i=0;i<info.size();i++){
            sa=info.get(i);
            if(sa.getFrom().equals(fr)&&!sa.getVisited()){
                return true;
            }
        }
        return false;
    }

    //改！！！

   /* public static int handlestr(String a,int j) {
        for(int i=0;i<tran.size();i++) {
            tran_sync ts=tran.get(i);
            if(a.equals(ts.getName())&&j!=i) {
                return j;
            }
        }
        return -1;
    }

    public static boolean handlestr(String a,String b) {
        int t,i,cc;
        t=a.indexOf(b);
        String s1=a.substring(0, t);
        String s2=a.substring(t+b.length());
        tran_sync ts,td;
        for(i=0;i<tran.size();i++) {
            ts=tran.get(i);
            if(s1==ts.getName()) {
                cc=handlestr(s2,i);
                if(cc!=-1) {
                    td=tran.get(cc);
                    if(b=="==") {
                        if(ts.getNum()==td.getNum())
                            return true;
                    }
                    if(b==">=") {
                        if(ts.getNum()>=td.getNum())
                            return true;
                    }
                    if(b=="<=") {
                        if(ts.getNum()<=td.getNum())
                            return true;
                    }
                    if(b==">") {
                        if(ts.getNum()>td.getNum())
                            return true;
                    }
                    if(b=="<") {
                        if(ts.getNum()<td.getNum())
                            return true;
                    }
                }else {
                    int res=Integer.parseInt(s2);
                    if(b=="==") {
                        if(ts.getNum()==res)
                            return true;
                    }
                    if(b==">=") {
                        if(ts.getNum()>=res)
                            return true;
                    }
                    if(b=="<=") {
                        if(ts.getNum()<=res)
                            return true;
                    }
                    if(b==">") {
                        if(ts.getNum()>res)
                            return true;
                    }
                    if(b=="<") {
                        if(ts.getNum()<res)
                            return true;
                    }
                }
            }
        }
        return false;
    }
    */

    public static List<dynamic> connecttrans(){
        // 加载数据库驱动  com.mysql.jdbc.Driver
        String driver = "com.mysql.cj.jdbc.Driver";
        // 获取mysql连接地址
        String url = "jdbc:mysql://localhost:3306/graduation";
        // 数据名称
        String username = "root";
        // 数据库密码
        String password = "141500";
        // 获取一个数据的连接
        Connection conn = null;
        List<dynamic> aa=new ArrayList();
        // 获取连接的一个状态
        try{
            Class.forName(driver);
            //getConnection()方法，连接MySQL数据库！
            conn= DriverManager.getConnection(url,username,password);
            if(!conn.isClosed())
                System.out.println("数据库连接成功！");
            //创建statement类对象，用来执行SQL语句！
            Statement Statement=conn.createStatement();
            //要执行的SQL语句
            String sql="select * from transitions" ;
            //ResultSet类，用来存放获取的结果集！
            ResultSet rs=Statement.executeQuery(sql);


            //String active=null;
            boolean active;
            String name=null;
            String from=null;
            String to=null;
            String guard=null;
            String sync=null;
            String assignment=null;
            String template=null;

            while(rs.next()){
                name=rs.getString("namea");
                //active=rs.getString("active");
                active=rs.getBoolean("activea");
                from=rs.getString("froma");
                to=rs.getString("toa");
                guard=rs.getString("guarda");
                sync=rs.getString("synca");
                assignment=rs.getString("assignmenta");
                template=rs.getString("template");
                //输出结果
                // System.out.println(name+"\t"+active+"\t"+from+"\t"+to+"\t"+guard+"\t"+sync+"\t"+assignment );
                dynamic ab=new dynamic(name,active,from,to,guard,sync,assignment,template);
                aa.add(ab);
            }
            rs.close();
            conn.close();
        }
        catch(ClassNotFoundException e){
            //数据库驱动类异常处理
            System.out.println("数据库驱动加载失败！");
            e.printStackTrace();
        }
        catch(SQLException e1){
            //数据库连接失败异常处理
            e1.printStackTrace();
        }
        catch(Exception e2){
            e2.printStackTrace();
        }
        finally{
            System.out.println("-------------------------------");
            System.out.println("数据库数据获取成功！");
        }
        return aa;
    }

    public static List<tran_sync> connectco(){
        // 加载数据库驱动  com.mysql.jdbc.Driver
        String driver = "com.mysql.cj.jdbc.Driver";
        // 获取mysql连接地址
        String url = "jdbc:mysql://localhost:3306/graduation";
        // 数据名称
        String username = "root";
        // 数据库密码
        String password = "141500";
        // 获取一个数据的连接
        Connection conn = null;
        List<tran_sync> aa=new ArrayList();
        // 获取连接的一个状态
        try{
            Class.forName(driver);
            //getConnection()方法，连接MySQL数据库！
            conn=DriverManager.getConnection(url,username,password);
            if(!conn.isClosed())
                System.out.println("数据库连接成功！");
            //创建statement类对象，用来执行SQL语句！
            Statement Statement=conn.createStatement();
            //要执行的SQL语句
            String sql="select * from tran_syncs" ;
            //ResultSet类，用来存放获取的结果集！
            ResultSet rs=Statement.executeQuery(sql);


            //String active=null;
            String active;
            String name=null;


            while(rs.next()){
                name=rs.getString("co_sync_name");
                active=rs.getString("type");

                //输出结果
                // System.out.println(name+"\t"+active+"\t"+from+"\t"+to+"\t"+guard+"\t"+sync+"\t"+assignment );
                //dynamic ab=new dynamic(name,active,from,to,guard,sync,assignment);
                tran_sync ab=new tran_sync(name);
                ab.setType(active);
                aa.add(ab);
            }
            rs.close();
            conn.close();
        }
        catch(ClassNotFoundException e){
            //数据库驱动类异常处理
            System.out.println("数据库驱动加载失败！");
            e.printStackTrace();
        }
        catch(SQLException e1){
            //数据库连接失败异常处理
            e1.printStackTrace();
        }
        catch(Exception e2){
            e2.printStackTrace();
        }
        finally{
            System.out.println("-------------------------------");
            System.out.println("数据库数据获取成功！");
        }
        return aa;
    }

    public static List<locations> connectloc(){
        // 加载数据库驱动  com.mysql.jdbc.Driver
        String driver = "com.mysql.cj.jdbc.Driver";
        // 获取mysql连接地址
        String url = "jdbc:mysql://localhost:3306/graduation";
        // 数据名称
        String username = "root";
        // 数据库密码
        String password = "141500";
        // 获取一个数据的连接
        Connection conn = null;
        List<locations> aa=new ArrayList();
        // 获取连接的一个状态
        try{
            Class.forName(driver);
            //getConnection()方法，连接MySQL数据库！
            conn=DriverManager.getConnection(url,username,password);
            if(!conn.isClosed())
                System.out.println("数据库连接成功！");
            //创建statement类对象，用来执行SQL语句！
            Statement Statement=conn.createStatement();
            //要执行的SQL语句
            String sql="select * from locations" ;
            //ResultSet类，用来存放获取的结果集！
            ResultSet rs=Statement.executeQuery(sql);


            //String active=null;

            String name=null;
            String inva=null;
            boolean comm;

            while(rs.next()){
                name=rs.getString("loc_name");
                inva=rs.getString("invar");
                comm=rs.getBoolean("commited");


                //输出结果

                locations ab=new locations(name,comm,inva);
                aa.add(ab);
            }
            rs.close();
            conn.close();
        }
        catch(ClassNotFoundException e){
            //数据库驱动类异常处理
            System.out.println("数据库驱动加载失败！");
            e.printStackTrace();
        }
        catch(SQLException e1){
            //数据库连接失败异常处理
            e1.printStackTrace();
        }
        catch(Exception e2){
            e2.printStackTrace();
        }
        finally{
            System.out.println("-------------------------------");
            System.out.println("数据库数据获取成功！");
        }
        return aa;
    }

    public static List<funcs> connectfunc(){
        // 加载数据库驱动  com.mysql.jdbc.Driver
        String driver = "com.mysql.cj.jdbc.Driver";
        // 获取mysql连接地址
        String url = "jdbc:mysql://localhost:3306/graduation";
        // 数据名称
        String username = "root";
        // 数据库密码
        String password = "141500";
        // 获取一个数据的连接
        Connection conn = null;
        List<funcs> aa=new ArrayList();
        // 获取连接的一个状态
        try{
            Class.forName(driver);
            //getConnection()方法，连接MySQL数据库！
            conn=DriverManager.getConnection(url,username,password);
            if(!conn.isClosed())
                System.out.println("数据库连接成功！");
            //创建statement类对象，用来执行SQL语句！
            Statement Statement=conn.createStatement();
            //要执行的SQL语句
            String sql="select * from funcs" ;
            //ResultSet类，用来存放获取的结果集！
            ResultSet rs=Statement.executeQuery(sql);


            //String active=null;

            String name=null;
            String body=null;
            boolean comm;

            while(rs.next()){
                name=rs.getString("func_name");
                body=rs.getString("func_body");



                //输出结果

                funcs ff=new funcs(name,body);
                aa.add(ff);
            }
            rs.close();
            conn.close();
        }
        catch(ClassNotFoundException e){
            //数据库驱动类异常处理
            System.out.println("数据库驱动加载失败！");
            e.printStackTrace();
        }
        catch(SQLException e1){
            //数据库连接失败异常处理
            e1.printStackTrace();
        }
        catch(Exception e2){
            e2.printStackTrace();
        }
        finally{
            System.out.println("-------------------------------");
            System.out.println("数据库数据获取成功！");
        }
        return aa;
    }

    public static StringBuilder opt_code(String a){
        String[] b=a.split("\n");
        Stack<Character> sc=new Stack<Character>();
        String ss=new String();
        int len=0;
        for(int i=0;i<b.length;i++) {
            ss = b[i];
            if (ss.contains("{")) {
                len=sc.size();

                b[i] = add_tab(ss,len*4);
                sc.push('{');
            }else if (ss.equals("}")){

                sc.pop();
                len = sc.size();
                b[i] = add_tab(ss, len * 4);
            }
            else{
                len=sc.size();
                b[i]=add_tab(ss,len*4);

            }
        }

        StringBuilder sr=new StringBuilder();
        for(int i=0;i<b.length;i++){
            sr.append(b[i]);

        }
        return sr;


    }

    public static String add_tab(String a,int b){
        String ss=new String();
        for(int i=0;i<b;i++){
            ss+=" ";
        }
        a=ss+a+"\n";
        return a;
    }





public static void main(String[] args) {

        starta a=new starta();

        a.show();

      //  start_ana(in,out);
    }
}


