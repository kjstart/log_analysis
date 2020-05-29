package com.alvin.loganalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanLog {

	public static void main(String[] args) {
		// 2020-05-26 21:21:11.853 ERROR 1 --- [-8-thread-23097]
		// o.h.e.j.b.i.BatchingBatch : HHH0003
		// 2020-05-26 21:23:00.850 INFO 1 --- [-8-thread-23097]
		// c.o.s.b.r.i.SfdcHttpRequestBase : Reque
		// date time level 1 --- thread class message
		Pattern p = Pattern.compile(
				"^(\\d{4}-\\d{2}-\\d{2})\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S{1}.*)$");
		GroupLCSMap map = new GroupLCSMap();

		File f = new File(args[0]);
		FileReader fr = null;
		BufferedReader br = null;
		int lineCounter = 0;
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				lineCounter++;
				if (lineCounter % 1000000 == 0) {
					System.out.println("Scaned line:" + lineCounter);
					if (lineCounter % 1000000 == 0) {
						System.out.println("LCS size:" + map.getLCSSize());
					}
				}

				Matcher matcher = p.matcher(line);
				if (matcher.find()) {
					map.insert(matcher.group(7), matcher.group(3), matcher.group(8).substring(2));
				}

				continue;
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

		p("Finish index.");
		BufferedWriter bwLogObj = null;
		try {
			bwLogObj = new BufferedWriter(new FileWriter(new File(args[1])));
			map.printLCS(bwLogObj);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				bwLogObj.flush();
				bwLogObj.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// p(map.toString());
		BufferedWriter bwLog = null;
		try {
			bwLog = new BufferedWriter(new FileWriter(new File(args[2])));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		lineCounter = 0;
		f = new File(args[0]);
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);

			String line;
			Integer count = 0;
			while ((line = br.readLine()) != null) {
				lineCounter++;
				if (lineCounter % 1000000 == 0) {
					System.out.println("Scaned line:" + lineCounter);
				}

				Matcher matcher = p.matcher(line);
				if (matcher.find()) {
					LogMessage msg = new LogMessage();
					msg.setDate(matcher.group(1));
					msg.setTime(matcher.group(2));
					msg.setLevel(matcher.group(3));
					msg.setThread(matcher.group(6));
					msg.setLogClass(matcher.group(7));
					msg.setMessage(matcher.group(8).substring(2));
					LCSObject mapObj = map.getMatch(msg.getLogClass(), msg.getMessage());
					if (mapObj != null) {
						try {
							bwLog.write(msg.getDate() + "||" + msg.getTime() + "||" + msg.getThread() + "||"
									+ mapObj.getId());
							bwLog.newLine();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						count++;
					}
				}

				continue;
			}

			System.out.println("total found:" + count);
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
				bwLog.flush();
				bwLog.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void p(String s) {
		System.out.println(s);
	}
}
