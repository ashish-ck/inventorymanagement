package com.drivojoy.inventory.servicesImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.models.UserReports;
import com.drivojoy.inventory.repositories.UserReportsRepository;
import com.drivojoy.inventory.services.IUserReportsService;

@Component
public class UserReportsServiceImpl implements IUserReportsService{
	
	@Autowired
	private UserReportsRepository reportsRepository;
	private final Logger logger = LoggerFactory.getLogger(UserReportsServiceImpl.class);

	@Override
	public List<UserReports> getAll() {
		logger.debug("Fetching all user reports");
		return reportsRepository.findAll();
	}

	@Override
	public UserReports saveReport(UserReports userReport) {
		logger.debug("Saving report : "+userReport.toString());
		return reportsRepository.save(userReport);
	}

	@Override
	public UserReports getById(long id) {
		logger.debug("Fetching report with id : "+id);
		UserReports report = reportsRepository.findOne(id);
		return report;
	}

	@Override
	public UserReports getByPath(String url) {
		logger.debug("Fetching report with url : "+url);
		UserReports report = reportsRepository.findByReportUrl(url);
		return report;
	}

	
	
}
