package com.x.processplatform.assemble.bam.jaxrs.period;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.utils.DateRange;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.CompanyStub;
import com.x.processplatform.assemble.bam.stub.DepartmentStub;

class ActionListCountCompletedTaskByDepartment extends ActionListCountCompletedTask {

	ActionResult<WrapOutMap> execute(String applicationId, String processId, String activityId, String companyName)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			Business business = new Business(emc);
			List<DateRange> os = this.listDateRange();
			WrapOutMap wrap = new WrapOutMap();
			for (DateRange o : os) {
				String str = DateTools.format(o.getStart(), DateTools.format_yyyyMM);
				List<WrapOutMap> list = new ArrayList<>();
				wrap.put(str, list);
				for (DepartmentStub stub : this.listStub(companyName)) {
					WrapOutMap pair = new WrapOutMap();
					pair.put("name", stub.getName());
					pair.put("value", stub.getValue());
					pair.put("level", stub.getLevel());
					pair.put("companyName", stub.getCompanyName());
					pair.put("companyValue", stub.getCompanyValue());
					pair.put("companyLevel", stub.getCompanyLevel());
					Long count = this.count(business, o, applicationId, processId, activityId, companyName,
							stub.getValue(), StandardJaxrsAction.EMPTY_SYMBOL);
					pair.put("count", count);
					Long duration = this.duration(business, o, applicationId, processId, activityId, companyName,
							stub.getValue(), StandardJaxrsAction.EMPTY_SYMBOL);
					pair.put("duration", duration);
					list.add(pair);
				}
			}
			result.setData(wrap);
			return result;
		}
	}

	private List<DepartmentStub> listStub(String companyName) throws Exception {
		List<DepartmentStub> list = new ArrayList<>();
		for (CompanyStub o : ThisApplication.period.getCompletedTaskCompanyStubs()) {
			if (StringUtils.equals(o.getValue(), companyName)
					|| StringUtils.equals(companyName, StandardJaxrsAction.EMPTY_SYMBOL)) {
				list.addAll(o.getDepartmentStubs());
			}
		}
		return list;
	}
}