package com.drivojoy.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.drivojoy.inventory.models.UserReports;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface UserReportsRepository extends JpaRepository<UserReports, Long>{

	/**
	 * Finds user reports by its url
	 * @param reportUrl Report url
	 * @return UserReports
	 */
	UserReports findByReportUrl(String reportUrl);
}
