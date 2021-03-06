package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoListByIdsException( Throwable e ) {
		super("根据ID列表查询分类信息对象时发生异常。", e );
	}
}
