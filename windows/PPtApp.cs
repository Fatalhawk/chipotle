using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PPt = Microsoft.Office.Interop.PowerPoint;
using Microsoft.Office.Interop.PowerPoint;
using System.Runtime.InteropServices;
using System.Threading;
using Microsoft.Office.Core;
using System.Windows.Forms;
using System.Drawing;

namespace Networkings
{
    class powerpointApp
    {

        public static void doCommand()
        {
            PPt.Application myPPtApp = null;
            try
            {
                myPPtApp =
                  System.Runtime.InteropServices.Marshal.GetActiveObject(
                  "PowerPoint.Application") as PPt.Application;
            }
            catch (System.Runtime.InteropServices.COMException e)
            {
                Console.WriteLine(
                  String.Format("PPt application was not running: {0}",
                  e.Message));
                return;
            }

            if (myPPtApp != null)
            {
                Console.WriteLine(
                  "Successfully attached to running instance of PowerPoint.");
                Presentation pres = null;
                try
                {
                    pres = myPPtApp.ActivePresentation;
                }
                catch (Exception e)
                {
                    Console.WriteLine("No presentation open");
                }

                //do stuff
            }
        }

        public void bold(PPt.Application myPPtApp)
        {
            var window = myPPtApp.ActiveWindow;
            if (window.Selection.TextRange.Font.Bold == Microsoft.Office.Core.MsoTriState.msoFalse)
            {
                window.Selection.TextRange.Font.Bold = Microsoft.Office.Core.MsoTriState.msoTrue;
            }
            else
            {
                window.Selection.TextRange.Font.Bold = Microsoft.Office.Core.MsoTriState.msoFalse;
            }
        }

        public void underline(PPt.Application myPPtApp)
        {
            var window = myPPtApp.ActiveWindow;
            if (window.Selection.TextRange.Font.Underline == Microsoft.Office.Core.MsoTriState.msoFalse)
            {
                window.Selection.TextRange.Font.Underline = Microsoft.Office.Core.MsoTriState.msoTrue;
            }
            else
            {
                window.Selection.TextRange.Font.Underline = Microsoft.Office.Core.MsoTriState.msoFalse;
            }
        }

        public void strikethrough(PPt.Application myPPtApp)
        {
            var window = myPPtApp.ActiveWindow;
            if (window.Selection.TextRange2.Font.StrikeThrough == Microsoft.Office.Core.MsoTriState.msoFalse)
            {
                window.Selection.TextRange2.Font.StrikeThrough = Microsoft.Office.Core.MsoTriState.msoTrue;
            }
            else
            {
                window.Selection.TextRange2.Font.StrikeThrough = Microsoft.Office.Core.MsoTriState.msoFalse;
            }
        }

        public void shadow(PPt.Application myPPtApp)
        {
            var window = myPPtApp.ActiveWindow;
            if (window.Selection.TextRange2.Font.Shadow.Visible == Microsoft.Office.Core.MsoTriState.msoFalse)
            {
                window.Selection.TextRange2.Font.Shadow.Visible = Microsoft.Office.Core.MsoTriState.msoTrue;
            }
            else
            {
                window.Selection.TextRange2.Font.Shadow.Visible = Microsoft.Office.Core.MsoTriState.msoFalse;
            }
        }

        public void fillRGB(int R, int B, int G, PPt.Application myPPtApp)
        {
            PPt.ShapeRange ppshr = myPPtApp.ActiveWindow.Selection.ShapeRange;
            ppshr.Fill.ForeColor.RGB = R + 256 * G + 256 * 256 * B;
        }

        public void outlineToggle(PPt.Application myPPtApp)
        { 
            PPt.ShapeRange ppshr = myPPtApp.ActiveWindow.Selection.ShapeRange;
            if(ppshr.Line.Visible == Microsoft.Office.Core.MsoTriState.msoFalse)
            {
                ppshr.Line.Visible = Microsoft.Office.Core.MsoTriState.msoTrue;
            }
            else
            {
                ppshr.Line.Visible = Microsoft.Office.Core.MsoTriState.msoFalse;
            }
            
        }


        public void outlineRGB(int R, int G, int B, PPt.Application myPPtApp)
        {
            PPt.ShapeRange ppshr = myPPtApp.ActiveWindow.Selection.ShapeRange;
            ppshr.Line.ForeColor.RGB = R + 256 * G + 256 * 256 * B;
        }


        public void nextSlide(PPt.Application myPPtApp)
        {
            Slides slides = getSlides(myPPtApp);
            Slide slide = getCurrentSlide(myPPtApp);
            int slideIndex = slide.SlideIndex + 1;
            if(slideIndex <= getSlideCount(myPPtApp))
            {
                try
                {
                    slide = slides[slideIndex];
                    slides[slideIndex].Select();
                }
                catch
                {
                    myPPtApp.SlideShowWindows[1].View.Next();
                    slide = myPPtApp.SlideShowWindows[1].View.Slide;
                }
            }
        }

        private void prevSlide(PPt.Application myPPtApp)
        {
            Slides slides = getSlides(myPPtApp);
            Slide slide = getCurrentSlide(myPPtApp);
            int slideIndex = slide.SlideIndex - 1;
            if (slideIndex >= 1)
            {
                try
                {
                    slide = slides[slideIndex];
                    slides[slideIndex].Select();
                }
                catch
                {
                    myPPtApp.SlideShowWindows[1].View.Previous();
                    slide = myPPtApp.SlideShowWindows[1].View.Slide;
                }
            }
        }

        public Slide getCurrentSlide(PPt.Application myPPtApp)
        {
            return myPPtApp.ActivePresentation.Slides[myPPtApp.ActiveWindow.Selection.SlideRange.SlideNumber];
        }

        public Slides getSlides(PPt.Application myPPtApp)
        {
            return myPPtApp.ActivePresentation.Slides;
        }


        public int getSlideCount(PPt.Application myPPtApp)
        {
            return myPPtApp.ActivePresentation.Slides.Count;
        }

        public void addSlideEnd(PPt.Application myPPtApp)
        {
            int slidecount = getSlideCount(myPPtApp);
            myPPtApp.ActivePresentation.Slides.Add(slidecount + 1, PPt.PpSlideLayout.ppLayoutBlank);
        }

        public void addTextBox(PPt.Application myPPtApp)
        {
            Presentation activePres = myPPtApp.ActivePresentation;
            float widthLocation = (activePres.PageSetup.SlideWidth / 2) - 50;
            float heightLocation = (activePres.PageSetup.SlideHeight / 2) - 50;
            var textBox = activePres.Slides[myPPtApp.ActiveWindow.Selection.SlideRange.SlideNumber].Shapes.AddTextbox(Microsoft.Office.Core.MsoTextOrientation.msoTextOrientationHorizontal, widthLocation, heightLocation, 100, 100);
            textBox.TextEffect.Text = " ";
            textBox.TextEffect.Alignment = Microsoft.Office.Core.MsoTextEffectAlignment.msoTextEffectAlignmentCentered;
        }

        public void startPres(PPt.Application myPPtApp)
        {
            Presentation activePres = myPPtApp.ActivePresentation;
            activePres.SlideShowSettings.Run();
        }
    }
}
