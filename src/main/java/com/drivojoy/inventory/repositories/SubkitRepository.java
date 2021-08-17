package com.drivojoy.inventory.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.drivojoy.inventory.models.Subkit;

/**
 * 
 * @author ashish.singh
 *
 */
@Repository
public interface SubkitRepository extends JpaRepository<Subkit, Long>{

	/**
	 * Finds a subkit by subkit number
	 * @param subkitNumber Subkit number
	 * @return Subkit
	 */
	Subkit findBySubkitNumber(String subkitNumber);
	
	/**Find subkit by subkit number
	 * @param subkitNumber Subkit number
	 * @return Subkit
	 */
	Subkit findByKitNumber(String subkitNumber);
	
	/**Find subkits by kit number and location
	 * @param kitNumber Kit number
	 * @param warehouseId Warehouse id
	 * @return List of subkits
	 */
	@Query(value="SELECT subkit.id, subkit.location, subkit.subkit_number, subkit.kit_number, subkit.status FROM subkit WHERE subkit.kit_number = :kit_number AND subkit.location = :warehouse_id", nativeQuery=true)
	List<Object[]> getSubkitByKitNumberAndLocation(@Param("kit_number") String kitNumber, @Param("warehouse_id") long warehouseId);
}
