package com.chalet.lskpi.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.chalet.lskpi.model.UserInfo;
import com.chalet.lskpi.service.UserService;
import com.chalet.lskpi.utils.BirtReportUtils;
import com.chalet.lskpi.utils.BrowserUtils;
import com.chalet.lskpi.utils.DateUtils;
import com.chalet.lskpi.utils.LsAttributes;
import com.chalet.lskpi.utils.StringUtils;

/**
 * @author Chalet
 * @version 创建时间：2014年1月2日 下午9:19:21
 * 类说明
 */

@Controller
public class BaseController {
	
	Logger logger = Logger.getLogger(BaseController.class);
    
    @Autowired
    @Qualifier("userService")
    private UserService userService;
    
    public boolean isCurrentUserValid(UserInfo currentUser, String currentUserTel, ModelAndView view, boolean isVerifyUserLevel ){
    	
    	if( null == currentUserTel || "".equalsIgnoreCase(currentUserTel) || null == currentUser ){
    		view.addObject(LsAttributes.JSP_VERIFY_MESSAGE, LsAttributes.NO_USER_FOUND_WEB);
        	view.setViewName("index");
        	return false;
    	}
    	
    	if( isVerifyUserLevel ){
    	    if(  ! ( LsAttributes.USER_LEVEL_REP.equalsIgnoreCase(currentUser.getLevel()) 
    	            || LsAttributes.USER_LEVEL_DSM.equalsIgnoreCase(currentUser.getLevel()))){
    	        view.addObject(LsAttributes.JSP_VERIFY_MESSAGE, LsAttributes.RETURNED_MESSAGE_3);
    	        view.setViewName("index");
    	        return false;
    	    }
    	}
    	
    	return true;
    }

    public String verifyCurrentUser(HttpServletRequest request, ModelAndView view){
//    	String currentUserTelephone = request.getParameter(LsAttributes.CURRENT_OPERATOR);
    	String currentUserTelephone = "18647666988";
    	//String currentUserTelephone = "18916837652";

        try{
        	if( null == currentUserTelephone || "".equalsIgnoreCase(currentUserTelephone)  ){
        		currentUserTelephone = (String)request.getSession(true).getAttribute(LsAttributes.CURRENT_OPERATOR);
        	}else{
        	    if( !BrowserUtils.isMoblie(request.getHeader("User-Agent")) ){
        	        //currentUserTelephone = StringUtils.decode(currentUserTelephone);
        	        logger.info("decode the parameter telephone, currentUserTelephone is " + currentUserTelephone);
        	    }
        	    
        		request.getSession(true).setAttribute(LsAttributes.CURRENT_OPERATOR, currentUserTelephone);
        	}
        	
        	if( null == currentUserTelephone || "".equalsIgnoreCase(currentUserTelephone)  ){
        		logger.warn("fail to get the user tel from APP side.");
        		view.addObject(LsAttributes.JSP_VERIFY_MESSAGE, LsAttributes.NO_USER_FOUND);
        	}else{
        		UserInfo user = (UserInfo)request.getSession(true).getAttribute(LsAttributes.CURRENT_OPERATOR_OBJECT);
        		if( user == null || !user.getTelephone().equalsIgnoreCase(currentUserTelephone) ){
        			user = userService.getUserInfoByTel(currentUserTelephone);
        			request.getSession(true).setAttribute(LsAttributes.CURRENT_OPERATOR_OBJECT, user);
        		}

        		if( user == null ){
        			logger.warn("fail to get the user info.");
        			view.addObject(LsAttributes.JSP_VERIFY_MESSAGE, LsAttributes.NO_USER_FOUND);
        		}
        	}
        }catch(EmptyResultDataAccessException er){
        	logger.info("there is no user found whose telephone is " + currentUserTelephone);
        	view.addObject(LsAttributes.JSP_VERIFY_MESSAGE, LsAttributes.NO_USER_FOUND);
        }catch(Exception e){
        	logger.error("fail to verify the current user,",e);
        	view.addObject(LsAttributes.JSP_VERIFY_MESSAGE, LsAttributes.NO_USER_FOUND);
        }
        
        return currentUserTelephone;
    }
    
    public void populateDailyReportTitle(UserInfo currentUser, ModelAndView view, String datatype){
        //set the title of the table
          StringBuffer selfTitle = new StringBuffer();
          StringBuffer childTitle = new StringBuffer();
          switch(currentUser.getLevel()){ 
              case LsAttributes.USER_LEVEL_DSM:
                  selfTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                      .append(currentUser.getRegion())
                      .append(LsAttributes.DAILYREPORTTITLE_2)
                      .append(LsAttributes.USER_LEVEL_DSM)
                      .append(LsAttributes.DAILYREPORTTITLE_SPLIT_1);
                  if( !LsAttributes.MONTHLYREPORTTITLE.equalsIgnoreCase(datatype) ){
                	  selfTitle.append(DateUtils.getYesterDayForDailyReportTitle());
                  }else{
                      selfTitle.append(DateUtils.getLastMonthForTitle());
                  }
                  selfTitle.append(LsAttributes.DAILYREPORTTITLE_SPLIT_2).append(datatype);
                  
                  childTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                  .append(currentUser.getName())
                  .append(LsAttributes.DAILYREPORTTITLE_2)
                  .append(LsAttributes.DAILYREPORTTITLE_REP)
                  .append(LsAttributes.DAILYREPORTTITLE_SPLIT_1);
                  if( !LsAttributes.MONTHLYREPORTTITLE.equalsIgnoreCase(datatype) ){
                	  childTitle.append(DateUtils.getYesterDayForDailyReportTitle());
                  }else{
                      childTitle.append(DateUtils.getLastMonthForTitle());
                  }
                  childTitle.append(LsAttributes.DAILYREPORTTITLE_SPLIT_2).append(datatype);
                  break;
              case LsAttributes.USER_LEVEL_RSM:
                  selfTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                  .append(currentUser.getRegionCenterCN())
                  .append(LsAttributes.DAILYREPORTTITLE_2)
                  .append(LsAttributes.USER_LEVEL_RSM)
                  .append(LsAttributes.DAILYREPORTTITLE_SPLIT_1);
                  if( !LsAttributes.MONTHLYREPORTTITLE.equalsIgnoreCase(datatype) ){
                      selfTitle.append(DateUtils.getYesterDayForDailyReportTitle());
                  }else{
                      selfTitle.append(DateUtils.getLastMonthForTitle());
                  }
                  selfTitle.append(LsAttributes.DAILYREPORTTITLE_SPLIT_2).append(datatype);
                  
                  childTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                  .append(currentUser.getRegion())
                  .append(LsAttributes.DAILYREPORTTITLE_2)
                  .append(LsAttributes.USER_LEVEL_DSM)
                  .append(LsAttributes.DAILYREPORTTITLE_SPLIT_1);
                  if( !LsAttributes.MONTHLYREPORTTITLE.equalsIgnoreCase(datatype) ){
                      childTitle.append(DateUtils.getYesterDayForDailyReportTitle());
                  }else{
                      childTitle.append(DateUtils.getLastMonthForTitle());
                  }
                  childTitle.append(LsAttributes.DAILYREPORTTITLE_SPLIT_2).append(datatype);
                  break;
              case LsAttributes.USER_LEVEL_RSD:
                  selfTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                  .append(LsAttributes.DAILYREPORTTITLE_4)
                  .append(LsAttributes.DAILYREPORTTITLE_2)
                  .append(LsAttributes.USER_LEVEL_RSD)
                  .append(LsAttributes.DAILYREPORTTITLE_SPLIT_1);
                  if( !LsAttributes.MONTHLYREPORTTITLE.equalsIgnoreCase(datatype) ){
                      selfTitle.append(DateUtils.getYesterDayForDailyReportTitle());
                  }else{
                      selfTitle.append(DateUtils.getLastMonthForTitle());
                  }
                  selfTitle.append(LsAttributes.DAILYREPORTTITLE_SPLIT_2).append(datatype);
                  
                  childTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                  .append(currentUser.getRegionCenterCN())
                  .append(LsAttributes.DAILYREPORTTITLE_2)
                  .append(LsAttributes.USER_LEVEL_RSM)
                  .append(LsAttributes.DAILYREPORTTITLE_SPLIT_1);
                  if( !LsAttributes.MONTHLYREPORTTITLE.equalsIgnoreCase(datatype) ){
                      childTitle.append(DateUtils.getYesterDayForDailyReportTitle());
                  }else{
                      childTitle.append(DateUtils.getLastMonthForTitle());
                  }
                  childTitle.append(LsAttributes.DAILYREPORTTITLE_SPLIT_2).append(datatype);
                  break;
              case LsAttributes.USER_LEVEL_BM:
                  selfTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                  .append(LsAttributes.DAILYREPORTTITLE_4)
                  .append(LsAttributes.DAILYREPORTTITLE_2)
                  .append(LsAttributes.USER_LEVEL_RSD)
                  .append(LsAttributes.DAILYREPORTTITLE_SPLIT_1);
                  if( !LsAttributes.MONTHLYREPORTTITLE.equalsIgnoreCase(datatype) ){
                      selfTitle.append(DateUtils.getYesterDayForDailyReportTitle());
                  }else{
                      selfTitle.append(DateUtils.getLastMonthForTitle());
                  }
                  selfTitle.append(LsAttributes.DAILYREPORTTITLE_SPLIT_2).append(datatype);
                  break;
          }
          view.addObject("selfTitle", selfTitle.toString());
          view.addObject("childTitle", childTitle.toString());
      }
    
    public void populateMonthlyDataTitle(UserInfo currentUser, ModelAndView view, String datatype){
    	//set the title of the monthly table
    	StringBuffer monthlyDataTitle = new StringBuffer();
    	switch(currentUser.getLevel()){ 
    	case LsAttributes.USER_LEVEL_DSM:
    		monthlyDataTitle.append(LsAttributes.DAILYREPORTTITLE_1)
    		.append(currentUser.getRegion())
    		.append(LsAttributes.DAILYREPORTTITLE_2)
//    		.append(LsAttributes.USER_LEVEL_DSM)
    		.append(" ")
    		.append(currentUser.getName())
    		.append(" ")
    		.append(datatype);
    		break;
    	case LsAttributes.USER_LEVEL_RSM:
    		monthlyDataTitle.append(LsAttributes.DAILYREPORTTITLE_1)
    		.append(currentUser.getRegionCenterCN())
    		.append(LsAttributes.DAILYREPORTTITLE_2)
//    		.append(LsAttributes.USER_LEVEL_RSM)
    		.append(" ")
    		.append(currentUser.getRegion())
    		.append(" ")
    		.append(datatype);
    		break;
    	case LsAttributes.USER_LEVEL_RSD:
    		monthlyDataTitle.append(LsAttributes.DAILYREPORTTITLE_1)
    		.append(LsAttributes.DAILYREPORTTITLE_4)
    		.append(LsAttributes.DAILYREPORTTITLE_2)
//    		.append(LsAttributes.USER_LEVEL_RSD)
    		.append(" ")
    		.append(currentUser.getRegionCenterCN())
    		.append(" ")
    		.append(datatype);
    		break;
    	case LsAttributes.USER_LEVEL_BM:
    		break;
    	}
    	view.addObject("monthlyDataTitle", monthlyDataTitle.toString());
    }
    
    public void populateHomeWeeklyReportTitle(UserInfo currentUser, ModelAndView view, String datatype, String dsmName){
        StringBuffer homeDataTitle = new StringBuffer();
        StringBuffer childTitle = new StringBuffer();
        
        switch(currentUser.getLevel()){ 
            case LsAttributes.USER_LEVEL_REP:
                homeDataTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                .append(dsmName)
                .append(LsAttributes.DAILYREPORTTITLE_2)
                .append(" ")
                .append(LsAttributes.DAILYREPORTTITLE_REP)
                .append(datatype);
                break;
            case LsAttributes.USER_LEVEL_DSM:
                homeDataTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                .append(currentUser.getRegion())
                .append(LsAttributes.DAILYREPORTTITLE_2)
                .append(LsAttributes.USER_LEVEL_DSM)
                .append(" ")
                .append(datatype);
                
                childTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                .append(currentUser.getName())
                .append(LsAttributes.DAILYREPORTTITLE_2)
                .append(LsAttributes.USER_LEVEL_REP)
                .append(" ")
                .append(datatype);
                break;
            case LsAttributes.USER_LEVEL_RSM:
                homeDataTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                .append(currentUser.getRegionCenterCN())
                .append(LsAttributes.DAILYREPORTTITLE_2)
                .append(LsAttributes.USER_LEVEL_RSM)
                .append(" ")
                .append(datatype);
                
                childTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                .append(currentUser.getRegion())
                .append(LsAttributes.DAILYREPORTTITLE_2)
                .append(LsAttributes.USER_LEVEL_DSM)
                .append(" ")
                .append(datatype);
                break;
            case LsAttributes.USER_LEVEL_RSD:
                homeDataTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                .append(LsAttributes.DAILYREPORTTITLE_4)
                .append(LsAttributes.DAILYREPORTTITLE_2)
                .append(LsAttributes.USER_LEVEL_RSD)
                .append(" ")
                .append(datatype);
                
                childTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                .append(currentUser.getRegionCenterCN())
                .append(LsAttributes.DAILYREPORTTITLE_2)
                .append(LsAttributes.USER_LEVEL_RSM)
                .append(" ")
                .append(datatype);
                break;
            case LsAttributes.USER_LEVEL_BM:
                homeDataTitle.append(LsAttributes.DAILYREPORTTITLE_1)
                .append(LsAttributes.DAILYREPORTTITLE_4)
                .append(LsAttributes.DAILYREPORTTITLE_2)
                .append(LsAttributes.USER_LEVEL_RSD)
                .append(" ")
                .append(datatype);
                break;
        }
        view.addObject("homeDataTitle", homeDataTitle.toString());
        view.addObject("childTitle", childTitle.toString());
    }
    
    public void checkAndCreateFileFolder(String filePath){
        File file = new File(filePath);
        if( !file.exists() ){
            logger.info("filePath " + filePath + " is not found, create it automaticlly");
            file.mkdirs();
        }
    }
    
    public void createAllDSMDailyReport(BirtReportUtils html, String basePath, String yesterday, long systemTime, String department ) throws Exception{
    	checkAndCreateFileFolder(basePath + "resAllDSMDailyReport/"+yesterday);
    	checkAndCreateFileFolder(basePath + "pedAllDSMDailyReport/"+yesterday);
    	
    	String dailyPEDReportName = basePath + "pedAllDSMDailyReport/"+yesterday+"/儿科日报-DSM-"+yesterday+"-"+systemTime+".xlsx";
    	String dailyRESReportName = basePath + "resAllDSMDailyReport/"+yesterday+"/呼吸科日报-DSM-"+yesterday+"-"+systemTime+".xlsx";
    	
    	if( "2".equalsIgnoreCase(department) && !new File(dailyPEDReportName).exists() ){
    		html.runAllDailyReport( basePath + "reportDesigns/exportDSMPEDDaily.rptdesign",yesterday,dailyPEDReportName);
    		logger.info("the PED report of all DSM is done.");
    	}else{
    		logger.info("The PED report of all DSM is already generated, no need to do again.");
    	}
    	
    	if( "1".equalsIgnoreCase(department) && !new File(dailyRESReportName).exists() ){
    		html.runAllDailyReport( basePath + "reportDesigns/exportDSMRESDaily.rptdesign",yesterday,dailyRESReportName);
    		logger.info("the RES report of all DSM is done.");
    	}else{
    		logger.info("The RES report of all DSM is already generated, no need to do again.");
    	}
    }
    
    /**
     * RE2 周周报 PDF
     * @param html
     * @param basePath
     * @param yesterday
     * @param systemTime
     * @param department
     * @throws Exception
     */
    public void createRe2Report(BirtReportUtils html, String basePath, long systemTime, String selfLevel, String fileNameWithFolder) throws Exception{
    	String re2ReportName = new StringBuffer(basePath).append(fileNameWithFolder).toString();
    	if( LsAttributes.USER_LEVEL_RSD.equals(selfLevel) ){
    		html.runRe2Report( basePath + "reportDesigns/weeklyMonthlyRESReportBU.rptdesign",re2ReportName);
    	}else if( LsAttributes.USER_LEVEL_RSM.equals(selfLevel) ){
    		html.runRe2Report( basePath + "reportDesigns/weeklyMonthlyRESReportBU.rptdesign",re2ReportName);
    	}else if( LsAttributes.USER_LEVEL_DSM.equals(selfLevel) ){
    		html.runRe2Report( basePath + "reportDesigns/weeklyMonthlyRESReportBU.rptdesign",re2ReportName);
    	}
		logger.info("the RES report of re2 is done.");
    }
    
    public void createAllRSMDailyReport(BirtReportUtils html, String basePath, String yesterday, long systemTime, String department ) throws Exception{
        checkAndCreateFileFolder(basePath + "pedAllRSMDailyReport/"+yesterday);
        checkAndCreateFileFolder(basePath + "resAllRSMDailyReport/"+yesterday);
        
    	String dailyPEDReportName = basePath + "pedAllRSMDailyReport/"+yesterday+"/儿科日报-RSM-"+yesterday+"-"+systemTime+".xlsx";
    	String dailyRESReportName = basePath + "resAllRSMDailyReport/"+yesterday+"/呼吸科日报-RSM-"+yesterday+"-"+systemTime+".xlsx";
    	
    	if( "2".equalsIgnoreCase(department) && !new File(dailyPEDReportName).exists() ){
    		html.runAllDailyReport( basePath + "reportDesigns/exportRSMPEDDaily.rptdesign",yesterday,dailyPEDReportName);
    		logger.info("the PED report of all RSM is done.");
    	}else{
    		logger.info("The PED report of all RSM is already generated, no need to do again.");
    	}
    	
    	if( "1".equalsIgnoreCase(department) && !new File(dailyRESReportName).exists() ){
    		html.runAllDailyReport( basePath + "reportDesigns/exportRSMRESDaily.rptdesign",yesterday,dailyRESReportName);
    		logger.info("the RES report of all RSM is done.");
    	}else{
    		logger.info("The RES report of all RSM is already generated, no need to do again.");
    	}
    }
    
    public void populateDailyReportTitle4AllRSM(ModelAndView view){
    	StringBuffer titleSuffix = new StringBuffer();
    	titleSuffix.append(LsAttributes.DAILYREPORTTITLE_2)
        .append(LsAttributes.USER_LEVEL_RSM)
        .append(LsAttributes.DAILYREPORTTITLE_SPLIT_1)
        .append(DateUtils.getYesterDayForDailyReportTitle())
        .append(LsAttributes.DAILYREPORTTITLE_SPLIT_2).append(LsAttributes.DAILYREPORTTITLE_3);
        
        view.addObject("titleSuffix", titleSuffix.toString());
    }
    
    public void populateHomeWeeklyReportTitle4AllRSM(ModelAndView view, String pedType){
    	StringBuffer titleSuffix = new StringBuffer();
    	titleSuffix.append(LsAttributes.DAILYREPORTTITLE_2)
    	.append(LsAttributes.USER_LEVEL_RSM);
    	
    	if( "emerging".equalsIgnoreCase(pedType) ){
    		titleSuffix.append(LsAttributes.HOMEWEEKLYREPORTTITLE_EMERGING);
    	}else if( "room".equalsIgnoreCase(pedType) ){
    		titleSuffix.append(LsAttributes.HOMEWEEKLYREPORTTITLE_ROOM);
    	}
    	titleSuffix.append(LsAttributes.HOMEWEEKLYREPORTTITLE);
    	
    	view.addObject("titleSuffix", titleSuffix.toString());
    }
}
