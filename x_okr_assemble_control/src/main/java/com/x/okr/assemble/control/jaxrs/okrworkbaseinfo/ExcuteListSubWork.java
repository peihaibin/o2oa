package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ExcuteListSubWork extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListSubWork.class );
	
	protected ActionResult<List<WrapOutOkrWorkBaseInfo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<List<WrapOutOkrWorkBaseInfo>>();
		List<WrapOutOkrWorkBaseInfo> wraps = null;
		List<String> ids = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
		}
		if(check){
			try {
				ids = okrWorkBaseInfoService.listByParentId( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkBaseInfoProcessException( e, "根据指定工作ID查询所有下级工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if(check){
			if( ids != null && !ids.isEmpty()){
				try {
					okrWorkBaseInfoList = okrWorkBaseInfoService.listByIds(ids);
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkBaseInfoProcessException( e, "根据具体工作ID列表查询具体工作信息列表时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if(check){
			if( okrWorkBaseInfoList != null && !okrWorkBaseInfoList.isEmpty()){
				try {
					wraps = wrapout_copier.copy(okrWorkBaseInfoList);
				} catch (Exception e) {
					Exception exception = new WorkBaseInfoProcessException( e, "将查询结果转换为可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if(check){
			if( wraps != null && !wraps.isEmpty() ){
				for( WrapOutOkrWorkBaseInfo wrapOutOkrWorkBaseInfo : wraps ){
					try {
						okrWorkDetailInfo = okrWorkDetailInfoService.get( wrapOutOkrWorkBaseInfo.getId() );
						if( okrWorkDetailInfo != null ){
							wrapOutOkrWorkBaseInfo.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
							wrapOutOkrWorkBaseInfo.setDutyDescription( okrWorkDetailInfo.getDutyDescription() );
							wrapOutOkrWorkBaseInfo.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
							wrapOutOkrWorkBaseInfo.setMajorIssuesDescription( okrWorkDetailInfo.getMajorIssuesDescription() );
							wrapOutOkrWorkBaseInfo.setProgressAction( okrWorkDetailInfo.getProgressAction() );
							wrapOutOkrWorkBaseInfo.setProgressPlan( okrWorkDetailInfo.getProgressPlan() );
							wrapOutOkrWorkBaseInfo.setResultDescription( okrWorkDetailInfo.getResultDescription() );
						}
					} catch (Exception e) {
						Exception exception = new WorkBaseInfoProcessException( e, "查询指定ID的工作详细信息时发生异常。ID：" + wrapOutOkrWorkBaseInfo.getId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		result.setData(wraps);
		
		return result;
	}
	
}