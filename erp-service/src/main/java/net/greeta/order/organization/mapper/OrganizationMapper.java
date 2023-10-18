package net.greeta.order.organization.mapper;

import net.greeta.order.organization.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import net.greeta.order.organization.OrganizationDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrganizationMapper {
    OrganizationDTO organizationToOrganizationDTO(Organization organization);
    Organization organizationDTOToOrganization(OrganizationDTO organizationDTO);
}
