package net.greeta.order.organization.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import net.greeta.order.organization.OrganizationDTO;
import net.greeta.order.organization.model.Organization;

public interface OrganizationRepository extends CrudRepository<Organization, Long> {

    @Query("""
           SELECT new net.greeta.order.organization.OrganizationDTO(o.id, o.name, o.address)
           FROM Organization o
           WHERE o.id = :id
           """)
    OrganizationDTO findDTOById(Long id);
}
