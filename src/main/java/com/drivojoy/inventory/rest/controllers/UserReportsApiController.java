package com.drivojoy.inventory.rest.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.dto.ApiResponse;
import com.drivojoy.inventory.models.UserReports;
import com.drivojoy.inventory.services.IUserReportsService;
import com.drivojoy.inventory.utils.Constants;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value="/api/reports")
public class UserReportsApiController {

	@Autowired
	private IUserReportsService reportService;

	private final Logger logger = LoggerFactory.getLogger(UserReportsApiController.class);

	/**
	 * API End point to fetch all generated user reports
	 * @return List of user report
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<ApiResponse<List<UserReports>>> getReports(){
		logger.debug("fetch all reports api invoked");
		List<UserReports> reports = null;
		try{
			reports = reportService.getAll();
			ApiResponse<List<UserReports>> response = new ApiResponse<List<UserReports>>(true, reports, "Reports fetched successfully!");
			return new ResponseEntity<ApiResponse<List<UserReports>>>(response, HttpStatus.OK);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			ApiResponse<List<UserReports>> response = new ApiResponse<List<UserReports>>(false, null, Constants._GENERIC_ERROR_MESSAGE);
			if(ex.getMessage() != null){
				response.setMessage(ex.getMessage());
			}
			return new ResponseEntity<ApiResponse<List<UserReports>>>(response, HttpStatus.OK);
		}
	}

	/**
	 * API End point to download user report
	 * @param reportId User Report Id
	 * @param response HttpResponse Object
	 * @return Input Stream resource
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/getById/{userReference}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public ResponseEntity<InputStreamResource> downloadUnit(@RequestParam("reportId") long reportId, HttpServletResponse response){
		logger.debug("fetch report by id : "+reportId);
		UserReports report = null;
		try{
			report = reportService.getById(reportId);
			if(report!=null){
				try{
					String url = "/"+report.getReportUrl();
					File file = new File(url);
					InputStream inputStream = new FileInputStream(file);
				    return ResponseEntity
				            .ok()
				            .header("Content-disposition", file.getName())
				            .contentType(MediaType.parseMediaType("application/octet-stream"))
				            .body(new InputStreamResource(inputStream));
				}catch(Exception ex){
					logger.error("Exception thrown while sending file to client "+ex.getMessage());
					return new ResponseEntity<InputStreamResource>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
			return new ResponseEntity<InputStreamResource>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
