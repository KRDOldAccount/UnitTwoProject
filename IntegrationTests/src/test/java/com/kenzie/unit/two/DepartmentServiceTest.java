package com.kenzie.unit.two;

import com.kenzie.unit.two.iam.lambda.models.CreateDepartmentRequest;
import com.kenzie.unit.two.iam.models.Department;
import com.kenzie.unit.two.iam.service.DepartmentService;
import com.kenzie.unit.two.iam.storage.Storage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class DepartmentServiceTest {

    @Mock
    Storage storage;

    @InjectMocks
    DepartmentService departmentService;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @AfterEach
    void afterEach() {
        System.out.println(":)");
    }

    @Test
    void createNewDepartment_TASK_7() {
        //GIVEN
        DepartmentService departmentService = new DepartmentService(storage);

        //WHEN
        String departmentName = RandomStringUtils.random(20);
        CreateDepartmentRequest createDepartmentRequest = new CreateDepartmentRequest();
        createDepartmentRequest.setDepartmentName(departmentName);

        //THEN
        Department department = departmentService.createDepartment(createDepartmentRequest);
        assertTrue(department.getId() != null);
    }

    @Test
    void throwExceptionDepartmentNameAlreadyExists_TASK_7() {
        //GIVEN
        DepartmentService departmentService = new DepartmentService(storage);
        Department departmentMock = mock(Department.class);
        //WHEN
        String departmentName = RandomStringUtils.random(20);

        CreateDepartmentRequest createDepartmentRequest = new CreateDepartmentRequest();
        createDepartmentRequest.setDepartmentName(departmentName);
        when(storage.getDepartmentByName(createDepartmentRequest.getDepartmentName())).thenReturn(null);
        departmentService.createDepartment(createDepartmentRequest);


        //THEN
        when(storage.getDepartmentByName(createDepartmentRequest.getDepartmentName())).thenReturn(departmentMock);
        assertThrows(IllegalArgumentException.class,
                () -> departmentService.createDepartment(createDepartmentRequest));
    }

    @Test
    void getDepartmentByName_TASK_7(){
        //GIVEN
        DepartmentService departmentService = new DepartmentService(storage);

        //WHEN
        String departmentName = RandomStringUtils.random(20);

        CreateDepartmentRequest createDepartmentRequest = new CreateDepartmentRequest();
        createDepartmentRequest.setDepartmentName(departmentName);
        when(storage.getDepartmentByName(createDepartmentRequest.getDepartmentName())).thenReturn(null);
        Department department = departmentService.createDepartment(createDepartmentRequest);


        //THEN
        when(storage.getDepartmentByName(createDepartmentRequest.getDepartmentName())).thenReturn(department);
        Department queriedDepartment = departmentService.getDepartmentByName(departmentName);
        assertTrue(department.getId().equals(queriedDepartment.getId()));
        verify(storage, times(2)).getDepartmentByName(createDepartmentRequest.getDepartmentName());
    }

    @Test
    void getAllDepartments(){
        //GIVEN
        DepartmentService departmentService = new DepartmentService(storage);
        List<Department> departmentsMock = new ArrayList<>();
        //WHEN
        String departmentName = RandomStringUtils.random(20);

        CreateDepartmentRequest createDepartmentRequest = new CreateDepartmentRequest();
        createDepartmentRequest.setDepartmentName(departmentName);
        when(storage.getDepartmentByName(createDepartmentRequest.getDepartmentName())).thenReturn(null);
        Department department = departmentService.createDepartment(createDepartmentRequest);

        //THEN
        departmentsMock.add(department);
        when(storage.getDepartments()).thenReturn(departmentsMock);
        List<Department> departments = departmentService.getDepartments();
        assertTrue(departments.contains(department));
        verify(storage).getDepartments();
    }
}