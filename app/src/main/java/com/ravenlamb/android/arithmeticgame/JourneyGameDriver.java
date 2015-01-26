package com.ravenlamb.android.arithmeticgame;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kl on 12/12/2014.
 * at least one number is greater than 10
 * for valid equation, animate operand cells and replace
 */
public class JourneyGameDriver extends BaseGameDriver {
    public static final String REASON_INCORRECT="Equation is not correct";
    public static final String REASON_TOO_SMALL="Operands are too small";

    int operandMin=10;
    String reasonStr;

    boolean op1OnGrid=true;
    boolean op2OnGrid=true;
    boolean resultOnGrid=true;

    public JourneyGameDriver(int r, int c) {
        super(r, c);
    }

    public Coord[] replaceOperandCells(){
        //todo
        ArrayList<Coord> tempList=new ArrayList<Coord>();
        if(op1OnGrid){
            Coord[] temp=op1.getAll();
            for(int i=0;i<temp.length;i++){
                cells[temp[i].x][temp[i].y]=random.nextInt(10);
            }
            op1OnGrid=false;
            tempList.addAll(Arrays.asList(temp));
        }
        if(op2OnGrid){
            Coord[] temp=op2.getAll();
            for(int i=0;i<temp.length;i++){
                cells[temp[i].x][temp[i].y]=random.nextInt(10);
            }
            op2OnGrid=false;
            tempList.addAll(Arrays.asList(temp));
        }
        if(resultOnGrid){
            Coord[] temp=result.getAll();
            for(int i=0;i<temp.length;i++){
                cells[temp[i].x][temp[i].y]=random.nextInt(10);
            }
            resultOnGrid=false;
            tempList.addAll(Arrays.asList(temp));
        }
        Coord[] coords=new Coord[tempList.size()];
        for(int i=0;i<coords.length;i++){
            coords[i]=tempList.get(i);
        }
        return coords;
    }

    @Override
    public void shiftOperand() {
        super.shiftOperand();
        op1OnGrid=op2OnGrid;
        op2OnGrid=resultOnGrid;
        resultOnGrid=true;
    }

    @Override
    protected int computeStatus() {
        int superStatus= super.computeStatus();
        reasonStr="";
        if(superStatus!=OP_INVALID){
            int[] nums=new int[3];
            nums[0]=op1.number;
            nums[1]=op2.number;
            nums[2]=result.number;
            Arrays.sort(nums);

//            String newSet=getSetString(op1.number, op2.number,result.number);
//            one number must be >=10, at most have one 0
//            if(nums[2]<operandMin || nums[1]==0){
            if(nums[1]==0){
                currStatus=OP_INVALID;
                reasonStr=REASON_TOO_SMALL;
                return currStatus;
            }
        }else{

            reasonStr=REASON_INCORRECT;
        }
        return superStatus;
    }

    @Override
    public int getCellStatus(int x, int y) {
        if(op1 !=null && op1OnGrid && op1.isPartOfOperand(x,y)){
            return CELL_OPERAND1;
        }
        if(op2 !=null && op2OnGrid && op2.isPartOfOperand(x,y)){
            return CELL_OPERAND2;
        }
        if(result !=null && resultOnGrid && result.isPartOfOperand(x,y)){
            return CELL_RESULT;
        }
        if(current !=null && current.isPartOfOperand(x,y)){
            return  CELL_CURR_COORD;
        }

        return CELL_NOTSELECTED;
    }

    /**
     * Return reason for invalid equation
     * @return
     */
    public String invalidReason(){
        if(currStatus==OP_UNTESTED){
            computeStatus();
        }
        return reasonStr;

    }
}
