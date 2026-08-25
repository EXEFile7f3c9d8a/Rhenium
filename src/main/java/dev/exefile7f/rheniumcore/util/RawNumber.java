package dev.exefile7f.rheniumcore.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class RawNumber extends Number{
    private final String original;
    public RawNumber(String number){
        new BigDecimal(number);
        this.original = number;
    }
    public RawNumber(Byte number){this.original = number.toString();}
    public RawNumber(Short number){this.original = number.toString();}
    public RawNumber(Integer number){this.original = number.toString();}
    public RawNumber(Long number){this.original = number.toString();}
    public RawNumber(Float number){this.original = number.toString();}
    public RawNumber(Double number){this.original = number.toString();}
    public RawNumber(BigInteger number){this.original = number.toString();}
    public RawNumber(BigDecimal number){this.original = number.toPlainString();}
    public RawNumber(AtomicInteger number){this.original = number.toString();}
    public RawNumber(AtomicLong number){this.original = number.toString();}
    public RawNumber(AtomicReference<Number> number){this.original = number.toString();}
    @Override
    public String toString(){
        return this.original;
    }
    public String getOriginal(){
        return toString();
    }
    @Override
    public byte byteValue(){
        return Byte.parseByte(original);
    }
    @Override
    public short shortValue(){
        return Short.parseShort(original);
    }
    @Override
    public int intValue(){
        return Integer.parseInt(original);
    }
    @Override
    public long longValue(){
        return Long.parseLong(original);
    }
    @Override
    public float floatValue(){
        return Float.parseFloat(original);
    }
    @Override
    public double doubleValue(){
        return Double.parseDouble(original);
    }
}
