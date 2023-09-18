package javaapplication2;

import java.io.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import static java.lang.System.out;

public class JavaApplication2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws FileNotFoundException, IOException, ClassNotFoundException {
        NewJFrame1 start = new NewJFrame1();
        start.setVisible(true);

    }
    static void CheckParallelFullCase(String [] lines, FileWriter fw)throws IOException{
        String [] words = new String[100000];
        String [] linesplit = new String[100000];
        String [] Errors = new String[100000];
        int [] caselinenum = new int[100000];
        int i=0,j=0,k=0,z=0,e=0;
        while(lines[i] != null){
            lines[i] = lines[i].trim();
            //System.out.println(lines[i]); 
            linesplit = lines[i].split(" ");
            for(String a:linesplit){
                if(a.equals("case")){
                    caselinenum[z]=i;
                    z++;
                }
                words[j] = a;
                j++;
            }
            i++;
        }//split words
        int [] caseindex = new int[100000];
        i=0;
        while(words[i]!=null){
            if(words[i].equals("case")){
                //System.out.println(words[i]);
                
                caseindex[k]=i;
                k++;
            }
            i++;
        }//get case index
        i=0;j=0;
        while(caseindex[i]!= 0){//check full case
            if(words[caseindex[i]+2].equals("full_case") || words[caseindex[i]+3].equals("full_case")
            || words[caseindex[i]+4].equals("full_case") ||
            words[caseindex[i]+5].equals("full_case") ||
            words[caseindex[i]+6].equals("full_case")){//check for synopsis fullcase
                i++;
                continue;
            }
            
            j=caseindex[i];
            while(!(words[j].equals("endcase"))){//check for default
                if(words[j].equals("default"))break;
                j++;
            }
            if(words[j].equals("default")){
                i++;
                continue;
            }
            String [] outputs = new String[100000];
            int c=0;
            j=caseindex[i];
            int maxout=0;
            while(!(words[j].equals("endcase"))){//search for output in case
            if(words[j].equals("=")){
                outputs[c] = words[j-1];
                //System.out.println(outputs[c]);
                c++;
            }
                j++;
                maxout=c;
            }
            String [] initializations = new String[100000];
             c=0;
             int maxin=0;
            j=caseindex[i];
            while(!(words[j].equals("module"))){//search for output initialization
            if(words[j].equals("=")){
                initializations[c] = words[j-1];
                //System.out.println(initializations[c]);
                c++;
            }
                j--;
                maxin=c;
                if(j<0)break;
            }
            int flag=0;
            for(int m=0;m<maxout;m++){
                flag=0;
                for(int b=0;b<maxin;b++){
                    if(outputs[m].equals(initializations[b])){
                        //System.out.println("ok");
                        flag=1;
                        break;
                    }
                }
                if(flag==0){
                    break;
                }
            }
            if(flag==1){
                i++;
                continue;
            }
            //System.out.println("not Full case at line "+(caselinenum[i]+1));
            Errors[e] = "not Full case at line "+(caselinenum[i]+1);
            e++;
            i++;
        }//full case check
        
        i=0;j=0;
        while(caseindex[i]!= 0){
            if(words[caseindex[i]+2].equals("parallel_case") || words[caseindex[i]+3].equals("parallel_case")
            || words[caseindex[i]+4].equals("parallel_case") ||
            words[caseindex[i]+5].equals("parallel_case")||
            words[caseindex[i]+6].equals("parallel_case")){//check for synopsis fullcase
                i++;
                continue;
            }
            String [] cases = new String[100000];
            j=caseindex[i];
            int c=0;
            while(!(words[j].equals("endcase"))){//check for cases
                if(words[j].equals(":")){
                    cases[c] = words[j-1];
                    c++;
                }
                j++;
            }
            for(int o=0;o<c;o++){
                for(int l=o+1;l<c;l++){
                    if(cases[o].equals(cases[l])){
                        //System.out.println("not parallel case at line "+(caselinenum[i]+1));
                        Errors[e] = "not parallel case at line "+(caselinenum[i]+1);
                        e++;
                    }
                }
            }
            i++;
        }//parallel case check
        //FileWriter fw = new FileWriter(path);
        fw.append("Not Full/Parallel Cases: \n");
        for (int s = 0 ; Errors[s]!=null ; s++){
            fw.append("\t"+Errors[s]+" \n");
        }
        fw.append("\n");
        //fw.close();
        
    }
    static void UnreachableStateChecker(String[] line , FileWriter fw){
        String [] states = new String[20];
        int[] statesFlags = new int[20];
        int[] statesLoc = new int[20];
        int statesCounter = 0 , counter = 0;
        for (int j = 0 ; line[j]!=null ; j++){
            for (int k  = 0 ; k < line[j].length() ; k++){
                if (line[j].startsWith("//"))
                    break;
                if (line[j].startsWith("localparam")){
                    k=10;
                    while(!(line[j-1].endsWith(";")))
                    {
                        while(line[j].charAt(k)==' ' || line[j].charAt(k)=='[' || line[j].charAt(k)==']' || Character.isDigit(line[j].charAt(k)) || line[j].charAt(k)==':' || line[j].charAt(k)=='\t'){
                            if(k<line[j].length())
                                k++;
                            else
                                break;
                        }
                        int delim;
                        if (line[j].indexOf(" ", k+1)< line[j].indexOf("=", k+1))
                            delim = line[j].indexOf(" ", k+1);
                        else
                            delim = line[j].indexOf("=", k+1);
                        states[statesCounter] = line[j].substring(k, delim);
                        statesFlags[statesCounter] += 1;
                        statesLoc[statesCounter++] = j;
                        k=0;
                        j++;
                    }
                    break;
                }
                if (states[counter]!=null && line[j].contains("=")){
                    for (int start = line[j].indexOf("=") + 1; start < line[j].length() && states[counter]!=null ; start++){
                        if(line[j].contains(states[counter])){
                            statesFlags[counter]--;
                        }
                        counter++;
                    }
                    counter=0;
                    break;
                }
            }
        }
        try{
            //FileWriter fw = new FileWriter(directory);
            fw.append("FSM States: \n");
            int flag = 0;
            for (int s = 0 ; states[s]!=null ; s++){
                if(statesFlags[s]==1){
                    fw.append("\t");
                    fw.append(states[s]);
                    fw.append(" initialized in line ");
                    fw.append((char) statesLoc[s]); 
                    fw.append(" is never reached.\n");
                    flag++;
                }
            }
            if (flag == 0)
                fw.append("\t No unreachable states.\n");
            //fw.close();
            fw.append("\n");
        }
        catch (IOException e){
            System.out.println("IO Error!");
        }

    }
    static void unitiliazedRegs(String []line, FileWriter fw) throws IOException {
        String[] regs = new String[100];
        String[] plusRegs = new String[100];
        int indexRegs = 0;
        String[] tempt2 = new String[line.length];
        String[] temp = new String[line.length];

        for (int i = 1; i < line.length; i++) {
            tempt2[0] += line[i];
        }

        temp = tempt2[0].split(" ");

        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].replaceAll("[\\[\\](){},;]", "");
            //            temp[i].split(" ");
        }
        int vb = 0;
        for (int t = 1; t < temp.length; t++) {
            if (temp[t].equals("=") || temp[t].equals("<=")) {
                plusRegs[vb] = temp[t - 1];
                vb++;
            }
        }

        for (int m = 0; m < temp.length; m++) {

            if (temp[m].contains("reg")) {
                regs[indexRegs] = temp[m + 1];
                indexRegs++;

            }
        }

        boolean found = false;
        fw.append("Uninitialized registers: \n");
        boolean flag = false;
        for (int i = 0; i < regs.length; i++) {
            found = false;
            for (int j = 0; j < plusRegs.length; j++) {

                try {
                    if (regs[i].equals(plusRegs[j]) || line[i].contains(plusRegs[j])) {
                        found = true;
                    }
                } catch (Exception e) {
                    continue;
                }

            }
            if (regs[i] == null) {
                continue;
            }
            if (!found) {
                int of = 0;
                while (line[of] != null) {
                    if ((line[of].contains(regs[i]))) {
                        try {
                            int lineno = of + 1;

                            fw.append("\t" + regs[i] + " is not initialized at line " + (lineno) + "\n");
                            //fw.close();
                            flag = true;

                        } catch (IOException e) {
                            System.out.println("IO Error!");
                        }

                    }
                    of++;
                }
            }
        }
        if(!flag)fw.append("\tNo uninitialized registers found.\n");
        fw.append("\n");
    }
    static void arithmeticOperation(String[] line, FileWriter fw) throws IOException {

        String[] variablesArray = new String[100];
        String[] numbersArray = new String[10000];
        char[] chararray1 = new char[10];
        char[] chararray2 = new char[10];
        int indexVariablesArray = 0;
        int indexNumbersArray = 0;
        String[] tempt2 = new String[line.length];
        String[] temp = new String[line.length];

        for (int i = 1; i < line.length; i++) {
            tempt2[0] += line[i];
        }

        temp = tempt2[0].split(" ");

        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].replaceAll("[\\[\\](){},;]", "");
            //            temp[i].split(" ");
        }

        for (int m = 0; m < temp.length; m++) {
            if (temp[m].contains("'b")) {

                temp[m] = temp[m].substring(3, temp[m].length());

                variablesArray[indexVariablesArray] = temp[m - 2];
                numbersArray[indexNumbersArray] = temp[m];

                //                chararray = numbersArray[indexVariablesArray].toCharArray();

                indexVariablesArray++;
                indexNumbersArray++;

            }
        }


        int indexoffirst = 0, indexofsecond = 0;
        int r1 = 0, r1Max = 0, r2Max = 0, r2 = 0, carry = 0;
        for (int overflow = 0; overflow < temp.length; overflow++) {
            if (temp[overflow].contains("+")) {
                for (int k = 0; k < variablesArray.length; k++) {
                    if (temp[overflow - 1].equals(variablesArray[k])) {
                        indexoffirst = k;
                        chararray1 = numbersArray[indexoffirst].toCharArray();
                    }
                    if (temp[overflow + 1].equals(variablesArray[k])) {
                        indexofsecond = k;
                        chararray2 = numbersArray[indexofsecond].toCharArray();
                    }
                }

                String result = null;
                for (int i = 0; i < chararray1.length; i++) {
                    //                    System.out.println(chararray1[i]);

                    if (chararray1[chararray1.length - 1 - i] == '1') {
                        r1 += Math.pow(2, i);
                    }
                    r1Max += Math.pow(2, i);

                }
                for (int j = 0; j < chararray2.length; j++) {
                    if (chararray2[chararray2.length - 1 - j] == '1') {
                        r2 += Math.pow(2, j);
                    }
                    r2Max += Math.pow(2, j);
                }
                fw.append("Aritmetic overflow:\n");
                if (((r1 + r2) > Math.max(r1Max, r2Max))) {
                    int of = 0;
                    while (line[of] != null) {
                        if (line[of].contains("+") && line[of].contains(variablesArray[indexoffirst]) &&
                                line[of].contains(variablesArray[indexofsecond])) {

                            try {
                                int lineno = of + 1;

                                fw.append("\tArithmetic overflow at line " + (lineno));
                                fw.append(" between " + variablesArray[indexoffirst] + " and " + variablesArray[indexofsecond] + "\n");
                                //fw.close();
                            } catch (IOException e) {
                                System.out.println("IO Error!");
                            }

                            break;

                        }

                        of ++;
                    }
                }
                else{
                    fw.append("\tNo arithmetic overflow found");
                }
                fw.append("\n");
            }
        }

    }
    static void CheckMultipleBusses(String [] string, FileWriter fw) throws IOException{
        String [][]intialw= new String[2][100];
        String [][]alwaysq= new String[2][100];
        int firstdone=0;
        int ww=0;
        int w=0;
        int al=0;
        int all=0;
        int qq=0;
        int q=0;
        String []words=new String[10000];
        String []linesplits=new String[10000];
        String []errors=new String[10000]; 
        int CountErrors=-1;
        int i=0;
        int j=0;
        int countintial=0;
        int countintial2=0;
        int countalways=0;
        int countalways2=0;
        while(string[i]!=null) {
            string[i]=string[i].trim();
            i++;
        }
        i=0;
        while(string[i]!=null){
            string[i]=string[i].trim();
           // System.out.println(string[i]);
            linesplits=string[i].split(" ");
            for(String a: linesplits){
                words[j]=a;
                j++;
            }
            i++;
        }
        for(int z=0;z<words.length;z++){
            if(words[z]==null) break;
            words[z]=words[z].trim();
            if((words[z].length()==0) || (words[z].length()==1)){
                for(int f=z;f<words.length;f++){
                    if(words[f]==null) break;
                    words[f]=words[f+1];
                }   
            }
        }
        for(int z=0;z<words.length;z++){
            try{
            words[z]=words[z].replace("(","");
            words[z]=words[z].replace(")","");
            words[z]=words[z].replace("@","");
            words[z]=words[z].replace("{","");
            words[z]=words[z].replace("}","");
            words[z]=words[z].replace(",","");
            if(words[z]==null) break;}
            catch(Exception e){
                continue;
            }
        }
        for(int y=0;y<1000;y++){
            try{
            if((words[y]).equals("initial")){
                for(int g=y+2;g<1000;g++){
                    if(words[g].equals("if")) countintial++;
                    if(words[g].equals("end")) {
                        if(countintial==0) break;
                        countintial--;
                    }
                    if(words[g].equals("if"))continue;
                    if(words[g].contains("'")) continue;
                    if(words[g].contains("#")) continue;
                    if(words[g-1].equals("<="))continue;
                    if(words[g-1].equals("="))continue;
                    if(words[g+1].equals("<="))continue;
                    if(words[g].equals("end"))continue;
                    if(words[g].equals("begin"))continue;
                    if(words[g].equals("="))continue;
                    if(words[g].equals("<="))continue;
                    if(words[g].equals("else"))continue;
                    intialw[ww][w]=words[g];
                    w++;
                }
                ww++;
                w=0;
            }
            if((words[y]).equals("always")){            
                for(int g=y+2;g<1000;g++){
                    if(words[g].equals("if")) countalways++;
                    if(words[g].equals("end")) {
                        if(countalways==0) break;
                        countalways--;
                    }
                    if(words[g].equals("if"))continue;
                //    if(words[g].contains("1")) continue;
                    if(words[g].contains("'")) continue;
                    if(words[g-1].equals("<="))continue;
                    if(words[g-1].equals("="))continue;
                    if(words[g+1].equals("<="))continue;
                    if(words[g].equals("end"))continue;
                    if(words[g].equals("begin"))continue;
                    if(words[g].equals("="))continue;
                    if(words[g].equals("<="))continue;
                    if(words[g].equals("else"))continue;
                    alwaysq[all][al]=words[g];
                    al++;
                }
                all++;
                al=0;
            } 
            }
            catch(Exception e){
                continue;
            }
        }

        for(int yy=0;yy<2;yy++){
            for(int zz=yy;zz<2;zz++){
                for(int uu =1;uu<50;uu++){
                    for(int ii=uu;ii<50;ii++){
                        if(alwaysq[yy][ii]==null)continue;
                        if(ii==uu && yy==zz)continue;
                        try{
                        if(alwaysq[yy][ii].equals(alwaysq[zz][uu])) {
                            if(zz==yy){CountErrors++;
                                countalways=0;
                                errors[CountErrors]=alwaysq[zz][uu]+" MULTIDRIVEN in lines ";
                                for(int x=0;x<50;x++){
                                    if(string[x].equals("initial")) break;
                                    if(string[x].contains("always")) ++countalways;
                                    if(countalways==zz+1){
                                        if(string[x].contains(alwaysq[zz][uu])) {
                                            errors[CountErrors]+=x+1;
                                            firstdone++;
                                            if(firstdone==2)break;
                                            if(firstdone==1){
                                                errors[CountErrors]+=" and ";
                                            }
                                        }
                                    }
                                }
                            }
                            else{
                                if(alwaysq[zz][0].equals(alwaysq[yy][0])){;
                                    System.out.println();
                                    countalways2=0;
                                    firstdone=0;
                                    CountErrors++;
                                    errors[CountErrors]=alwaysq[zz][uu]+" MULTIDRIVEN in lines ";
                                    for(int gg=0;gg<100;gg++){
                                        if(string[gg].contains("always"))countalways2++;
                                        if(string[gg].equals("initial")) break;
                                        if(countalways2==zz+1 || countalways2==yy+1){
                                            if(string[gg].contains(alwaysq[zz][uu])) {
                                                errors[CountErrors]+=gg+1;
                                                firstdone++;
                                                if(firstdone==2)break;
                                                if(firstdone==1){
                                                    errors[CountErrors]+=" and ";
                                                }
                                            }
                                        }
                                    }
                                    //CountErrors++;
                                }
                                else{
                                    continue;
                                }
                            }
                        }
                        }
                        catch(Exception e){
                            continue;
                        }
                    }
                }
            }
        }
        for(int yy=0;yy<2;yy++){
            for(int zz=yy;zz<2;zz++){
                for(int uu =0;uu<40;uu++){
                    for(int ii=uu;ii<40;ii++){                                        
                        if(intialw[yy][ii]==null)continue;
                        if(ii==uu && yy==zz)continue;
                        try{
                        if(intialw[yy][ii].equals(intialw[zz][uu])) {
                            System.out.println();
                            if(zz==yy){
                                CountErrors++;
                                countintial=0;
                                firstdone=0;    
                                errors[CountErrors]=intialw[zz][uu]+" MULTIDRIVEN in lines ";
                                for(int x=0;x<50;x++){
                                    if(string[x].equals("always"))break;
                                    if(string[x].contains("initial")) ++countintial;
                                    if(countintial==zz+1){
                                        if(string[x].contains(intialw[zz][uu])) {
                                            errors[CountErrors]+=x+1;
                                            firstdone++;
                                            if(firstdone==2)break;
                                            if(firstdone==1){
                                                errors[CountErrors]+=" and ";
                                            }
                                        }
                                    }
                                }
                            }
                            else{
                                    System.out.println();
                                    countintial2=0;
                                    firstdone=0;
                                    CountErrors++;
                                    errors[CountErrors]=intialw[zz][uu]+" MULTIDRIVEN in lines ";
                                    for(int gg=0;gg<10000;gg++){
                                        if(string[gg].equals("always"))break;
                                        if(string[gg].contains("initial"))countintial2++;
                                        if(countintial2==zz+1 || countintial2==yy+1){
                                            if(string[gg].contains(intialw[zz][uu])) {
                                                errors[CountErrors]+=gg+1;
                                                firstdone++;
                                                if(firstdone==2)break;
                                                if(firstdone==1){
                                                    errors[CountErrors]+=" and ";
                                                }
                                            }
                                        }
                                    }
                                }
                        }
                        }
                        catch(Exception e){
                            continue;
                        }
                    }
                }
            }
        }
        fw.append("Multi-driven buses/registers:\n");
        CountErrors=0;
        while(errors[CountErrors]!=null){
            fw.append("\t" + errors[CountErrors++] + " \n");
        }
        if (CountErrors==0){
            fw.append("\tNo multi-driven busses/registers.\n");
        }
        fw.append("\n");
    }
}