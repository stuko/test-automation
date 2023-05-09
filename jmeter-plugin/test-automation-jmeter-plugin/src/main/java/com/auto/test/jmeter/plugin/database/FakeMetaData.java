package com.auto.test.jmeter.plugin.database;

import java.util.ArrayList;
import java.util.List;

public class FakeMetaData{
	List<FakeData> fakeDataList;
	public FakeMetaData() {
		fakeDataList = new ArrayList<>();
	}
	public void importData(String line) {
		String[] lines = line.split("[,]");
		for(String l : lines) {
			if(l.indexOf("{") >= 0 && l.indexOf("{") >= 0 ) {
				String m = l.substring(l.indexOf("{")+1,l.indexOf("}"));
				FakeData f = new FakeData();
				f.value(m);
				fakeDataList.add(f);
			}
		}
	}
}

