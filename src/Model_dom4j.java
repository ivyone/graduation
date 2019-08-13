
import java.io.File;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Model_dom4j {
    //private List<tran_sync> tranlist = new ArrayList<tran_sync>();
    private List<locations> loclist = new ArrayList<locations>();
    private List<dynamic> dyna = new ArrayList<dynamic>();
    private List<String> inital = new ArrayList<String>();
    private  List<String> temp_input=new ArrayList<>();
    private  String  decla_str=new String();
    private String str_to_delete=new String();
    private List<funcs>  decla_template=new ArrayList<>();

    public List<funcs> Get_decla_template(){
        return decla_template;
    }
    public String Get_decla_str()
    {
        return decla_str;
    }
    public String Get_str_to_delete(){
        return str_to_delete;
    }

    public List<locations> Get_loclist(){
        return loclist;
    }
    public List<dynamic> Get_dyna(){
        return dyna;
    }

    public void xml(String in) {

        SAXReader reader = new SAXReader();
        Document document = null;

        try {
            document = reader.read(new File(in));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //获取整个文档
        Element root = document.getRootElement();

        //解析declaration
        String secla = root.element("declaration").getText();
        List<String> input_useless=new ArrayList<>();
        decla_str=handle_declaration(secla,input_useless);
       // handle_tran_syncs(secla);
        System.out.println(decla_str);


        List tem = root.elements("template");
       
        for (int i = 0; i < tem.size(); i++) {
            Element ele = (Element) tem.get(i);
            Element ini = (Element) ele.element("init");
            String ina = ini.attributeValue("ref");

            inital.add(ina);
        }
       for (int i = 0; i < tem.size(); i++) {
            Element ele = (Element) tem.get(i);
            List<Element> loc = ele.elements("location");
            handle_location(loc);
        }
        write_loc();

        for (int i = 1; i < tem.size(); i++) {

            Element ele = (Element) tem.get(i);
            String template_name=ele.element("name").getText();
            List<Element> dyn = ele.elements("transition");
            handle_dynamic(dyn,template_name);
        }
        write_dyna();


        for (int i = 0; i < tem.size(); i++) {
            Element ele = (Element) tem.get(i);
            String dec = null;
            List<String>  input=new ArrayList<>();
            String template_name=ele.element("name").getText();
            //ele.element("declaration").getText();
            if (ele.element("declaration") != null) {
                funcs fun=new funcs();
                dec = ele.element("declaration").getText();
               dec= handle_declaration(dec,input);
               fun.setBody(dec);
               fun.setTemplate(template_name);
               fun.setInput(input);
               decla_template.add(fun);
               System.out.println(dec);
            } else {

            }

        }
      //  write_dec();
    }

    /*void write_dec() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//加载数据库驱动
            System.out.println("加载数据库驱动成功");
            String url = "jdbc:mysql://localhost:3306/graduation";//声明数据库test的url
            String user = "root";//数据库的用户名
            String password = "141500";//数据库的密码
            //建立数据库连接，获得连接对象conn(抛出异常即可)
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("连接数据库成功");
            for (int i = 0; i < fun.size(); i++) {
                funcs tt = new funcs();
                tt = fun.get(i);

                //生成一条mysql语句
                String sql = "insert into funcs(func_name,func_body) values('" + tt.getName() + "','" + tt.getBody() + "')";
                Statement stmt = conn.createStatement();//创建一个Statement对象
                stmt.executeUpdate(sql);//执行sql语句


                System.out.println("插入到数据库成功");
            }
            conn.close();
            System.out.println("关闭数据库成功");
        } catch (ClassNotFoundException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }//
        catch (SQLException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }


    }
    */

    String func_handle_array(String a){

        if(a.contains("[")){


            String[] b=a.split(" ");
            int spot1=b[2].indexOf('[');
            int spot2=b[2].indexOf(']');
            String sa=b[2].substring(spot1,spot2+1);
            String sb=b[2].substring(0,spot1);
            String res;
            res=b[0]+ " "+b[1]+sa+" memory "+sb+") public {\n";
            return res;
        }
        return a+"\n";
    }

    String defin_handle_array(String a){
        String[] b=a.split(" ");
        if(a.contains("];")&&b.length>1){

            int spot1=b[b.length-1].indexOf('[');
            int spot2=b[b.length-1].indexOf(']');
            String sb=b[b.length-1].substring(0,spot1);
            String sa=b[b.length-1].substring(spot1,spot2+1);
            String res;
            res=b[0]+sa;
            for(int i=1;i<b.length;i++){
                if(i==b.length-1)
                    res+=" "+sb+";\n";
                else
                    res+=" "+b[i];
            }
            return res;
        }
        return a+"\n";
    }

   String handle_declaration(String a,List<String> input) {
       temp_input.clear();
        String[] b = a.split("\n");
        String c, f,ed;
        String[] d;
        funcs ttq = new funcs();
        List<String> ss = Arrays.asList(b);
        String bb = new String();
        int j = -1;
        for (int i = 1; i < b.length; i++) {
            c = b[i];
            d=c.split(" ");
            if(c.contains("mapping")){
                String temp="mapping(";
                String[] edd;
                int jj=i;
                edd=b[++jj].split(" ");
                temp+=edd[1].substring(0,edd[1].length()-1)+" => ";
                edd=b[++jj].split(" ");
                str_to_delete="."+edd[1].substring(0,edd[1].length()-1);
                temp+=edd[0]+") public ";
                ++jj;

                String tempa=b[++jj];
                int spot1=tempa.indexOf(' ');
                int spot2=tempa.indexOf('[');
                temp+=tempa.substring(spot1,spot2)+";\n";
                i=jj;
                temp=temp.replace("int","uint");
                bb+=temp;
            }else if(c.contains("output")||c.contains("chan")||(c.contains("this")&&c.contains("int"))||c.contains("return")||(c.contains("address(0)")&&c.contains("int")))
                continue;
            else  if(c.contains("input")){
                int spot=c.indexOf("input");
                int spot2;
                f=c.substring(0,spot);
                if(f.contains("//")){
                    spot=f.indexOf("/");
                    spot2=f.indexOf(";");
                    ed=f.substring(spot+2);
                    f=f.substring(0,spot2);
                    if(ed.length()>0&&!ed.equals(" "))
                    f=f.replace("int",ed);
                    f=f.replace("int","uint");
                     input.add(f);
                }
            }
            else if(c.contains("struct")&&!c.contains("construct")){
                String struct_s="struct ",temp;

                for(j=i+1;j<b.length;j++)
                    if(b[j].contains("}"))
                        break;
                 struct_s+=b[j].substring(1,b[j].length()-1)+"{\n";
                 int jj;
                 for(jj=i+1;jj<j;jj++){
                     temp=b[jj];
                     if(temp.contains("//")){
                         int spot = temp.indexOf('/');
                         f = temp.substring(spot + 2);
                         ed = temp.replace("int", f);
                         spot=ed.indexOf('/');
                         ed=ed.substring(0,spot);
                         struct_s+=ed+"\n";
                     }else
                         struct_s+=temp+"\n";
                 }
                 struct_s+="}\n";
                 struct_s=struct_s.replace("int","uint");
                 bb+=struct_s;
                 i=j;
            }else if(c.contains("void")&&c.contains("{")&&c.contains("//")){   //是函数并且无返回值并且有注释


                     int spot2 = c.indexOf("/");
                     String replacement = c.substring(spot2 + 2);
                     String temp;

                     temp = "function ";
                     temp += d[1].replace("int", replacement)+" ";
                     temp += d[2].substring(0, d[2].length() - 1) + " public {";
                     temp=temp.replace("int","uint");
                     temp=func_handle_array(temp);
                     bb += temp + "\n";



            }else if(d[0].equals("int")&&c.contains("{")){   //是函数并且有返回值
                String temp="function ";
                 if(c.contains("//")){
                     int spot2 = c.indexOf("/");
                     String replacement = c.substring(spot2 + 2);

                     temp += d[1].replace("int", replacement)+" ";
                     temp += d[2].substring(0, d[2].length() - 1) + " public view ";
                 }else
                     for(j=1;j<d.length;j++) {
                         if(j==d.length-1)
                         temp += d[j].substring(0, d[j].length() - 1) + " public view ";
                         else
                             temp+=d[j];
                     }
                  for(j=i+1;j<b.length;j++) {
                      if(b[j].contains("//return")){
                          int spot=b[j].indexOf(";");
                          temp+="returns("+b[j].substring(0,spot)+"){\n";
                      }
                  }
                temp=temp.replace("int","uint");
                  bb+=temp;

            }
            else if(c.contains("//")) {

                int spot = c.indexOf('/');
                f = c.substring(spot + 2);
                d=f.split(" ");
                String notes=d[0];
                String temp;
                if(spot==0){
                    f=c.substring(2);
                    bb+=f+"\n";
                }else {
                    if (d.length == 1 && (notes.equals("public") || notes.equals("storage"))) {
                        d = c.split(" ");
                        temp = d[0] + " " + notes + " " + d[1] + "\n";
                        temp = defin_handle_array(temp);
                        bb += temp;
                    } else {

                        ed = c.replace("int", f);
                      //  ed=c.replace("clock",f);
                        spot = ed.indexOf('/');
                        ed = ed.substring(0, spot);
                        ed = defin_handle_array(ed);
                        bb += ed;
                    }
                }
            }
            else if(c.contains("this")&&!c.contains("int")){
                c=c.replace("this","msg.sender");
                bb+=c+"\n";
            }
            else{
                c=c.replace("int","uint");
                int spot=c.indexOf(str_to_delete);


                while ((spot!=-1&&!str_to_delete.equals(""))&&c.length()>0) {
                    //String str_delete="\\"+str_to_delete;
                   // c = c.replaceFirst(str_delete, "");
                    if(spot+str_to_delete.length()==c.length())
                        c=c.substring(0,spot);
                    else if(c.charAt(spot+str_to_delete.length())=='.')
                    c=delete_str_for_mapping(spot,c);
                    else
                        break;
                    spot=c.indexOf(str_to_delete);
                }
                c=defin_handle_array(c);
                bb+=c;
            }




        }
        bb.replace("int","uint");
        return  bb;


    }

    String delete_str_for_mapping(int spot,String cs){
        String d=new String();
        d=cs.substring(0,spot);
        d+=cs.substring(spot+str_to_delete.length());
        return d;

    }


    void write_loc() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//加载数据库驱动
            System.out.println("加载数据库驱动成功");
            String url = "jdbc:mysql://localhost:3306/graduation";//声明数据库test的url
            String user = "root";//数据库的用户名
            String password = "141500";//数据库的密码
            //建立数据库连接，获得连接对象conn(抛出异常即可)
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("连接数据库成功");
            String dele="delet From locations";
            Statement stmt = conn.createStatement();//创建一个Statement对象
            stmt.executeUpdate(dele);
            for (int i = 0; i < loclist.size(); i++) {
                locations tt = new locations();
                tt = loclist.get(i);
                String ch = null;
                if (tt.isCommited()) ch = "1";
                else ch = "0";
                //生成一条mysql语句

                String sql = "insert into locations(loc_id,loc_name,commited) values('" + tt.getId() + "','" + tt.getName() + "'," + ch + ")";

                stmt.executeUpdate(sql);//执行sql语句
                if (tt.getInva() != null) {
                    sql = "update locations set invar = '" + tt.getInva() + "' where loc_id= '" + tt.getId() + "'";
                    stmt.executeUpdate(sql);
                }

                //System.out.println("插入到数据库成功");
            }
            conn.close();
            System.out.println("关闭数据库成功");
        } catch (ClassNotFoundException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }//
        catch (SQLException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

    }


    void handle_location(List a) {


        for (int i = 0; i < a.size(); i++) {
            Element ele = (Element) a.get(i);
            locations lla = new locations();
            String name = ele.element("name").getText();
            String id = ele.attributeValue("id");
            Element com = ele.element("committed");
            lla.setName(name);
            lla.setId(id);
            if (com == null)
                lla.setCommited(false);
            else
                lla.setCommited(true);
            Element invae = ele.element("label");
            if (invae != null) {
                String inva = invae.getText();
                if (inva.indexOf("&gt") != -1)
                    inva.replace("&gt", ">=");
                else if (inva.indexOf("&lt") != -1) {
                    inva.replace("&lt", "<=");
                }
                lla.setInva(inva);
            }
            loclist.add(lla);


        }


    }

    public String loc_loctions(String a) {
        for (int i = 0; i < loclist.size(); i++) {
            locations sa = loclist.get(i);
            if (a.equals(sa.getId()))
                return sa.getName();
        }
        return null;
    }

    public void handle_dynamic(List a,String template) {


        for (int i = 0; i < a.size(); i++) {
            Element ele = (Element) a.get(i);
            dynamic lla = new dynamic();
            Element tes = ele.element("source");
            String from = tes.attributeValue("ref");
            lla.setTemplate(template);
            for (int k = 0; k < inital.size(); k++) {
                String uin = inital.get(k);
                if (uin.equals(from)) {
                    lla.setActive(true);
                    break;
                }
            }
            from = loc_loctions(from);
            lla.setFrom(from);

            tes = ele.element("target");
            String to = tes.attributeValue("ref");
            to = loc_loctions(to);
            lla.setTo(to);
            List lab = ele.elements("label");
            String lname = "e" + Integer.toString(dyna.size());
            lla.setName(lname);
            for (int j = 0; j < lab.size(); j++) {
                Element laa = (Element) lab.get(j);
                String kind = laa.attributeValue("kind");
                String ua = null;
                if (kind.equals("guard")) {
                    ua = laa.getText();
                    if (ua.indexOf("&gt") != -1)
                        ua.replace("&gt", ">=");
                    else if (ua.indexOf("&lt") != -1)
                        ua.replace("&lt", "<=");
                    lla.setGuard(ua);
                }
                if (kind.equals("synchronisation")) {
                    ua = laa.getText();
                    lla.setSync(ua);
                }
                if (kind.equals("assignment")) {
                    ua = laa.getText();
                    ua.replace(":", "");
                    lla.setAssignment(ua);
                }


            }
            dyna.add(lla);

        }


    }

    void write_dyna() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//加载数据库驱动
            System.out.println("加载数据库驱动成功");
            String url = "jdbc:mysql://localhost:3306/graduation";//声明数据库test的url
            String user = "root";//数据库的用户名
            String password = "141500";//数据库的密码
            //建立数据库连接，获得连接对象conn(抛出异常即可)
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("连接数据库成功");
            Statement stmt = conn.createStatement();//创建一个Statement对象
            String dele="delete from transitions";
            stmt.executeUpdate(dele);
            for (int i = 0; i < dyna.size(); i++) {
                dynamic tt = new dynamic();
                tt = dyna.get(i);
                String ch = null;
                if (tt.isActive()) ch = "1";
                else ch = "0";
                //生成一条mysql语句

                String sql = "insert into transitions(namea,activea,froma,toa,template,visited) values('" + tt.getName() + "'," + ch + ",'" + tt.getFrom() + "','" + tt.getTo() + "','"+tt.getTemplate()+"',0);";
                //Statement stmt = conn.createStatement();//创建一个Statement对象
                stmt.executeUpdate(sql);//执行sql语句
                if (tt.getGuard() != null) {
                    String sqla = "update transitions set guarda = '" + tt.getGuard() + "' where namea= '" + tt.getName() + "'";
                    stmt.executeUpdate(sqla);//执行sql语句
                }
                if (tt.getSync() != null) {
                    String sqla = "update transitions set synca = '" + tt.getSync() + "' where namea= '" + tt.getName() + "'";
                    stmt.executeUpdate(sqla);//执行sql语句
                }
                if (tt.getAssignment() != null) {
                    String sqla = "update transitions set assignmenta = '" + tt.getAssignment() + "' where namea= '" + tt.getName() + "'";
                    stmt.executeUpdate(sqla);//执行sql语句
                }
               // System.out.println("插入到数据库成功");
            }
            conn.close();
            System.out.println("关闭数据库成功");
        } catch (ClassNotFoundException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }//
        catch (SQLException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();


        }
    }
}