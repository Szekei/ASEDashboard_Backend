package com.mfg.Model;

import java.util.Arrays;

/**
 * Created by I309908 on 1/18/2017.
 */
public class BackEndCoverage {
    public String key;
    public String name;
    public Msr[] msr;

    @Override
    public String toString() {
        return "BackEndCoverage{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", msr=" + Arrays.toString(msr) +
                '}';
    }
}
