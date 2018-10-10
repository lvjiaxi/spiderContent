package org;

public class Bracket {
    public static String BracketChineseToEnglish(String chapterName) {
        StringBuffer s =new StringBuffer(chapterName);
        System.out.println(s.indexOf("("));
        System.out.println(s.indexOf(")"));
        String[] cnNumber={"零","壹","贰","叁","肆","伍","陆","柒","捌","玖","一","二","三","四","五","六","七","八","九"};
        int sshi=0;
        int ge=0;
        int num=0;
        String shi="十";
        System.out.println(s.lastIndexOf("十"));
        if (s.lastIndexOf(")")-s.lastIndexOf("(")<=4&&s.lastIndexOf(")")!=-1&&s.lastIndexOf("(")!=-1){
            if (s.lastIndexOf("十")>s.lastIndexOf("(")&&s.lastIndexOf("十")<s.lastIndexOf(")")){
                int ss=s.lastIndexOf(shi)-1;
                for (int i = 0; i < cnNumber.length; i++) {
                    if (cnNumber[i].equals(s.substring(ss, ss + 1))){
                        if (i>=10){
                            sshi=i-9;
                            num=num+sshi*10;
                        }else {
                            num=num+i*10;
                        }
                    }
                }
            }
            if (s.lastIndexOf(")")-1>s.lastIndexOf("(")){
                int g=s.lastIndexOf(")")-1;
                for (int i = 0; i < cnNumber.length; i++) {
                    if (cnNumber[i].equals(s.substring(g, g + 1))){
                        if (i>=10){
                            ge=i-9;
                            num=num+ge;
                        }else {
                            num=num+i;
                        }
                    }
                }
            }
            String n= String.valueOf(num);
            s.replace(s.lastIndexOf("(")+1,s.lastIndexOf(")"),n);
            System.out.println(s);

        }
        String q= String.valueOf(s);
        return q;
    }
}
