import java.io.*;
import java.nio.charset.Charset;

/**
 * 可能出现的测试：
 * 1.没有紧跟的情况
 * 2.没有指定保存的目录
 * 3.指定的文件不是.c文件
 * 4.-o 为最后一个 没接的话可能越界
 * 5.命令行
 */
public class WC {

    public static String save_path="result.txt";
    public static String stop_path="stop.txt";
    public static  StringBuilder string_args=new StringBuilder();
    public static  String param="-a";
    public static boolean err = false;
    public static int countchars(String filename){
        String text = readToString(filename);
        char[]ch = text.toCharArray();
        int decrese=0;
        for(int i=0;i<ch.length;i++)
        {
            if (ch[i]=='\n'||ch[i]=='\r') decrese++;
        }

        return ch.length-decrese;
    }
    public static int countlines(String fileName){
        File file = new File(fileName);
        BufferedReader reader = null;
        int line=0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    return line-1;
    }
    public static void readFileByLines(String fileName) {

        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
    public static int count_word (String fileName)
    {
       String[]replace_part={" ",",","\t"};
       String text=null;
       text = readToString(fileName);
       text = text.replaceAll("\r\n", ".");
       for(int i=0;i<replace_part.length;i++)
       {
           try {
               text =text.replaceAll(replace_part[i],".");
           }
          catch (NullPointerException e){
              System.out.println(e);
          }
       }
        System.out.println(text);
       String[]t=text.split("\\.");
       int de=0;
       String []stopword = null;
       if(param.contains("-e"))stopword= get_stopword(stop_path);
       if(null==stopword){
       for(int i=0;i<t.length;i++)
       {if(t[i].equals("")) de++;}
       }
       else {
           for(int i=0;i<t.length;i++)
           {
               if(t[i].equals("")){de++;continue;}
               for(int j=0;j<stopword.length;j++)
               {
                   if (t[i].equals(stopword[j]))de++;
               }
             }
       }
       return t.length-de ;

    }
    public static boolean is_emptyline(String text)
    {
        char[]ch = text.toCharArray();
        boolean flag = false;
        int chars=0;
        for(int i=0;i<ch.length;i++){
                if(ch[i]==' '|| ch[i]=='\t'||ch[i]=='\n'||ch[i]=='\r') continue;
                else chars++;
        }
        if(chars<=1)flag=true;
        return flag;
    }
    public static boolean is_expline(String text){
        char[]ch = text.toCharArray();
        int word =0;
        boolean flag=false;
        for(int i=0;i<ch.length;i++)
        {
            if (ch[i]=='/'&& ch[i+1]=='/'&& i+1<=ch.length-1){
                if(word<=1) {
                    flag = true;
                    break;
                }
            }
                else    word++;
        }
        return flag;
    }
    public static int[] countdetail(String fileName) throws IOException{
        File file=new File(fileName);

        if (!file.exists() || file.isDirectory())
        throw new FileNotFoundException();
        int emptyline=0;
        int codeline=0;
        int expline=0;
        boolean multiexp =false;
        CharSequence exp_start = "/**";
        CharSequence exp_end = "*/";
        BufferedReader br=new BufferedReader(new FileReader(file));
        String temp=null;
        StringBuffer sb=new StringBuffer();
        temp=br.readLine();
        while(temp!=null){
            if(multiexp) expline++;
            if(temp.contains(exp_start))multiexp=true;
            if(temp.contains(exp_end)){expline++;multiexp=false;temp=br.readLine();continue;}
            if(!multiexp)
            {
                if(is_expline(temp))expline++;
                else if(is_emptyline(temp))emptyline++;
                else codeline++;}

            temp=br.readLine();
        }
        int[]result={codeline,emptyline,expline};
    return result ;
    }
    public static void traverseFolder2(String path){
        int count=0;
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
//                System.out.println("文件夹是空的!");
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
//                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolder2(file2.getAbsolutePath());
                    } else {
                        String filename = file2.getName();


                        if(filename.endsWith(".c")){
                            count++;
                            System.out.println(count);
                            String filepath = path + '\\'+filename;
//                            System.out.println("文件路径:" + filepath);
                            //如果有.c文件
                            int words=0;
                            int lines=0;
                            int chars=0;
                            int []detail = new int[3];
                            try {
                                if(null==param) break;
                                if(param.contains("-a")) detail = countdetail(filepath);
                                if(param.contains("-w")) words=count_word(filepath);
                                if(param.contains("-c")) chars=countchars(filepath);
                                if(param.contains("-l")) lines= countlines(filepath);
                                 saveresult(words,lines,chars,detail,filename);
                            }
                        catch (IOException e){
                                e.printStackTrace();
                        }
                        }
//                        System.out.println("文件:" + file2.getName());

                    }
                }
            }
        }
    }
    public static void WC(String []args){
        int words=0;
        int lines=0;
        int chars=0;
        int[]detail = new int[3];

        CharSequence save=".txt";
        String filepath=null;


        for (int i=0;i<args.length;i++)
        {
           if(args[i].endsWith(".c")) {filepath =args[i];continue;}
           if(args[i].contains(".txt")) continue;
           //停用词的文件判断
            if(args[i].equals("-e")){
                if(i==args.length-1){ System.out.println("没有指定停用词文本");err=true;}
                else if (!(args[i+1].contains(save))) System.out.println("停用词文本需紧跟在-e后");
                else stop_path=args[i+1];
            }

           if(args[i].equals("-o")){
               if(i==args.length-1) {System.out.println("没有指定输出结果文本");err=true;}
               else if (!(args[i+1].contains(save))) System.out.println("result.txt需紧跟在-o后");
               else save_path=args[i+1];
           }
           string_args.append(args[i]);
        }
        param = string_args.toString();
//        System.out.println(param);
        if(!err){
        for (int i=0;i<args.length;i++)
        {
            if(args[i].equals("-s")) {String path =  System.getProperty("user.dir");traverseFolder2(path);break;}
           if(null!=filepath)
           { if(args[i].equals("-c")) chars=countchars(filepath);
               if(args[i].equals("-l")) lines=countlines(filepath);
               if(args[i].equals("-w")) words=count_word(filepath);
            try {
                if(args[i].equals("-a")) detail=countdetail(filepath);
          }
            catch (IOException e){
              e.printStackTrace();
            }
            }
        }
        saveresult(words,lines,chars,detail,filepath);
        }
    }
    public static String[] get_stopword(String fileName){
        String text=null;
        text = readToString(fileName);
        text = text.replaceAll("\r\n", ".");
        String [ ]temp = text.split("\\.");
        return temp;
    }
    public static void saveresult(int words,int lines,int chars,int[]detail,String filename){
      // 根据参数来存取 基本功能存取三项
        //追加写入文件

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(save_path, true)));
            //判断要写入哪些信息
            if(chars>=1) out.write(filename+","+"字符数:"+ chars+"\r\n");
            if(words>=1) out.write(filename+","+"单词数:"+ words+"\r\n");
            if(lines>=1) out.write(filename+","+"行数:"+ lines+"\r\n");
            if(param.contains("-a")&&null!=filename) out.write(filename+","+"代码行/空行/注释行:"+ detail[0]+"/"+ detail[1]+"/"+ detail[2]+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void  main(String[]args)
    {
        if(args.length==0)System.out.println("no args");
        String path = System.getProperty("user.dir");

        WC(args);
    }

}
