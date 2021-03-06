package com.x.meeting.assemble.control;

import com.x.base.core.project.Context;
import com.x.collaboration.core.message.Collaboration;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			Collaboration.start(context());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			Collaboration.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
