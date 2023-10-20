package com.example.backend.services;
import com.example.backend.entity.model.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.IOException;
import java.util.List;


public class UserExcelExporter {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	private List<User> userList;

	
	public UserExcelExporter(List<User> userList) {
		this.userList=userList;
		workbook = new XSSFWorkbook();

	}

	private void createCell(Row row,int columnCount, Object value,CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell=row.createCell(columnCount);
		if(value instanceof Long) {
			cell.setCellValue((Long) value);
		}else if(value instanceof Integer) {
			cell.setCellValue((Integer) value);
		}else if(value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		}else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}
	private void writeHeaderLine() {
		sheet=workbook.createSheet("User");

		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		XSSFFont font=workbook.createFont();
		font.setBold(true);
		font.setFontHeight(20);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		createCell(row,0,"User Information",style);
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,5));
		font.setFontHeightInPoints((short)(10));

		row=sheet.createRow(1);
		font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "User Id", style);
        createCell(row, 1, "User Name", style);
        createCell(row, 2, "User email", style);
        createCell(row, 3, "User password", style);
		createCell(row, 5, "User Image", style);

	}

	private void writeDataLines() {
		int rowCount=2;

		CellStyle style=workbook.createCellStyle();
		XSSFFont font=workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);

		for(User stu:userList) {
			Row row=sheet.createRow(rowCount++);
			int columnCount=0;
			createCell(row, columnCount++, stu.getId(), style);
			createCell(row, columnCount++, stu.getName(), style);
			createCell(row, columnCount++, stu.getEmail(), style);
			createCell(row, columnCount++, stu.getPassword(), style);

			if (stu.getProfileImg() != null) {
				createImageCell(row, columnCount, stu.getProfileImg(), style);
			}
		}
	}

	private void createImageCell(Row row, int columnCount, byte[] imageData, CellStyle style) {
		if (imageData != null) {
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, columnCount, row.getRowNum(), columnCount + 1, row.getRowNum() + 1);
			drawing.createPicture(anchor, workbook.addPicture(imageData, Workbook.PICTURE_TYPE_JPEG));
		}
	}

	public void export(HttpServletResponse response) throws IOException{
		writeHeaderLine();
		writeDataLines();

		ServletOutputStream outputStream=response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	
}
