package com.ibm.hr_pj.Models;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long unitId;
    private String unitName;
    private String unitSupervisor;
    private int numberOfEmployeesAtTheUnit;
    @ManyToOne
    @JoinColumn
    private Departments departments;
}
