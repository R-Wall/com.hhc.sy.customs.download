package com.hhc.sy.customs.download.handlers;

import org.eclipse.ui.IStartup;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.refresh.TaskApprovalRecordUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentTaskInBox;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class CustomStarter implements IStartup{

	@Override
	public void earlyStartup() {
		try {
			TCSession session = (TCSession) AIFUtility.getDefaultSession();
			TCComponent userInbox = session.getUser().getUserInBox();
			TCComponent taskInbox = userInbox.getRelatedComponent("contents");
			if (taskInbox != null && taskInbox instanceof TCComponentTaskInBox) {
				TaskApprovalRecordUtil util = new TaskApprovalRecordUtil();
				util.refreshTaskInbox((TCComponentTaskInBox)taskInbox);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(e);
		}
	}
	
}
