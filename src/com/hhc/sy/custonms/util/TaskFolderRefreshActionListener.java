package com.hhc.sy.custonms.util;

import com.func.rac.common.listener.refresh.FuncRefreshActionEvent;
import com.func.rac.common.listener.refresh.IFuncRefreshActionListener;
import com.teamcenter.rac.commands.refresh.RefreshOperation;
import com.teamcenter.rac.util.MessageBox;

public class TaskFolderRefreshActionListener implements IFuncRefreshActionListener {

	@Override
	public boolean condition(FuncRefreshActionEvent event) {
		return true;
	}

	@Override
	public void premise(FuncRefreshActionEvent event) {

	}

	@Override
	public void post(FuncRefreshActionEvent event) {
		Object obj =  event.getObject();
		if (obj instanceof RefreshOperation) {
			try {
				TaskApprovalRecordUtil util = new TaskApprovalRecordUtil();
				util.customRefresh((RefreshOperation) obj);
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.post(e);
			}
		}
	}

}
