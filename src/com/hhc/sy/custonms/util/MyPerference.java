package com.hhc.sy.custonms.util;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class MyPerference {
	
	private static TCPreferenceService service;

	public static TCPreferenceService getService() throws TCException {
		if (service == null) {
			TCSession session = (TCSession) AIFUtility.getDefaultSession();
			service = session.getPreferenceService();
			service.refresh();
		}
		return service;
	}

	public static String[] getStringValues(String perferenceName) throws TCException {

		String values[] = getService().getStringValues(perferenceName);

		return values;

	}
	
}
