package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.appinfo.exception.AppInfoProcessException;
import com.x.cms.core.entity.AppInfo;

import net.sf.ehcache.Element;

public class ExcuteListAll extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListAll.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutAppInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		List<WrapOutAppInfo> wraps = null;
		List<AppInfo> appInfoList = null;
		Boolean check = true;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "all" );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutAppInfo> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			try {
				appInfoList = appInfoServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new AppInfoProcessException( e, "查询所有应用栏目信息对象时发生异常" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( check ){
				if( appInfoList != null && !appInfoList.isEmpty() ){
					try {
						wraps = WrapTools.appInfo_wrapout_copier.copy( appInfoList );
						SortTools.desc( wraps, "appInfoSeq");
						cache.put(new Element( cacheKey, wraps ));
						result.setData(wraps);
					} catch (Exception e) {
						Exception exception = new AppInfoProcessException( e, "将查询出来的应用栏目信息对象转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		
		return result;
	}
}