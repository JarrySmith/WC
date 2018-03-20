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

    public static String save_path = "result.txt";
    public static String stop_path = "stoplist.txt";
    public static StringBuilder string_args = new StringBuilder();
    public static String param = "-a";
    public static String filetype = null;
    public static boolean err = false;

    public static int countchars(String filename) {
        String text = readToString(filename);
        char[] ch = text.toCharArray();
        int decrese = 0;
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == '\n' || ch[i] == '\r') decrese++;
        }

        return ch.length - decrese;
    }

    public static int countlines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
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
        return line - 1;
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

    public static int count_word(String fileName) {
        String[] replace_part = {" ", ",", "\t"};
        String text = null;
        text = readToString(fileName);
        text = text.replaceAll("\r\n", ".");
        for (int i = 0; i < replace_part.length; i++) {
            try {
                text = text.replaceAll(replace_part[i], ".");
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
//        System.out.println(text);
        String[] t = text.split("\\.");
        int de = 0;
        String[] stopword = null;
        if (param.contains("-e")) stopword = get_stopword(stop_path);
        if (null == stopword) {
            for (int i = 0; i < t.length; i++) {
                if (t[i].equals("")) de++;
            }
        } else {
            for (int i = 0; i < t.length; i++) {
                if (t[i].equals("")) {
                    de++;
                    continue;
                }
                for (int j = 0; j < stopword.length; j++) {
                    if (t[i].equals(stopword[j])) de++;
                }
            }
        }
        return t.length - de;

    }

    public static boolean is_emptyline(String text) {
        char[] ch = text.toCharArray();
        boolean flag = false;
        int chars = 0;
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == ' ' || ch[i] == '\t' || ch[i] == '\n' || ch[i] == '\r') continue;
            else chars++;
        }
        if (chars <= 1) flag = true;
        return flag;
    }

    public static boolean is_expline(String text) {
        char[] ch = text.toCharArray();
        int word = 0;
        boolean flag = false;
//        System.out.println(ch);
        for (int i = 0; i < ch.length; i++) {
            if(i<ch.length-1){
            if (ch[i] == '/' && ch[i + 1] == '/' && i + 1 <= ch.length - 1) {
                if (word <= 1) {
                    flag = true;
                    break;
                }}
              else  if (ch[i] == '/' && ch[i + 1] == '*' && i + 1 <= ch.length - 1) {
                    if (word <= 1) {
                        flag = true;
                        break;
                    }
                }
             else if(ch[i] == ' ' || ch[i] == '\t' )continue;else word++;}
             else if(!(ch[i] == ' ') && !(ch[i] == '\t') ) word++;
        }
        return flag;
    }

    public static int[] countdetail(String fileName) throws IOException {
        File file = new File(fileName);

        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        int emptyline = 0;
        int codeline = 0;
        int expline = 0;
        boolean multiexp = false;
        CharSequence exp_start = "/*";
        CharSequence exp_end = "*/";
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();
        while (temp != null) {
            //多行注释
            if (temp.contains(exp_start)&&temp.contains(exp_end)) {expline++ ;temp=br.readLine();continue;}
            if(temp.contains(exp_start)&&!temp.contains(exp_end)) {multiexp=true;expline++;}
            if (multiexp&&!temp.contains("/*")&&!temp.contains("*/")) expline++;
            if (temp.contains(exp_end)&&multiexp) {
                if (temp.charAt(temp.length()-1)=='/')
                {  expline++;
                multiexp = false;
                temp = br.readLine();
                continue;}
                else multiexp=false;
            }
            //单行注释
            if (!multiexp) {
                if (is_expline(temp)) expline++;
                else if (is_emptyline(temp)) emptyline++;
                else codeline++;
            }

            temp = br.readLine();
        }
        int[] result = {codeline, emptyline, expline};
        return result;
    }

    public static void traversepath(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            //如果是个空文件夹
            if (files.length == 0) {
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        //遍历子目录
                        traversepath(file2.getAbsolutePath());
                    } else {
                        String filename = file2.getName();
                        if (filename.endsWith(filetype)) {
                            String filepath = path + '\\' + filename;
                            int words = 0;
                            int lines = 0;
                            int chars = 0;
                            int[] detail = new int[3];
                            //根据参数进行统计
                            try {
                                if (null == param) break;
                                if (param.contains("-a")) detail = countdetail(filepath);
                                if (param.contains("-w")) words = count_word(filepath);
                                if (param.contains("-c")) chars = countchars(filepath);
                                if (param.contains("-l")) lines = countlines(filepath);
                                saveresult(words, lines, chars, detail, filename);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void WC(String[] args) {
        int words = 0;
        int lines = 0;
        int chars = 0;
        int[] detail = new int[3];

        CharSequence save = ".txt";
        String filepath = null;

        boolean checked_file = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains(".txt")) continue;
            //停用词的文件判断
            if (args[i].equals("-e")) {
                if (i == args.length - 1) {
                    System.out.println("没有指定停用词文本");
                    err = true;
                } else if (!(args[i + 1].contains(save))) System.out.println("停用词文本需紧跟在-e后");
                else stop_path = args[i + 1];
            }
            //判断是否函数输出文本
            if (args[i].equals("-o")) {
                if (i == args.length - 1) {
                    System.out.println("没有指定输出结果文本");
                    err = true;
                } else if (!(args[i + 1].contains(save))) System.out.println("result.txt需紧跟在-o后");
                else save_path = args[i + 1];
            }
            string_args.append(args[i]);
        }
        param = string_args.toString();
        if (!err) {
            if (param.contains("-s")) {
                //默认path为当前目录
                String path =  System.getProperty("user.dir");
                for (int i = args.length - 1; i > 0; i--) {
                    if (args[i].contains("*.")) {
                        //获取指定文件类型和指定目录
                        if(args[i].length()>15) path= args[i].split("\\.")[0].replace("*","");
                        filetype = "." + args[i].split("\\.")[1];
                    }
                }
                //开始遍历目录
                traversepath(path);
            } else {
                //错误判断
                for (int i = 0; i < args.length; i++) {
                    if (args[i].contains("*.")){err=true;System.out.println("只有输入-s才能进行全目录遍历规定文件");}
                    if (args[i].contains(".")) {
                        filepath = args[i];
                        break;
                    }
                }
            if(!err)
            {
                //及非遍历统计
            if (param.contains("-c")) chars = countchars(filepath);
            if (param.contains("-l")) lines = countlines(filepath);
            if (param.contains("-w")) words = count_word(filepath);
            try {
                if (param.contains("-a")) detail = countdetail(filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }
    }
        saveresult(words,lines,chars,detail,filepath);
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
//        String []a = {"-a","a.c","-o","a.txt"};
//        WC(a);
//        if(args.length==0)System.out.println("no args");
        String path = System.getProperty("user.dir");
        WC(args);
    }

}
