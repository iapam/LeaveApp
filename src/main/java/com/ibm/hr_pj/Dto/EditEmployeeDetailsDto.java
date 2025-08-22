package com.ibm.hr_pj.Dto;

import com.ibm.hr_pj.Models.Departments;
import com.ibm.hr_pj.Models.EmployeeRole;
import com.ibm.hr_pj.Models.Login;
import com.ibm.hr_pj.Models.Unit;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EditEmployeeDetailsDto {
    private String employeeId;
    private String name;
    private String email;
    private String annualSalary;
    private String grade;
    private String profession;
    private String employmentType;
    private String address;
    private long numberOfDaysEntitled;
    private String unit;
    private String departments;
    private EmployeeRole employeeRole;
}
