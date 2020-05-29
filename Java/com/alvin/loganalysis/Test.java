package com.alvin.loganalysis;

import java.util.HashSet;
import java.util.Set;

public class Test {

	public static void main(String[] args) {
		String a = "16,17,16,17,16,17,18,16,17,16,17,16,17,18,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,18,18,16,17,18,18,18,20,18,20,16,17,16,17,18,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,16,17,16,17,18,18,18,18,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,16,17,16,17,16,17,16,17,16,17,18,18,16,17,18,16,17,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,16,17,16,17,16,17,18,16,17,16,17,16,17,16,17,18,18,18,16,17,16,17,16,17,18,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,18,16,17,16,17,16,17,16,17,16,17,16,17,18,16,17,16,17,16,17,16,17,16,17,16,17,18,18,16,17,18,18,18,18,16,17,16,17,16,17,21,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,18,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,18,18,18,16,17,16,17,16,17,16,17,18,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17,16,17";

		findSubseq(a);
	}
	
	public static void findSubseq(String oriSeq) {
		String parts[] = oriSeq.split(",");
		String seq = null;
		int hit = -1;
		int hitBegin = -1;
		Set<String> seqSet = new HashSet<String>();
		for (int i = 0; i < parts.length - 1; i++) {
			if (seq == null) {
				hit = search(parts, parts[i], i + 1);
				if (hit > 0) {
					hitBegin = hit;
					seq = parts[i];
				}
			} else {
				if(i>=hitBegin) {
					if (seq.indexOf(",") > 0) {
						seqSet.add(seq);
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
							seqSet.add(seq);
						}
						seq = null;
					}
				} else {
					if (seq.indexOf(",") > 0) {
						seqSet.add(seq);
					}
					seq = null;
				}
			}
		}
		
		seqSet.forEach((x) -> System.out.println(x));
	}

	public static int search(String[] parts, String key, int from) {
		for (int j = from; j < parts.length; j++) {
			if (parts[j].equals(key)) {
				return j;
			}
		}
		return -1;
	}

}
