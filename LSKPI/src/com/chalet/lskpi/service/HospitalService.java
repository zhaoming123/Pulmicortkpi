package com.chalet.lskpi.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chalet.lskpi.model.Doctor;
import com.chalet.lskpi.model.Hospital;
import com.chalet.lskpi.model.HospitalSalesQueryObj;
import com.chalet.lskpi.model.HospitalSalesQueryParam;
import com.chalet.lskpi.model.KPIHospital4Export;
import com.chalet.lskpi.model.Monthly12Data;
import com.chalet.lskpi.model.MonthlyData;
import com.chalet.lskpi.model.MonthlyRatioData;
import com.chalet.lskpi.model.UserInfo;

/**
 * @author Chalet
 * @version 创建时间：2013年11月24日 下午5:14:28
 * 类说明
 */

public interface HospitalService {
	public Hospital getHospitalByName(String hospitalName) throws Exception;
	
	public Hospital getHospitalByCode(String hospitalCode) throws Exception;
	
	public List<Hospital> getHospitalsByUserTel(String telephone, String department) throws Exception;
	
	public List<Hospital> getPedWhHospitalsByUserTel(String telephone, String department) throws Exception;

	public List<Hospital> getMonthlyHospitalsByUserTel(String telephone) throws Exception;
	
	public void insert(List<Hospital> hospitals) throws Exception;
	
	public void insertMonthlyData( MonthlyData monthlyData ) throws Exception;
	
	public void delete() throws Exception;
	
	public MonthlyData getMonthlyData( String hospitalCode , Date date) throws Exception;
	
	public MonthlyData getMonthlyDataById( int id ) throws Exception;
	
	public void updateMonthlyData( MonthlyData monthlyData ) throws Exception;
	
	public List<Hospital> getHospitalsByKeyword( String keyword) throws Exception;
	
	public UserInfo getPrimarySalesOfHospital(String hospitalCode) throws Exception;
	
	public List<MonthlyRatioData> getMonthlyRatioData(UserInfo currentUser) throws Exception;
	public List<MonthlyRatioData> getChildMonthlyRatioData(UserInfo currentUser) throws Exception;
	public MonthlyRatioData getUpperUserMonthlyRatioData(UserInfo currentUser) throws Exception;
	
	public List<Hospital> getAllHospitals() throws Exception;
	
	public List<MonthlyData> getMonthlyDataByDate(Date startDate, Date endDate) throws Exception;
	
	public List<Monthly12Data> get12MontlyDataOfUser(UserInfo user) throws Exception;
	public List<Monthly12Data> get12MontlyDataByCountory() throws Exception;
	
	public List<HospitalSalesQueryObj> getHospitalSalesList(HospitalSalesQueryParam queryParam) throws Exception;
	
	public List<MonthlyRatioData> getMonthlyCollectionData(Date chooseDate) throws Exception;
	
	public MonthlyRatioData getMonthlyCollectionSumData(Date chooseDate) throws Exception;
	
	public List<Hospital> getHospitalsOfHomeCollectionByUserTel(String telephone) throws Exception;
	public List<Doctor> getDoctorsOfCurrentUser( UserInfo currentUser ) throws Exception;
	public int getExistedDrNumByHospitalCode( String hospitalCode, String drName ) throws Exception;
	public int getExistedDrNumByHospitalCodeExcludeSelf(long dataId, String hospitalCode, String drName ) throws Exception;
	public int getTotalDrNumOfHospital(String hospitalCode) throws Exception;
	public void insertDoctor(Doctor doctor) throws Exception;
	public void updateDoctor(Doctor doctor) throws Exception;
	public void deleteDoctor(Doctor doctor, String currentUserTel) throws Exception;
	public void updateDoctorRelationship(int doctorId, String salesCode) throws Exception;
	public Doctor getDoctorById(int doctorId) throws Exception;
	
	public boolean hasLastWeeklyData() throws Exception;
	public void generateWeeklyDataOfHospital() throws Exception;
	public void generateWeeklyDataOfHospital(Date refreshDate) throws Exception;
	public int deleteOldHospitalWeeklyData(String duration) throws Exception;
	
	public List<KPIHospital4Export> getKPIHospitalByDepartment(String department) throws Exception;
	
    /**
     * 返回值是一个Map的list，Map中的键值对为<RSM,KPI医院数>
     * @return HOS count map
     * @throws Exception
     */
    public List<Map<String, Integer>> getKPIHosNumMap(String department, String isRe2, String level) throws Exception;
    
    /**
     * 返回值是一个Map的list，Map中的键值对为<RSM,销售数>
     * @return sales count map
     * @throws Exception
     */
    public List<Map<String, Integer>> getKPISalesNumMap(String department, String isRe2, String level) throws Exception;
    
    public void uploadPortNumData(Hospital hospitalWithPortNum) throws Exception;
    
    public void updateWHBWStatus(Hospital hospital) throws Exception;
    
    public void removeAllHospitalWhbwStatus() throws Exception;
    public void updateWHBWStatus(List<Hospital> hospitals) throws Exception;
}
