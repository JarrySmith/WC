import java.io.*;
import java.nio.charset.Charset;

/**
 * 可能出现的测试：
 * 1.没有紧跟的情况
 * 2.没有指定保存的目录
 * 3.指定的文件不是.c文件
 */
public class WC {

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
       for(int i=0;i<t.length;i++)
           if(t[i].equals(""))de++;
       return t.length ;

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

    public static int[] countdetail(String fileName) throws IOException{
        File file=new File(fileName);

        if (!file.exists() || file.isDirectory())
        throw new FileNotFoundException();
        int emptyline=0;
        int codeline=0;
        int expline=0;
        BufferedReader br=new BufferedReader(new FileReader(file));
        String temp=null;
        StringBuffer sb=new StringBuffer();
        temp=br.readLine();
        while(temp!=null){

            temp=br.readLine();
        }
    return null ;
    }
    public static void saveresult(String savepath,int words,int lines,int chars){
      // 根据参数来存取 基本功能存取三项
        //追加写入文件
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("a.c", true)));
            out.write("s"+"\r\n");
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
//        System.out.print(args.length);
        int words=0;
        int lines=0;
        int chars=0;
        System.out.println(is_emptyline("{    \r\n\t"));

        CharSequence cfile = ".c";
        CharSequence save=".txt";
        String path=null;
        String save_path=null;
        for (int i=0;i<args.length;i++)
        {
           if(args[i].contains(cfile)) path=args[i];
           //停用词的文件判断
            if(args[i].equals("-e")){
                if (!(args[i+1].contains(save))) System.out.println("result.txt需紧跟在-e后");
                else save_path=args[i+1];
            }
//           if(args[i].contains(save)) save_path=args[i];
           if(args[i].equals("-o")){
               if (!(args[i+1].contains(save))) System.out.println("result.txt需紧跟在-o后");
               else save_path=args[i+1];
           }
        }
        if(null==path) System.out.println("只能接受C文件的文本");
        for (int i=0;i<args.length;i++)
        {
            if(args[i]=="-c") chars=countchars(path);
            if(args[i]=="-l") lines=countlines(path);
            if(args[i]=="-w") words=count_word(path);
            if(args[i]=="-o") saveresult(save_path,words,lines,chars);
        }


//        String path = "a.c";
//        String text = null;
//        text = readToString(path);
//        System.out.print(countlines(path));
//        System.out.print(count_word(path));
//        System.out.print(countchars(path));
 


    }
}
