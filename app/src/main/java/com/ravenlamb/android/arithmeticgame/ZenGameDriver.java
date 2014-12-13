package com.ravenlamb.android.arithmeticgame;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by kl on 12/2/2014.
 */
public class ZenGameDriver extends BaseGameDriver {
//    public static final int OP_DUPLICATE=5;
    int operandMin=10;
//    int minOperandLength=2;

    HashSet<String> history;

    public ZenGameDriver(int r, int c) {
        super(r, c);
        history= new HashSet<String>();
    }

//    @Override
//    public boolean endingCoord(int x, int y) {
//        //todo make sure operand digit >1
////        return super.endingCoord(x,y);
//        if(super.endingCoord(x,y) && current.getNumberOfDigits()>=2){
//            return true;
//        }
//        result=null;
//        return false;
//    }

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
            //two numbers must be >=10, at most have one 0
            if(nums[1]<operandMin || nums[1]==0){
                currStatus=OP_INVALID;
                return currStatus;
            }
            String newSet= "{"+nums[0]+","+nums[1]+","+nums[2]+"}";
            if(history.contains(newSet)){
                currStatus=OP_INVALID;
                return currStatus;
            }
            history.add(newSet);
        }
        return superStatus;
    }

    @Override
    public String getCurrEquation() {
        if(currStatus==OP_UNTESTED){
            getStatus();
        }
        if( currStatus!=OP_INVALID){
            return super.getCurrEquation();
        }
        return "";
    }

//    public String getSetString(int n1, int n2, int n3){
//        int[] nums=new int[3];
//        nums[0]=n1;
//        nums[1]=n2;
//        nums[2]=n3;
//        Arrays.sort(nums);
//
//        return "{"+nums[0]+","+nums[1]+","+nums[2]+"}";
//    }



}
