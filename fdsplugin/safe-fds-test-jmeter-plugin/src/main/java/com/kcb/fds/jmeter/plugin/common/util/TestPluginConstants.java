package com.kcb.fds.jmeter.plugin.common.util;

public class TestPluginConstants {
    public static String fds_data_path = "./fds/data/";
    public static String file_encoding = "UTF-8";
    public static String fds_test_define_path = "./fds/";
    public static String fds_test_define_file = "test-define.txt";
    public static String fds_ahnlab_define_file = "ahnlab-define.txt";
    public static String fds_random_key_data = "@{RAND_KEY}";
    public static String fds_ref_data = "@REF"; // @REF(addr)
    public static String fds_substring_data = "@SUBSTR"; // @SUBSTR(ZZZZ,0,1)
    public static String fds_substring_from = "FROM"; // @SUBSTR(ZZZZ,0,1)
    public static String fds_substring_to = "TO"; // @SUBSTR(ZZZZ,0,1)
    public static String fds_like_data = "%"; // @SUBSTR(ZZZZ,0,1)
    public static int fds_random_char_length = 15;
    public static String eq = "=";
    public static String lt = "<";
    public static String gt = ">";
    public static String ne = "!=";
    public static String lte = "<=";
    public static String gte = ">=";
    public static String like = "like";
    
}
