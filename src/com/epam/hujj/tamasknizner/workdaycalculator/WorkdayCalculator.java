package com.epam.hujj.tamasknizner.workdaycalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkdayCalculator {

	private static final Logger logger = LoggerFactory.getLogger(WorkdayCalculator.class);

	private LocalDate startDate = new LocalDate("2016-01-01");
	private LocalDate endDate = new LocalDate("2017-01-01");
	private ResourceBundle workdayResources;
	private List<String> keys;
	private List<Integer> daysOfWeek;
	private List<LocalDate> allDays;
	private Map<String, String> formats;
	private String filename;
	private boolean initialized = false;
	private List<LocalDate> vacationDays;
	private List<LocalDate> extraVacationDays;
	private List<LocalDate> extraWorkDays;

	public WorkdayCalculator(String filename) {
		logger.info("WorkdayCalculator object created.");
		this.filename = filename;
	}

	public void init() {
		if (initialized) {
			logger.warn("Already initialized!");
			return;
		}
		readPropertyFile(filename);
		daysOfWeek = readWeekdayNumbers();
		formats = readFormats();
		allDays = initAllDays();

		vacationDays = initVacationDays();
		extraVacationDays = initExtraVacationDays();
		extraWorkDays = initExtraWorkDays();

		initialized = true;
	}

	private List<LocalDate> initExtraWorkDays() {
		List<LocalDate> list = new ArrayList<>();
		for (String key : keys) {
			if (key.startsWith("extra-workday.")) {
				LocalDate ld = LocalDate.parse(workdayResources.getString(key),
						DateTimeFormat.forPattern(((String) formats.get("extra-workday-format"))));
				list.add(ld);
				logger.info("New extra workday date added: {}", ld.toString());
			}
		}
		return list;
	}

	private List<LocalDate> initExtraVacationDays() {
		List<LocalDate> list = new ArrayList<>();
		for (String key : keys) {
			if (key.startsWith("extra-vacation.")) {
				LocalDate ld = LocalDate.parse(workdayResources.getString(key),
						DateTimeFormat.forPattern(((String) formats.get("extra-vacation-format"))));
				list.add(ld);
				logger.info("New extra vacation date added: {}", ld.toString());
			}
		}
		return list;
	}

	private List<LocalDate> initVacationDays() {
		List<LocalDate> list = new ArrayList<>();
		for (String key : keys) {
			if (key.startsWith("vacation.")) {
				LocalDate ld = LocalDate.parse(workdayResources.getString(key),
						DateTimeFormat.forPattern(((String) formats.get("vacation-format"))));
				ld = ld.plusYears(16);
				list.add(ld);
				logger.info("New vacation date added: {}", ld.toString());
			}
		}
		return list;
	}

	public void doCalculation() {
		if (!initialized) {
			logger.error("Not initialized!");
			return;
		}
		logger.info("Instance is initialized, doing calculations...!");
		//allDays = removeWeekends();
		//allDays = removeVacationDays();
		//allDays = addExtraWorkdays();
		//allDays = removeExtraVacationDays();
		allDays = removeAll();
		logResult();
	}
	
	private void logResult() {
		logger.info("=== Calculation result ===");
		for (LocalDate localDate : allDays) {
			logger.info("{} is workday.", localDate.toString());
		}
	}

	private Map<String, String> readFormats() {
		Map<String, String> map = new HashMap<>();
		for (String key : keys) {
			if (key.contains("format")) {
				String value = workdayResources.getString(key);
				map.put(key, value);
				logger.info("Format for {} is {}", key, value);
			}
		}
		return map;
	}

	private List<Integer> readWeekdayNumbers() {
		List<Integer> list = new ArrayList<>();
		for (String key : keys) {
			if (key.contains("workday.") && !key.contains("-")) {
				String value = workdayResources.getString(key);
				list.add(Integer.parseInt(value));
				logger.info("New workday number added: {}", value);
			}
		}
		return list;
	}
	
	private List<LocalDate> removeAll() {
		List<LocalDate> list = new ArrayList<>();
		for (LocalDate localDate : allDays) {
			if(daysOfWeek.contains(localDate.getDayOfWeek()) && !vacationDays.contains(localDate) && !extraWorkDays.contains(localDate) && !extraVacationDays.contains(localDate)) {
				list.add(localDate);
			}
		}
		return list;
	}
	/*
	private List<LocalDate> removeWeekends() {
		List<LocalDate> removedList = new ArrayList<>();
		for (LocalDate localDate : allDays) {
			if (daysOfWeek.contains(localDate.getDayOfWeek())) {
				removedList.add(localDate);
			}
		}
		return removedList;
	}

	private List<LocalDate> removeVacationDays() {
		List<LocalDate> list = new ArrayList<>();
		for (LocalDate localDate : allDays) {
			if (!vacationDays.contains(localDate)) {
				list.add(localDate);
			}
		}
		return list;
	}

	private List<LocalDate> addExtraWorkdays() {
		List<LocalDate> list = new ArrayList<>();
		for (LocalDate localDate : allDays) {
			if (!extraWorkDays.contains(localDate)) {
				list.add(localDate);
			}
		}
		return list;
	}

	private List<LocalDate> removeExtraVacationDays() {
		List<LocalDate> list = new ArrayList<>();
		for (LocalDate localDate : allDays) {
			if (!extraVacationDays.contains(localDate)) {
				list.add(localDate);
			}
		}
		return list;
	}
	*/
	private List<LocalDate> initAllDays() {
		List<LocalDate> list = new ArrayList<>();
		for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
			list.add(date);
		}
		return list;
	}

	private void readPropertyFile(String bundleName) {
		logger.info("readPropertyFile bundleName: {}", bundleName);
		workdayResources = ResourceBundle.getBundle(bundleName);
		keys = Collections.list(workdayResources.getKeys());
	}

}
