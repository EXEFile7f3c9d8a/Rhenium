package dev.exefile7f.rheniumcore.util;

public class BitMask{
    private int nextBits;
    private int bits;
    public BitMask(){}
    public int create(){
        if(nextBits >= 64)return 0;
        return 1 << nextBits++;
    }
    public boolean isSet(int num){
        return (bits & num) != 0;
    }
    public BitMask enable(int num){
        bits |= num;
        return this;
    }
    public BitMask enable(int... nums){
        for(int i = 0; i < nums.length; i++){
            enable(nums[i]);
        }
        return this;
    }
    public BitMask disable(int num){
        bits &= ~num;
        return this;
    }
    public BitMask disable(int... nums){
        for(int i = 0; i < nums.length; i++){
            disable(nums[i]);
        }
        return this;
    }
    public BitMask flipAll(){
        bits = ~bits;
        return this;
    }
    public BitMask flip(int num){
        if(isSet(num))disable(num);
        else enable(num);
        return this;
    }
}
