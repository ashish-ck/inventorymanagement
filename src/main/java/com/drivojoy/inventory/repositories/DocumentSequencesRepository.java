package com.drivojoy.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.DocumentSequences;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface DocumentSequencesRepository extends JpaRepository<DocumentSequences, Integer> {
	
	/**
	 * Finds the document sequence object for a particular documentType.
	 * The Constants file contains list of document types.
	 * @param documentType String representation of document type
	 * @return Object of document sequences
	 * @see com.drivojoy.inventory.utils.Constants
	 */
	DocumentSequences findByDocumentType(String documentType);
}
