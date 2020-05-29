package com.alvin.loganalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CleanGrafana {

	public static void main(String[] args) throws IOException, ParseException {
		File dir = new File("/Users/lialiu/Downloads/network-export-0");
		File[] files = dir.listFiles();
		List<String> allData = new ArrayList<String>();
		Set<String> dupSet = new HashSet<String>();
		//2020-05-16T12:01:00
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date lastDate = null;
		Date endDate = sdf.parse("2020-05-27 11:59:45");
		for (File file2 : files) {
			System.out.println(file2);
			BufferedReader reader = new BufferedReader(new FileReader(file2));
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				if (line.contains("inbound")) {
					continue;
				}
				String lineData[] = line.split(";");
				String time = lineData[1].replace("\"", "").replace("-04:00", "").replace("T", " ");
				Date date = sdf.parse(time);
				if(date.getTime() >endDate.getTime()) {
					continue;
				}
				String value = lineData[2];
				if(dupSet.contains(time)) {
					System.out.println("dup::"+time+","+value);
					continue;
				}
				dupSet.add(time);
				if (value.endsWith("null")) {
					continue;
				}
		
				
				// System.out.println(time+","+value);
				allData.add(time + "," + value);
			}
		}
		Collections.sort(allData);
		
		for(String l:allData) {
			Date date = sdf.parse(l.split(",")[0]);
		if(lastDate != null && date.getTime() - lastDate.getTime() != 15000) {
			System.out.println(sdf.format(lastDate));
			System.out.println(sdf.format(date));
			System.out.println(date.getTime() - lastDate.getTime());
		}
		
		lastDate = date;
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/lialiu/Downloads/outbound.csv")));

		allData.forEach((d) -> {
			try {
				bw.write(d);
				bw.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		bw.close();
	}

}
