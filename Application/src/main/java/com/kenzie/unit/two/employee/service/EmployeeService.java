package com.kenzie.unit.two.employee.service;

import com.kenzie.unit.two.employee.lambda.models.ViewEmployeePayCheckRequest;
import com.kenzie.unit.two.employee.service.models.Employee;
import com.kenzie.unit.two.iam.entities.Roles;
import com.kenzie.unit.two.iam.models.Role;
import com.kenzie.unit.two.iam.models.User;
import com.kenzie.unit.two.iam.service.RoleService;
import com.kenzie.unit.two.iam.service.UserRoleService;
import com.kenzie.unit.two.iam.service.UserService;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class EmployeeService {
    private final UserRoleService userRoleService;
    private final UserService userService;
    private final RoleService roleService;
    private final Logger log = LogManager.getLogger();
    private final ClassLoader loader = EmployeeService.class.getClassLoader();

    public EmployeeService(UserRoleService userRoleService, UserService userService, RoleService roleService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
        this.roleService = roleService;
    }

    public Employee getEmployeePayCheck(ViewEmployeePayCheckRequest request) {
        Employee employee = null;

        Role viewPayCheck = roleService.getRoleByRoleName(Roles.VIEW_PAYCHECK.getRoleName());
        User user = userService.getUserByUserName(request.getRequesterUserName());

        if (!userRoleService.doesUserHaveRole(user, viewPayCheck)) {
            throw new UserOrRoleNotFoundException("Employee does not have the required role");
        } else {
            try {
                File file = new File(loader.getResource("employee.csv").getPath());
                CSVReaderHeaderAware reader = new CSVReaderHeaderAware(new FileReader(file));
                Map<String, String> values;
                while (((values = reader.readMap())) != null) {
                    String id = values.get("Id");
                    String userName = values.get("Username");
                    String department = values.get("Department");
                    String payCheck = values.get("Paycheck");

                    if (theCorrectUser(request.getEmployeeUserName(), userName)) {
                        if (inTheSameDepartment(user.getDepartment().getName(), department)) {
                            log.info("Audit: User " + user.getUserName() + " viewed employee " + userName +
                                    " paycheck information");
                            employee = new Employee(id, userName, user.getDepartment(), payCheck);
                        } else {
                            throw new UserOrRoleNotFoundException("User does not belong to employee's department");
                        }
                    }
                }
            } catch (IOException | CsvValidationException e) {
                log.error("Exception occurred", e);
            }
            if (employee == null) {
                throw new EmployeeNotFoundException("Employee cannot be found");
            }
            return employee;
        }
    }

    public boolean theCorrectUser(String requestingUsername, String userName) {
        return userName.equalsIgnoreCase(requestingUsername);
    }

    public boolean inTheSameDepartment(String requestingDepartment, String userDepartment) {
        return requestingDepartment.equals(userDepartment);
    }
}
