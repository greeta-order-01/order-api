package net.greeta.order.department.management;

import net.greeta.order.OrganizationAddEvent;
import net.greeta.order.OrganizationRemoveEvent;
import net.greeta.order.department.mapper.DepartmentMapper;
import net.greeta.order.employee.EmployeeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import net.greeta.order.department.DepartmentDTO;
import net.greeta.order.department.DepartmentExternalAPI;
import net.greeta.order.department.DepartmentInternalAPI;
import net.greeta.order.department.repository.DepartmentRepository;
import net.greeta.order.employee.EmployeeInternalAPI;

import java.util.List;

@Service
public class DepartmentManagement implements DepartmentInternalAPI, DepartmentExternalAPI {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentManagement.class);
    private DepartmentRepository repository;
    private EmployeeInternalAPI employeeInternalAPI;
    private DepartmentMapper mapper;

    public DepartmentManagement(DepartmentRepository repository,
                                EmployeeInternalAPI employeeInternalAPI,
                                DepartmentMapper mapper) {
        this.repository = repository;
        this.employeeInternalAPI = employeeInternalAPI;
        this.mapper = mapper;
    }

    @Override
    public DepartmentDTO getDepartmentByIdWithEmployees(Long id) {
        DepartmentDTO d = repository.findDTOById(id);
        List<EmployeeDTO> dtos = employeeInternalAPI.getEmployeesByDepartmentId(id);
        d.employees().addAll(dtos);
        return d;
    }

    @ApplicationModuleListener
    void onNewOrganizationEvent(OrganizationAddEvent event) {
        LOG.info("onNewOrganizationEvent(orgId={})", event.getId());
        add(new DepartmentDTO(null, event.getId(), "HR"));
        add(new DepartmentDTO(null, event.getId(), "Management"));
    }

    @ApplicationModuleListener
    void onRemovedOrganizationEvent(OrganizationRemoveEvent event) {
        LOG.info("onRemovedOrganizationEvent(orgId={})", event.getId());
        repository.deleteByOrganizationId(event.getId());
    }

    @Override
    public DepartmentDTO add(DepartmentDTO department) {
        return mapper.departmentToEmployeeDTO(
                repository.save(mapper.departmentDTOToEmployee(department))
        );
    }

    @Override
    public List<DepartmentDTO> getDepartmentsByOrganizationId(Long id) {
        return repository.findByOrganizationId(id);
    }

    @Override
    public List<DepartmentDTO> getDepartmentsByOrganizationIdWithEmployees(Long id) {
        List<DepartmentDTO> departments = repository.findByOrganizationId(id);
        for (DepartmentDTO dep: departments) {
            dep.employees().addAll(employeeInternalAPI.getEmployeesByDepartmentId(dep.id()));
        }
        return departments;
    }
}
