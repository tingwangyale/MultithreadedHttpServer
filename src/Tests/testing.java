package Tests;

import java.util.HashMap;

public class testing {
	public static void main(String[] args) {
		HashMap<Integer, Integer> map = new HashMap<>(); 
		System.out.println(map);
		if (map.isEmpty()) {
			System.out.println("null");
		} else {
			System.out.println(" not null");
		}
	}
}
