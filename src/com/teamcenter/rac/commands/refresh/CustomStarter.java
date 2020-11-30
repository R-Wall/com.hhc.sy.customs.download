package com.teamcenter.rac.commands.refresh;

import org.eclipse.ui.IStartup;

import com.teamcenter.rac.util.MessageBox;

public class CustomStarter implements IStartup{

	@Override
	public void earlyStartup() {
		try {
			TaskApprovalRecordUtil util = new TaskApprovalRecordUtil();
			util.init();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(e);
		}
	}
	
}
