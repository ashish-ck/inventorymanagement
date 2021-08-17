package com.drivojoy.inventory.servicesImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drivojoy.inventory.models.DocumentSequences;
import com.drivojoy.inventory.repositories.DocumentSequencesRepository;
import com.drivojoy.inventory.services.IDocumentSequencesService;
import com.drivojoy.inventory.utils.Constants;

@Component
public class DocumentSequencesServiceImpl implements IDocumentSequencesService {
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private DocumentSequencesRepository docSequenceRepository;
	private final Logger logger = LoggerFactory.getLogger(DocumentSequencesServiceImpl.class);

	@Override
	public String getNextSequence(String documentType, String docPrefix) {
		logger.debug("Looking up last used sequence for document type : "+documentType);
		DocumentSequences docSeq = null;
		try{
			docSeq = docSequenceRepository.findByDocumentType(documentType);
			if(docSeq != null){
				logger.debug("Next Document sequence for docType "+documentType+" is: "+docSeq.getDocPrefix()
					+ "-" +(docSeq.getSequence() + 1));
				return docSeq.getDocPrefix()+ "-" +(docSeq.getSequence() + 1);
			}else{
				//docSeq = createSequence(documentType, docPrefix);
				return docPrefix+"-1";
			}
		}catch(Exception ex){
			logger.error("Exception thrown while fetching sequence for document"+ex.getMessage());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}
	
	@Override
	@Transactional
	public DocumentSequences createSequence(String documentType, String docPrefix){
		DocumentSequences docSeq = new DocumentSequences(0, documentType, docPrefix, 1);
		logger.debug("Creating new document sequence : "+docSeq.toString());
		try{
			docSeq = docSequenceRepository.save(docSeq);
			em.flush();
			return docSeq;
		}catch(Exception ex){
			logger.error("Exception thrown while creating new sequence "+ex.getCause());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}
	
	@Override
	@Transactional
	public DocumentSequences incrementSequence(String documentType, String docPrefix){
		logger.debug("Incrementing document sequence for document : "+documentType);
		DocumentSequences docSeq = null;
		try{
			docSeq = docSequenceRepository.findByDocumentType(documentType);
			if(docSeq != null){
				docSeq.setSequence(docSeq.getSequence()+1);
				logger.debug("Incremented sequence for docType "+documentType+" : "+docSeq.getSequence());
				docSequenceRepository.save(docSeq);
				//em.flush();
				return docSeq;
			}else{
				logger.info("Requested document sequence does not exist, creating one now");
				return createSequence(documentType, docPrefix);
			}
		}catch(Exception ex){
			logger.error("Exception thrown while incrementing sequence "+ex.getCause());
			throw new RuntimeException(Constants._GENERIC_ERROR_MESSAGE);
		}
	}

}
