using System;
using System.Diagnostics;
using System.Linq;
using Microsoft.Office.Interop.PowerPoint;

namespace App.Program
{
    public class PowerpointApp : ProgramBase
    {
        public override string ProgramType
        {
            get
            {
                return "MICROSOFT_POWERPOINT";
            }
        }

        Application myPPtApp = null;
        public PowerpointApp(ref Process pObj, IntPtr hWnd, string title) : base(ref pObj, hWnd, title)
        {

        }

        public bool bindWindow()
        {
            try
            {
                myPPtApp =
                  System.Runtime.InteropServices.Marshal.GetActiveObject(
                  "PowerPoint.Application") as Application;
            }
            catch (System.Runtime.InteropServices.COMException e)
            {
                Console.WriteLine(
                  String.Format("PPt application was not running: {0}",
                  e.Message));
            }

            if (myPPtApp != null)
            {
                Console.WriteLine(
                  "Successfully attached to running instance of PowerPoint.");
                Presentation pres = null;
                try
                {
                    pres = myPPtApp.ActivePresentation;
                    return true;
                }
                catch (Exception e)
                {
                    Console.WriteLine("No presentation open");
                    return false;
                }

                //do stuff
            }
            return false;
        }

        public override void performAction(string cmd)
        {
            try
            {
                Command cmdEnum;
                if (myPPtApp == null)
                {
                    if (!bindWindow())
                    {
                        throw new NullReferenceException();
                    }
                }
                if (Enum.TryParse(cmd, out cmdEnum))
                {
                    switch (cmdEnum)
                    {
                        case Command.BOLD:
                            bold();
                            break;
                        case Command.UNDERLINE:
                            underline();
                            break;
                        case Command.STRIKETHROUGH:
                            strikethrough();
                            break;
                        case Command.SHADOW:
                            shadow();
                            break;
                        case Command.OUTLINE_TOGGLE:
                            outlineToggle();
                            break;
                        case Command.NEXT_SLIDE:
                            nextSlide();
                            break;
                        case Command.PREVIOUS_SLIDE:
                            prevSlide();
                            break;
                    }
                }
            }
            catch (NullReferenceException e)
            {
                Console.WriteLine("Thrown by performAction in Powerpoint.cs");
                Console.WriteLine(e.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine("Thrown by performAction in Powerpoint.cs");
                Console.WriteLine(e.ToString());
            }
        }

        private enum Command
        {
            BOLD,
            UNDERLINE,
            STRIKETHROUGH,
            SHADOW,
            OUTLINE_TOGGLE,
            NEXT_SLIDE,
            PREVIOUS_SLIDE
        }

        #region Action Commands
        public void bold()
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

        public void underline()
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

        public void strikethrough()
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

        public void shadow()
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

        public void fillRGB(int R, int B, int G)
        {
            ShapeRange ppshr = myPPtApp.ActiveWindow.Selection.ShapeRange;
            ppshr.Fill.ForeColor.RGB = R + 256 * G + 256 * 256 * B;
        }

        public void outlineToggle()
        { 
            ShapeRange ppshr = myPPtApp.ActiveWindow.Selection.ShapeRange;
            if(ppshr.Line.Visible == Microsoft.Office.Core.MsoTriState.msoFalse)
            {
                ppshr.Line.Visible = Microsoft.Office.Core.MsoTriState.msoTrue;
            }
            else
            {
                ppshr.Line.Visible = Microsoft.Office.Core.MsoTriState.msoFalse;
            }
            
        }


        public void outlineRGB(int R, int G, int B)
        {
            ShapeRange ppshr = myPPtApp.ActiveWindow.Selection.ShapeRange;
            ppshr.Line.ForeColor.RGB = R + 256 * G + 256 * 256 * B;
        }


        public void nextSlide()
        {
            Slides slides = getSlides();
            Slide slide = getCurrentSlide();
            int slideIndex = slide.SlideIndex + 1;
            if(slideIndex <= getSlideCount())
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

        private void prevSlide()
        {
            Slides slides = getSlides();
            Slide slide = getCurrentSlide();
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

        public Slide getCurrentSlide()
        {
            return myPPtApp.ActivePresentation.Slides[myPPtApp.ActiveWindow.Selection.SlideRange.SlideNumber];
        }

        public Slides getSlides()
        {
            return myPPtApp.ActivePresentation.Slides;
        }


        public int getSlideCount()
        {
            return myPPtApp.ActivePresentation.Slides.Count;
        }

        public void addSlideEnd()
        {
            int slidecount = getSlideCount();
            myPPtApp.ActivePresentation.Slides.Add(slidecount + 1, PpSlideLayout.ppLayoutBlank);
        }

        public void addTextBox(Application myPPtApp)
        {
            Presentation activePres = myPPtApp.ActivePresentation;
            float widthLocation = (activePres.PageSetup.SlideWidth / 2) - 50;
            float heightLocation = (activePres.PageSetup.SlideHeight / 2) - 50;
            var textBox = activePres.Slides[myPPtApp.ActiveWindow.Selection.SlideRange.SlideNumber].Shapes.AddTextbox(Microsoft.Office.Core.MsoTextOrientation.msoTextOrientationHorizontal, widthLocation, heightLocation, 100, 100);
            textBox.TextEffect.Text = " ";
            textBox.TextEffect.Alignment = Microsoft.Office.Core.MsoTextEffectAlignment.msoTextEffectAlignmentCentered;
        }

        public void startPres(Application myPPtApp)
        {
            Presentation activePres = myPPtApp.ActivePresentation;
            activePres.SlideShowSettings.Run();
        }
        #endregion
    }
}
