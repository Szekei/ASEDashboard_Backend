package com.mfg.Model;

import java.util.Arrays;

/**
 * Created by I309908 on 1/17/2017.
 */
public class CodeDebt {
    public String key;
    public String name;
    public Msr[] msr;

    @Override
    public String toString() {
        return "CodeDebt{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", msr=" + Arrays.toString(msr) +
                '}';
    }
}
