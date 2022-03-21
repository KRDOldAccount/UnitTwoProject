package com.kenzie.unit.two.iam.models;

import com.kenzie.unit.two.iam.entities.Roles;

import com.kenzie.ata.ExcludeFromJacocoGeneratedReport;

import java.util.List;

@ExcludeFromJacocoGeneratedReport
public class FunctionalRole {

    // Roles required to do this action
    private List<Roles> roles;

    public FunctionalRole(List<Roles> roles) {
        this.roles = roles;
    }

    // Compare incoming list to the ones required for this class.
    public Boolean matches(List<Role> roleList) {
        // TODO Task 5 - Write your code here ...
        for (Roles role : roles) {
            boolean foundRole = false;
            for (Role role2 : roleList) {
                if (role.getRoleName().equalsIgnoreCase(role2.getRoleName())) {
                    foundRole = true;
                }
            }
            if (foundRole == false) {
                return false;
            }
        }

        return true;
    }
}
