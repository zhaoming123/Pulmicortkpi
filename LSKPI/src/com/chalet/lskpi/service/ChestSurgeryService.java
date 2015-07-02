package com.chalet.lskpi.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chalet.lskpi.model.ChestSurgeryData;
import com.chalet.lskpi.model.Hospital;
import com.chalet.lskpi.model.MobileCHEDailyData;
import com.chalet.lskpi.model.MonthlyStatisticsData;
import com.chalet.lskpi.model.ReportProcessData;
import com.chalet.lskpi.model.ReportProcessDataDetail;
import com.chalet.lskpi.model.TopAndBottomRSMData;
import com.chalet.lskpi.model.UserInfo;
import com.chalet.lskpi.model.WeeklyRatioData;

public interface ChestSurgeryService {

    public ChestSurgeryData getChestSurgeryDataByHospital(String hospitalCode) throws Exception;
    public List<ChestSurgeryData> getChestSurgeryDataByDate(Date createdatebegin, Date createdateend) throws Exception;
    public ChestSurgeryData getChestSurgeryDataByHospitalAndDate(String hospitalCode, Date createdate) throws Exception;
    public ChestSurgeryData getChestSurgeryDataById(int id) throws Exception;
    public void insert(ChestSurgeryData chestSurgeryData, UserInfo operator, Hospital hospital) throws Exception;
    public void insert(ChestSurgeryData chestSurgeryData) throws Exception;
    public void update(ChestSurgeryData chestSurgeryData, UserInfo operator) throws Exception;
    
    public MobileCHEDailyData getDailyCHEParentData4Mobile(String telephone, String level)throws Exception;
    public List<MobileCHEDailyData> getDailyCHEData4Mobile( String telephone, UserInfo currentUser ) throws Exception;
    public List<MobileCHEDailyData> getDailyCHEChildData4Mobile( String telephone, UserInfo currentUser ) throws Exception;
    
    public List<MobileCHEDailyData> getDailyCHEData4MobileByRegionCenter(String regionCenter) throws Exception;
    
    public TopAndBottomRSMData getTopAndBottomRSMData(Timestamp paramDate) throws Exception;
    

	public ReportProcessData getSalesSelfReportProcessData(String telephone) throws Exception;
	public List<ReportProcessDataDetail> getSalesSelfReportProcessDetailData(String telephone) throws Exception;
	
	public ReportProcessData getDSMSelfReportProcessData(String telephone) throws Exception;
	public List<ReportProcessDataDetail> getDSMSelfReportProcessDetailData(String telephone) throws Exception;
	
	public ReportProcessData getRSMSelfReportProcessData(String telephone) throws Exception;
	public List<ReportProcessDataDetail> getRSMSelfReportProcessDetailData(String telephone) throws Exception;
	
	
	public List<WeeklyRatioData> getWeeklyData4Mobile( UserInfo currentUser ) throws Exception;
    public WeeklyRatioData getWeeklyCountoryData4Mobile() throws Exception;
    public WeeklyRatioData getHospitalWeeklyData4Mobile( String hospitalCode ) throws Exception;
    public WeeklyRatioData getLowerWeeklyData4Mobile( UserInfo currentUser, String lowerUserCode ) throws Exception;
    
    public int removeOldWeeklyData(String duration) throws Exception;
    public void generateWeeklyDataOfHospital() throws Exception;
    public void generateWeeklyDataOfHospital(Date refreshDate) throws Exception;
    public boolean hasLastWeeklyData() throws Exception;
    
    public List<MonthlyStatisticsData> getMonthlyStatisticsData(String beginDuraion, String endDuraion, String level) throws Exception;
    public MonthlyStatisticsData getMonthlyStatisticsCountryData(String beginDuraion, String endDuraion) throws Exception;
    public MonthlyStatisticsData getCoreOrEmergingMonthlyStatisticsCountryData(String beginDuraion, String endDuraion, String dragonType) throws Exception;
    public Map<String,MonthlyStatisticsData> getCoreOrEmergingMonthlyStatisticsData(String beginDuraion, String endDuraion, String level, String dragonType) throws Exception;
}
