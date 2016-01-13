package co.crazytech.commons.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomCharacters {
	
	public static String randomString(int stringLength){
		String randomStr = "";
		for (int i = 0; i < stringLength; i++) {
			randomStr += randomChar();
		}
		return randomStr;
	}
	
	public static String randomChar(){
		List<String> letters = new ArrayList<String>();
		letters.add("a");letters.add("g");letters.add("l");letters.add("q");letters.add("v");
		letters.add("b");letters.add("h");letters.add("m");letters.add("r");letters.add("w");
		letters.add("c");letters.add("i");letters.add("n");letters.add("s");letters.add("x");
		letters.add("d");letters.add("j");letters.add("o");letters.add("t");letters.add("y");
		letters.add("e");letters.add("k");letters.add("p");letters.add("u");letters.add("z");
		letters.add("f");
		letters.add("A");letters.add("G");letters.add("L");letters.add("Q");letters.add("V");
		letters.add("B");letters.add("H");letters.add("M");letters.add("R");letters.add("W");
		letters.add("C");letters.add("I");letters.add("N");letters.add("S");letters.add("X");
		letters.add("D");letters.add("J");letters.add("O");letters.add("T");letters.add("Y");
		letters.add("E");letters.add("K");letters.add("P");letters.add("U");letters.add("Z");
		letters.add("F");
		letters.add("0");letters.add("3");letters.add("5");letters.add("7");letters.add("9");
		letters.add("1");letters.add("4");letters.add("6");letters.add("8");
		letters.add("2");
		Collections.shuffle(letters);
		return letters.get(0);
	}
}
