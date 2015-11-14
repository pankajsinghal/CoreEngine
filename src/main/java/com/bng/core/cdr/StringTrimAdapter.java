package com.bng.core.cdr;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringTrimAdapter extends XmlAdapter<String, String> {
    @Override
    public String unmarshal(String v) throws Exception {
        if (v == null)
            return null;
        return v.trim().toLowerCase();
    }
    @Override
    public String marshal(String v) throws Exception {
        if (v == null)
            return null;
        return v.trim().toLowerCase();
    }
}