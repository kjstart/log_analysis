package com.alvin.loganalysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GroupLCSMap {
	private static final ConcurrentMap<String, LCSMap> groupMap = new ConcurrentHashMap<String, LCSMap>();
	private int lineId = 0;

	public void insert(String logClass, String level, String message) {
		LCSMap map = groupMap.computeIfAbsent(logClass, (x) -> new LCSMap());
		map.insert(level, message, lineId++);
	}

	public LCSObject getMatch(String logClass, String message) {
		LCSMap map = groupMap.get(logClass);
		if (map != null) {
			return map.getMatch(message);
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		groupMap.entrySet().forEach((e) -> {
			String logClass = e.getKey();
			for (int i = 0; i < e.getValue().size(); i++) {
				sb.append("\tObject " + i + ", entryCount: " + e.getValue().objectAt(i).count() + ":\n\t\t" + logClass
						+ " : " + e.getValue().objectAt(i).toString() + "\n");
			}
		});
		return sb.toString();
	}

	public void printLCS() {
		groupMap.entrySet().forEach((e) -> {
			String logClass = e.getKey();
			for (int i = 0; i < e.getValue().size(); i++) {
				LCSObject logObj = e.getValue().objectAt(i);
				System.out.println(logObj.getId() + "||" + logObj.count() + "||" + logObj.getLevel() + "||" + logClass + "||" + logObj.getMessage());
			}
		});
	}
	
	public void printLCS(BufferedWriter bw) {
		groupMap.entrySet().forEach((e) -> {
			String logClass = e.getKey();
			for (int i = 0; i < e.getValue().size(); i++) {
				LCSObject logObj = e.getValue().objectAt(i);
				try {
					bw.write(logObj.getId() + "||" + logObj.count() + "||" + logObj.getLevel() + "||" + logClass + "||" + logObj.getMessage());
					bw.newLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	public int getLCSSize() {
		int size = 0;
		for(LCSMap subMap: groupMap.values()) {
			size += subMap.size();
		}
		return size;
	}
}
