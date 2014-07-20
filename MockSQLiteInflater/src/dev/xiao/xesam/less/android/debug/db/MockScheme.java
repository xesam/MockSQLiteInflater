package dev.xiao.xesam.less.android.debug.db;

import java.util.HashMap;

/**
 * Created by xe on 14-7-18.
 */
public class MockScheme {
    private static final HashMap<String, String> TYPE_MAP = new HashMap<String, String>();

    static {
        TYPE_MAP.put("int", "integer");
        TYPE_MAP.put("float", "real");
        TYPE_MAP.put("string", "text");
    }

    public static String convertType(String inType) {
        if (TYPE_MAP.containsKey(inType)) {
            return TYPE_MAP.get(inType);
        } else {
            return inType;
        }
    }

    String name;
    String type;

    public MockScheme(String name, String type) {
        this.name = name;
        this.type = convertType(type);
    }

    public MockScheme(String name) {
        this.name = name;
        this.type = convertType("string");
    }

    @Override
    public String toString() {
        return "MockScheme{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
