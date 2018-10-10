package org;

public class CompareChineseToEnglish {

    public static String ChineseToEnglish(String chapterName){

        StringBuffer s = new StringBuffer(chapterName);
        String wan="万";
        String qian="千";
        String bai="百";
        String shi="十";
        int swan=0;
        int sqian=0;
        int sbai=0;
        int sshi=0;
        int sge=0;
        int num=0;
//        String yi="一";
//        String er="二";
//        String san="三";
//        String si="四";
//        String wu="五";
//        String liu="六";
//        String qi="七";
//        String ba="八";
//        String jiu="九";
        String[] cnNumber={"零","壹","贰","叁","肆","伍","陆","柒","捌","玖","一","二","三","四","五","六","七","八","九"};
        //0    1    2   3    4    5   6    7    8   9   10

        if (s.indexOf(wan)<s.indexOf("章")&&s.indexOf(wan)!=-1){
            int w=s.indexOf(wan)-1;
            for (int i = 0; i <cnNumber.length ; i++) {
                if (cnNumber[i].equals(s.substring(w, w + 1))){
                    if (i>=10){
                        swan=i-9;
                        num=swan*10000;
                    }else {
                        num=i*10000;
                    }
                }
            }
            System.out.println(num);
        }


        if (s.indexOf(qian)<s.indexOf("章")&&s.indexOf(qian)!=-1){
            int q=s.indexOf(qian)-1;
            for (int i = 0; i < cnNumber.length; i++) {
                if (cnNumber[i].equals(s.substring(q, q + 1))){
                    if (i>=10){
                        sqian=i-9;
                        num=num+sqian*1000;
                    }else {
                        num=num+i*1000;
                    }
                }

            }
            System.out.println(num);
        }

        if (s.indexOf(bai)<s.indexOf("章")&&s.indexOf(bai)!=-1){
            int b=s.indexOf(bai)-1;
            for (int i = 0; i < cnNumber.length; i++) {
                if (cnNumber[i].equals(s.substring(b, b + 1))){
                    if (i>=10){
                        sbai=i-9;
                        num=num+sbai*100;
                    }else {
                        num=num+i*100;
                    }
                }

            }
            System.out.println(num);
        }

        if (s.indexOf(shi)<s.indexOf("章")&&s.indexOf(shi)!=-1){
            int ss=s.indexOf(shi)-1;
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
            System.out.println(num);
        }
        if (s.indexOf("章")-1>s.indexOf(shi)||s.indexOf("章")-1>0){
            int g=s.indexOf("章")-1;
            for (int i = 0; i < cnNumber.length; i++) {
                if (cnNumber[i].equals(s.substring(g, g + 1))){
                    if (i>=10){
                        sge=i-9;
                        num=num+sge;
                    }else {
                        num=num+i;
                    }
                }
            }
        }
        System.out.println(num);
        String n= String.valueOf(num);
        if (s.indexOf(shi)!=-1||s.indexOf(bai)!=-1||s.indexOf(qian)!=-1||s.indexOf(wan)!=-1||s.indexOf("章")-1>0){
            s.replace(0,s.indexOf("章")+1,n);
        }



        String replace= String.valueOf(s);
        return replace;
    }
}

