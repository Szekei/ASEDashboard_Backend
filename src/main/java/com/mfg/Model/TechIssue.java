package com.mfg.Model;

/**
 * Created by I309908 on 1/17/2017.
 */
public class TechIssue {
    public String total;
    public Issue[] issues;

    @Override
    public String toString() {
        String xx = issues.length==0?"sss":issues[0].severity;
        return total +"success  "+ xx;
    }
}
