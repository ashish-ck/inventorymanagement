package com.drivojoy.inventory.rest.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.drivojoy.inventory.models.Tag;
import com.drivojoy.inventory.services.ITagService;

/**
 * 
 * @author ashishsingh
 *
 */
@RestController
@RequestMapping(value="/api/tags/")
public class TagController {

	@Autowired
	private ITagService tagsService;

	private final Logger logger = LoggerFactory.getLogger(TagController.class);
	
	/**
	 * API End point to fetch all tags
	 * @return List of tag
	 */
	@PreAuthorize("hasAnyRole('ADMIN,HUBMANAGER,INVENTORYMANAGER')")
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<List<Tag>> getAllTags(){
		logger.debug("fetch all tags api invoked");
		List<Tag> tags = null;
		try{
			tags = tagsService.getTags();
		}catch(Exception ex){
			logger.error("Controller caught exception from service : "+ex.getCause());
		}
		if(tags != null)
			return new ResponseEntity<List<Tag>>(tags, HttpStatus.OK);
		
		return new ResponseEntity<List<Tag>>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
}
