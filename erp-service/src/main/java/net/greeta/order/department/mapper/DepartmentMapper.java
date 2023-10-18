package net.greeta.order.department.mapper;

import net.greeta.order.department.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import net.greeta.order.department.DepartmentDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DepartmentMapper {
    DepartmentDTO departmentToEmployeeDTO(Department department);
    Department departmentDTOToEmployee(DepartmentDTO departmentDTO);
}
