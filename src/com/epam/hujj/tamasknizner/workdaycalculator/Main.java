package com.epam.hujj.tamasknizner.workdaycalculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		logger.info("Main started..");
		WorkdayCalculator wc = new WorkdayCalculator("workday");
		wc.init();
		wc.doCalculation();
	}

}
