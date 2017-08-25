/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 * Description:
 * ProcessInterface is a template base class for all apps.
 * All specific applications will inherit from ProcessInterface as it provides neccesary basic attributes such as 
 *      -window handle
 *      -icon
 *      -process
 *      -other general information about window
 * ProcessInterface (the base class) will also be the default for applications we have not programmed for and thus do not know specific commands.
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
        protected Process process;
        //process encapsulated in processInterface 
        public Process ProcessProp {
            get { return process; }
            set { process = value; }
        }
        public delegate void updateDele();
        public updateDele updateList;

        [DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);

        //process's main handle as observed by program
        protected IntPtr handle;
        //process's icon
        protected Icon processIcon;
        public string windowTitle;
        public WinEventDelegate dele;

        protected IntPtr titleChangeHook;

        //used for retrieving window handle
        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool SetForegroundWindow(IntPtr hWnd);


        protected uint WINEVENT_OUTOFCONTEXT = 0;
        /**
         * Constructor
         * @param: Process that this object manages
         **/
        public ProcessInterface(ref Process pObj, IntPtr hWnd, string title, updateDele updater)
        {
            windowTitle = title;
            process = pObj;
            handle = hWnd;
            initProcessIcon();
            updateList = updater;
            dele = new WinEventDelegate(WinEventProc);
            titleChangeHook = SetWinEventHook(EVENT_OBJECT_NAMECHANGE, EVENT_OBJECT_NAMECHANGE, IntPtr.Zero, dele, 0, 0, WINEVENT_OUTOFCONTEXT);
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

        public delegate void WinEventDelegate(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime);

        [DllImport("user32.dll")]
        public static extern IntPtr SetWinEventHook(uint eventMin, uint eventMax, IntPtr hmodWinEventProc, WinEventDelegate lpfnWinEventProc, uint idProcess, uint idThread, uint dwFlags);

        protected uint EVENT_OBJECT_NAMECHANGE = 0x800C;

        public virtual void WinEventProc(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime)
        {
            if (hwnd == handle)
            {
                StringBuilder sb_title = new StringBuilder(256);
                int length = GetWindowText(hwnd, sb_title, sb_title.Capacity);
                windowTitle = sb_title.ToString();
                updateList();
            }

        }


        public virtual void performAction(int action)
        {

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
         * MOVE TO PROCESSHANDLER
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
                Console.WriteLine(e.ToString());
                process.Kill();
            }
            catch (Exception e)
            {
                process.Kill();
                Console.WriteLine(e.ToString());
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

        public Process getProcess()
        {
            return process;
        }

        public string getProcessName()
        {
            try
            {
                return process.ProcessName;
            }
            catch (InvalidOperationException e)
            {
                Console.WriteLine(e.ToString());
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

        /**
         * Obsolete
         * -does not work in many cases and has been replced
         **/
        public bool hasExited()
        {
            if (process == null)
            {
                return false;
            }
            try
            {
                return process.HasExited;
            }
            catch (System.ComponentModel.Win32Exception e)
            {
                //Console.WriteLine("Throwing for: {0}", process.MainWindowTitle);
                //Console.WriteLine(e.ToString());
                return false;
            }
        }
    }
}
