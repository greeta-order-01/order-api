package net.greeta.order;

import net.greeta.order.employee.EmployeeDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import net.greeta.order.department.DepartmentDTO;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppRestControllerTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @Order(1)
    void shouldAddNewEmployee() {
        EmployeeDTO emp = new EmployeeDTO(null, 1L, 1L, "Test", 30, "HR");
        emp = restTemplate.postForObject("/api/employees", emp, EmployeeDTO.class);
        assertNotNull(emp.id());
    }

    @Test
    @Order(1)
    void shouldAddNewDepartment() {
        DepartmentDTO dep = new DepartmentDTO(null, 1L, "Test");
        dep = restTemplate.postForObject("/api/departments", dep, DepartmentDTO.class);
        assertNotNull(dep.id());
    }

    @Test
    @Order(2)
    void shouldFindDepartmentWithEmployees() {
        DepartmentDTO dep = restTemplate.getForObject("/api/departments/{id}/with-employees", DepartmentDTO.class, 1L);
        assertNotNull(dep);
        assertNotNull(dep.id());
    }
}
