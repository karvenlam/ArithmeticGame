package com.ravenlamb.android.arithmeticgame;


import android.util.Log;

import java.security.SecureRandom;


/**
 * Created by kl on 12/2/2014.
 */
public class BaseGameDriver {

    public static final String TAG=BaseGameDriver.class.getName();
    public static final String UNKNOWN_VALUE="???";
    public static final String UNKNOWN_OPERATOR="?";


    //cell status
    public static final int CELL_NOTSELECTED=0;
    public static final int CELL_OPERAND1=1;
    public static final int CELL_OPERAND2=2;
    public static final int CELL_RESULT=3;
    public static final int CELL_CURR_COORD=4;

    //operations
    public static final int OP_UNTESTED=-1;
    public static final int OP_INVALID=0;
    public static final int OP_ADDITION=1;
    public static final int OP_SUBTRACTION=2;
    public static final int OP_MULTIPLICATION=3;
    public static final int OP_DIVISION=4;

    public static final String[] OPERATORS= new String[]{"?", "+", "-", "\u00D7", "\u00F7"};


    protected int rows=6;
    protected int cols=6;
    protected int[][] cells;

    SecureRandom random;//random.nextInt(10);

//    Coord currStartCoord;
//    Coord currHoverCoord;//might not need this
//    Coord currEndCoord;
    protected int currStatus;//OP_UNTESTED, OP_INVALID, ...
//    boolean readyForNewNumber =false;

    protected Operand op1;
    protected Operand op2;
    protected Operand result;
    protected Operand current;

    public BaseGameDriver(int r, int c){
        rows=r;
        cols=c;
        cells=new int[rows][cols];

        random=new SecureRandom();
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                cells[i][j]=random.nextInt(10);
            }
        }

        //todo need to remove this later
//        op1=new Operand(new Coord(0,4), new Coord(4,4));
//        op2=new Operand(new Coord(4,4), new Coord(4,2));
//        result=new Operand(new Coord(2,2), new Coord(2,2));
//        readyForNewNumber =true;
    }

    /* Start a new number */
    public void startingCoord(int x, int y){
        if(result!=null){
            shiftOperand();//todo need to test for result, currStatus =OP_UNTESTED also set in shiftOperand
        }
        //
        if(getCellStatus(x,y)==CELL_NOTSELECTED){
            current=new Operand(new Coord(x,y), new Coord(x,y));
//            Log.d(TAG,"startingCoord: "+x+" "+y );
        }
    }

    public void hoveringCoord(int x, int y){
        //toto need to test x,y is selected

        if(current==null){
            //startingCoord(x, y);
            return;
        }
        if(!current.isPartOfOperand(x,y) && getCellStatus(x,y)!=CELL_NOTSELECTED){
            current=null;
            return;
        }
        if(current.startCoord.x!=x || current.startCoord.y!=y){
            current.changeEndCoord(x, y);
        }
//        Log.d(TAG,"hoveringCoord: "+x+" "+y );
    }

    /* check whether number is valid,
    if so, set to appropriate operand
    else, clear currStartCoord
    * */
    public boolean endingCoord(int x, int y){
        //return whether endCoord is valid

        if(current==null ){
            return false;
        }
        //end coord is valid if it's in the same row, the same column or in diagonal as the start coord
        if(getCellStatus(x,y)==CELL_NOTSELECTED){
            current.changeEndCoord(x,y);
        }
        if(current.isPointsInStraightLine()){
            //one digit is still valid
            result=current;
            getStatus();
//            readyForNewNumber =false;
//            Log.d(TAG,"endingCoord: "+x+" "+y );
            return true;
        }else
        {
            return false;
        }
    }

    /*
    op1 [+ - x / ] op2 = result
    assign currStatus with the 3 operands and
    return the operator
     */
    protected int getStatus(){
        currStatus= OP_INVALID;
        if(op1 == null || op2 == null || result==null){
            currStatus= OP_INVALID;
            return currStatus;
        }
        if(op1.number+op2.number == result.number){
            currStatus= OP_ADDITION;
        }else if(op1.number-op2.number == result.number){
            currStatus= OP_SUBTRACTION;
        }else if(op1.number*op2.number == result.number){
            currStatus= OP_MULTIPLICATION;
        }else if(op2.number != 0 && op1.number/op2.number == result.number){
            currStatus= OP_DIVISION;
        }
//        Log.d(TAG,"getStatus: "+currStatus );
        return currStatus;
    }


    public String getCurrEquation(){
        String temp="";
        if(op1 == null || op2 == null || result==null){
            return "";
        }
        if(op1.number+op2.number == result.number){
            return op1.number+"+"+op2.number+"="+result.number;
        }
        if(op1.number-op2.number == result.number){
            return op1.number+"-"+op2.number+"="+result.number;
        }
        if(op1.number*op2.number == result.number){
            return op1.number+"\u00D7"+op2.number+"="+result.number;
        }
        if(op1.number/op2.number == result.number){
            return op1.number+"\u00F7"+op2.number+"="+result.number;
        }
        return temp;
    }


    public void shiftOperand(){
        //todo: shift result to op2, op2 to op1, clear result and curr Coords
        if(currStatus==OP_UNTESTED){
            return;
        }
        op1=op2;
        op2=result;
        result=null;
        current=null;
        currStatus=OP_UNTESTED;
    }

    public int getCellNum(int x, int y){
        if(cells !=null && x<rows && y<cols){
            return cells[x][y];
        }else
        {
            return -1;
        }
    }

    public int getCellStatus(int x, int y){
        if(op1 !=null && op1.isPartOfOperand(x,y)){
            return CELL_OPERAND1;
        }
        if(op2 !=null && op2.isPartOfOperand(x,y)){
            return CELL_OPERAND2;
        }
        if(result !=null && result.isPartOfOperand(x,y)){
            return CELL_RESULT;
        }
        if(current !=null && current.isPartOfOperand(x,y)){
            return  CELL_CURR_COORD;
        }

        return CELL_NOTSELECTED;
    }

    @Override
    public String toString() {
        StringBuilder temp=new StringBuilder();
        if(op1!=null){
            temp.append("op1:");
            temp.append(op1.toString());
            temp.append("\n");
        }
        if(op2!=null){
            temp.append("op2:");
            temp.append(op2.toString());
            temp.append("\n");
        }
        if(result!=null){
            temp.append("result:");
            temp.append(result.toString());
            temp.append("\n");
        }
        if(current!=null){
            temp.append("current:");
            temp.append(current.toString());
            temp.append("\n");
        }
        return temp.toString();
    }

    public class Operand{
        public Coord startCoord;
        public Coord endCoord;
        public int number;
        int stepX=0;
        int stepY=0;
        int numberOfDigits=0;

        public Operand(Coord s, Coord e){
            startCoord=new Coord(s.x, s.y);
            endCoord=new Coord(e.x, e.y);
            if(isPointsInStraightLine()){
                computeNumber();
            }else{
                number=-1;
//                Log.d(TAG,"operand constructor: " +this.toString() );
            }
        }

        protected void computeNumber(){
            //todo, assign number
            // drop leading zeroes and move start coord
            number=0;
            stepX=returnStep(startCoord.x,endCoord.x);
            stepY=returnStep(startCoord.y,endCoord.y);
            int tempX=startCoord.x;
            int tempY=startCoord.y;
            numberOfDigits=getNumberOfDigits();
//            Log.i(TAG,"computeNumber: "+this.toString() );
            for(int i=0;i< numberOfDigits;i++){
                number=number*10 + BaseGameDriver.this.cells[tempX][tempY];
                if(number==0 && numberOfDigits >1){
//                    if number is 0 and startCoord!=endCoord
                    endCoord.x=startCoord.x;
                    endCoord.y=startCoord.y;
                    stepX=0;
                    stepY=0;
                    numberOfDigits=getNumberOfDigits();
                    break;
//                    Log.d(TAG,"computeNumber: zero: "+this.toString() );
                }
//                Log.d(TAG,"computeNumber: "+i+" "+this.toString() );
//                Log.wtf(TAG,"computeNumber:wtf "+i+" ("+stepX+","+stepY+") ("+tempX+","+tempY+")" );
                tempX+=stepX;
                tempY+=stepY;
            }
        }

        public boolean changeEndCoord(int x, int y){
            //return true if endCoord is changed
            //return false if endCoord is not changed or x,y is not valid
            if(endCoord.x==x && endCoord.y==y){
//                Log.d(TAG,"changeEndCoord: before false: "+this.toString());
                return false;
            }
            if(isPointsInStraightLine(startCoord.x, startCoord.y, x, y)){
                endCoord=new Coord(x,y);
                computeNumber();
//                Log.d(TAG,"changeEndCoord: before true: "+this.toString());
                return true;
            }
            return false;
        }


        public boolean isPartOfOperand(int x, int y){
            //todo need to debug
            //single point
            if(startCoord.x==x && startCoord.y==y){
                return true;
            }
            int slopexfactor=(stepX==0)?0:(x-startCoord.x)/stepX;
            int slopeyfactor=(stepY==0)?0:(y-startCoord.y)/stepY;
            //vertical, startCoord.x==x
            if(startCoord.x==endCoord.x && startCoord.x==x){
                return slopeyfactor<numberOfDigits && slopeyfactor>0;
                //the endCoord should be at numOfDigits-1
                //slopeyfactor must be >0 since it should not be at starting point
            }
            //horizontal, startCoord.y==y
            if(startCoord.y==endCoord.y && startCoord.y==y){
                return slopexfactor<numberOfDigits && slopexfactor>0;
                //the endCoord should be at numOfDigits-1
            }
            //diagonal,
            if(slopexfactor==slopeyfactor && slopexfactor<numberOfDigits && slopexfactor>0){
                return true;
            }
            return false;
        }

        public boolean isPointsInStraightLine(){
            return isPointsInStraightLine(startCoord.x, startCoord.y, endCoord.x, endCoord.y);
        }

        //end coord is valid if it's in the same row, the same column or in diagonal as the start coord
        public boolean isPointsInStraightLine(int start_x, int start_y, int end_x, int end_y){
            if(start_x==end_x || start_y==end_y
                    || Math.abs(start_x-end_x)==Math.abs(start_y-end_y)){
                return true;
            }else
            {
                return false;
            }
        }

        public Coord[] getAll(){
            return getAll(startCoord.x, startCoord.y, endCoord.x, endCoord.y);
        }


        public Coord[] getAll(int start_x, int start_y, int end_x, int end_y){

            if(start_x==end_x || start_y==end_y
                    || Math.abs(start_x-end_x)==Math.abs(start_y-end_y)){
                int sx=returnStep(start_x, end_x);
                int sy=returnStep(start_y, end_y);
                int tx=start_x;
                int ty=start_y;
                int ds=getNumberOfDigits(start_x,start_y,end_x,end_y);
                Coord[] temp=new Coord[ds];
                for (int i = 0; i < ds; i++) {
                    temp[i]=new Coord(tx+i*sx, ty+i*sy);
                }
                return temp;
            }
            return null;
        }

        public int returnStep(int a, int b){
            if(b>a){
                return 1;
            }else if(a>b){
                return -1;
            }
            return 0;
        }

        public int getNumberOfDigits(){
            if(!isPointsInStraightLine()){
//                Log.d(TAG,"getNumberOfDigits: "+this.toString() );
                return -1;
            }
            if(startCoord.x==endCoord.x ){
                return Math.abs(startCoord.y-endCoord.y)+1;
            }else{
                return Math.abs(startCoord.x-endCoord.x)+1;
            }
        }

        public int getNumberOfDigits(int start_x, int start_y, int end_x, int end_y){
            if(!this.isPointsInStraightLine(start_x, start_y, end_x, end_y)){
//                Log.d(TAG,"getNumberOfDigits:_ "+start_x+", "+start_y+", "+ end_x+", "+ end_y );
                return -1;
            }
            if(start_x==end_x){
                return Math.abs(start_y-end_y)+1;
            }else{
                return Math.abs(start_x-end_x)+1;
            }

        }

        @Override
        public String toString() {
            StringBuilder temp=new StringBuilder();
            temp.append("{");
            temp.append(startCoord.toString());
            temp.append(endCoord.toString());
            temp.append(" ");
            temp.append(number);
            temp.append(" ");
            temp.append(numberOfDigits);
            temp.append(" ");
            Coord[] tc=getAll();
            for (int i = 0; i < tc.length; i++) {
                temp.append(tc[i].toString());
            }
            temp.append("}");
            return temp.toString();
        }
    }

    public class Coord{
        public int x;
        public int y;

        public Coord(int tx, int ty){
            x=tx;
            y=ty;
        }

        @Override
        public boolean equals(Object o) {
            Coord c2=(Coord) o;
            return (x==c2.x && y==c2.y);
        }

        @Override
        public String toString() {
            return "("+x+","+y+")";
        }
    }

    public int getCurrStatus(){ return  currStatus;}
    public String getOperator(){ return (currStatus!=OP_UNTESTED)?OPERATORS[currStatus]: UNKNOWN_OPERATOR; }
    public String getOp1Number(){ return (op1==null)?UNKNOWN_VALUE:String.valueOf(op1.number);}
    public String getOp2Number(){ return (op2==null)?UNKNOWN_VALUE:String.valueOf(op2.number);}
    public String getResultNumber(){ return (result==null)?UNKNOWN_VALUE:String.valueOf(result.number);}
    public int getLargest(){
        if(result.number >= op1.number && result.number >= op2.number){
            return result.number;
        }else if (op1.number >= op2.number){
            return op1.number;
        }else
        {
            return op2.number;
        }
    }


    public double getScore(){
        double temp=0;
        //todo log +1 * for */, log +
        if(currStatus==OP_UNTESTED){
            getStatus();
        }
        if(currStatus==OP_ADDITION || currStatus==OP_SUBTRACTION){
            temp+=Math.log10(op1.number)+Math.log10(op2.number)+Math.log10(result.number);
        }else if(currStatus==OP_MULTIPLICATION || currStatus==OP_DIVISION){
            temp+=(Math.log10(op1.number)+1)*(Math.log10(op2.number)+1)*(Math.log10(result.number)+1);
        }
        return temp;
    }
}
