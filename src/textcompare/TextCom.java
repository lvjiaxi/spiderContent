package textcompare;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class fileInfo {

    static final int MAXLINECOUNT = 40000;

    List list;
    public int maxLine;/* After input done, # lines in file.  */
    node symbol[]; /* The symtab handle of each line. */
    int other[]; /* Map of line# to line# in other file */
    private String reg = "(?is)([^\\s]+)";

    fileInfo( String content ) {
        symbol = new node [ MAXLINECOUNT+2 ];
        other  = null;// allocated later!
        content = content == null ? "" : content;
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(content);
        list = new ArrayList<String>();
        int i=0;
        int max = MAXLINECOUNT - 2;
        while (m.find()) {
            if (m.groupCount() >= 1) {
                String s = m.group(1);
                if(i==max)break;
                list.add(s);
                i++;
            }
        }
        m = null; p = null;
// try {
// file = new DataInputStream(
// new FileInputStream( filename));
// } catch (IOException e) {
//  System.err.println("Diff can't read file " +
// filename );
//  System.err.println("Error Exception was:" + e );
//  System.exit(1);
// }
    }
    void alloc() {
        other  = new int[symbol.length + 2];
    }
};

public class TextCom {

    final int UNREAL=Integer.MAX_VALUE;

    fileInfo oldinfo, newinfo;

    int blocklen[];

    int samnums = 0;

    int dif = 0;

    public String getClear(String str){
        String reg = "[^\u4e00-\u9fa5]"; // 不要试图　修改所有非中文字符
        str = str.replaceAll(reg, " ");
        str = str.replaceAll(" +", " ");
        return str;
    }
    public static void main(String argstrings[]) throws IOException
    {
// if ( argstrings.length != 2 ) {
//  System.err.println("Usage: diff oldfile newfile" );
//  System.exit(1);
// }
        TextCom d = new TextCom();
// String s1 = BBFile.readFile("E:/1.html", "GBK");
// String s2 = BBFile.readFile("E:/2.html", "GBK");
// s1 = d.getClear(s1);
// s2 = d.getClear(s2);
        String s1 = "当夜风 高徐经 太困了 早早的 睡了过";
        String s2 = "当夜风 高徐经 太困了 早早的 睡了过去";
// s1 = d.getClear(s1);
// s2 = d.getClear(s2);
        System.out.println(d.getRate(s1,s2));
        System.out.println(d.dif);

        d.doDiff(s1, s2);
//
//
//
        System.out.println(d.dif);
        return;
    }
    public double getRate(String s1,String s2){
        int all = s1.replaceAll(" ", "").length() + s2.replaceAll(" ", "").length();
        if(all == 0 ) return 1;
        this.doDiff(s1, s2);
        return 1 - (double)this.dif/all;
    }

    /** Construct a Diff object. */
    public TextCom() {
    }

    /** Do one file comparison. Called with both filenames. */
    public void doDiff(String oldFile, String newFile) {
// println( ">>>> Difference of file \"" + oldFile +
// "\" and file \"" + newFile + "\".\n");
        oldinfo = new fileInfo(oldFile);
        newinfo = new fileInfo(newFile);


        try {
            inputscan( oldinfo );
            inputscan( newinfo );
        } catch (IOException e) {
            System.err.println("Read error: " + e);
        }

        blocklen = new int[ (oldinfo.maxLine>newinfo.maxLine?
                oldinfo.maxLine : newinfo.maxLine) + 2 ];
        oldinfo.alloc();
        newinfo.alloc();

        transform();
        printout();
    }

    void inputscan( fileInfo pinfo ) throws IOException
    {
        pinfo.maxLine = 0;
//     while ((linebuffer = pinfo.file.readLine()) != null) {
//       storeline( linebuffer, pinfo );
//     }
        List L = pinfo.list;
        for(int i =0 ;i<L.size();i++){
            storeline((String)L.get(i),pinfo);
        }
    }

    void storeline( String linebuffer, fileInfo pinfo )
    {
        int linenum = ++pinfo.maxLine;    /* note, no line zero */
        if ( linenum > fileInfo.MAXLINECOUNT ) {
            System.err.println( "MAXLINECOUNT exceeded, must stop." );
            System.exit(1);
        }
        pinfo.symbol[ linenum ] =
                node.addSymbol( linebuffer, pinfo == oldinfo, linenum );
    }

    void transform()
    {
        int oldline, newline;
        int oldmax = oldinfo.maxLine + 2;  /* Count pseudolines at  */
        int newmax = newinfo.maxLine + 2;  /* ..front and rear of file */

        for (oldline=0; oldline < oldmax; oldline++ )
            oldinfo.other[oldline]= -1;
        for (newline=0; newline < newmax; newline++ )
            newinfo.other[newline]= -1;

        scanunique();  /* scan for lines used once in both files */
        scanafter();   /* scan past sure-matches for non-unique blocks */
        scanbefore();  /* scan backwards from sure-matches */
        scanblocks();  /* find the fronts and lengths of blocks */
    }

    void scanunique()
    {
        int oldline, newline;
        node psymbol;

        for( newline = 1; newline <= newinfo.maxLine; newline++ ) {
            psymbol = newinfo.symbol[ newline ];

            if ( psymbol.symbolIsUnique()) {        // 1 use in each file
                oldline = psymbol.linenum;
                newinfo.other[ newline ] = oldline; // record 1-1 map
                oldinfo.other[ oldline ] = newline;
                //s1 = "我们 你们 我们 你们 b cc b1 t";
// s2 = "我们 你们 我们 你们 a cc a12 t" ;
                //打印结果 cc t
            }
        }
        newinfo.other[ 0 ] = 0;
        oldinfo.other[ 0 ] = 0;
        newinfo.other[ newinfo.maxLine + 1 ] = oldinfo.maxLine + 1;
        oldinfo.other[ oldinfo.maxLine + 1 ] = newinfo.maxLine + 1;
    }

    void scanafter()
    {
        int oldline, newline;

        for( newline = 0; newline <= newinfo.maxLine; newline++ ) {
            oldline = newinfo.other[ newline ];
            if ( oldline >= 0 ) {/* is unique in old & new */
                for(;;) {/* scan after there in both files */
                    if ( ++oldline > oldinfo.maxLine   ) break;
                    if ( oldinfo.other[ oldline ] >= 0 ) break;
                    if ( ++newline > newinfo.maxLine   ) break;
                    if ( newinfo.other[ newline ] >= 0 ) break;


                    if ( newinfo.symbol[ newline ] !=
                            oldinfo.symbol[ oldline ] ) break;  // not same

                    newinfo.other[newline] = oldline; // record a match
                    oldinfo.other[oldline] = newline;
                }
            }
        }
    }

    void scanbefore()
    {
        int oldline, newline;

        for( newline = newinfo.maxLine + 1; newline > 0; newline-- ) {
            oldline = newinfo.other[ newline ];
            if ( oldline >= 0 ) {               /* unique in each */
                for(;;) {
                    if ( --oldline <= 0                ) break;
                    if ( oldinfo.other[ oldline ] >= 0 ) break;
                    if ( --newline <= 0                ) break;
                    if ( newinfo.other[ newline ] >= 0 ) break;

   /* oldline and newline exist,
and aren't marked yet */

                    if ( newinfo.symbol[ newline ] !=
                            oldinfo.symbol[ oldline ] ) break;  // not same

                    newinfo.other[newline] = oldline; // record a match
                    oldinfo.other[oldline] = newline;
                }
            }
        }
    }
    void scanblocks()
    {
        int oldline, newline;
        int oldfront = 0;      // line# of front of a block in old, or 0
        int newlast = -1;      // newline's value during prev. iteration

        for( oldline = 1; oldline <= oldinfo.maxLine; oldline++ )
            blocklen[ oldline ] = 0;
        blocklen[ oldinfo.maxLine + 1 ] = UNREAL; // starts a mythical blk

        for( oldline = 1; oldline <= oldinfo.maxLine; oldline++ ) {
            newline = oldinfo.other[ oldline ];
            if ( newline < 0 ) oldfront = 0;  /* no match: not in block */
            else{                                   /* match. */
                if ( oldfront == 0 )         oldfront = oldline;
                if ( newline != (newlast+1)) oldfront = oldline;
                ++blocklen[ oldfront ];
            }
            newlast = newline;
        }
    }

    /* The following are global to printout's subsidiary routines */
// enum{ idle, delete, insert, movenew, moveold,
// same, change } printstatus;
    public static final int
            idle = 0, delete = 1, insert = 2, movenew = 3, moveold = 4,
            same = 5, change = 6;
    int printstatus;
    boolean anyprinted;
    int printoldline, printnewline;     // line numbers in old & new file

    /**
     * printout - Prints summary to stdout.
     * Expects all data structures have been filled out.
     */
    void printout()
    {
        printstatus = idle;
        anyprinted = false;
        for( printoldline = printnewline = 1; ; ) {
            if ( printoldline > oldinfo.maxLine ) { newconsume(); break;}
            if ( printnewline > newinfo.maxLine ) { oldconsume(); break;}
            if (      newinfo.other[ printnewline ] < 0 ) {
                if ( oldinfo.other[ printoldline ] < 0 )
                    showchange();
                else
                    showinsert();
            }
            else if ( oldinfo.other[ printoldline ] < 0 )
                showdelete();
            else if ( blocklen[ printoldline ] < 0 )
                skipold();
            else if ( oldinfo.other[ printoldline ] == printnewline )
                showsame();
            else
                showmove();
        }

        if ( anyprinted == true ) println( ">>>> End of differences."  );
        else                     println( ">>>> Files are identical." );
    }

    void newconsume()
    {
        for(;;) {
            if ( printnewline > newinfo.maxLine )
                break;        /* end of file */
            if ( newinfo.other[ printnewline ] < 0 ) showinsert();
            else                                    showmove();
        }
    }

    void oldconsume()
    {
        for(;;) {
            if ( printoldline > oldinfo.maxLine )
                break;       /* end of file */
            printnewline = oldinfo.other[ printoldline ];
            if ( printnewline < 0 ) showdelete();
            else if ( blocklen[ printoldline ] < 0 ) skipold();
            else showmove();
        }
    }

    void showdelete()
    {
        if ( printstatus != delete )
            println( ">>>> DELETE AT " + printoldline);
        printstatus = delete;
        oldinfo.symbol[ printoldline ].showSymbol();
        dif +=oldinfo.symbol[printoldline].line.length();
        anyprinted = true;
        printoldline++;
    }

    void showinsert()
    {
        if ( printstatus == change ) println( ">>>>     CHANGED TO" );
        else if ( printstatus != insert )
            println( ">>>> INSERT BEFORE " + printoldline );
        printstatus = insert;
        newinfo.symbol[ printnewline ].showSymbol();
        dif +=newinfo.symbol[printnewline].line.length();
        anyprinted = true;
        printnewline++;
    }

    void showchange()
    {
        if ( printstatus != change )
            println( ">>>> " + printoldline + " CHANGED FROM");
        printstatus = change;
        oldinfo.symbol[ printoldline ].showSymbol();
        dif +=oldinfo.symbol[printoldline].line.length();
        anyprinted = true;
        printoldline++;
    }

    void skipold()
    {
        printstatus = idle;
        for(;;) {
            if ( ++printoldline > oldinfo.maxLine )
                break;     /* end of file  */
            if ( oldinfo.other[ printoldline ] < 0 )
                break;    /* end of block */
            if ( blocklen[ printoldline ]!=0)
                break;          /* start of another */
        }
    }

    void skipnew()
    {
        int oldline;
        printstatus = idle;
        for(;;) {
            if ( ++printnewline > newinfo.maxLine )
                break;    /* end of file  */
            oldline = newinfo.other[ printnewline ];
            if ( oldline < 0 )
                break;                         /* end of block */
            if ( blocklen[ oldline ] != 0)
                break;              /* start of another */
        }
    }

    void showsame()
    {
        int count;
        printstatus = idle;
        if ( newinfo.other[ printnewline ] != printoldline ) {
            System.err.println("BUG IN LINE REFERENCING");
            System.exit(1);
        }
        count = blocklen[ printoldline ];
        printoldline += count;
        printnewline += count;

    }

    void showmove()
    {
        int oldblock = blocklen[ printoldline ];
        int newother = newinfo.other[ printnewline ];
        int newblock = blocklen[ newother ];

        if ( newblock < 0 ) skipnew();         // already printed.
        else if ( oldblock >= newblock ) {     // assume new's blk moved.
            blocklen[newother] = -1;         // stamp block as "printed".
            println( ">>>> " + newother +
                    " THRU " + (newother + newblock - 1) +
                    " MOVED TO BEFORE " + printoldline );
            for( ; newblock > 0; newblock--, printnewline++ )
                newinfo.symbol[ printnewline ].showSymbol();
            dif +=newinfo.symbol[printnewline].line.length();
            anyprinted = true;
            printstatus = idle;

        } else                /* assume old's block moved */
            skipold();      /* target line# not known, display later */
    }

    /** Convenience wrapper for println */
    public void println(String s) {
//System.out.println(s);
    }
}; // end of main class!

class node{                       /* the tree is made up of these nodes */
    node pleft, pright;
    int linenum;

    static final int freshnode = 0,
            oldonce = 1, newonce = 2, bothonce = 3, other = 4;

    int /* enum linestates */ linestate;
    String line;

    static node panchor = null;    /* symtab is a tree hung from this */

    /**
     * Construct a new symbol table node and fill in its fields.
     * @param        stringA line of the text file
     */
    node( String pline)
    {
        pleft = pright = null;
        linestate = freshnode;
        /* linenum field is not always valid */
        line = pline;
    }

    /**
     * matchsymbol       Searches tree for a match to the line.
     * @param  pline, a line of text
     * If node's linestate == freshnode, then created the node.
     */
    static node matchsymbol( String pline )
    {
        int comparison;
        node pnode = panchor;
        if ( panchor == null ) return panchor = new node( pline);
        for(;;) {
            comparison = pnode.line.compareTo(pline);
            if ( comparison == 0 ) return pnode;          /* found */

            if ( comparison < 0 ) {
                if ( pnode.pleft == null ) {
                    pnode.pleft = new node( pline);
                    return pnode.pleft;
                }
                pnode = pnode.pleft;
            }
            if ( comparison > 0 ) {
                if ( pnode.pright == null ) {
                    pnode.pright = new node( pline);
                    return pnode.pright;
                }
                pnode = pnode.pright;
            }
        }
        /* NOTE: There are return stmts, so control does not get here. */
    }

    static node addSymbol( String pline, boolean inoldfile, int linenum )
    {
        node pnode;
        pnode = matchsymbol( pline );  /* find the node in the tree */
        if ( pnode.linestate == freshnode ) {
            pnode.linestate = inoldfile ? oldonce : newonce;
        } else {
            if (( pnode.linestate == oldonce && !inoldfile ) ||
                    ( pnode.linestate == newonce &&  inoldfile ))
                pnode.linestate = bothonce;
            else pnode.linestate = other;
        }
        if (inoldfile) pnode.linenum = linenum;
        return pnode;
    }

    boolean symbolIsUnique()
    {
        return (linestate == bothonce );
    }

    void showSymbol()
    {
        System.out.println("line===" + line);
    }
}