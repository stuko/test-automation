package com.kcb.fds.jmeter.plugin.common.factor.value.fake;

import java.util.ArrayList;
import java.util.List;

public class FakeMetaData {
	private List<FakeData> fakeDataList;
	public FakeMetaData() {
		setFakeDataList(new ArrayList<>());
	}
	public boolean isFakeData(String l){
		if(l.indexOf("{") >= 0 && l.indexOf("}") >= 0 ) return true;
		else return false;
	}
	public void importData(String line) {
		String[] lines = line.split("[,]");
		for(String l : lines) {
			if(l.indexOf("{") >= 0 && l.indexOf("}") >= 0 ) {
				String m = l.substring(l.indexOf("{")+1,l.indexOf("}"));
				FakeData f = new FakeData();
				f.value(m);
				getFakeDataList().add(f);
			}
		}
	}

	public List<FakeData> getFakeDataList() {
		return fakeDataList;
	}

	public void setFakeDataList(List<FakeData> fakeDataList) {
		this.fakeDataList = fakeDataList;
	}
}

