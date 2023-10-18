package net.greeta.order.employee.mapper;

import net.greeta.order.employee.EmployeeDTO;
import net.greeta.order.employee.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {
    EmployeeDTO employeeToEmployeeDTO(Employee employee);
    Employee employeeDTOToEmployee(EmployeeDTO employeeDTO);
}
