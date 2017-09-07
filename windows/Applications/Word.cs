/*
 * Current Problems:
 * -opening word while program is running will cause unwanted result in bindWordWindow()
 * -no document has been chosen and thus no child window called "_WwG" will be present
 * -myWordApp will = null
 * -when opening word, another "opening word" sequence window will be present
 */

using System;
using Word = Microsoft.Office.Interop.Word;
using Microsoft.Office.Interop.Word;
using Microsoft.Office.Core;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Reflection;
using System.Text;

namespace App.Program
{
    class WordApp : ProgramBase
    {
        public WordApp(ref Process pObj, IntPtr hWnd, string title) : base(ref pObj, hWnd, title)
        {
            childWindow = 0;
            bindWordWindow();
        }

        private int childWindow;


        private delegate bool EnumChildCallback(int hwnd, ref int lParam);

        [DllImport("User32.dll")]
        private static extern bool EnumChildWindows(int hWndParent, EnumChildCallback lpEnumFunc, ref int lParam);

        [DllImport("User32.dll")]
        private static extern int GetClassName(int hWnd, StringBuilder lpClassName, int nMaxCount);

        private static bool EnumChildProc(int hwndChild, ref int lParam)
        {
            StringBuilder buf = new StringBuilder(128);
            GetClassName(hwndChild, buf, 128);
            if (buf.ToString() == "_WwG")
            {
                lParam = hwndChild;
                return false;
            }
            return true;
        }

        private bool bindWordWindow()
        {
            try
            {
                EnumChildCallback cb = new EnumChildCallback(EnumChildProc);
                EnumChildWindows(handle.ToInt32(), cb, ref childWindow);

                if (childWindow != 0)
                {
                    // We call AccessibleObjectFromWindow, passing the constant OBJID_NATIVEOM (defined in winuser.h) 
                    // and IID_IDispatch - we want an IDispatch pointer into the native object model.
                    //
                    const uint OBJID_NATIVEOM = 0xFFFFFFF0;
                    Guid IID_IDispatch = new Guid("{00020400-0000-0000-C000-000000000046}");
                    IDispatch ptr;

                    int hr = AccessibleObjectFromWindow(childWindow, OBJID_NATIVEOM, IID_IDispatch.ToByteArray(), out ptr);

                    if (hr >= 0)
                    {
                        myWordApp = (Word.Application)ptr.GetType().InvokeMember("Application", BindingFlags.GetProperty, null, ptr, null);
                    }
                }

                if (myWordApp != null)
                {
                    Console.WriteLine(
                      "Successfully attached to running instance of Word.");

                    try
                    {
                        Word.Document myWordDocument = myWordApp.ActiveDocument;
                        return true;
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e.ToString());
                        Console.WriteLine("No document open");
                        return false;
                    }
                }
            }
            catch(NullReferenceException e)
            {

            }
            catch(Exception e)
            {

            }
            return false;
        }

        [DllImport("Oleacc.dll")]
        static extern int AccessibleObjectFromWindow(int hwnd, uint dwObjectID, byte[] riid, out IDispatch ptr);

        Word.Application myWordApp;


        [ComImport, InterfaceType(ComInterfaceType.InterfaceIsIUnknown), Guid("00020400-0000-0000-C000-000000000046")]
        public interface IDispatch
        {
        }

        private enum CommandList
        {
            BOLD,
            ITALICIZE,
            UNDERLINE,
            STRIKETHROUGH,
            HIGHLIGHT
        }

        public override void performAction(string x)
        {
            CommandList cmd;

            try
            {
                if(myWordApp == null)
                {
                    if (!bindWordWindow())
                    {
                        throw new NullReferenceException();
                    }
                }
                if (Enum.TryParse(x, out cmd))
                {
                    switch (cmd)
                    {
                        case CommandList.BOLD:
                            bold();
                            break;
                        case CommandList.ITALICIZE:
                            italicize();
                            break;
                        case CommandList.UNDERLINE:
                            underline();
                            break;
                        case CommandList.STRIKETHROUGH:
                            strikethrough();
                            break;
                        case CommandList.HIGHLIGHT:
                            highlight();
                            break;
                    }
                }
            }
            catch (NullReferenceException e)
            {
                Console.WriteLine("Thrown by performAction in Word.cs");
                Console.WriteLine(e.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine("Thrown by performAction in Word.cs");
                Console.WriteLine(e.ToString());
            }
        }

        public string getStyleName()
        {
            return myWordApp.Selection.get_Style().NameLocal;
        }

        public void bold()
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


        public void italicize()
        {
            if (myWordApp.Selection.Font.Italic == 0)
            {
                myWordApp.Selection.Font.Italic = 1;
            }
            else
            {
                myWordApp.Selection.Font.Italic = 0;
            }
        }

        public void underline()
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

        public void strikethrough()
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

        public void createCustomStyle(Word.Application myWordApp, Word.Document myWordDocument, string name) //incomplete
        {
            Style sectionHeadingExt = myWordDocument.Styles.Add(name);
            //sectionHeadingExt.set_BaseStyle(SectionHeadingInt);
            sectionHeadingExt.Font.Size = 14;
            sectionHeadingExt.Font.Color = WdColor.wdColorBlack;
            sectionHeadingExt.Font.Bold = (int)MsoTriState.msoFalse;
            sectionHeadingExt.ParagraphFormat.LineSpacingRule = WdLineSpacing.wdLineSpaceMultiple;
            sectionHeadingExt.ParagraphFormat.LineSpacing = myWordApp.LinesToPoints((float)1.11);
            sectionHeadingExt.ParagraphFormat.SpaceBefore = 0;
            sectionHeadingExt.ParagraphFormat.SpaceAfter = 0;
            myWordApp.Selection.set_Style("myStyle");
        }

        public void highlight(WdColorIndex color = WdColorIndex.wdDarkYellow)
        {
            myWordApp.Selection.Range.HighlightColorIndex = color;
        }

        public void colorRGB(Word.Application myWordApp, int R, int G, int B)
        {
            myWordApp.Selection.Font.Fill.ForeColor.RGB = R + 256 * G + 256 * 256 * B;
        }

        public void colorDefault(Word.Application myWordApp, WdColorIndex color = WdColorIndex.wdRed)
        {
            myWordApp.Selection.Font.ColorIndex = color;
        }

        public Word.Styles getAllStyleNames(Word.Document myWordDocument)
        {

            foreach (Word.Style currentStyle in myWordDocument.Styles)
            {
                Console.WriteLine(currentStyle.NameLocal);
            }
            return myWordDocument.Styles;
        }


        public void goToPage(Word.Application myWordApp, int pageNumber)
        {
            object what = Microsoft.Office.Interop.Word.WdGoToItem.wdGoToPage;
            object which = Microsoft.Office.Interop.Word.WdGoToDirection.wdGoToFirst;
            object count = pageNumber;
            myWordApp.Selection.GoTo(ref what, ref which, ref count);
        }

        public int getCurrentPageNumber(Word.Application myWordApp)
        {
            int currentPageNum = myWordApp.Selection.get_Information(Word.WdInformation.wdActiveEndPageNumber);
            return currentPageNum;

        }

        public int getLastPageNumber(Word.Application myWordApp)
        {
            int totalPageNum = myWordApp.ActiveDocument.ComputeStatistics(Word.WdStatistic.wdStatisticPages);
            return totalPageNum;
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
}

