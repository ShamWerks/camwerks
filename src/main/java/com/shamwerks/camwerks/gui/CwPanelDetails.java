package com.shamwerks.camwerks.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Config;
import com.shamwerks.camwerks.config.Constants.CamType;
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

		//System.out.println(fileContent);
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

		//System.out.println("fileContent = " + fileContent);
		detailsEditPane.setText(fileContent);
	}



	public String parseHtmlTags(String fileContent){

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
			String colDuration1 = "";
			String colDuration127 = "";
			String colDuration05 = "";
			String colOverlap1 = "";
			String colOverlap127 = "";
			String colOverlap05 = "";
			String colOpenClose1 = "";
			String colOpenClose127 = "";
			String colOpenClose05 = "";
			String colRocker125 = "";
			String colRocker14  = "";
			for(String key : camshaft.getKeys() ){
				Cam cam = camshaft.getCam(key);
				String admExh = cam.getCamType() == CamType.INTAKE?Lang.getText(LangEntry.TEMPLATE_DETAILS_INTAKE):Lang.getText(LangEntry.TEMPLATE_DETAILS_EXHAUST);
				colHeaders     += "<th>"+Lang.getText(LangEntry.TEMPLATE_DETAILS_CYLINDER) + " " + cam.getCylNumber() + "<br>" + admExh + " " + cam.getCamNumber() + "</th>";
				colLifts       += "<td align='right'>" + cam.getMaxLift() + "mm</td>";
				colRocker125   += "<td align='right'>" + Toolbox.round(cam.getMaxLift() * 1.25F, 2) + "mm</td>";
				colRocker14    += "<td align='right'>" + Toolbox.round(cam.getMaxLift() * 1.4F, 2) + "mm</td>";
				colDuration1   += "<td align='right'>" + cam.getDuration(1) + "°</td>";
				colDuration127 += "<td align='right'>" + cam.getDuration( 1.27F ) + "°</td>";
				colDuration05  += "<td align='right'>" + cam.getDuration( 0.5F ) + "°</td>";
				
				String intExh = cam.getCamType() == CamType.INTAKE?"A":"E";
				
				colOpenClose05  += "<td>AO"+intExh+" / RF"+intExh+"</td>";
				colOpenClose1   += "<td>AO"+intExh+" / RF"+intExh+"</td>";
				colOpenClose127 += "<td>AO"+intExh+" / RF"+intExh+"</td>";
			}

			for(int c=1; c<=camshaft.getNbCylinders(); c++){
				colCylHeaders += "<th>"+Lang.getText(LangEntry.TEMPLATE_DETAILS_CYLINDER) + " " + c + "</th>";
				colOverlap1   += "<td align='right'>" + camshaft.getOverlap(c, 1) + "°</td>";
				colOverlap127 += "<td align='right'>" + camshaft.getOverlap( c, 1.27d ) + "°</td>";
				colOverlap05  += "<td align='right'>" + camshaft.getOverlap(c, 0.5d ) + "°</td>";
			}

			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_HEADERS]", colHeaders);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_LIFTS]", colLifts);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_DURATION_0.5]", colDuration05);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_DURATION_1]", colDuration1);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_DURATION_1.27]", colDuration127);

			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_ROCKER_1.25]", colRocker125);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_ROCKER_1.4]", colRocker14);

			fileContent = fileContent.replace("[CYLINDERS_COLUMNS_HEADERS]", colCylHeaders);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OVERLAP_0.5]", colOverlap05);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OVERLAP_1]", colOverlap1);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OVERLAP_1.27]", colOverlap127);

			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OPENCLOSE_0.5]", colOpenClose127);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OPENCLOSE_1]", colOpenClose127);
			fileContent = fileContent.replace("[CAMSHAFT_COLUMNS_OPENCLOSE_1.27]", colOpenClose127);
		}

		return fileContent;
	}
}










