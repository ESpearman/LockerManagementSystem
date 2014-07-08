package lms.application;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

import acceptanceTests.EventLoop;
import acceptanceTests.Register;
import lms.businesslogic.CurrentTermInfo;
import lms.businesslogic.LockerPrice;
import lms.businesslogic.RentLocker;
import lms.domainobjects.Building;
import lms.domainobjects.Locker;
import lms.domainobjects.Rental;
import lms.domainobjects.Student;

import java.util.ArrayList;

public class LockerWindow
{
	private Shell shell;
	private Display display;
	
	private Combo drpBuilding;
	private Combo drpLocker;
	
	private ArrayList<Building> buildingsAL = Building.getAll();
	private String buildings[] = new String[buildingsAL.size()];
	private ArrayList<Locker> lockersAL;
	private String lockers[];
	
	private Locker selectedLocker;
	private Student potentialRenter;
	private double price;
	
	private Button btnBack;
	private Button btnRent;
	
	private Button chkAgreement;
	private Label lblPrice;

	public void runWindow()
	{
		// ============ create new window ( centre on monitor ) =====
		shell = new Shell();
		shell.setSize(384, 192);
		
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		
		shell.setLocation (x, y);
		shell.setText("Select Locker");
		
		
		// ===== locker combo ( dropdown list ) =======
		drpLocker = new Combo(shell, SWT.NONE);
		drpLocker.setBounds(186, 31, 172, 23);
		drpLocker.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				selectedLocker = lockersAL.get(drpLocker.getSelectionIndex());
				price = LockerPrice.calculatePrice(potentialRenter, selectedLocker);
				lblPrice.setText("Price: " + price);
			}
		});
		
		// ======= building combo ( dropdown list ) =======
		drpBuilding = new Combo(shell, SWT.NONE);
		
		//Build up buildings to select from
		for(int i = 0; i < buildingsAL.size(); i++)
		{
			buildings[i] = buildingsAL.get(i).getName();
		}
		drpBuilding.setItems(buildings);
		
		drpBuilding.setBounds(10, 31, 172, 23);
		drpBuilding.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Building building = buildingsAL.get(drpBuilding.getSelectionIndex());
				lockersAL = Locker.getFreeByBuildingAndTerm(building.getId(), CurrentTermInfo.currentTerm.getId());
				lockers = new String[lockersAL.size()];
				
				//build up lockers to select from
				for(int i = 0; i < lockersAL.size(); i++)
				{
					lockers[i] = Integer.toString(lockersAL.get(i).getNumber());
				}
				drpLocker.setItems(lockers);
				drpLocker.setEnabled(true);
			}
		});
		

		// ========= agree check button =========
		chkAgreement = new Button(shell, SWT.CHECK);
		chkAgreement.setAlignment(SWT.CENTER);
		chkAgreement.setBounds(306, 88, 52, 23);
		chkAgreement.setText("Agree");
		
		
		// ========= back button ===========
		btnBack = new Button(shell, SWT.NONE);
		btnBack.setBounds(10, 117, 111, 27);
		btnBack.setText("Back");
		btnBack.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				// back button is selected
				shell.close();
			}
		});

		
		// ======== rent button ===========
		btnRent = new Button(shell, SWT.NONE);
		btnRent.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				if(drpLocker.getSelectionIndex() != -1 && drpBuilding.getSelectionIndex() != -1 && chkAgreement.getSelection())
				{
					Rental newRental = RentLocker.rent(potentialRenter.getId(), selectedLocker.getId(), CurrentTermInfo.currentTerm.getId(), price);
					
					if(newRental != null)
					{
						new PopupWindow("Completed","Rented");
						
						shell.close();
					}
					else
					{
						new PopupWindow("Error","The student is already renting a locker this term");
					}					
				}
				else
				{
					new PopupWindow("Error","Locker has not been selected or student has not agreed");
				}
			}
		});
		btnRent.setBounds(247, 117, 111, 27);
		btnRent.setText("Rent");
		
		
		
		lblPrice = new Label(shell, SWT.NONE);
		lblPrice.setBounds(10, 68, 172, 23);
		lblPrice.setText("Price: ");
		
		Label lblBuilding = new Label(shell, SWT.NONE);
		lblBuilding.setBounds(10, 10, 55, 15);
		lblBuilding.setText("Building");
		
		Label lblLocker = new Label(shell, SWT.NONE);
		lblLocker.setBounds(186, 10, 55, 15);
		lblLocker.setText("Locker");
		

		// ======shell open, close ========
		shell.open();
		
		if(EventLoop.isEnabled())
		{
			while (!shell.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
		}
		
	}
	
	public LockerWindow(Shell previousShell, Student newStudent)
	{
		Register.newWindow(this);
		display = Display.getDefault();
		potentialRenter = newStudent;
		runWindow();
	}
}
