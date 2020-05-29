package com.alvin.loganalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class CleanLog3 {

	public static void main(String[] args) throws IOException, ParseException {
		Map<String, Double> dateMap = loadDate("/Users/lialiu/Downloads/outbound.csv");

		// dateMap.forEach((x,y) -> System.out.println(x+","+y));
		System.out.println(dateMap.size());

		File f = new File("/Users/lialiu/Downloads/2020-05-16_log.txt");
		FileReader fr = null;
		BufferedReader br = null;
		Set<String> oriSeqSet = new HashSet<String>();
		Map<String, Integer> seqMap = new TreeMap<String, Integer>();
		Map<String, String> threadMap = new HashMap<String, String>();
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/lialiu/Downloads/log_2.csv")));
			
			String line;
			while ((line = br.readLine()) != null) {
				String[] lineParts = line.split(",");
				String date = lineParts[0];
				String time = lineParts[1];
				time = time.substring(0, time.length() - 4);
				String thread = lineParts[2];
				String log = lineParts[3];
				Double v = dateMap.get(date + " " + time);
				if (v == null) {
					System.out.println(date + " " + time);
				}
				bw.write(log+","+ v.intValue());
				bw.newLine();
			}
			
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Map<String, Integer> findSubseq(String oriSeq) {
		String parts[] = oriSeq.split(",");
		String seq = null;
		int hit = -1;
		int hitBegin = -1;
		Map<String, Integer> seqMap = new HashMap<String, Integer>();
		for (int i = 0; i < parts.length - 1; i++) {
			if (seq == null) {
				hit = search(parts, parts[i], i + 1);
				if (hit > 0) {
					hitBegin = hit;
					seq = parts[i];
				}
			} else {
				if (i >= hitBegin) {
					if (seq.indexOf(",") > 0) {
						combineMap(seqMap, seq, 1);
					}
					seq = null;
					continue;
				}
				hit++;
				if (hit < parts.length) {
					if (parts[i].equals(parts[hit])) {
						seq += "," + parts[i];
					} else {
						if (seq.indexOf(",") > 0) {
							combineMap(seqMap, seq, 1);
						}
						seq = null;
					}
				} else {
					if (seq.indexOf(",") > 0) {
						combineMap(seqMap, seq, 1);
					}
					seq = null;
				}
			}
		}

		return seqMap;
	}

	public static void combineMap(Map<String, Integer> map, String key, Integer count) {
		map.put(key, map.computeIfAbsent(key, (x) -> 0) + count);
	}

	public static int search(String[] parts, String key, int from) {
		for (int j = from; j < parts.length; j++) {
			if (parts[j].equals(key)) {
				return j;
			}
		}
		return -1;
	}

	public static void sortMap(Map map) {
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			// 升序排序
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		for (Entry<String, Integer> e : list) {
			System.out.println(e.getKey() + ":" + e.getValue());
		}
	}

	public static Map<String, Double> loadDate(String path) throws IOException, ParseException {
		File f = new File(path);
		FileReader fr = null;
		BufferedReader br = null;
		Map<String, Double> dateMap = new TreeMap<String, Double>();
		fr = new FileReader(f);
		br = new BufferedReader(fr);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String line;
		Double lastV = null;
		Date lastD = null;
		while ((line = br.readLine()) != null) {
			String linePart[] = line.split(",");
			Date d = sdf.parse(linePart[0]);
			Double v = Double.parseDouble(linePart[1]);
			if (lastV != null) {
				Double gap = v - lastV;
				Double step = (v - lastV) / 15;
				for (int i = 1; i < 15; i++) {
					dateMap.put(sdf.format(new Date(lastD.getTime() + 1000 * i)), lastV + step * i);
				}
			}
			dateMap.put(linePart[0], v);

			lastD = d;
			lastV = v;
		}

		br.close();
		return dateMap;
	}
}
