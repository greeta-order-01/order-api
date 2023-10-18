package net.greeta.order.department;

public interface DepartmentExternalAPI {

    DepartmentDTO getDepartmentByIdWithEmployees(Long id);
    DepartmentDTO add(DepartmentDTO department);
}
