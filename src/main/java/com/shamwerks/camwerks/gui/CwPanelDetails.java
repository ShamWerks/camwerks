package com.shamwerks.camwerks.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Config;
import com.shamwerks.camwerks.config.Constants.CamType;
import com.shamwerks.camwerks.config.Constants.ValveOpenClose;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.config.Toolbox;
import com.shamwerks.camwerks.pojo.Cam;
import com.shamwerks.camwerks.pojo.Camshaft;

class CwPanelDetails extends JPanel {

	private static final long serialVersionUID = 1L;

	JEditorPane detailsEditPane = new JEditorPane();

	public CwPanelDetails() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		setLayout(new GridLayout(1, 0, 0, 0));
		detailsEditPane.setContentType("text/html");
		detailsEditPane.setEditable(false);
		detailsEditPane.setEditorKit(new HTMLEditorKit());        

		Path path = FileSystems.getDefault().getPath("config/templates/welcome_" + CamWerks.getInstance().getConfig().getLanguage() + ".html");
		String fileContent = "-EMPTY-";
		try {
			fileContent = new String(Files.readAllBytes(path));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		fileContent = parseHtmlTags(fileContent);

		detailsEditPane.setText( fileContent );
		add(detailsEditPane);

	}

	public void updateCamshaftDetails(){
		//Path path = FileSystems.getDefault().getPath("config/templates/details_" + CamWerks.getInstance().getConfig().getLanguage() + ".html");
		Path path = FileSystems.getDefault().getPath("config/templates/camshaft-details.html");
		
		String fileContent = "-EMPTY-";
		try {
			fileContent = new String(Files.readAllBytes(path));
		} catch (IOException e) {
			e.printStackTrace();
		} 

		fileContent = parseHtmlTags(fileContent);

		detailsEditPane.setText(fileContent);
	}



	public String parseHtmlTags(String fileContent){
		NumberFormat df = new DecimalFormat("#0.00");     
		
		double[] thresholds = {0.5, 1, 1.27}; //TODO: Put in config file!
		
		final String dirTemplates = "file:" + File.separator + System.getProperty("user.dir") + File.separator + "config" + File.separator + "templates" + File.separator;

		fileContent = fileContent.replace("[CAMWERKS_VERSION]", Config.VERSION);
		fileContent = fileContent.replace("[FILE_PATH]", dirTemplates);

		//Headers
		for (LangEntry entry : LangEntry.values()) {
			if(entry.name().startsWith("TEMPLATE")){
				fileContent = fileContent.replaceAll("\\["+entry.name()+"\\]", Lang.getText( entry ));
			}
		}

		/*
    	   $regex = "#\[IMG_(\d+)\]#";  //(.+)\]
  		$nbMatches = preg_match_all($regex , $inBody , $matches);
  		for($i=0 ; $i<count($matches[1]);$i++){
    		$imgID = $matches[1][$i];
    		//echo $i.'---'.$imgID."<br>\n";
    		$inBody = str_replace('[IMG_'.$imgID.']',getImgHtml($imgID),$inBody);
  		}
		 */
		Camshaft camshaft = CamWerks.getInstance().getCamshaft();

		if(camshaft != null){
			
			fileContent = fileContent.replace("[CAMSHAFT_NAME]", camshaft.getName());
			//columns
			String colHeaders = "";
			String colCylHeaders = "";
			String colLifts = "";
			String colDuration = "";
			String colOverlap = "";
			String colLobeCenter = "";
			//String colOpenClose1 = "";
			//String colOpenClose127 = "";
			String colRocker125 = "";
			String colRocker14  = "";
						
			
			double tdcCranckShift = 90.0; //delta between mark on camshaft on top and TDC
			for(String key : camshaft.getKeys() ){
				Cam cam = camshaft.getCam(key);
				String admExh = cam.getCamType() == CamType.INTAKE?Lang.getText(LangEntry.TEMPLATE_DETAILS_INTAKE):Lang.getText(LangEntry.TEMPLATE_DETAILS_EXHAUST);
				colHeaders     += "<th>"+Lang.getText(LangEntry.TEMPLATE_DETAILS_CYLINDER) + " " + cam.getCylNumber() + "<br>" + admExh + " " + cam.getCamNumber() + "</th>";
				colLifts       += "<td align='right'>" + cam.getMaxLift() + "mm</td>";
				colRocker125   += "<td align='right'>" + Toolbox.round(cam.getMaxLift() * 1.25F, 2) + "mm</td>";
				colRocker14    += "<td align='right'>" + Toolbox.round(cam.getMaxLift() * 1.4F, 2) + "mm</td>";

				/*
				String intExh = cam.getCamType() == CamType.INTAKE?"A":"E";
				
//				colOpenClose1  += "<td>AO"+intExh+" ";
//				colOpenClose1  += Toolbox.round(( cam.getPeakStep() - cam.getThresholdStep(1, ValveOpenClose.OPEN)  )*(360.0F/camshaft.getNbSteps()) * 2,2);
//				colOpenClose1  += "° / RF"+intExh+" ";
//				colOpenClose1  += Toolbox.round(( cam.getThresholdStep(1, ValveOpenClose.CLOSE) - cam.getPeakStep() )*(360.0F/camshaft.getNbSteps()) * 2,2);
//				colOpenClose1  += "°</td>";
//				
//				colOpenClose127  += "<td>AO"+intExh+" ";
//				colOpenClose127  += Toolbox.round(( cam.getPeakStep() - cam.getThresholdStep(1.27, ValveOpenClose.OPEN)  )*(360.0F/camshaft.getNbSteps()) * 2,2);
//				colOpenClose127  += "° / RF"+intExh+" ";
//				colOpenClose127  += Toolbox.round(( cam.getThresholdStep(1.27, ValveOpenClose.CLOSE) - cam.getPeakStep() )*(360.0F/camshaft.getNbSteps()) * 2,2);
//				colOpenClose127  += "°</td>";


				colOpenClose1  += "<td>AO"+intExh+" ";
				colOpenClose1  += cam.getThresholdAngle(1, ValveOpenClose.OPEN, camshaft.getNbSteps()) - tdcCranckShift;
				colOpenClose1  += "° / RF"+intExh+" ";
				double rf1 = cam.getThresholdAngle(1, ValveOpenClose.CLOSE, camshaft.getNbSteps()) - tdcCranckShift;
				colOpenClose1  += df.format(rf1 % 180);
				colOpenClose1  += "°</td>";
				
				colOpenClose127  += "<td>AO"+intExh+" ";
				colOpenClose127  += cam.getThresholdAngle(1.27, ValveOpenClose.OPEN, camshaft.getNbSteps()) - tdcCranckShift;
				colOpenClose127  += "° / RF"+intExh+" ";
				double rf2 = cam.getThresholdAngle(1.27, ValveOpenClose.CLOSE, camshaft.getNbSteps()) - tdcCranckShift;
				colOpenClose127  += df.format(rf2 % 180);
				colOpenClose127  += "°</td>";
*/			
			}

			//Cylinders column headers
			for(int c=1; c<=camshaft.getNbCylinders(); c++){
				colCylHeaders += "<th>"+Lang.getText(LangEntry.TEMPLATE_DETAILS_CYLINDER) + " " + c + "</th>";
			}
			
			// overlap lines
			for(int i=0 ; i<thresholds.length ; i++){
				colOverlap   += "<tr>";
				colDuration   += "<tr>";
				colOverlap += "<th>" + Lang.getText( LangEntry.TEMPLATE_COLUM_OVERLAP_DURATION ) + " @ " + thresholds[i] + "</th>";
				colDuration   += "<th>" + Lang.getText( LangEntry.TEMPLATE_COLUM_HEADER_DURATION ) + " @ " + thresholds[i] + "</th>";
				for(int c=1; c<=camshaft.getNbCylinders(); c++){
					colOverlap   += "<td align='right'>" ;
					colOverlap   += df.format(camshaft.getOverlap(c, thresholds[i]));
					colOverlap   += "&deg;</td>";
				}
				for(String key : camshaft.getKeys() ){
					Cam cam = camshaft.getCam(key);
					colDuration   += "<td align='right'>" + df.format(cam.getDuration(thresholds[i])) + "&deg;</td>";
				}
				colOverlap   += "</tr>";
				colDuration   += "</tr>";
			}

			//Lobe Center
			colLobeCenter += "<tr>";
			colLobeCenter += "<th>" + Lang.getText( LangEntry.TEMPLATE_COLUM_HEADER_LOBECENTER) + "</th>";
			for(int c=1; c<=camshaft.getNbCylinders(); c++){
				colLobeCenter+= "<td align='right'>" + df.format(camshaft.getLobeCenter(c)) + "&deg;</td>";
			}
			colLobeCenter += "</tr>";

			
			
			
			
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_HEADERS]", colHeaders);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_LIFTS]", colLifts);

			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_DURATION]", colDuration);
			
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_ROCKER_1.25]", colRocker125);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_ROCKER_1.4]", colRocker14);

			fileContent = fileContent.replace("[CYLINDERS_COLUMNS_HEADERS]", colCylHeaders);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OVERLAP]", colOverlap);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_LOBECENTER]", colLobeCenter);
			
			//fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OPENCLOSE_1]", colOpenClose1);
			//fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OPENCLOSE_1.27]", colOpenClose127);
		}

		return fileContent;
	}
}










