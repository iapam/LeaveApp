package com.ibm.hr_pj.Controllers;

import com.ibm.hr_pj.Dto.*;
import com.ibm.hr_pj.Models.*;
import com.ibm.hr_pj.Repositories.EmployeeDetailsRepository;
import com.ibm.hr_pj.Services.EmployeeService;
import com.ibm.hr_pj.Services.PdfGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeDetailsService;
    private final EmployeeService employeeService;
    private final EmployeeDetailsRepository employeeDetailsRepository;
    private final PdfGeneratorService pdfGeneratorService;
    @GetMapping("/employee/profile")
    public EmployeeDetailsRegistrationRequest employeeProfile() {
        Login login= (Login) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmployeeDetailsRegistrationRequest employeeDetails=employeeDetailsService.employeeDetails(login);
        return employeeDetails;
    }
    @PostMapping("/apply/leave")
public String leaveRequest(@ModelAttribute LeaveRequest leaveRequest, Model model) throws ParseException, IOException {
    Login login= (Login) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
   String leaveApplication= employeeService.leaveApplication(leaveRequest,login);
        DefaultLeaveApplicationDto detail=new DefaultLeaveApplicationDto();
        model.addAttribute("leaveApplication",leaveApplication);
        model.addAttribute("defaultLeaveDetails",detail);
return "/leave";
}
@GetMapping("/leave/updateStatusUH/{id}/{status}/{department}")
public String unitHeadStatusUpdate(@PathVariable long id,@PathVariable String status,@PathVariable String department, Model model) throws IOException {
       StatusUpdateDto statusUpdateDto=new StatusUpdateDto(id,status,department,false);
    Login login= (Login) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     String unitStatusUpdate=employeeService.updateLeaveStatus(statusUpdateDto,login);
    EmployeeDetail detail=employeeDetailsRepository.findEmployeeDetailsByLogin(login);
    List< LeaveDetailsDto> leaveDetailsDto=employeeService.leaveDetailsDto(detail.getDepartments());
    model.addAttribute("listOfLeave",leaveDetailsDto);
        return "/supervisorsDashboard";
}
    @GetMapping("/leave/updateStatusUH/{id}/{status}/{department}/replacement")
    public String unitHeadStatusUpdateReplacement(@PathVariable long id,@PathVariable String status,@PathVariable String department, Model model) throws IOException {
        StatusUpdateDto statusUpdateDto=new StatusUpdateDto(id,status,department,true);
        Login login= (Login) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String unitStatusUpdate=employeeService.updateLeaveStatus(statusUpdateDto,login);
        EmployeeDetail detail=employeeDetailsRepository.findEmployeeDetailsByLogin(login);
        List< LeaveDetailsDto> leaveDetailsDto=employeeService.leaveDetailsDto(detail.getDepartments());
        SupervisorStats supervisorStats=employeeService.supervisorStats(login.getEmployeeId());
        model.addAttribute("supervisorStats",supervisorStats);
        model.addAttribute("listOfLeave",leaveDetailsDto);
        return "/supervisorsDashboard";
    }
@GetMapping("/leave/updatemedsup/{id}/{status}")
public String medsupStatusUpdate(@PathVariable long id,@PathVariable String status,Model model){
employeeService.medsupStatusUpdate(id,status);
    List<MedSupStatusDto>allMedSupPendingLeaves=employeeService.allPendingMesupLeaves("pending");
    model.addAttribute("medSupPendingLeave",allMedSupPendingLeaves);
    return "/medsupDashboard";
}
@GetMapping("/supervisor/dashboard")
public String supervisorsDashboard(Model model){
    Login login=(Login) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    EmployeeDetail detail=employeeDetailsRepository.findEmployeeDetailsByLogin(login);
   List<LeaveDetailsDto> leaveDetailsDto=employeeService.leaveDetailsDto(detail.getDepartments());
   SupervisorStats supervisorStats=employeeService.supervisorStats(login.getEmployeeId());
   model.addAttribute("supervisorStats",supervisorStats);
    model.addAttribute("listOfLeave",leaveDetailsDto);
        return "/supervisorsDashboard";
}
@GetMapping("/medsup/dashboard/page")
    public String medSupDashboardPage(Model model) throws JRException, IOException {
    Login login=(Login) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    EmployeeDetail detail=employeeDetailsRepository.findEmployeeDetailsByLogin(login);
    List<MedSupStatusDto>allMedSupPendingLeaves=employeeService.allPendingMesupLeaves("pending");
    model.addAttribute("medSupPendingLeave",allMedSupPendingLeaves);
       return "/medsupDashboard";
}
@GetMapping("/HR/dashboard/page")
public String hrDashboard(Model model){
    Login login=(Login) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    EmployeeDetail detail=employeeDetailsRepository.findEmployeeDetailsByLogin(login);
    int dueLeave=employeeService.dueLeaves();
    List<MedSupStatusDto>allMedSupApprovedLeaves=employeeService.allPendingMesupLeaves("approved");
    HRStats hrStats=employeeService.hrStats();
    model.addAttribute("hrStats",hrStats);
    model.addAttribute("medApprovedLeave",allMedSupApprovedLeaves);
        return "/HRdashboard";
}
@GetMapping("/print/leave/form/{departmentId}/{employeeID}/{leaveId}")
public String printLeaveForm(@PathVariable Long departmentId,@PathVariable String employeeID,@PathVariable Long leaveId, HttpServletResponse response) throws JRException, IOException {
        List<LeaveFormDto>leaveFormDto=employeeService.leaveFormDto(leaveId,employeeID,departmentId);
    pdfGeneratorService.exportPdf(response,leaveFormDto);
    System.out.println("The form data "+leaveFormDto);
    return "/HRdashboard";
}
@GetMapping("/hr/overdue/leaves/table")
public String overDueLeaveTable(Model model){
        List<DueLeavesDetatilsDto>leavesDetatilsDtos=employeeService.dueLeavesDetails();
        model.addAttribute("leavesDetatilsDtos",leavesDetatilsDtos);
        return "/OverDueLeavestable";
}
@GetMapping("/leaves/end/{leaveId}")
public String endLeave(@PathVariable Long leaveId){
        employeeService.endLeaves(leaveId);

        return "redirect:/hr/overdue/leaves/table";
}
@GetMapping("/hr/ended/leaves/table")
public String endedLeaveDetails(Model model){
    List<EndedLeavesDetailsDto>endedLeaveDetails=employeeService.endedLeaveDetails();
    model.addAttribute("endedLeaveDetailsDto",endedLeaveDetails);
 return "/EndedLeavestable";
}
@GetMapping("/hr/all/employees/table")
public String allEmployeesDetails(Model model){
      List<AllEmployeesDetailsDto>  allEmployeesDetailsDtos=employeeService.allEmployees();
      model.addAttribute("allEmployeesDetailsDtos",allEmployeesDetailsDtos);
  return "/AllEmployeesTable";
}
@GetMapping("/hr/employees/on/leave/table")
public String employeesOnLeaveDetails(Model model){
        List<EmployeesOnLeaveDto>employeesOnLeave=employeeService.employeesOnLeaveDetails();
        model.addAttribute("employeesOnLeave",employeesOnLeave);
        return "/EmployeesOnLeaveDetailsTable";
}
@GetMapping("/hr/employees/editPage")
public String editPage(Model model,@RequestParam("employeeID") String employeeID){
    EditEmployeeDetailsDto editEmployeeDetailsDto=employeeService.getEmployeeDetails(employeeID);
    Map<String, List<String>> unitsByDepartment = new  HashMap<>();
    Map<String,List<Unit>> departmentsAndUnit=employeeService.getUnitAndDepartments();
    for(Map.Entry<String,List<Unit>> entry:departmentsAndUnit.entrySet()){
        for(Unit unit:entry.getValue()){
            unitsByDepartment.put(entry.getKey(),List.of(unit.getUnitName()));
        }
    }
    model.addAttribute("unitsByDepartment",unitsByDepartment);
    employeeService.getEmployeeDetails("hr");

// Repeat for professionsByDepartment and employeeRoleByDepartment if needed
    List<Departments>departments=employeeService.getAllDepartments();
    model.addAttribute("departments", departments);
    model.addAttribute("selectedDepartment", editEmployeeDetailsDto.getDepartments()); // for selected option
    model.addAttribute("editEmployeeDetailsDto",editEmployeeDetailsDto);
    return "/EditPage";
}
@GetMapping("/hr/employees/searchPage")
public String searchPage(){
        return "/searchPage";
}

}
