package com.apogee.characteranalyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Analyze an individual file. The analysis saves:
 * <ul>
 * <li>The full path to the file</li>
 * <li>The file extension</li>
 * <li>The number of appearances of each character</li>
 * <li>For each character, the number of appearances for each character that is
 * observed to follow it</li>
 * <li>For each character that follows a character, the number of appearances
 * for each character after that.</li>
 * </ul>
 */
public class AnalyzeFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileToAnalyze;
	private String fileExtension;
	private CharacterCount ccount;
	private transient FileInputStream in = null;

	public static enum Status {
		NOOP, INITIAL, ANALYZED, FAILED
	};

	private Status status;

	/**
	 * @throws IOException
	 * 
	 */
	public AnalyzeFile(String fileToAnalyze) throws IOException {
		this.fileToAnalyze = fileToAnalyze;
		int index = 0;
		if (fileToAnalyze.contains("/")) {
			index = fileToAnalyze.lastIndexOf('/');
		} else if (fileToAnalyze.contains("\\")) {
			index = fileToAnalyze.lastIndexOf('\\');
		}
		String filename = fileToAnalyze.substring(index + 1);
		if (filename.contains(".")) {
			fileExtension = filename.substring(filename.lastIndexOf('.') + 1);
		} else {
			fileExtension = "";
		}
		ccount = new CharacterCount();
		in = new FileInputStream(fileToAnalyze);
		status = Status.INITIAL;
	}

	private AnalyzeFile() {

	}

	public void run() {
		if (status != Status.INITIAL)
			return;
		Character c = null;
		Character d = null;
		Character e = null;
		while (true) {
			try {
				int i = in.read();
				if (i == -1) {
					status = Status.ANALYZED;
					return;
				}
				if (i > 127) {
					continue;
				}
				c = d;
				d = e;
				e = new Character((char) i);
				if (Character.isUpperCase(e)) {
					e = Character.toLowerCase(e);
				}
				if (Character.isWhitespace(e)) {
					e = ' ';
				}
				if (c != null) {
					CharacterCount cc = ccount.get(c);
					cc.inc(d, e);
					ccount.inc();
				}
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
				status = Status.FAILED;
				return;
			}
		}
	}

	public void save(String filename) throws IOException {

	}

	public void save() throws IOException {

	}

	public static AnalyzeFile getAnalysis(String analysisFile) {
		return new AnalyzeFile();
	}

	/**
	 * Obtain a list of the characters observed in the document, ordered by
	 * number of instances.
	 * 
	 * @return
	 */
	public List<Character> getObservedCharacters() {
		TreeSet<CharacterCount> ccset = new TreeSet<>(ccount.values());
		List<Character> result = new ArrayList<>(ccset.size());
		for(CharacterCount cc: ccset) {
			result.add(cc.getChar());
		}
		return result;
	}
	
	public int getCount(Character c) {
		CharacterCount cc = ccount.get(c);
		if (cc == null) return 0;
		return cc.getCount();
	}

	public String toString() {
		StringBuffer res = new StringBuffer("file: " + fileToAnalyze);
		res.append("\next: " + fileExtension);
		res.append("\n");
		res.append(ccount.toString());
		return res.toString();
	}

	public static void main(String[] args) throws Exception {
		AnalyzeFile af = new AnalyzeFile(args[0]);
		af.run();
		int totalChars = af.ccount.getCount();
		System.out.println("Total number of chars: " + totalChars);
		System.out.println("Top 20 characters, with counts:");
		List<Character> list = af.getObservedCharacters();
		float subtot = 0;
		for(int i=0; i<20 && i<list.size(); i++) {
			Character c = list.get(i);
			int x = af.getCount(c);
			subtot += x;
			System.out.println("\t" + c + " : " + x + " (" + subtot / (float)totalChars + ")" );
		}
		System.out.println("==========");
		System.out.println(af);
	}

	private static class CharacterCount extends
			HashMap<Character, CharacterCount> implements Serializable,
			Comparable<CharacterCount> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Character character;
		private int count;

		public CharacterCount() {
			super();
			count = 0;
			character = null;
		}

		public CharacterCount(Character c) {
			this();
			character = c;
		}

		public void inc() {
			count++;
		}

		public void inc(Character c) {
			if (c == null) {
				inc();
				return;
			}
			count++;
			CharacterCount cCount = get(c);
			cCount.inc();
		}

		public void inc(Character c, Character d) {
			if (d == null) {
				inc(c);
				return;
			}
			count++;
			CharacterCount cCount = get(c);
			cCount.inc(d);
		}

		public int getCount() {
			return count;
		}

		@Override
		public CharacterCount get(Object o) {
			CharacterCount cc = super.get(o);
			if (cc == null) {
				Character c = (Character) o;
				cc = new CharacterCount(c);
				super.put(c, cc);
			}
			return cc;
		}

		public String toString() {
			return toString(0);
		}

		public String toString(int indent) {
			String prefix = "";
			for (int i = 0; i < indent; i++) {
				if (i == indent - 1) {
					prefix += " ";
				} else {
					prefix += ".";
				}
			}
			StringBuffer buf = new StringBuffer();
			SortedSet<CharacterCount> set = new TreeSet<>(values());
			for (CharacterCount cc : set) {
				buf.append(prefix + "'" + cc.getChar() + "' [" + cc.getCount()
						+ "]");
				if (indent == 0) {
					buf.append(" s=" + cc.size());
					int x = 0;
					for (CharacterCount child : cc.values()) {
						x += child.size();
					}
					buf.append(" avg(ss)=" + (x / cc.size()));
				}
				buf.append("\n");
				buf.append(cc.toString(indent + 3));
			}
			return buf.toString();
		}

		public Character getChar() {
			return character;
		}

		@Override
		public int compareTo(CharacterCount o) {
			int x = Integer.compare(count, o.count);
			if (x != 0)
				return -x;
			return character.compareTo(o.character);
		}

	}

}
