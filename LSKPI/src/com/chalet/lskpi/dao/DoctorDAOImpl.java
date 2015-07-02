package com.chalet.lskpi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.chalet.lskpi.mapper.DoctorRowMapper;
import com.chalet.lskpi.mapper.DoctorToBeDeletedRowMapper;
import com.chalet.lskpi.model.Doctor;
import com.chalet.lskpi.model.DoctorToBeDeleted;
import com.chalet.lskpi.utils.DataBean;

@Repository("doctorDAO")
public class DoctorDAOImpl implements DoctorDAO {

    @Autowired
    @Qualifier("dataBean")
    private DataBean dataBean;
    
    private Logger logger = Logger.getLogger(DoctorDAOImpl.class);
    
    public int getTotalDrNumOfHospital(String hospitalCode) throws Exception {
        StringBuffer sql = new StringBuffer("")
        .append(" select count(1) ")
        .append(" from tbl_doctor ")
        .append(" where hospitalCode=?");
        return dataBean.getJdbcTemplate().queryForInt(sql.toString(), new Object[]{hospitalCode});
    }
    
    public int getTotalRemovedDrNumOfHospital(String hospitalCode) throws Exception {
        StringBuffer sql = new StringBuffer("")
        .append(" select count(1) ")
        .append(" from tbl_doctor_history ")
        .append(" where hospitalCode=?");
        return dataBean.getJdbcTemplate().queryForInt(sql.toString(), new Object[]{hospitalCode});
    }
    
    public int getExistedDrNumByHospitalCode(String hospitalCode, String drName) throws Exception {
        StringBuffer sql = new StringBuffer("")
        .append(" select count(1) ")
        .append(" from tbl_doctor ")
        .append(" where hospitalCode=? and ( name = ? or name like ?)");
        return dataBean.getJdbcTemplate().queryForInt(sql.toString(), new Object[]{hospitalCode,drName,drName+"(%)"});
    }
    
    public int getExistedDrNumByHospitalCodeExcludeSelf(long dataId, String hospitalCode, String drName) throws Exception {
        StringBuffer sql = new StringBuffer("")
        .append(" select count(1) ")
        .append(" from tbl_doctor ")
        .append(" where hospitalCode=? and ( name = ? or name like ?) and id != ?");
        return dataBean.getJdbcTemplate().queryForInt(sql.toString(), new Object[]{hospitalCode,drName,drName+"(%)",dataId});
    }

    public int insertDoctor(final Doctor doctor) throws Exception {
        logger.info(">>HospitalDAOImpl insertDoctor");
        
        final String sql = "insert into tbl_doctor(id,name,code,hospitalCode,salesCode,createdate,modifydate) values(null,?,LPAD(?,4,'0'),?,?,date_sub(NOW(),interval 7 day),NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        dataBean.getJdbcTemplate().update(new PreparedStatementCreator(){
            @Override
            public PreparedStatement createPreparedStatement(
                    Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, doctor.getName());
                ps.setString(2, doctor.getCode());
                ps.setString(3, doctor.getHospitalCode());
                ps.setString(4, doctor.getSalesCode());
                return ps;
            }
        }, keyHolder);
        logger.info("insertDoctor,returned id is "+keyHolder.getKey().intValue());
        return keyHolder.getKey().intValue();
    }
    
    public void insertDoctors(final List<Doctor> doctors) throws Exception {
        logger.info(">>HospitalDAOImpl insertDoctors when uploading doctor");
        String insertSQL = "insert into tbl_doctor(id,name,code,hospitalCode,salesCode,createdate,modifydate) values(null,?,?,?,?,date_sub(NOW(),interval 7 day),now())";
        dataBean.getJdbcTemplate().batchUpdate(insertSQL, new BatchPreparedStatementSetter() {
            
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, doctors.get(i).getName());
                ps.setString(2, doctors.get(i).getCode());
                ps.setString(3, doctors.get(i).getHospitalCode());
                ps.setString(4, doctors.get(i).getSalesCode());
            }
            
            @Override
            public int getBatchSize() {
                return doctors.size();
            }
        });
    }
    
    public void updateDoctorRelationship(int doctorId, String salesCode) throws Exception {
        StringBuffer sql = new StringBuffer("update tbl_doctor set ");
        sql.append("modifydate=NOW()");
        sql.append(", salesCode=? ");
        sql.append(" where id=? ");
        dataBean.getJdbcTemplate().update(sql.toString(), new Object[]{salesCode,doctorId});
    }

    public void updateDoctor(Doctor doctor) throws Exception {
        StringBuffer sql = new StringBuffer("update tbl_doctor set ");
        sql.append("modifydate=NOW()");
        sql.append(", name=? ");
        sql.append(" where id=? ");
        dataBean.getJdbcTemplate().update(sql.toString(), new Object[]{doctor.getName(),doctor.getId()});
    }
    
    public void backupDoctor(final Doctor doctor) throws Exception {
        final StringBuffer sql = new StringBuffer("")
        .append("insert into tbl_doctor_history(drName,drCode,doctorId,hospitalCode,salesCode,reason,createdate,modifydate)")
        .append(" select name as drName, code as drCode, id as doctorId,hospitalCode,salesCode,?,now(),now() from tbl_doctor ")
        .append(" where id=?");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        dataBean.getJdbcTemplate().update(new PreparedStatementCreator(){
            @Override
            public PreparedStatement createPreparedStatement(
                    Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, doctor.getReason());
                ps.setInt(2, doctor.getId());
                return ps;
            }
        }, keyHolder);
        logger.info("backupDoctor,returned id is "+keyHolder.getKey().intValue());
    }
    
    public void deleteDoctor(Doctor doctor) throws Exception {
        String sql = "delete from tbl_doctor where id=?";
        dataBean.getJdbcTemplate().update(sql, new Object[]{doctor.getId()});
    }
    
    public void cleanDoctor() throws Exception {
        dataBean.getJdbcTemplate().update("delete from tbl_doctor");
    }

    public Doctor getDoctorById(int doctorId) throws Exception {
        StringBuffer sb = new StringBuffer("");
        sb.append("select d.id, d.name as drName, d.code as drCode, d.hospitalCode,")
        .append(" h.name as hospitalName, ")
        .append(" d.salesCode, ")
        .append(" ( select distinct name from tbl_userinfo u where u.userCode = d.salesCode and u.superior = h.dsmCode and u.region = h.rsmRegion ) as salesName")
        .append(" from tbl_doctor d, tbl_hospital h ")
        .append(" where d.hospitalCode = h.code and d.id=? ");
        return dataBean.getJdbcTemplate().queryForObject(sb.toString(), new Object[]{doctorId},new DoctorRowMapper());
    }
    

    public List<Doctor> getDoctorsBySalesCode(String salesCode) throws Exception {
        List<Doctor> doctors = new ArrayList<Doctor>();
        StringBuffer sql = new StringBuffer("")
        .append(" select d.id ")
        .append(" , d.name as drName ")
        .append(" , d.code as drCode ")
        .append(" , h.code as hospitalCode ")
        .append(" , h.name as hospitalName ")
        .append(" , case when d.salesCode is null or d.salesCode='' or d.salesCode='#N/A' then 'Vacant' else d.salesCode end salesCode ")
        .append(" , case when d.salesCode is null or d.salesCode='' or d.salesCode='#N/A' then 'Vacant' else ")
        .append(" (select distinct u.name from tbl_userinfo u where u.region = h.rsmRegion and u.superior = h.dsmCode and u.userCode=d.salesCode) end salesName ")
        .append(" from tbl_doctor d, tbl_hospital h, tbl_hos_user hu ")
        .append(" where d.hospitalCode = hu.hosCode and h.code=hu.hosCode and hu.userCode = ?")
        .append(" and not exists (")
        .append(" 	select 1 from tbl_doctor_approval da ")
        .append(" 	where da.drId = d.id ")
        .append(" 	and da.status = '0' ")
        .append(" )");
        doctors = dataBean.getJdbcTemplate().query(sql.toString(), new Object[]{salesCode}, new DoctorRowMapper());
        return doctors;
    }
    
    public List<Doctor> getDoctorsByDsmCode(String dsmCode) throws Exception {
        List<Doctor> doctors = new ArrayList<Doctor>();
        StringBuffer sql = new StringBuffer("")
        .append(" select d.id ")
        .append(" , d.name as drName ")
        .append(" , d.code as drCode ")
        .append(" , h.code as hospitalCode ")
        .append(" , h.name as hospitalName ")
        .append(" , case when d.salesCode is null or d.salesCode='' or d.salesCode='#N/A' then 'Vacant' else d.salesCode end salesCode ")
        .append(" , case when d.salesCode is null or d.salesCode='' or d.salesCode='#N/A' then 'Vacant' else ")
        .append(" (select distinct u.name from tbl_userinfo u where u.region = h.rsmRegion and u.superior = h.dsmCode and u.userCode=d.salesCode) end salesName ")
        .append(" from tbl_doctor d, tbl_hospital h ")
        .append(" where d.hospitalCode = h.code and h.dsmCode=? ")
        .append(" and not exists (")
        .append(" 	select 1 from tbl_doctor_approval da ")
        .append(" 	where da.drId = d.id ")
        .append(" 	and da.status = '0' ")
        .append(" )");
        doctors = dataBean.getJdbcTemplate().query(sql.toString(), new Object[]{dsmCode}, new DoctorRowMapper());
        return doctors;
    }
    
    public List<Doctor> getDoctorsByRegion(String region) throws Exception {
        List<Doctor> doctors = new ArrayList<Doctor>();
        StringBuffer sql = new StringBuffer("")
        .append(" select d.id ")
        .append(" , d.name as drName ")
        .append(" , d.code as drCode ")
        .append(" , h.code as hospitalCode ")
        .append(" , h.name as hospitalName ")
        .append(" , case when d.salesCode is null or d.salesCode='' then 'Vacant' else d.salesCode end salesCode ")
        .append(" , case when d.salesCode is null or d.salesCode='' then 'Vacant' else ")
        .append(" (select distinct u.name from tbl_userinfo u where u.userCode=d.salesCode) end salesName ")
        .append(" from tbl_doctor d, tbl_hospital h ")
        .append(" where d.hospitalCode = h.code and h.rsmRegion=? ") 
        .append(" and not exists (")
        .append(" 	select 1 from tbl_doctor_approval da ")
        .append(" 	where da.drId = d.id ")
        .append(" 	and da.status = '0' ")
        .append(" )");
        doctors = dataBean.getJdbcTemplate().query(sql.toString(), new Object[]{region}, new DoctorRowMapper());
        return doctors;
    }
    
    public List<Doctor> getDoctorsByRegionCenter(String regionCenter) throws Exception {
        List<Doctor> doctors = new ArrayList<Doctor>();
        StringBuffer sql = new StringBuffer("")
        .append(" select d.id ")
        .append(" , d.name as drName ")
        .append(" , d.code as drCode ")
        .append(" , h.code as hospitalCode ")
        .append(" , h.name as hospitalName ")
        .append(" , case when d.salesCode is null or d.salesCode='' then 'Vacant' else d.salesCode end salesCode ")
        .append(" , case when d.salesCode is null or d.salesCode='' then 'Vacant' else (select distinct u.name from tbl_userinfo u where u.userCode=d.salesCode) end salesName ")
        .append(" from tbl_doctor d, tbl_hospital h ")
        .append(" where d.hospitalCode = h.code and h.region=? ")
        .append(" and not exists (")
        .append(" 	select 1 from tbl_doctor_approval da ")
        .append(" 	where da.drId = d.id ")
        .append(" 	and da.status = '0' ")
        .append(" )");
        doctors = dataBean.getJdbcTemplate().query(sql.toString(), new Object[]{regionCenter}, new DoctorRowMapper());
        return doctors;
    }
    
    public boolean drHasLastWeekData(int doctorId, Date beginDate, Date endDate) throws Exception {
        int count = dataBean.getJdbcTemplate().queryForInt("select count(1) from tbl_home_data where doctorId=? and createdate between ? and ?", new Object[]{doctorId,new Timestamp(beginDate.getTime()),new Timestamp(endDate.getTime())});
        return count>0;
    }
    
	@Override
	public boolean isDrExists(String hospitalCode, String drName)
			throws Exception {
		int count = dataBean.getJdbcTemplate().queryForInt("select count(1) from tbl_doctor where hospitalCode=? and name=?", new Object[]{hospitalCode,drName});
		return count>0;
	}

	@Override
	public List<DoctorToBeDeleted> getAllDoctorsToBeDeleted() throws Exception {
		List<DoctorToBeDeleted> doctors = new ArrayList<DoctorToBeDeleted>();
        StringBuffer sql = new StringBuffer("")
        .append(" select d.id as drId")
        .append(" , d.name as drName ")
        .append(" , h.code as hospitalCode ")
        .append(" , h.name as hospitalName ")
        .append(" , da.deleteReason as deleteReason ")
        .append(" from tbl_doctor d, tbl_doctor_approval da, tbl_hospital h ")
        .append(" where d.id = da.drId and d.hospitalCode = h.code and da.status='0' ");
        doctors = dataBean.getJdbcTemplate().query(sql.toString(), new DoctorToBeDeletedRowMapper());
        return doctors;
	}
	
	@Override
	public void storeToBeDeletedDoctor(final DoctorToBeDeleted doctor, final String currentUserTel)
			throws Exception {
		final StringBuffer sql = new StringBuffer("")
        .append(" insert into tbl_doctor_approval(drId,deleteReason,status,createdate,modifydate,operatorTel)")
        .append(" values(?,?,'0',NOW(),NOW(),?)");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        dataBean.getJdbcTemplate().update(new PreparedStatementCreator(){
            @Override
            public PreparedStatement createPreparedStatement(
                    Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, doctor.getDrId());
                ps.setString(2, doctor.getDeleteReason());
                ps.setString(3, currentUserTel);
                return ps;
            }
        }, keyHolder);
        logger.info("store the to be deleted doctor,returned id is "+keyHolder.getKey().intValue());
	}

	@Override
	public String getDeleteReasonByDrId(int drId) throws Exception {
		StringBuffer sql = new StringBuffer("")
        .append(" select distinct deleteReason ")
        .append(" from tbl_doctor_approval ")
        .append(" where drId=? and status = '0' ");
        List<String> deleteReasons = dataBean.getJdbcTemplate().queryForList(sql.toString(), new Object[]{drId}, String.class);
        if( null != deleteReasons ){
        	return deleteReasons.get(0);
        }else{
        	return "UNKNOWN";
        }
	}
	
	@Override
	public void updateApprovalStatus(Doctor doctor, String currentUserTel, String status) throws Exception {
		StringBuffer sql = new StringBuffer("update tbl_doctor_approval set ");
        sql.append("modifydate=NOW()")
	        .append(", status=? ")
	        .append(", operatorTel=? ")
	        .append(" where drId=? and status='0' ");
        dataBean.getJdbcTemplate().update(sql.toString(), new Object[]{status,currentUserTel,doctor.getId()});
	}
	
    public Doctor getDoctorByDoctorNameAndHospital(String doctorName,String hospitalCode) throws Exception {
        StringBuffer sb = new StringBuffer("");
        sb.append("select d.id, d.name as drName, d.code as drCode, d.hospitalCode,")
        .append(" '' as hospitalName, ")
        .append(" d.salesCode, ")
        .append(" '' as salesName")
        .append(" from tbl_doctor d ")
        .append(" where d.hospitalCode = ? and d.name=? ");
        return dataBean.getJdbcTemplate().queryForObject(sb.toString(), new Object[]{hospitalCode,doctorName},new DoctorRowMapper());
    }
	
    public DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(DataBean dataBean) {
        this.dataBean = dataBean;
    }
}
