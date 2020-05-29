package com.alvin.loganalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class CleanLog2 {

	public static void main(String[] args) {
		File f = new File("/Users/lialiu/Downloads/log1.txt");
		FileReader fr = null;
		BufferedReader br = null;
		Set<String> oriSeqSet = new HashSet<String>();
		Map<String,Integer> seqMap = new TreeMap<String,Integer>();
		Map<String, String> threadMap = new HashMap<String, String>();
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				String[] lineParts = line.split(",");
				String time = lineParts[0];
				String thread = lineParts[2];
				String log = lineParts[3];
				if(threadMap.containsKey(thread)) {
					threadMap.put(thread, threadMap.get(thread)+ "," + log);
				}else {
					threadMap.put(thread,log);
				}
			}
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
		System.out.println(threadMap.size());
		// threadSet.forEach((x) -> System.out.println(x));
		for (String seq : threadMap.values()) {
			oriSeqSet.add(seq);
		}
		System.out.println(oriSeqSet.size());
		for (String oriSeq : oriSeqSet) {
//			if(oriSeq.contains("18,16,17,16,17,16,17,16,17,16,17,16,17")) {
//				System.out.println(oriSeq);
//			}
			for(Map.Entry<String, Integer> seqEntity:findSubseq(oriSeq).entrySet()){
				combineMap(seqMap, seqEntity.getKey(),seqEntity.getValue());
			}
		}
		System.out.println(seqMap.size());
		sortMap(seqMap);
//		seqSet.forEach((x) -> System.out.println(x));
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
        
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {  
            //升序排序  
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
                return o1.getValue().compareTo(o2.getValue());  
            }  
        });  
          
        for (Entry<String, Integer> e: list) {  
            System.out.println(e.getKey()+":"+e.getValue());  
        }  
	}
}
