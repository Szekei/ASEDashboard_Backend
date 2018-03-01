package com.mfg.Model;

import java.util.Arrays;

/**
 * Created by I309908 on 1/23/2017.
 */
public class BackEndDetail {
    public Cells[] cells;

    @Override
    public String toString() {
        return "BackEndDetail{" +
                "cells=" + Arrays.toString(cells) +
                '}';
    }
}
