package lms.business.logic;

import java.io.File;
import java.io.IOException;

import lms.business.Building;
import lms.business.Locker;
import lms.business.LockerSize;
import lms.business.Student;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

public class SpreadsheetImporter 
{
	public static void importStudents(String relativePath)
	{
		
		try 
		{
			String absolutePath = new File("").getAbsolutePath();
			String fullPath = absolutePath + relativePath;
			
			Workbook workbook = WorkbookFactory.create(new File(fullPath));
			Sheet studentSheet = workbook.getSheetAt(0);

			Student currStudent;
			boolean isScienceStudent = true;
			
			for (Row row : studentSheet) 
			{
				String firstName;
				String lastName;
				String email;
				int studentNumber;
				
				if(!isRowEmpty(row))
				{
	
					firstName = row.getCell(0).getStringCellValue();
					lastName = row.getCell(1).getStringCellValue();
					email = row.getCell(2).getStringCellValue();
					studentNumber = (int)row.getCell(3).getNumericCellValue();
	
					currStudent = new Student(firstName, lastName, email, studentNumber, isScienceStudent, CurrentTermInfo.id);
					currStudent.save();
				}
			}

		}
		
		catch(IOException ioe) 
		{
			ioe.printStackTrace();
		}
		
		catch(InvalidFormatException ife) 
		{
			ife.printStackTrace();
		}
	}
	
	public static void importLockers(String relativePath)
	{
		try 
		{
			String absolutePath = new File("").getAbsolutePath();
			String fullPath = absolutePath + relativePath;
			
			Workbook workbook = WorkbookFactory.create(new File(fullPath));
			Sheet lockerSheet = workbook.getSheetAt(0);

			Locker currLocker;
			
			for (Row row : lockerSheet) 
			{
				Building building;
				int lockerNumber;
				LockerSize size;

				if(!isRowEmpty(row))
				{
					building = new Building(row.getCell(0).getStringCellValue());
					building.save();
					lockerNumber = (int)row.getCell(1).getNumericCellValue();
				
					if(row.getCell(2).getStringCellValue() == "FULL")
					{
						size = LockerSize.FULL;
					}
					else
					{
						size = LockerSize.HALF;
					}

					currLocker = new Locker(CurrentTermInfo.id, lockerNumber, building.getId(), size);
					currLocker.save();
				}
			}

		}
		
		catch(IOException ioe) 
		{
			ioe.printStackTrace();
		}
		
		catch(InvalidFormatException ife) 
		{
			ife.printStackTrace();
		}
	}
	
	private static boolean isRowEmpty(Row row)
	{
				
		for(Cell cell : row)
		{
			if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
			{
				return false;
			}
		}
		
		return true;
	}
}