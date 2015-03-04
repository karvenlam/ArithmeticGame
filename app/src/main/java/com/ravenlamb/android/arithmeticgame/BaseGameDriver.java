package com.ravenlamb.android.arithmeticgame;


import java.security.SecureRandom;


/**
 * Created by kl on 12/2/2014.
 * BaseGameDriver is the base class of the Arithmetic Game mechanics.
 *
 * BaseGameDriver generates a random numeric grid. Allows user to select 3 numbers in a straight line
 * one grid cell at a time. And verify whether the 3 numbers form a correct arithmetic equation.
 *
 * operand1 (+-x/) operand2 = result
 *
 * @author Karven Lam
 * @version 1.0 2014-12-14
 * @see com.ravenlamb.android.arithmeticgame.BaseGridView
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
    public static final int OP_NEGATIVE_SUBTRACTION=5;

    public static final String[] OPERATORS= new String[]{"?", "+", "-", "\u00D7", "\u00F7","-"};


    protected int rows=6;
    protected int cols=6;
    protected int[][] cells;

    SecureRandom random;//random.nextInt(10);

    //current status of arithmetic equation
    protected int currStatus;//OP_UNTESTED, OP_INVALID, ...

    protected Operand op1;
    protected Operand op2;
    protected Operand result;
    //currently partially selected
    protected Operand current;

    public BaseGameDriver(int r, int c){
        rows=r;
        cols=c;
        cells=new int[rows][cols];

        random=new SecureRandom();
        initGrid();
    }

    public void initGrid(){
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                cells[i][j]=random.nextInt(10);
            }
        }
    }

    public static boolean isValidOperator(int operator){
        return (operator==BaseGameDriver.OP_ADDITION ||
                operator==BaseGameDriver.OP_SUBTRACTION ||
                operator==BaseGameDriver.OP_MULTIPLICATION ||
                operator==BaseGameDriver.OP_DIVISION ||
                operator==BaseGameDriver.OP_NEGATIVE_SUBTRACTION);
    }

    /**
     * Start selecting a new number at position (x, y) if position is not already selected.
     * If result already contains a number, shift the operands to the left.
     * Should be called by onTouchEvent ACTION_DOWN
     *
     * @param x row x in the grid
     * @param y column y in the grid
     */
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

    /**
     * Continue selecting the number. If position has not been selected, attempt to change the ending
     * coordinate of the current selecting number.
     * Should be called by ACTION_MOVE
     *
     * @param x
     * @param y
     */
    public void hoveringCoord(int x, int y){
        if(current==null){
            return;//ACTION_DOWN coordinate is not valid
        }
        if(!current.isPartOfOperand(x,y) && getCellStatus(x,y)!=CELL_NOTSELECTED){
            current=null;//intersecting another operand
            return;
        }
        if(current.startCoord.x!=x || current.startCoord.y!=y){
            current.changeEndCoord(x, y);
        }
//        Log.d(TAG,"hoveringCoord: "+x+" "+y );
    }


    /**
     * Ending the selection of the current number. Assign current selected number to result.
     * Call the computeStatus method to test whether this equation is correct.
     * Should be called by onTouchEvent ACTION_UP
     *
     * @param x
     * @param y
     * @return return true if starting point and ending point of current is valid and the value of current is assigned to result.
     * @see #computeStatus()
     */
    public boolean endingCoord(int x, int y){
        //return whether endCoord is valid
        if(current==null ){
            return false;//starting coordinate is not valid
        }
        if(getCellStatus(x,y)==CELL_NOTSELECTED){//(x,y) should be selected from hoveringCoord, but just in case
            current.changeEndCoord(x,y);//attempt to change end coordinate for current
        }
        if(current.isPointsInStraightLine()){//double checking starting point and ending point of current is in straight line
            result=current;
            current=null;
            computeStatus();
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

    /**
     * compute the current status of this object. Value can be OP_INVALID, OP_ADDITION, OP_SUBTRACTION,
     * OP_MULTIPLICATION, OP_DIVISION.
     *
     * @return the current equation status
     */
    protected int computeStatus(){
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
        }else if(op2.number != 0 && op1.number == op2.number * result.number){
            currStatus= OP_DIVISION;
        }else if(op1.number-op2.number == -result.number){
            currStatus= OP_NEGATIVE_SUBTRACTION;
        }
//        Log.d(TAG,"computeStatus: "+currStatus );
        return currStatus;
    }

    /**
     * return the current equation in a string if it is correct.
     * most likely used for debugging
     * @return the current equation in a string
     */
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

    /**
     * shift the operands and the properties of the operands one position to the left.
     * set the result and current selected number to null.
     * used when starting to select a new number
     */
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

    /**
     *
     * @param x
     * @param y
     * @return return the number stored in position x, y
     */
    public int getCellNum(int x, int y){
        if(cells !=null && x<rows && y<cols){
            return cells[x][y];
        }else
        {
            return -1;
        }
    }

    /**
     * Return the status at position x, y
     * Either not selected, part of operand1, operand2, result or currently selected number
     * @param x
     * @param y
     * @return
     */
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

    /**
     * Operand class, include starting coordinate, and ending coordinate
     * the selected number is stored after each change of ending coordinate
     *
     */
    public class Operand{
        public Coord startCoord;
        public Coord endCoord;
        public int number;
        int stepX=0;
        int stepY=0;
        int numberOfDigits=0;

        /**
         * initialize operand and try to compute number if coordinates are in a straight line
         * usually initialized with a pair of identical coordinate
         * @param s
         * @param e
         */
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

        /**
         * obtain the number from the line formed by the coordinates on the grid
         * if the number starts with zero, it is zero
         */
        protected void computeNumber(){
            //no leading zero
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
//                    if number has a leading zero, it is zero
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

        /**
         *
         * @param x
         * @param y
         * @return
         */
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


        /**
         * Return whether position x, y is part of the operand
         * @param x
         * @param y
         * @return true if position x, y is part of the operand
         */
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
            return slopexfactor == slopeyfactor && slopexfactor < numberOfDigits && slopexfactor > 0;
        }

        /**
         *
         * @return Return true if start coordinate and end coordinate are in a vertical, horizontal or diagonal line
         */
        public boolean isPointsInStraightLine(){
            return isPointsInStraightLine(startCoord.x, startCoord.y, endCoord.x, endCoord.y);
        }

        /**
         *
         * @param start_x
         * @param start_y
         * @param end_x
         * @param end_y
         * @return Return true if start coordinate and end coordinate are in a vertical, horizontal or diagonal line
         */
        public boolean isPointsInStraightLine(int start_x, int start_y, int end_x, int end_y){
            //end coord is valid if it's in the same row, the same column or in diagonal as the start coord
            if(start_x==end_x || start_y==end_y
                    || Math.abs(start_x-end_x)==Math.abs(start_y-end_y)){
                return true;
            }else
            {
                return false;
            }
        }

        /**
         *
         * @return return all coordinates between starting coordinate and ending coordinate inclusive
         */
        public Coord[] getAll(){
            return getAll(startCoord.x, startCoord.y, endCoord.x, endCoord.y);
        }

        /**
         *
         * @param start_x
         * @param start_y
         * @param end_x
         * @param end_y
         * @return return all coordinates between starting coordinate and ending coordinate inclusive
         */
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

        /**
         * Return 0 if a=b, 1 if b>a, -1 if a>b
         * used to calculate direction from starting coordinate to ending coordinate
         * @param a
         * @param b
         * @return
         */
        public int returnStep(int a, int b){
            if(b>a){
                return 1;
            }else if(a>b){
                return -1;
            }
            return 0;
        }

        /**
         * Return the length of the operand
         * @return
         */
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

        /**
         * Return the length of operand between starting coordinate and ending coordinate
         * @param start_x
         * @param start_y
         * @param end_x
         * @param end_y
         * @return
         */
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

    /**
     * return the largest of the operand
     * @return
     */
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



    /**
     * score calculation
     * @return
     */
    public double getScore(){
        double temp=0;
        //todo log +1 * for */, log +
        //todo change to simpler score, change log base to logScore
        if(currStatus==OP_UNTESTED){
            computeStatus();
        }
        if(currStatus==OP_ADDITION || currStatus==OP_SUBTRACTION || currStatus==OP_NEGATIVE_SUBTRACTION){
            temp+=op1.number+op2.number+result.number;
        }else if(currStatus==OP_MULTIPLICATION || currStatus==OP_DIVISION){
            int op1num=(op1.number==0)?1:op1.number;
            int op2num=(op2.number==0)?1:op2.number;
            int resultnum=(result.number==0)?1:result.number;
            temp+=(op1.number+op2.number+result.number)*((Math.log10(op1num)+1)*(Math.log10(op2num)+1)*(Math.log10(resultnum)+1));
        }
        return temp;
    }


    /**
     * log score calculation
     * @return
     */
    public double getLogScore(){
        double temp=0;
        //todo log +1 * for */, log +
        //todo change to simpler score, change log base to logScore
        if(currStatus==OP_UNTESTED){
            computeStatus();
        }
        int op1num=(op1.number==0)?1:op1.number;
        int op2num=(op2.number==0)?1:op2.number;
        int resultnum=(result.number==0)?1:result.number;
        if(currStatus==OP_ADDITION || currStatus==OP_SUBTRACTION){
            temp+=Math.log10(op1num)+Math.log10(op2num)+Math.log10(resultnum);
        }else if(currStatus==OP_MULTIPLICATION || currStatus==OP_DIVISION){
            temp+=(Math.log10(op1num)+1)*(Math.log10(op2num)+1)*(Math.log10(resultnum)+1);
        }
        return temp;
    }
}
