using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.IO;
using Word = Microsoft.Office.Interop.Word;
using System.Drawing.Imaging;
using System.Drawing;
using System.Threading;
using Microsoft.Office.Interop.Word;

namespace WordToImg
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
           Thread.Sleep(6000);
           getImages("s");

        }

        public static void getImages(String s)
        {
            Console.WriteLine("noice");
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
                    //
                    object missing = System.Type.Missing;
                    object path1 = myWordDocument.Path + myWordApp.PathSeparator + myWordDocument.Name;
                    Console.WriteLine(path1);

                    foreach (Microsoft.Office.Interop.Word.Window window in myWordDocument.Windows)
                    {
                        foreach (Microsoft.Office.Interop.Word.Pane pane in window.Panes)
                        {
                            for (var i = 1; i <= pane.Pages.Count; i++)
                            {
                                var bits = pane.Pages[i].EnhMetaFileBits;
                                var target = path1 + i.ToString() + "_image.doc";
                                try
                                {
                                    using (var ms = new MemoryStream((byte[])(bits)))
                                    {
                                        var image = System.Drawing.Image.FromStream(ms);
                                        var pngTarget = Path.ChangeExtension(target, "png");
                                        image.Save(pngTarget, System.Drawing.Imaging.ImageFormat.Png);
                                    }
                                }
                                catch (System.Exception ex)
                                { }
                            }
                        }
                    }
                    Console.ReadLine();
                }
                catch (Exception e)
                {
                    Console.WriteLine("No document open");
                }
            }
        }
    }
}
