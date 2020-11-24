package com.hhc.sy.customs.download.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.hhc.sy.custonms.util.MyRacDatasetFile;
import com.hhc.sy.custonms.util.RacDatasetUtil;
import com.hhc.sy.download.DatasetDownloadApplication;
import com.hhc.sy.download.DatasetDownloadCheck;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.util.MessageBox;

public class DownloadDatasetsHandler extends AbstractHandler{
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		
		
		InterfaceAIFComponent[] targets = AIFUtility.getCurrentApplication().getTargetComponents();		
		if(targets == null || targets.length == 0) {
			return null;
		}
		try {
			String msg = DatasetDownloadCheck.DownloadCheck(targets);
			if(!msg.equals("")) {
				MessageBox.post(msg,"错误",MessageBox.ERROR);
				return null;
			}
			List<MyRacDatasetFile> targetDatasetFile = new ArrayList<MyRacDatasetFile>();;
			TCComponentItemRevision ir = null;
			for (InterfaceAIFComponent target : targets) {
				ir = null;
				if(target instanceof TCComponentItem) {
					ir = ((TCComponentItem)target).getLatestItemRevision();
				}else if(target instanceof TCComponentItemRevision) {
					ir = (TCComponentItemRevision) target;
				}else {
					continue;
				}
				
				List<TCComponentDataset> datasets = RacDatasetUtil.getDatasets(ir);
				for (TCComponentDataset dataset : datasets) {
					List<MyRacDatasetFile> datasetFile = RacDatasetUtil.getRacDatasetFile(dataset, ir);
					if(datasetFile != null && datasetFile.size() > 0) {
						targetDatasetFile.addAll(datasetFile);
					}
				}
			}			
			DatasetDownloadApplication window = new DatasetDownloadApplication(targetDatasetFile);
			window.open();
			
		}catch(Exception e) {
			e.printStackTrace();
			MessageBox.post(e);
		}		
		return null;
	}

}
