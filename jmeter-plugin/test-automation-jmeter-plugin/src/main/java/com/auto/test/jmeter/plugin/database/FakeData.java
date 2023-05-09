package com.auto.test.jmeter.plugin.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeData{
	
	static Logger logger = LoggerFactory.getLogger(FakeData.class);
	
	public long[] from;
	public long[] to;
	public String[] dateFormat;
	public String[] prefix;
	public String[] postfix;
	boolean[] isDate;
	
	public void value(String val) {
		// {XXXX(1~2)YYYY|XXXX(1~2)YYYY|XXXX(1~2)YYYY|XXXX(1~2)YYYY}
		// {(20200101~20200330)}
		if(val.indexOf("|") >= 0) {
			String[] vs = val.split("[|]");
			Random r = new Random();
			value(vs[r.nextInt((vs.length - 0) + 1) + 0]);
		}else {
			List<String> list = this.findPattern("\\d+~\\d+", val);
			if(list.size() > 0) {
				prefix = new String[list.size()];
				postfix = new String[list.size()];
				from = new long[list.size()];
				to = new long[list.size()];
				isDate = new boolean[list.size()];
				dateFormat = new String[list.size()];
				int i = 0;
				int start = 0;
				int end = 0;
				for(String s: list) {
					logger.debug("-------["+val+"]-------");
					logger.debug("( 위치 : " + val.indexOf("("));
					logger.debug(") 위치 : " + (val.indexOf(")")+1));
					logger.debug("길이  : " + val.length());
					logger.debug("---------------------");
					if(val.indexOf("(") == 0 && val.indexOf(")", start)+1 == val.length()) {
						prefix[i]= "";
						postfix[i] = "";
					}else {
						if(val.indexOf("(",start) >= 0)	prefix[i] = val.substring(start,val.indexOf("(",start));
						else prefix[i] = "";
						if(val.indexOf(")", start)+1 != val.length()) {
							if(val.indexOf("(",val.indexOf(")", start)+1) >= 0)
							  postfix[i] = val.substring(val.indexOf(")",start)+1,val.indexOf("(",val.indexOf(")", start)+1));
							else
							  postfix[i] = val.substring(val.indexOf(")",start)+1);
						}
						else postfix[i] = "";
						start = val.indexOf(")", start)+1;
					}
					String[] ms = s.split("[~]");
					from[i] = Long.parseLong(ms[0]);
					to[i] = Long.parseLong(ms[1]);
					isDate[i] = false;
					if(ms[0].length() == 8 && ms[1].length() == 8) {
						try {
							dateFormat[i] = "yyyyMMdd";
							SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd"); 
							Date d1 = dt.parse(ms[0]);
							Date d2 = dt.parse(ms[1]);
							isDate[i] = true;
						}catch(Exception e) {
							isDate[i] = false;
						}
					}else if(ms[0].length() == 14 && ms[1].length() == 14 ) {
						try {
							dateFormat[i] = "yyyyMMddHHmmss";
							SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmmss"); 
							Date d1 = dt.parse(ms[0]);
							Date d2 = dt.parse(ms[1]);
							isDate[i] = true;
						}catch(Exception e) {
							isDate[i] = false;
						}
					}
					logger.debug("-------[Result]["+val+"]-------");
					logger.debug("Prefix: " + prefix[i]);
					logger.debug("Postfix : " + postfix[i]);
					logger.debug("from: " + from[i]);
					logger.debug("to: " + to[i]);
					logger.debug("---------------------");
					i++;
				}
			}else {
				prefix = new String[1];
				postfix = new String[1];
				from = new long[1];
				to = new long[1];
				prefix[0] = val;
				postfix[0] = val;
				from[0] = -1;
				to[0] = -1;
			}
		}
	}
	
	public String value() {
		Random r = new Random();
		logger.debug("From : " + from.length);
		logger.debug("Prefix : " + prefix.length);
		logger.debug("Postfix : " + postfix.length);
		logger.debug("DateFormat : " + dateFormat.length);
		StringBuilder sb = new StringBuilder();
		if(from[0] > 0 && to[0] > 0) {
			for(int i = 0; i < from.length ; i++) {
				String middle = "";
				if(isDate[i]) {
					try {
						SimpleDateFormat dt = new SimpleDateFormat(dateFormat[i]); 
						Date d1 = dt.parse(to[i]+"");
						Date d2 = dt.parse(from[i]+"");
						long calDate = d1.getTime() - d2.getTime();
						int rr = (int)(Math.random()*calDate)+1;
						Calendar c1 = Calendar.getInstance();
						c1.setTime(d2);
						c1.add(Calendar.MILLISECOND, rr);
						middle = dt.format(c1.getTime());
					}catch(Exception e) {
						logger.error(e.toString(),e);
					}
				}else {
					int x = (int)((to[i] - from[i]) + 1);
					middle = (r.nextInt(x) + from[i]) + "";
				}
				sb.append((prefix[i]==null ? "" : prefix[i])+middle+(postfix[i]==null ? "" : postfix[i]));
			}
		}else {
			return prefix[0];
		}
		return sb.toString();
	}
	
	public List<String> findPattern(String pattern, String value){
		List<String> list = new ArrayList<>();
		try {
			// 숫자 ~ 숫자 : \\d+~\\d+
			Pattern p = Pattern.compile("\\(("+pattern+")\\)");
			Matcher m = p.matcher(value);
			while (m.find()) {//Finds Matching Pattern in String
			      list.add(m.group(1));//Fetching Group from String
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}