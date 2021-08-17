package com.drivojoy.inventory.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.models.UserReports;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IUserReportsService {

	/**
	 * Gets a list of all generated user reports
	 * @return List of user reports
	 */
	public List<UserReports> getAll();
	
	/**
	 * Saves a new user report
	 * @param userReport User report
	 * @return User reports
	 */
	public UserReports saveReport(UserReports userReport);
	
	/**
	 * Finds a user report by id
	 * @param id User report id
	 * @return User reports
	 */
	public UserReports getById(long id);
	
	/**
	 * Finds a user report by url
	 * @param url Url
	 * @return User report
	 */
	public UserReports getByPath(String url);

}
