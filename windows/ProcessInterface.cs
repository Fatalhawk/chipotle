/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

//UPDATE TO DICTIONARIES

using System;
using System.Diagnostics;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Text;

namespace Networking
{
    /**
     * Class to define base operations on process
     * Meant to be inherited by more app-specific classes
     **/ 
    public class ProcessInterface
    {
        private Process process;
        //process encapsulated in processInterface 
        public Process ProcessProp {
            get { return process; }
            set { process = value; }
        }

        [DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);

        //process's main handle as observed by program
        private IntPtr handle;
        //process's icon
        private Icon processIcon;
        private string windowTitle;

        //used for retrieving window handle
        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool SetForegroundWindow(IntPtr hWnd);

        /**
         * Constructor
         * @param: Process that this object manages
         **/
        public ProcessInterface(ref Process pObj, IntPtr hWnd, string title)
        {
            windowTitle = title;
            process = pObj;
            handle = hWnd;
            initProcessIcon();
        }

        private void initProcessIcon()
        {
            try
            {
                processIcon = Icon.ExtractAssociatedIcon(process.MainModule.FileName);
            }
            //for each catch send an app-default icon to the phone to use as a placeholder
            catch (System.ComponentModel.Win32Exception e)
            {
                processIcon = null;
                //Console.WriteLine(e.ToString());
            }
            catch (ArgumentException e)
            {
                processIcon = null;
                //Console.WriteLine(e.ToString());
            }
            catch(Exception e)
            {
                processIcon = null;
                //Console.WriteLine(e.ToString());
            }
        }

        /**
         * makes window of process the foreground window
         **/
        public bool setForegroundApp()
        {
            if (!SetForegroundWindow(handle))
            {
                return false;
            }
            return true;
        }

        [DllImport("user32.dll")]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool IsWindowVisible(IntPtr hWnd);

        [DllImport("user32.dll", EntryPoint = "GetWindowText",
        ExactSpelling = false, CharSet = CharSet.Auto, SetLastError = true)]
        private static extern int GetWindowText(IntPtr hWnd,
            StringBuilder lpWindowText, int nMaxCount);

        [DllImport("user32.dll", EntryPoint = "EnumDesktopWindows",
        ExactSpelling = false, CharSet = CharSet.Auto, SetLastError = true)]
        private static extern bool EnumDesktopWindows(IntPtr hDesktop,
            EnumDelegate lpEnumCallbackFunction, IntPtr lParam);

        // Define the callback delegate's type.
        private delegate bool EnumDelegate(IntPtr hWnd, int lParam);
        private IntPtr confirmationHandle;

        private bool filterCallBack(IntPtr hWnd, int lParam)
        {
            if (IsWindowVisible(hWnd))
            {
                confirmationHandle = hWnd;
                return true;
            }
            return false;
        }

        private void getConfirmationHandle(out IntPtr conHandle)
        {
            conHandle = new IntPtr();
            if (!EnumDesktopWindows(handle, filterCallBack, IntPtr.Zero))
            {
                conHandle = IntPtr.Zero;
            }
            else
            {
                conHandle = confirmationHandle;
            }
        }

        private const UInt32 WM_CLOSE = 0x0010;
        private const UInt32 WM_KEYDOWN = 0x0100;
        private const UInt32 WM_SYSCOMMAND = 0x0112;
        IntPtr SC_CLOSE = new IntPtr(0xF060);
        private const int enterKey = 13;
        private IntPtr VK_RETURN = new IntPtr(enterKey);


        [DllImport("user32.dll", CharSet = CharSet.Auto)]
        private static extern IntPtr SendMessage(IntPtr hWnd, UInt32 Msg, IntPtr wParam, IntPtr lParam);


        [return: MarshalAs(UnmanagedType.Bool)]
        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        static extern bool PostMessage(IntPtr hWnd, uint Msg, IntPtr wParam, IntPtr lParam);


        /**
         * terminates the process and closes the window of the process
         **/
        public bool killApp()
        {
            try
            {
                PostMessage(handle, WM_SYSCOMMAND, SC_CLOSE, IntPtr.Zero);
                return true;
            }
            catch (NullReferenceException e)
            {
                process.Kill();
            }
            catch (Exception e)
            {
                process.Kill();
                //Console.WriteLine("Error: {0}", e.ToString());
            }
            return true;
        }
        
        /**
         * Accessor Methods 
         **/ 
        public void printProcessInfo()
        {
            Console.WriteLine("{0}\tID: {1}\tHandle: {1}", process.ProcessName, process.Id, process.MainWindowHandle);
        }

        public string getTitle()
        {
            return windowTitle;
        }

        public Icon getIcon()
        {
            return processIcon;
        }

        public string getProcessName()
        {
            try
            {
                return process.ProcessName;
            }
            catch (InvalidOperationException e)
            {
                return "lol";
            }
        }

        public bool Equals(ProcessInterface otherPI)
        {
            return (process.Id == otherPI.process.Id);
        }

        public int getHandle()
        {
            return handle.ToInt32();
        }
    }
}
