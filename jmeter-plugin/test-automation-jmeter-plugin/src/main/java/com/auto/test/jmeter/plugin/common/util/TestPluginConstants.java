package com.auto.test.jmeter.plugin.common.util;

public class TestPluginConstants {
    public static String ta_data_path = "./test/data/";
    public static String file_encoding = "UTF-8";
    public static String ta_test_define_path = "./test/";
    public static String ta_test_define_file = "test-define.txt";
    public static String ta_refer_define_file = "refer-define.txt";
    public static String ta_random_key_data = "@{RAND_KEY}";
    public static String ta_ref_data = "@REF"; // @REF(addr)
    public static String ta_substring_data = "@SUBSTR"; // @SUBSTR(ZZZZ,0,1)
    public static String ta_substring_from = "FROM"; // @SUBSTR(ZZZZ,0,1)
    public static String ta_substring_to = "TO"; // @SUBSTR(ZZZZ,0,1)
    public static String ta_like_data = "%"; // @SUBSTR(ZZZZ,0,1)
    public static int ta_random_char_length = 15;
    public static String eq = "=";
    public static String lt = "<";
    public static String gt = ">";
    public static String ne = "!=";
    public static String lte = "<=";
    public static String gte = ">=";
    public static String like = "like";
    
}
