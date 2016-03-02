package com.shamwerks.camwerks.config;


public enum LangEntry {
	
	MENU_NEW_CAMSHAFT,
    MENU_OPEN_FILE,
	MENU_CLOSE_FILE,
	MENU_SAVE_CSV,
	MENU_SAVE_CAM,
	MENU_HELP_ABOUT,
	MENU_OPEN_FILE_COMPARE_TO,
	
	OPEN_FILE_CHOOSE,
	
	TABLE_COLUMN_ANGLE        ( "table.column.angle"),
	TABLE_COLUMN_LIFT         ( "table.column.lift"),
	TABLE_COLUMN_SPEED        ( "table.column.speed"),
	TABLE_COLUMN_ACCELERATION ( "table.column.acceleration"),
	TABLE_COLUMN_INTAKE       ( "table.column.intake"),
	TABLE_COLUMN_EXHAUST      ( "table.column.exhaust"),
	TABLE_COLUMN_CYLINDER     ( "table.column.cylinder"),

	TAB_DETAILS      ( "tab.details"),
	TAB_CHART_LINE   ( "tab.chart.line"),
	TAB_CHART_CIRC   ( "tab.chart.circ"),
	TAB_CHART_RADIALCAM ( "tab.chart.radialcam"),

	CHART_LEGEND_INT     ( "chart.legend.int"),
	CHART_LEGEND_EXH     ( "chart.legend.exh"),
	CHART_LEGEND_CYL     ( "chart.legend.cyl"),
	CHART_LEGEND_DEGREES ( "chart.legend.degrees"),
	CHART_LEGEND_LIFT    ( "chart.legend.lift"),

	TEMPLATE_DETAILS_CYLINDER,
	TEMPLATE_DETAILS_INTAKE,
	TEMPLATE_DETAILS_EXHAUST,
	
	NEWCAMSHAFT_TITLE        ( "newcamshaft.title"),
	NEWCAMSHAFT_NAME         ( "newcamshaft.name"),
	NEWCAMSHAFT_LABEL_NAME   ( "newcamshaft.label.name"),
	NEWCAMSHAFT_LABEL_NBCYLS ( "newcamshaft.label.nbcyls"),
	NEWCAMSHAFT_LABEL_NBINT  ( "newcamshaft.label.nbint"),
	NEWCAMSHAFT_LABEL_NBEXH  ( "newcamshaft.label.nbexh"),
	NEWCAMSHAFT_LABEL_NBMEASURECYCLES ( "newcamshaft.label.nbmeasurecycles"),
	NEWCAMSHAFT_LABEL_DIRECTION       ( "newcamshaft.label.direction"),
	
	CAMSHAFT_DIRECTION_CLOCKWISE        ( "camshaft.direction.clockwise"),
	CAMSHAFT_DIRECTION_COUNTERCLOCKWISE ( "camshaft.direction.counterclockwise"),
	
	CAM_MEASURE_PLEASE_MSG        (  "cam.measure.please.msg"),
	CAM_MEASURE_SUCESS_TITLE      (  "cam.measure.success.title"),
	CAM_MEASURE_SUCESS_TEXT       (  "cam.measure.success.text"),
	CAM_DESCRIPTION               (  "cam.description"),
	CAM_INTAKE                    (  "cam.intake"),
	CAM_EXHAUST                   (  "cam.exhaust"),
	
	ERROR_ARDUINO_CONNECT_TITLE,
	ERROR_ARDUINO_CONNECT_TEXT,

	TEMPLATE_HEADER_LIFT,
	TEMPLATE_HEADER_OVERLAP,
	TEMPLATE_HEADER_DURATION,
	TEMPLATE_HEADER_OPENCLOSE,
	
	TEMPLATE_COLUM_HEADER_DURATION,
	TEMPLATE_COLUM_OVERLAP_DURATION,
	
	LAST;
	
	String value;
	private LangEntry(){
		this.value = this.name();
	}
	private LangEntry(String value){
		this.value = value;
	}
	public String getValue(){
		return value;
	}
}
