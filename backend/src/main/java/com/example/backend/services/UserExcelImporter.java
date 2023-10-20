package com.example.backend.services;
import com.example.backend.entity.model.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserExcelImporter {
	public List<User> excelImport(){
		List<User> listStudent=new ArrayList<>();
		int id=0;
		String name="";
		String email="";
		String password="";
		byte[] profileImg= "".getBytes();



		long start = System.currentTimeMillis();

		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream("D:\\Student_info.xlsx");
			Workbook workbook=new XSSFWorkbook(inputStream);
			Sheet firstSheet=workbook.getSheetAt(0);
			Iterator<Row> rowIterator=firstSheet.iterator();
			rowIterator.next();

			while(rowIterator.hasNext()) {
				Row nextRow = rowIterator.next();
				Iterator<Cell> cellIterator=nextRow.cellIterator();
				while(cellIterator.hasNext()) {
					Cell nextCell=cellIterator.next();
					int columnIndex=nextCell.getColumnIndex();
					switch (columnIndex) {
					case 0:
						id=(int) nextCell.getNumericCellValue();
						System.out.println(id);
						break;
					case 1:
						name=nextCell.getStringCellValue();
						System.out.println(name);
						break;
					case 2:
						email=nextCell.getStringCellValue();
						System.out.println(email);
						break;
					case 3:
						password=nextCell.getStringCellValue();
						System.out.println(password);
						break;
					case 4:
						profileImg= nextCell.getStringCellValue().getBytes();
						System.out.println(profileImg);
						break;

					}
					listStudent.add(new User(id, name, email, password, profileImg));
				}
			}

			workbook.close();
			long end = System.currentTimeMillis();
			System.out.printf("Import done in %d ms\n", (end - start));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return listStudent;
	}

}
