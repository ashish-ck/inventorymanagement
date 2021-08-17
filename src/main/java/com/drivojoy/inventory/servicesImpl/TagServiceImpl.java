package com.drivojoy.inventory.servicesImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.models.Tag;
import com.drivojoy.inventory.repositories.TagRepository;
import com.drivojoy.inventory.services.ITagService;
import com.drivojoy.inventory.utils.Constants;

@Component
public class TagServiceImpl implements ITagService{

	@Autowired
	private TagRepository tagRepository;

	private final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Tag> getTags() {
		logger.debug("fetching all tags ");
		try{
			return tagRepository.findAll();
		}catch(Exception ex){
			logger.error("Exception thrown while fetching tags"+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

	@Override
	public Collection<Tag> createTags(Collection<Tag> listOfTags) {
		if(listOfTags != null){
			logger.debug("Saving tags : "+listOfTags.toString());
			try{
				List<Tag> tagsToBeSaved = new ArrayList<>();
				for(Tag tag : listOfTags){
					if(tag.getId() == 0)
						tagsToBeSaved.add(tag);
				}
				listOfTags = tagRepository.save(tagsToBeSaved);
				return listOfTags;
			}catch(Exception ex){
				logger.error("Exception thrown while saving tags"+ex.getMessage());
				throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
			}
		}else{
			return new ArrayList<>();
		}
	}

	@Override
	public Tag getTagByValue(String tag) {
		logger.debug("fetching tag object for : "+tag);
		try{
			return tagRepository.findByName(tag);
		}catch(Exception ex){
			logger.error("Exception thrown while fetching tags"+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

}
