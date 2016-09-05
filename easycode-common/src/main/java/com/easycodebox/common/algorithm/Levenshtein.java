package com.easycodebox.common.algorithm;

/**
 * 
 * Levenshtein distance最先是由俄国科学家Vladimir Levenshtein在1965年发明，用他的名字命名。可以叫它edit distance（编辑距离）。
 * 原理很简单，就是返回将第一个字符串转换(删除、插入、替换)成第二个字符串的编辑次数。次数越少，意味着字符串相似度越高
 * Levenshtein distance可以用来：
 * Spell checking(拼写检查)
 * Speech recognition(语句识别)
 * DNA analysis(DNA分析)
 * Plagiarism detection(抄袭检测) 
 *
 */
public class Levenshtein {

	private static int compare(String str, String target) {
		int d[][]; 	// 矩阵
		int n = str.length(),
			m = target.length(),
			i, 		// 遍历str的
			j,		// 遍历target的
			temp; 	// 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		char ch1, 	// str的
			ch2; 	// target的
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) { 
			// 初始化第一列
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) { 
			// 初始化第一行
			d[0][j] = j;
		}

		for (i = 1; i <= n; i++) { 
			// 遍历str
			ch1 = str.charAt(i - 1);
			// 去匹配target
			for (j = 1; j <= m; j++) {
				ch2 = target.charAt(j - 1);
				if (ch1 == ch2) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 左边+1,上边+1, 左上角+temp取最小
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
						+ temp);
			}
		}
		return d[n][m];
	}

	private static int min(int one, int two, int three) {
		return (one = one < two ? one : two) < three ? one : three;
	}

	/**
	 * 获取两字符串的相似度
	 * @param str
	 * @param target
	 * @return
	 */
	public static float getSimilarityRatio(String str, String target) {
		return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
	}

	public static void main(String[] args) {
		String str = "af范德萨恶化与会员价 ";
		String target = "a价员会与化恶萨德范af会员价";
		System.out.println("similarityRatio=" + Levenshtein.getSimilarityRatio(str, target));
	}
}
