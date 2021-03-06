package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTask;
import com.x.processplatform.core.entity.element.Process;

class ActionListNextWithProcess extends ActionBase {

	ActionResult<List<WrapOutTask>> execute(EffectivePerson effectivePerson, String id, Integer count,
			String processFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = business.process().pick(processFlag);
			if (null == process) {
				throw new ProcessNotExistedException(processFlag);
			}
			EqualsTerms equals = new EqualsTerms();
			equals.put("person", effectivePerson.getName());
			equals.put("process", process.getId());
			ActionResult<List<WrapOutTask>> result = this.standardListNext(taskOutCopier, id, count, "sequence", equals,
					null, null, null, null, null, null, true, DESC);
			return result;
		}
	}
}
