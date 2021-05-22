package com.inhatc.mapsosa;

import java.util.HashMap;
import java.util.Map;

public class PhoneNumInfo {
    public String strName;

    public PhoneNumInfo(){
        // Default constructor required for calls to
        // DataSnapshot.getValue(FirebasePost.class)
    }

    public PhoneNumInfo(String Name) { this.strName = Name; }

    public void mSet_CName(String Name) { this.strName = Name; }
    public String mGet_CName() { return strName; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Phone Number", strName);

        return result;
    }
}
