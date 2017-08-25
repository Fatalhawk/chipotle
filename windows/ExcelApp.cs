using Microsoft.Office.Interop.Excel;
using System;
using System.Diagnostics;
using System.Runtime.InteropServices;
using Excel = Microsoft.Office.Interop.Excel;

namespace Networking //TODO: Shifting down
{
    class ExcelApp : ProcessInterface
    {
        //static void Main(string[] args)
        //{
        //    if (Console.ReadLine().Equals("start"))
        //    {
        //        Thread.Sleep(4000);
        //        excelApp f = new excelApp();
        //        f.excelCommand("s");
        //        Console.ReadLine();
        //    }
        //}
        public ExcelApp(ref Process pObj, IntPtr hWnd, string title, updateDele update) : base(ref pObj, hWnd, title, update)
        {
            excelCommand();
        }

        [Guid("00020893-0000-0000-C000-000000000046")]
        [InterfaceType(ComInterfaceType.InterfaceIsIDispatch)]
        public interface ExcelWindow
        {
        }

        [DllImport("Oleacc.dll")]
        static extern int AccessibleObjectFromWindow(IntPtr hwnd, uint dwObjectID, byte[] riid, out ExcelWindow ptr);

        Excel.Application myExcelApp;

        public void excelCommand()
        {
            const uint OBJID_NATIVEOM = 0xFFFFFFF0;
            Guid IID_IDispatch = new Guid("{00020400-0000-0000-C000-000000000046}");

            myExcelApp = null;
            ExcelWindow excelWindow = null;
            try
            {
                int hRes = AccessibleObjectFromWindow(handle, OBJID_NATIVEOM, IID_IDispatch.ToByteArray(), out excelWindow);
                if (hRes >= 0)
                {
                    myExcelApp = (Application)excelWindow.GetType().InvokeMember("Application", System.Reflection.BindingFlags.GetProperty, null, excelWindow, null);

                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }

            if (myExcelApp != null)
            {
                Console.WriteLine(
                  "Successfully attached to running instance of Excel.");
                Excel.Worksheet sheet = null;
                try
                {
                    Excel.Workbook workbook = myExcelApp.ActiveWorkbook;
                    if (workbook != null)
                    {
                        Console.WriteLine("got workbook");
                    }
                    //sheet = (Worksheet)workbook.Sheets[1];
                    sheet = myExcelApp.ActiveSheet;
                    if (sheet != null)
                    {
                        Console.WriteLine("got sheet");
                        sum(myExcelApp);
                    }

                }
                catch (Exception e)
                {
                    Console.WriteLine(e.ToString());
                }
            }
        }

        public void sum(Excel.Application myExcelApp)
        {
            Range currentSelection = myExcelApp.ActiveWindow.RangeSelection;
            double sum = myExcelApp.WorksheetFunction.Sum(currentSelection);
            Range under = Under(currentSelection, myExcelApp, true);
            under.Value = sum;
        }

        public void product(Excel.Application myExcelApp)
        {
            Range currentSelection = myExcelApp.ActiveWindow.RangeSelection;
            double product = myExcelApp.WorksheetFunction.Product(currentSelection);
            Range under = Under(currentSelection, myExcelApp, true);
            under.Value = product;
        }

        public void count(Excel.Application myExcelApp)
        {
            Range currentSelection = myExcelApp.ActiveWindow.RangeSelection;
            int count = currentSelection.Count;
            Range under = Under(currentSelection, myExcelApp, true);
            under.Value = count;
        }

        public void average(Excel.Application myExcelApp)
        {
            Range currentSelection = myExcelApp.ActiveWindow.RangeSelection;
            double average = myExcelApp.WorksheetFunction.Average(currentSelection);
            Range under = Under(currentSelection, myExcelApp, true);
            under.Value = average;
        }

        public void merge(Excel.Application myExcelApp)
        {
            Range currentSelection = myExcelApp.ActiveWindow.RangeSelection;
            if (currentSelection.MergeCells)
            {
                currentSelection.UnMerge();
            }
            else
            {
                currentSelection.Merge();
            }
        }

        public void border(Excel.Application myExcelApp)
        {
            Range currentSelection = myExcelApp.ActiveWindow.RangeSelection;
            Borders border = currentSelection.Borders;
            if (border.LineStyle == 1) //Excel.XlLineStyle.xlContinuous
            {
                border.LineStyle = Excel.XlLineStyle.xlLineStyleNone;
            }
            else
            {
                border.LineStyle = Excel.XlLineStyle.xlContinuous;
            }

        }


        public static Range Under(Range range, Excel.Application myExcelApp, Boolean insertFlag)
        {
            int lastColumn = range.Columns[range.Columns.Count].Column;
            int lastRow = range.Rows[range.Rows.Count].Row;
            Range underCell = (Excel.Range)myExcelApp.ActiveSheet.Cells[lastRow + 1, lastColumn];
            if (insertFlag == true)
            {
                insert(underCell);
            }
            underCell = (Excel.Range)myExcelApp.ActiveSheet.Cells[lastRow + 1, lastColumn];
            return underCell;
        }

        public static void insert(Range range)
        {
            range.Insert(XlInsertShiftDirection.xlShiftDown, false);
        }
    }
}