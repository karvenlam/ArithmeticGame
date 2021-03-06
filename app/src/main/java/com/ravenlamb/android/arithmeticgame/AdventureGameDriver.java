package com.ravenlamb.android.arithmeticgame;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kl on 12/29/2014.
 */
public class AdventureGameDriver extends BaseGameDriver {
    public static final String TAG=AdventureGameDriver.class.getName();
    public static final String REASON_INCORRECT="Equation is not correct";
    public static final String REASON_TOO_SMALL="Operands are too small";

    int operandMin=10;
    String reasonStr;

    boolean op1OnGrid=true;
    boolean op2OnGrid=true;
    boolean resultOnGrid=true;

    protected int[][] dropDistance;

    public AdventureGameDriver(int r, int c) {
        super(r, c);
        dropDistance=new int[r][c];
        for (int i = 0; i < rows ; i++) {
            for (int j = 0; j < cols; j++) {
                dropDistance[i][j]=0;
            }
        }
    }



    public Coord[] replaceOperandCells(){
        //todo change from replacement to dropping

        for (int i = 0; i < rows ; i++) {
            for (int j = 0; j < cols; j++) {
                dropDistance[i][j]=0;
            }
        }

        ArrayList<Coord> tempList=new ArrayList<Coord>();
        if(op1OnGrid){
            Coord[] temp=op1.getAll();
            for(int i=0;i<temp.length;i++){
                cells[temp[i].x][temp[i].y]=-1;
            }
            op1OnGrid=false;
        }
        if(op2OnGrid){
            Coord[] temp=op2.getAll();
            for(int i=0;i<temp.length;i++){
                cells[temp[i].x][temp[i].y]=-1;
            }
            op2OnGrid=false;
        }
        if(resultOnGrid){
            Coord[] temp=result.getAll();
            for(int i=0;i<temp.length;i++){
                cells[temp[i].x][temp[i].y]=-1;
            }
            resultOnGrid=false;
        }

        for (int i = 0; i < rows ; i++) {
            int nextNum=cols-1;
            for (int j = cols-1; j >=0; j--) {
//              for (int j = 0; j < cols; j++) {
                //need to replace
                //dropDistance = j-numFrom,
                for (int k = nextNum; k >= 0; k--) {
                    if (cells[i][k] != -1) {
                        nextNum = k;
                        break;
                    }//TODO HERE
                    if(k==0 && cells[i][0]==-1){
                        nextNum=-1;
                    }
                }
                if(nextNum<0){
                    cells[i][j] = random.nextInt(10);

                }else {
                    cells[i][j] = cells[i][nextNum];

                }
                dropDistance[i][j]=j-nextNum;
                if(dropDistance[i][j]>0){
                    tempList.addAll(Arrays.asList(new Coord(i,j)));
                }
                nextNum--;



            }
        }

//        for (int i = 0; i < rows ; i++) {
//            for (int j = 0; j < cols; j++) {
//                Log.d(TAG,"replaceOperandCells ("+i+","+j+"):"+cells[i][j] );
//            }
//        }
        Coord[] coords=new Coord[tempList.size()];
        for(int i=0;i<coords.length;i++){
            coords[i]=tempList.get(i);
        }
        return coords;
    }

    public int getDropDistance(int x, int y){
        return dropDistance[x][y];
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
            //one number must be >=10, at most have one 0
//            if(nums[2]<operandMin || nums[1]==0){
            if( nums[1]==0){
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
