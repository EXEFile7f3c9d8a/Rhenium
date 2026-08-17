package dev.exefile7f.rheniumcore.util;

public class BitMask{
    private long nextBits;
    private long bits;
    public BitMask(){}
    public long create(){
        if(nextBits >= 64)return 0;
        return 1L << nextBits++;
    }
    public boolean isSet(long num){
        return (bits & num) != 0;
    }
    public BitMask enable(long num){
        bits |= num;
        return this;
    }
    public BitMask disable(long num){
        bits &= ~num;
        return this;
    }
}
