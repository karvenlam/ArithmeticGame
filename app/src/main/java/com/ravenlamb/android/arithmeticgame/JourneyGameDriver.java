package com.ravenlamb.android.arithmeticgame;

import java.util.Arrays;

/**
 * Created by kl on 12/12/2014.
 * at least one number is greater than 10
 * for valid equation, animate operand cells and replace
 */
public class JourneyGameDriver extends BaseGameDriver {
    int operandMin=10;

    boolean op1OnGrid=false;
    boolean op2OnGrid=false;
    boolean resultOnGrid=false;

    public JourneyGameDriver(int r, int c) {
        super(r, c);
    }

    public void replaceOperandCells(){
        //todo
    }


    @Override
    protected int getStatus() {
        int superStatus= super.getStatus();
        if(superStatus!=OP_INVALID){
            int[] nums=new int[3];
            nums[0]=op1.number;
            nums[1]=op2.number;
            nums[2]=result.number;
            Arrays.sort(nums);

//            String newSet=getSetString(op1.number, op2.number,result.number);
            //one number must be >=10, at most have one 0
            if(nums[2]<operandMin || nums[1]==0){
                currStatus=OP_INVALID;
                return currStatus;
            }
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
}
