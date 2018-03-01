package com.mfg.Model;

/**
 * Created by I309908 on 1/17/2017.
 */
public class Msr {
    public String key;
    public float val;
    public String frmt_val;

    @Override
    public String toString() {
        return "key:"+key +"\n"
                + "val:" + val + "\n"
                + "frmt_val:" + frmt_val + "\n";
    }
}
