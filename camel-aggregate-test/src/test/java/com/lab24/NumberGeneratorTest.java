package com.lab24;

import static org.junit.Assert.*;

import org.junit.Test;

public class NumberGeneratorTest {

	private NumberGenerator ng = new NumberGenerator();
	
	@Test
	public void shouldGenerateRandomNumbers() {
		long num1 = ng.generateNumber();
		long num2 = ng.generateNumber();
		long num3 = ng.generateNumber();
		long num4 = ng.generateNumber();
		System.out.println(num1);
		System.out.println(num2);
		System.out.println(num3);
		System.out.println(num4);
		assertNotEquals(num1, num2);
		assertNotEquals(num1, num3);
		assertNotEquals(num1, num4);
	}

}
