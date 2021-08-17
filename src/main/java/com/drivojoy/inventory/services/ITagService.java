package com.drivojoy.inventory.services;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.models.Tag;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface ITagService {

	/**
	 * Fetches all existing tags
	 * @return List of tags
	 */
	List<Tag> getTags ();
	
	/**
	 * Finds a tag by name
	 * @param tag Tag
	 * @return Tag
	 */
	Tag getTagByValue (String tag);
	
	/**
	 * Creates new tags
	 * @param listOfTags List of tags
	 * @return List of tags
	 */
	Collection<Tag> createTags(Collection<Tag> listOfTags);
}
