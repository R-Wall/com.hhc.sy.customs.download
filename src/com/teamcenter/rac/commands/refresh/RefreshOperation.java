package com.teamcenter.rac.commands.refresh;

import com.hhc.sy.custonms.util.CustomRefreshAdapter;
import com.hhc.sy.custonms.util.TaskApprovalRecordUtil;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import java.util.ArrayList;
import java.util.List;

public class RefreshOperation
  extends AbstractAIFOperation
{
  private InterfaceAIFComponent m_target;
  private InterfaceAIFComponent[] m_theTargets;
  
  public RefreshOperation(InterfaceAIFComponent paramInterfaceAIFComponent)
  {
    super(Registry.getRegistry(RefreshOperation.class).getString("refresh") + "  ...", false);
    this.m_target = paramInterfaceAIFComponent;
  }
  
  public RefreshOperation(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent)
  {
    super(Registry.getRegistry(RefreshOperation.class).getString("refresh") + "  ...", false);
    this.m_theTargets = paramArrayOfInterfaceAIFComponent;
  }
  
	public void executeOperation() throws Exception {
		try {
			
			CustomRefreshAdapter.executeConditipon();
			
			CustomRefreshAdapter.executePre();
			
			if ((this.m_target instanceof TCComponent)) {
				((TCComponent) this.m_target).refresh();
			}
			if (this.m_theTargets != null) {
				ArrayList localArrayList = new ArrayList();
				for (Object object : m_theTargets) {
					if (object instanceof TCComponent) {
						localArrayList.add((TCComponent) object);
					}
				}
				TCComponentType.refresh(localArrayList);
			}

			TaskApprovalRecordUtil util = new TaskApprovalRecordUtil();
			if (this.m_target != null) {
				util.refreshFolder(this.m_target);
			}
			if (this.m_theTargets != null) {
				util.refreshFolders(m_theTargets);
			}
			
			CustomRefreshAdapter.executePost();
			
		} catch (Exception localException) {
			Object localObject = new MessageBox(localException);
			((MessageBox) localObject).setModal(true);
			((MessageBox) localObject).setVisible(true);
		}
	}
}
