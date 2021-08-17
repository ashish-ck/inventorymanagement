package com.drivojoy.inventory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.drivojoy.inventory.models.Roles;

/**
 * 
 * @author ashishsingh
 *
 */
@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

	/**
	 * Finds a role of by its name
	 * @param role Name of the role
	 * @return Roles
	 */
	Roles findByRole(String role);
}
