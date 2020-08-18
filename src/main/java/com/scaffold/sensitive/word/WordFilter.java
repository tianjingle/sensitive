package com.scaffold.sensitive.word;


import com.scaffold.sensitive.utils.SensitiveHelper;
import com.scaffold.sensitive.utils.StopWordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fanhua
 * @version 1.0
 */
@Slf4j
@Service
public class WordFilter {

	@Autowired
	private SensitiveHelper sensitiveHelper;

	private enum FilterState{SUCCESS,ERROR};
	private enum KeywordType {FILTER,STOP};
	private static FilterState FilterStatus =  FilterState.ERROR;
	private static Map<Integer,Integer> set = new HashMap<Integer,Integer>(); // 存储首字
	private static Map<Integer, WordNode> nodes = new HashMap<Integer, WordNode>(); // 存储节点
	private static Set<Integer> stopwdSet = new HashSet<Integer>(); // 停顿词
	private static char SIGN = '*'; // 敏感词过滤替换

	
	/**
	 * 初始化
	 */
	public void init() {
		// 获取敏感词
		if(FilterStatus == FilterState.ERROR) {
			long time1 = System.currentTimeMillis();
			addSensitiveWord(readWordFromFile(KeywordType.FILTER));
			addStopWord(StopWordUtils.getStopWord());
			long time2 = System.currentTimeMillis();
			log.info("敏感词数量:"+nodes.size());
			log.info("加载过滤库时间" + (time2 - time1) +"ms");
			FilterStatus = FilterState.SUCCESS;
		}
	}
	/**
	 * 设置
	 */
	public  void resetInit() {
		set.clear();
		nodes.clear();
		addSensitiveWord(readWordFromFile(KeywordType.FILTER));
		log.info("敏感词数量:" + nodes.size());
	}

	/**
	 * 增加敏感词
	 * @return
	 */
	private List<String> readWordFromFile(KeywordType type) {
		List<String> words = sensitiveHelper.getSensitives();
		return words;
	}


	/**
	 * 增加停顿词
	 * 
	 * @param words
	 */
	private void addStopWord(final List<String> words) {
		if (words != null && words.size() > 0) {
			char[] chs;
			for (String curr : words) {
				chs = curr.toCharArray();
				for (char c : chs) {
					stopwdSet.add(charConvert(c));
				}
			}
		}
	}

	/**
	 * 添加DFA节点
	 * @param words
	 */
	private void addSensitiveWord(final List<String> words) {
		if (words != null && words.size() > 0) {
			char[] chs;
			int fchar;
			int lastIndex;
			WordNode fnode; // 首字母节点
			for (String curr : words) {
				chs = curr.toCharArray();
				if(chs.length  == 0)
					continue;
				fchar = charConvert(chs[0]);
				if (!set.containsKey(fchar)) {// 没有首字定义
					set.put(fchar, fchar);// 首字标志位 可重复add,反正判断了，不重复了
					fnode = new WordNode(fchar, chs.length == 1);
					nodes.put(fchar, fnode);
				} else {
					fnode = nodes.get(fchar);
					if (!fnode.isLast() && chs.length == 1)
						fnode.setLast(true);
				}
				lastIndex = chs.length - 1;
				for (int i = 1; i < chs.length; i++) {
					fnode = fnode.addIfNoExist(charConvert(chs[i]), i == lastIndex);
				}
			}
		}
	}

	/**
	 * 过滤判断 将敏感词转化为成屏蔽词
	 * @param src
	 * @return
	 */
	public final String doFilter(final String src) {
		init();
		char[] chs = src.toCharArray();
		int length = chs.length;
		int currc;
		int k;
		WordNode node;
		for (int i = 0; i < length; i++) {
			currc = charConvert(chs[i]);
			if (!set.containsKey(currc)) {
				continue;
			}
			node = nodes.get(currc);// 日 2
			if (node == null)// 其实不会发生，习惯性写上了
				continue;
			boolean couldMark = false;
			int markNum = -1;
			if (node.isLast()) {// 单字匹配（日）
				couldMark = true;
				markNum = 0;
			}
			k = i;
			for (; ++k < length;) {
				int temp = charConvert(chs[k]);
				if (stopwdSet.contains(temp))
					continue;
				node = node.querySub(temp);
				if (node == null)// 没有了
					break;
				if (node.isLast()) {
					couldMark = true;
					markNum = k - i;// 3-2
				}
			}
			if (couldMark) {
				for (k = 0; k <= markNum; k++) {
					chs[k + i] = SIGN;
				}
				i = i + markNum;
			}
		}

		return new String(chs);
	}
	
	/**
	 * 是否包含敏感词
	 * @param src
	 * @return
	 */
	public final boolean isContains(final String src) {
		if (FilterStatus != FilterState.SUCCESS){
			init();
		}
		char[] chs = src.toCharArray();
		int length = chs.length;
		int currc;
		int k;
		WordNode node;
		for (int i = 0; i < length; i++) {
			currc = charConvert(chs[i]);
			if (!set.containsKey(currc)) {
				continue;
			}
			node = nodes.get(currc);// 日 2
			if (node == null)// 其实不会发生，习惯性写上了
				continue;
			boolean couldMark = false;
			if (node.isLast()) {// 单字匹配（日）
				couldMark = true;
			}
			// 继续匹配（中国/美丽中国），以长的优先
			k = i;
			for (; ++k < length;) {
				int temp = charConvert(chs[k]);
				if (stopwdSet.contains(temp))
					continue;
				node = node.querySub(temp);
				if (node == null)// 没有了
					break;
				if (node.isLast()) {
					couldMark = true;
				}
			}
			if (couldMark) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 大写转化为小写 全角转化为半角
	 * 
	 * @param src
	 * @return
	 */
	private int charConvert(char src) {
		int r = BCConvert.qj2bj(src);
		return (r >= 'A' && r <= 'Z') ? r + 32 : r;
	}

}