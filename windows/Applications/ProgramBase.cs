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


namespace App.Program
{
    /**
     * Class to define base operations on process
     * Meant to be inherited by more app-specific classes
     **/
    public class ProgramBase
    {
        public virtual string ProgramType { get { return "UNSUPPORTED"; } }
        public Process AssociatedProcess{ get; set; }
        //process encapsulated in processInterface 
        //public Process ProcessProp {
        //    get { return process; }
        //    set { process = value; }
        //}
        public delegate void updateDele();
        public updateDele updateList;

        [DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);

        //process's main handle as observed by program
        protected IntPtr handle;
        //process's icon
        protected Icon _appIcon;
        public Icon AppIcon { get { return _appIcon; } }
        protected Bitmap _windowCap;
        public Bitmap WindowCap { get { return _windowCap; } set { _windowCap = value; } }
        public string WindowTitle { get; set; }


        #region Interop Functions

        [return: MarshalAs(UnmanagedType.Bool)]
        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        static extern bool PostMessage(IntPtr hWnd, uint Msg, IntPtr wParam, IntPtr lParam);
        #endregion

        #region Interop Constants
        private const UInt32 WM_CLOSE = 0x0010;
        private const UInt32 WM_KEYDOWN = 0x0100;
        private const UInt32 WM_SYSCOMMAND = 0x0112;
        IntPtr SC_CLOSE = new IntPtr(0xF060);
        private const int enterKey = 13;
        private IntPtr VK_RETURN = new IntPtr(enterKey);
        #endregion

        /**
         * Constructor
         * @param: Process that this object manages
         **/
        public ProgramBase(ref Process pObj, IntPtr hWnd, string title)
        {
            WindowTitle = title;
            AssociatedProcess = pObj;
            handle = hWnd;
            initProcessIcon();
        }

        private void initProcessIcon()
        {
            try
            {
                _appIcon = Icon.ExtractAssociatedIcon(AssociatedProcess.MainModule.FileName);
            }
            //for each catch send an app-default icon to the phone to use as a placeholder
            catch (System.ComponentModel.Win32Exception e)
            {
                _appIcon = null;
                Console.WriteLine(e.ToString());
            }
            catch (ArgumentException e)
            {
                _appIcon = null;
                Console.WriteLine(e.ToString());
            }
            catch(Exception e)
            {
                _appIcon = null;
                Console.WriteLine(e.ToString());
            }
        }

        public virtual void performAction(string cmd)
        {

        }

        public virtual void performAction(string cmd, byte[] extraInfo)
        {

        }

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
                AssociatedProcess.Kill();
            }
            catch (Exception e)
            {
                AssociatedProcess.Kill();
                Console.WriteLine(e.ToString());
            }
            return true;
        }

        public bool Equals(ProgramBase otherPI)
        {
            return (AssociatedProcess.Id == otherPI.AssociatedProcess.Id);
        }

        public int getHandle()
        {
            return handle.ToInt32();
        }
    }
}
