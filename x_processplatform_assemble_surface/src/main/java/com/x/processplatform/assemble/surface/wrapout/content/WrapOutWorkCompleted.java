package com.x.processplatform.assemble.surface.wrapout.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.core.entity.content.WorkCompleted;

@Wrap(WorkCompleted.class)
public class WrapOutWorkCompleted extends WorkCompleted {

	private static final long serialVersionUID = 2395048971976018595L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	public static List<String> Excludes_FormData = new ArrayList<>(JpaObject.FieldsInvisible);

	static {
		Excludes_FormData.add("formData");
		Excludes_FormData.add("formMobileData");
	}

	private Long rank;

	private Control control;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public Control getControl() {
		return control;
	}

	public void setControl(Control control) {
		this.control = control;
	}

}
