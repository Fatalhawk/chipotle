using System;
using Word = Microsoft.Office.Interop.Word;
using Excel = Microsoft.Office.Interop.Excel;
using Microsoft.Office.Interop.Word;
using System.Threading;
using System.Reflection;

namespace shibe
{
    class WordApp
    {
        static void Main(string[] args)
        {
            if (Console.ReadLine().Equals("start"))
            {
                Thread.Sleep(4000);
                WordApp f = new WordApp();
                f.wordCommand("s");
            }
        }

        //INSERT FIELDS AS NECESSARY 
        public void wordCommand(String s)
        {
            Word.Application myWordApp = null;
            try
            {
                myWordApp =
                  System.Runtime.InteropServices.Marshal.GetActiveObject(
                  "Word.Application") as Word.Application;
            }
            catch (System.Runtime.InteropServices.COMException e)
            {
                Console.WriteLine(
                  String.Format("Word application was not running: {0}",
                  e.Message));
                return;
            }

            if (myWordApp != null)
            {
                Console.WriteLine(
                  "Successfully attached to running instance of Word.");

                try
                {
                    Word.Document myWordDocument = myWordApp.ActiveDocument;

                }
                catch (Exception e)
                {
                    Console.WriteLine("No document open");
                }
            }
        }

        public void bold(Word.Application myWordApp)
        {
            if (myWordApp.Selection.Font.Bold == 0)
            {
                myWordApp.Selection.Font.Bold = 1;
            }
            else
            {
                myWordApp.Selection.Font.Bold = 0;
            }
        }

        public void italicize(Word.Application myWordApp)
        {
            if (myWordApp.Selection.Font.Bold == 0)
            {
                myWordApp.Selection.Font.Italic = 1;
            }
            else
            {
                myWordApp.Selection.Font.Italic = 0;
            }
        }

        public void underline(Word.Application myWordApp)
        {
            if (myWordApp.Selection.Font.Underline == 0)
            {
                myWordApp.Selection.Font.Underline = WdUnderline.wdUnderlineSingle;
            }
            else
            {
                myWordApp.Selection.Font.Underline = 0;
            }
        }

        public void strikethrough(Word.Application myWordApp)
        {
            if (myWordApp.Selection.Font.StrikeThrough == 0)
            {
                myWordApp.Selection.Font.StrikeThrough = 1;
            }
            else
            {
                myWordApp.Selection.Font.StrikeThrough = 0;
            }
        }

        public void highlight(Word.Application myWordApp, WdColorIndex color = WdColorIndex.wdDarkYellow)
        {
            myWordApp.Selection.Range.HighlightColorIndex = color;
        }

        public void colorRGB(Word.Application myWordApp, int R, int G, int B)
        {
            myWordApp.Selection.Font.Fill.ForeColor.RGB = R + 256 *G + 256*256*B;
        }

        public void colorDefault(Word.Application myWordApp, WdColorIndex color = WdColorIndex.wdRed)
        {
            myWordApp.Selection.Font.ColorIndex = color;
        }

        public void goToPage(Word.Application myWordApp, int pageNumber)
        {
            object what = Microsoft.Office.Interop.Word.WdGoToItem.wdGoToPage;
            object which = Microsoft.Office.Interop.Word.WdGoToDirection.wdGoToFirst;
            object count = pageNumber;
            myWordApp.Selection.GoTo(ref what, ref which, ref count);
        }

        public void checkGrammar(Document s)
        {
            s.CheckGrammar();
        }
    
        public void clearFormatting(Word.Application myWordApp)
        {
            myWordApp.Selection.ClearFormatting();

        }

        public void setStyle(Word.Application myWordApp)
        {

            Object styleHeading2 = "Heading 2";
            Object styleHeading3 = "Heading 3";

            //myWordApp.Selection.Range.set_Style(ref styleHeading2);
            myWordApp.Selection.Range.set_Style(ref styleHeading3);
        }
    }
    class excelApp
    {
        public void ExcelCommand(String s)
        {
            Excel.Application myExcelApp = null;
            try
            {
                myExcelApp =
                  System.Runtime.InteropServices.Marshal.GetActiveObject(
                  "Excel.Application") as Excel.Application;
            }
            catch (System.Runtime.InteropServices.COMException e)
            {
                Console.WriteLine(
                  String.Format("Excel application was not running: {0}",
                  e.Message));
                return;
            }

            if (myExcelApp != null)
            {
                Console.WriteLine(
                  "Successfully attached to running instance of Excel.");
                try
                {
                    Excel.Workbook myWordDocument = myExcelApp.ActiveWorkbook;
                    //EXCECUTE COMMAND
                    myExcelApp.Selection.Font.Bold = 1;
                }
                catch (Exception e)
                {
                    Console.WriteLine("No workbook open");
                }
            }
        }
    }
    class Outlook
    {

    }
}

