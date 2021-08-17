package com.drivojoy.inventory.services;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.drivojoy.inventory.models.DocumentSequences;

/**
 * 
 * @author ashishsingh
 *
 */
@Service
@Transactional
public interface IDocumentSequencesService {
	
	/**
	 * Gets the next sequence for a particular document type and prefix. This does not update the database yet.
	 * @param documentType Doc type
	 * @param docPrefix Doc prefix
	 * @return sequence string
	 */
	public String getNextSequence(String documentType, String docPrefix);

	/**
	 * Creates a new document sequence for document type and prefix
	 * @param documentType Doc type
	 * @param docPrefix Doc prefix
	 * @return Document sequences
	 */
	DocumentSequences createSequence(String documentType, String docPrefix);

	/**
	 * Increments a sequence identified by a document type and prefix
	 * @param documentType Doc type
	 * @param docPrefix Doc prefix
	 * @return Document sequences
	 */
	DocumentSequences incrementSequence(String documentType, String docPrefix);
}
