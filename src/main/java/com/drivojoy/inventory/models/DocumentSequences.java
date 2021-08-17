package com.drivojoy.inventory.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Document Sequence entity that keeps a track of document serial numbers for various document types
 * @author ashishsingh
 *
 */
@Entity
public class DocumentSequences {
	
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private int id;
	@Column(nullable=false, unique=true)
	private String documentType;
	@Column(nullable=false, unique=true)
	private String docPrefix;
	private int sequence;
	
	public DocumentSequences(){}

	/**
	 * 
	 * @param id Id of the entity
	 * @param documentType Document type
	 * @param docPrefix Prefix for a particular document type
	 * @param sequence Current sequence of the document
	 */
	public DocumentSequences(int id, String documentType, String docPrefix, int sequence) {
		super();
		this.id = id;
		this.documentType = documentType;
		this.docPrefix = docPrefix;
		this.sequence = sequence;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocPrefix() {
		return docPrefix;
	}

	public void setDocPrefix(String docPrefix) {
		this.docPrefix = docPrefix;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Override
	public String toString() {
		return "DocumentSequences [id=" + id + ", documentType=" + documentType + ", docPrefix=" + docPrefix
				+ ", sequence=" + sequence + "]";
	}
	
}
