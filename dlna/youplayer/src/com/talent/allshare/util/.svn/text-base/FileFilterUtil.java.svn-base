package com.talent.allshare.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFilterUtil {
	public static List<File> getFiles(String path,String[] postfix) {
		File file = new File(path);
		File[] files = file.listFiles();
		List<File> lists = new ArrayList();
		if(files == null || files.length == 0 ){
			return null;
		}
		for (int i = 0; i < files.length; i++) {
			File childFile = files[i];
			System.out.println(""+childFile.getPath());
			if (childFile.isFile()) {
				childFile.getName();
				int index = childFile.getName().lastIndexOf(".");
				index++;
				String name = childFile.getName().substring(index,
						childFile.getName().length());
				for (int j = 0; j < postfix.length; j++) {
					if (name.equalsIgnoreCase(postfix[j])) {
						lists.add(childFile);
					}
				}

			} else if (childFile.isDirectory()
					&& formateToDepth(childFile.getAbsolutePath())) {
				List<File> lsts = getFiles(childFile.getAbsolutePath(), postfix);
				if(lsts != null&& lsts.size() != 0){
					lists.addAll(lsts);
				}
			}
		}
		return lists;
	}
	
	/**
	 * 扫描深度
	 * 
	 * @date 2013-5-14
	 * @param path
	 * @return
	 */
	public static boolean formateToDepth(String path) {
		int depth = 0;
		char[] chs = path.toCharArray();
		for (char ch : chs) {
			if (ch == '/') {
				depth++;
			}
		}

		if (depth <= 7) {
			return true;
		} else {
			return false;
		}
	}
}
