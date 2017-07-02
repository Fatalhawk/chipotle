using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace Networking
{
    public class ProcessInterface : Process
    {
        private Process process { get; set; }
        private IntPtr handle;

        //used for retrieving window handle
        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool SetForegroundWindow(IntPtr hWnd);


        /**
         * Constructor
         * @param: Process that this object manages
        **/
        public ProcessInterface(Process pObj)
        {
            process = pObj;
            findWindow();
        }

        public void findWindow()
        {
            // find window handle of Notepad
            handle = process.MainWindowHandle;//FindWindow(process.MainWindowHandle, "");
            if (!handle.Equals(IntPtr.Zero)){}
        }

        public void setForegroundApp()
        {
            if (SetForegroundWindow(handle))
            {
                Console.WriteLine("Now looking at: {0}", process.MainWindowTitle);
            }
        }

        [DllImport("user32.dll", CharSet = CharSet.Unicode)]
        public static extern int SendMessage(int hWnd, uint Msg, IntPtr wParam, IntPtr lParam);

        public void killApp()
        {
            const UInt32 VK_RETURN = 0x0D;
            try
            {
                setForegroundApp();
                process.CloseMainWindow();
                if (!process.HasExited)
                {
                    //Do something if not closed (are you sure?) message opens up
                    SendMessage(process.MainWindowHandle.ToInt32(), VK_RETURN, IntPtr.Zero, IntPtr.Zero);
                    //System.Windows.Forms.SendKeys.SendWait("{ENTER}");
                }
            }
            catch (NullReferenceException e)
            {
                process.Kill();
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: {0}", e.ToString());
            }
            finally
            {
            }
        }
        
        /**
         * Accessor Methods 
        **/ 
        public void printProcessInfo()
        {
            Console.WriteLine("{0}\tID: {1}", process.ProcessName, process.Id);
        }

        public string getTitle()
        {
            return process.MainWindowTitle;
        }
    }
}
