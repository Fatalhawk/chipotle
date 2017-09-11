/**
 * Author(s): Takahiro Tow
 * Last updated: August 2, 2017
 * Current Problems:
 * -using process.HasExited to determine if window is closed causes many errors:
 *      -microsoft office applications open up opening sequence window belonging to main .exe
 *      -multiple browser windows all run under the same main .exe
 *      -closing of one window will not result in the process.HasExited event to fire
 **/

//UPDATE TO DICTIONARIES
using System.Timers;
using System.Diagnostics;
using System;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Collections.Generic;
using App.Program;
using System.Drawing;

namespace Networking
{
    /**
     * Process Listener class to monitor open processes by polling
     * Notes
     * -currently seperated into own class in case more efficient way is found
     * -uses ElapsedEventHandlers
    **/
    public class WindowMonitor
    {
        //Timer to be used to control flow of polling
        private Timer pollTimer;
        IntPtr currentApp;
        IntPtr m_openclosehook;
        IntPtr titleChangeHook;

        public delegate void tableUpdater();
        public delegate void textUpdater(string txt);
        public event tableUpdater updateTable;
        public event textUpdater updateText;

        #region Constructor
        /**
         * initializes timer and starts it, triggering event sequence
         **/
        public WindowMonitor(tableUpdater tabUp, textUpdater textUp)
        {
            updateTable += new tableUpdater(tabUp);
            updateText += new textUpdater(textUp);
            //possible change to set global system hook for creation?
            //result so far: ALL window creations (even non-top level) raise event so doing so might actually 
            //have inverse results and cause program to crash
            pollingCall();
            pollTimer = new Timer(1000); //set polling rate to .5 Hz (every 2 seconds)
            pollTimer.Enabled = true;
            pollTimer.Elapsed += new ElapsedEventHandler(pollProcessList); //add invoked (to-be) method to event handler
            pollTimer.Start(); //start timer
            opencloseDele = new WinEventDelegate(OpenCloseEventProc);
            m_openclosehook = SetWinEventHook(EVENT_SYSTEM_FOREGROUND, EVENT_SYSTEM_FOREGROUND, IntPtr.Zero, opencloseDele, 0, 0, WINEVENT_OUTOFCONTEXT);
            titlechangeDele = new WinEventDelegate(TitleChangeEventProc);
            titleChangeHook = SetWinEventHook(EVENT_OBJECT_NAMECHANGE, EVENT_OBJECT_NAMECHANGE, IntPtr.Zero, titlechangeDele, 0, 0, WINEVENT_OUTOFCONTEXT);

            currentApp = GetForegroundWindow();
        }
        #endregion

        #region Interop Functions
        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern int GetWindowThreadProcessId(IntPtr handle, out int processId);

        delegate void WinEventDelegate(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime);

        [DllImport("user32.dll")]
        static extern IntPtr SetWinEventHook(uint eventMin, uint eventMax, IntPtr hmodWinEventProc, WinEventDelegate lpfnWinEventProc, uint idProcess, uint idThread, uint dwFlags);

        private const uint WINEVENT_OUTOFCONTEXT = 0;
        private const uint EVENT_SYSTEM_FOREGROUND = 3;

        [DllImport("user32.dll")]
        static extern int GetWindowText(IntPtr hWnd, System.Text.StringBuilder text, int count);

        [DllImport("user32.dll")]
        static extern IntPtr GetForegroundWindow();

        //EnumDesktopWindow declaration to be used to get top-level windows of desktop
        [DllImport("user32.dll", EntryPoint = "EnumDesktopWindows",
        ExactSpelling = false, CharSet = CharSet.Auto, SetLastError = true)]
        private static extern bool EnumDesktopWindows(IntPtr hDesktop,
        EnumDelegate lpEnumCallbackFunction, IntPtr lParam);
        //EnumDesktopWindows callback delegate declaration
        private delegate bool EnumDelegate(IntPtr hWnd, int lParam);

        [DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);

        [DllImport("user32.dll", ExactSpelling = true)]
        static extern IntPtr GetAncestor(IntPtr hwnd, GetAncestorFlags flags);

        [DllImport("user32.dll", EntryPoint = "GetWindowLong")]
        static extern IntPtr GetWindowLongPtr(IntPtr hWnd, int nIndex);

        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        public static extern IntPtr GetParent(IntPtr hWnd);

        [DllImport("dwmapi.dll")]
        static extern int DwmGetWindowAttribute(IntPtr hwnd, DWMWINDOWATTRIBUTE dwAttribute, out int pvAttribute, int cbAttribute);

        [DllImport("user32.dll")]
        static extern IntPtr GetShellWindow();

        [DllImport("user32.dll")]
        static extern IntPtr GetLastActivePopup(IntPtr hWnd);

        [DllImport("user32.dll")]
        public static extern bool GetWindowRect(IntPtr hWnd, out RECT lpRect);
        [DllImport("user32.dll")]
        public static extern bool PrintWindow(IntPtr hWnd, IntPtr hdcBlt, int nFlags);

        /*
         * Used to check for visibility in windows
         * function itself is not enough to determine visibility to user
         */
        [DllImport("user32.dll")]
        [return: MarshalAs(UnmanagedType.Bool)]
        static extern bool IsWindowVisible(IntPtr hWnd);

        WinEventDelegate opencloseDele = null;
        WinEventDelegate titlechangeDele = null;
        #endregion

        #region Interop Constants and Enums
        private static uint EVENT_OBJECT_NAMECHANGE = 0x800C;
        private const long WS_EX_APPWINDOW = 0x00040000L;
        private enum GetAncestorFlags
        {
            GA_PARENT = 1,
            GA_ROOT = 2,
            GA_GETROOTOWNER = 3
        }
        private enum DWMWINDOWATTRIBUTE : uint
        {
            NCRenderingEnabled = 1,
            NCRenderingPolicy,
            TransitionsForceDisabled,
            AllowNCPaint,
            CaptionButtonBounds,
            NonClientRtlLayout,
            ForceIconicRepresentation,
            Flip3DPolicy,
            ExtendedFrameBounds,
            HasIconicBitmap,
            DisallowPeek,
            ExcludedFromPeek,
            Cloak,
            Cloaked,
            FreezeRepresentation
        }
        #endregion

        //fix this function or use WM_GETICON
        public static Bitmap getWindowCap(IntPtr hwnd)
        {
            RECT rc;
            GetWindowRect(hwnd, out rc);

            Bitmap bmp = new Bitmap(rc.Width, rc.Height, System.Drawing.Imaging.PixelFormat.Format32bppArgb);
            Graphics gfxBmp = Graphics.FromImage(bmp);
            IntPtr hdcBitmap = gfxBmp.GetHdc();

            PrintWindow(hwnd, hdcBitmap, 0);

            gfxBmp.ReleaseHdc(hdcBitmap);
            gfxBmp.Dispose();

            Bitmap resizedBmp = new Bitmap(bmp, new Size(bmp.Width/10,bmp.Height/10));
            return resizedBmp;
        }

        #region Callback Functions
        /*
         * Callback for title changing event
         */
        private void TitleChangeEventProc(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime)
        {
            if (ProgramManager.ProcessDict.ContainsKey(hwnd.ToInt32()))
            {
                StringBuilder sb_title = new StringBuilder(256);
                int length = GetWindowText(hwnd, sb_title, sb_title.Capacity);
                updateText("Program " + hwnd.ToString() + " changed name from " + ProgramManager.ProcessDict[hwnd.ToInt32()].WindowTitle + " to " + sb_title.ToString());
                //tObj.updateTextbox("Program " + hwnd.ToString() + " changed name from " + ProgramManager.ProcessDict[hwnd.ToInt32()].WindowTitle + " to " + sb_title.ToString());
                ProgramManager.changeProgramTitle(hwnd.ToInt32(),sb_title.ToString());
                updateTable();
                 //tObj.updateGridView();
            }
        }

        /*
         * Callback for window opening and closing event
         * raises event for things such as start menu (ALL WINDOWS), etc. 
         */
        public void OpenCloseEventProc(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime)
        {
            if (ProgramManager.ProcessDict.Keys.Contains(hwnd.ToInt32()))
            {
                updateText(ProgramManager.ProcessDict[hwnd.ToInt32()].WindowTitle + " is now in focus");
                //tObj.updateTextbox(ProgramManager.ProcessDict[hwnd.ToInt32()].WindowTitle + " is now in focus");
                ProgramManager.updateWindowCap(currentApp.ToInt32(),getWindowCap(currentApp));
                currentApp = hwnd;
                //tObj.updateGridView();
                updateTable();
            }
        }

        // We use this function to filter windows.
        // This version selects visible windows that have titles.
        private bool FilterCallback(IntPtr hWnd, int lParam)
        {
            StringBuilder sb_title = new StringBuilder(256);
            int length = GetWindowText(hWnd, sb_title, sb_title.Capacity);
            string title = sb_title.ToString();
            var lShellWindow = GetShellWindow();
            Process pObj;
            uint processId;

            if (!isTopLevel(hWnd, lShellWindow, title)) return true;

            wPtrList.Add(hWnd.ToInt32());
            if (ProgramManager.ProcessDict.Keys.Contains(hWnd.ToInt32())) return true;

            //If the window is visible and has a title, save it(and it's not Program Manager).            
            //tObj.updateTextbox(GetParent(hWnd).ToString());
            GetWindowThreadProcessId(hWnd, out processId);
            pObj = Process.GetProcessById(checked((int)processId));
            //ApplicationFrameHost allows stock windows app to be interacted with through GUI
            //but causes additional problems such as creating hidden top-level windows to Mail, Photos, and Groove Music open
            //while checking for updates to those software in WinStoreApp

            if (pObj.ProcessName == "WINWORD")
            {
                ProgramManager.addProgram(hWnd.ToInt32(), new WordApp(ref pObj, hWnd, title));
            }
            else
            {
                ProgramManager.addProgram(hWnd.ToInt32(), new ProgramBase(ref pObj, hWnd, title));
            }
            ProgramManager.updateWindowCap(hWnd.ToInt32(), getWindowCap(hWnd));
            updateTable();
            //tObj.updateGridView();



            // Return true to indicate that we
            // should continue enumerating windows.
            return true;
        }

        // Return a list of the desktop windows' handles and titles.
        public void GetDesktopWindowHandlesAndTitles(out List<int> titles)
        {
            wPtrList = new List<int>();
            if (!EnumDesktopWindows(IntPtr.Zero, FilterCallback,
                IntPtr.Zero))
            {
                titles = null;
            }
            else
            {
                titles = wPtrList;
            }
        }

        private List<int> wPtrList;

        public delegate void updateFunc();

        private static bool IsInvisibleWin10BackgroundAppWindow(IntPtr hWnd)
        {
            int CloakedVal;
            int S_OK = 0x00000000;
            int hRes = DwmGetWindowAttribute(hWnd, DWMWINDOWATTRIBUTE.Cloaked, out CloakedVal, sizeof(int));
            if (hRes != S_OK)
            {
                CloakedVal = 0;
            }
            return CloakedVal > 0;
        }

        /*
         * @param: window handle, handle ot shell desktop
         * determines if window handle represents top level application window 
         * 4 checks: visibility (and invisibility), whether last popup is hWnd, and whether window has title
         */
        private bool isTopLevel(IntPtr hWnd, IntPtr lShellWindow, string title)
        {
            if (title == "Task Switching") { return false; }

            if (hWnd == lShellWindow) { return false; }

            if (IsInvisibleWin10BackgroundAppWindow(hWnd)) { return false; }

            if (!IsWindowVisible(hWnd)) { return false; }

            IntPtr root = GetAncestor(hWnd, GetAncestorFlags.GA_GETROOTOWNER);

            if (GetLastVisibleActivePopupOfWindow(root) != hWnd) { return false; }

            if (string.IsNullOrEmpty(title)) { return false; }

            return true;
        }

        private const int MaxLastActivePopupIterations = 50;

        private static IntPtr GetLastVisibleActivePopupOfWindow(IntPtr window)
        {
            var level = MaxLastActivePopupIterations;
            var currentWindow = window;
            while (level-- > 0)
            {
                var lastPopUp = GetLastActivePopup(currentWindow);

                if (IsWindowVisible(lastPopUp))
                    return lastPopUp;

                if (lastPopUp == currentWindow)
                    return IntPtr.Zero;

                currentWindow = lastPopUp;
            }

            //Log.Warn(string.Format("Could not find last active popup for window {0} after {1} iterations", window, MaxLastActivePopupIterations));
            return IntPtr.Zero;
        }
        #endregion

        #region Polling Functions
        public List<int> findKilledProcesses(List<int> currentKeys, List<int> newKeys)
        {
            List<int> deletedKeys = new List<int>();
            foreach (int key in currentKeys)
            {
                if (!newKeys.Contains(key))
                {
                    deletedKeys.Add(key);
                }
            }
            return deletedKeys;
        }

        private void pollProcessList(object sender, EventArgs e)
        {
            pollingCall();
        }

        private void pollingCall()
        {
            List<int> newWindows;
            GetDesktopWindowHandlesAndTitles(out newWindows);
            List<int> deletedKeys = findKilledProcesses(ProgramManager.ProcessDict.Keys.ToList(), newWindows);
            if (deletedKeys.Count > 0)
            {
                foreach (int key in deletedKeys)
                {
                    ProgramManager.ProcessDict.Remove(key);
                }
                updateTable();
                //tObj.updateGridView();
            }                  
        }
        #endregion

        #region RECT definition
        [StructLayout(LayoutKind.Sequential)]
        public struct RECT
        {
            private int _Left;
            private int _Top;
            private int _Right;
            private int _Bottom;

            public RECT(RECT Rectangle) : this(Rectangle.Left, Rectangle.Top, Rectangle.Right, Rectangle.Bottom)
            {
            }
            public RECT(int Left, int Top, int Right, int Bottom)
            {
                _Left = Left;
                _Top = Top;
                _Right = Right;
                _Bottom = Bottom;
            }

            public int X
            {
                get { return _Left; }
                set { _Left = value; }
            }
            public int Y
            {
                get { return _Top; }
                set { _Top = value; }
            }
            public int Left
            {
                get { return _Left; }
                set { _Left = value; }
            }
            public int Top
            {
                get { return _Top; }
                set { _Top = value; }
            }
            public int Right
            {
                get { return _Right; }
                set { _Right = value; }
            }
            public int Bottom
            {
                get { return _Bottom; }
                set { _Bottom = value; }
            }
            public int Height
            {
                get { return _Bottom - _Top; }
                set { _Bottom = value + _Top; }
            }
            public int Width
            {
                get { return _Right - _Left; }
                set { _Right = value + _Left; }
            }
            public Point Location
            {
                get { return new Point(Left, Top); }
                set
                {
                    _Left = value.X;
                    _Top = value.Y;
                }
            }
            public Size Size
            {
                get { return new Size(Width, Height); }
                set
                {
                    _Right = value.Width + _Left;
                    _Bottom = value.Height + _Top;
                }
            }

            public static implicit operator Rectangle(RECT Rectangle)
            {
                return new Rectangle(Rectangle.Left, Rectangle.Top, Rectangle.Width, Rectangle.Height);
            }
            public static implicit operator RECT(Rectangle Rectangle)
            {
                return new RECT(Rectangle.Left, Rectangle.Top, Rectangle.Right, Rectangle.Bottom);
            }
            public static bool operator ==(RECT Rectangle1, RECT Rectangle2)
            {
                return Rectangle1.Equals(Rectangle2);
            }
            public static bool operator !=(RECT Rectangle1, RECT Rectangle2)
            {
                return !Rectangle1.Equals(Rectangle2);
            }

            public override string ToString()
            {
                return "{Left: " + _Left + "; " + "Top: " + _Top + "; Right: " + _Right + "; Bottom: " + _Bottom + "}";
            }

            public override int GetHashCode()
            {
                return ToString().GetHashCode();
            }

            public bool Equals(RECT Rectangle)
            {
                return Rectangle.Left == _Left && Rectangle.Top == _Top && Rectangle.Right == _Right && Rectangle.Bottom == _Bottom;
            }

            public override bool Equals(object Object)
            {
                if (Object is RECT)
                {
                    return Equals((RECT)Object);
                }
                else if (Object is Rectangle)
                {
                    return Equals(new RECT((Rectangle)Object));
                }

                return false;
            }
        }
        #endregion
    }
}