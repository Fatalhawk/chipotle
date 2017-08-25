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

namespace Networking
{
    /**
     * Process Listener class to monitor open processes by polling
     * Notes
     * -currently seperated into own class in case more efficient way is found
     * -uses ElapsedEventHandlers
    **/
    public class ProcessListener
    {
        //Timer to be used to control flow of polling
        private Timer pollTimer;
        TestGUI tObj;
        WinEventDelegate dele = null;
        IntPtr m_openclosehook;

        /**
         * Constructor
         * initializes timer and starts it, triggering event sequence
         **/
        public ProcessListener(TestGUI tgui)
        {
            tObj = tgui;
            //possible change to set global system hook for creation?
            //result so far: ALL window creations (even non-top level) raise event so doing so might actually 
            //have inverse results and cause program to crash
            pollingCall();
            pollTimer = new Timer(1000); //set polling rate to .5 Hz (every 2 seconds)
            pollTimer.Enabled = true;
            pollTimer.Elapsed += new ElapsedEventHandler(pollProcessList); //add invoked (to-be) method to event handler
            pollTimer.Start(); //start timer
            dele = new WinEventDelegate(WinEventProc);
            m_openclosehook = SetWinEventHook(EVENT_SYSTEM_FOREGROUND, EVENT_SYSTEM_FOREGROUND, IntPtr.Zero, dele, 0, 0, WINEVENT_OUTOFCONTEXT);
        }

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern int GetWindowThreadProcessId(IntPtr handle, out int processId);

        delegate void WinEventDelegate(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime);

        [DllImport("user32.dll")]
        static extern IntPtr SetWinEventHook(uint eventMin, uint eventMax, IntPtr hmodWinEventProc, WinEventDelegate lpfnWinEventProc, uint idProcess, uint idThread, uint dwFlags);

        private const uint WINEVENT_OUTOFCONTEXT = 0;
        private const uint EVENT_SYSTEM_FOREGROUND = 3;

        [DllImport("user32.dll")]
        static extern int GetWindowText(IntPtr hWnd, System.Text.StringBuilder text, int count);



        /*
         * Used for WinEventProc callback/event handler to update foreground window, current window
         */
        [DllImport("user32.dll")]
        static extern IntPtr GetForegroundWindow();
        /*
         * event handler for new foreground window occurence
         * raises event for things such as start menu (ALL WINDOWS), etc. 
         */
        public void WinEventProc(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime)
        {
            if (ProcessHandler.getProcessDict().Keys.Contains(hwnd.ToInt32()))
            {
                tObj.updateTextbox("New window focus");
            }
        }

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
            List<int> deletedKeys = findKilledProcesses(ProcessHandler.getProcessDict().Keys.ToList(), newWindows);
            if (deletedKeys.Count > 0)
            {
                tObj.updateTextbox("WUT");
                foreach (int key in deletedKeys)
                {
                    ProcessHandler.removeProcess(key);
                }
                tObj.updateGridView2();
            }                  
        }

        //EnumDesktopWindow declaration to be used to get top-level windows of desktop
        [DllImport("user32.dll", EntryPoint = "EnumDesktopWindows",
        ExactSpelling = false, CharSet = CharSet.Auto, SetLastError = true)]
        private static extern bool EnumDesktopWindows(IntPtr hDesktop,
        EnumDelegate lpEnumCallbackFunction, IntPtr lParam);
        //EnumDesktopWindows callback delegate declaration
        private delegate bool EnumDelegate(IntPtr hWnd, int lParam);

        [DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);

        // Return a list of the desktop windows' handles and titles.
        public void GetDesktopWindowHandlesAndTitles(out List<int> titles)
        {
            wPtrList = new List<int>();
            if(!EnumDesktopWindows(IntPtr.Zero, FilterCallback,
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

        [DllImport("user32.dll", ExactSpelling = true)]
        static extern IntPtr GetAncestor(IntPtr hwnd, GetAncestorFlags flags);

        private const long WS_EX_APPWINDOW = 0x00040000L;

        public delegate void updateFunc();

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
            if (ProcessHandler.getProcessDict().Keys.Contains(hWnd.ToInt32())) return true;

            //If the window is visible and has a title, save it(and it's not Program Manager).            
            //tObj.updateTextbox(GetParent(hWnd).ToString());
            GetWindowThreadProcessId(hWnd, out processId);
            pObj = Process.GetProcessById(checked((int)processId));
            //ApplicationFrameHost allows stock windows app to be interacted with through GUI
            //but causes additional problems such as creating hidden top-level windows to Mail, Photos, and Groove Music open
            //while checking for updates to those software in WinStoreApp

            if (pObj.ProcessName == "WINWORD")
            {
                ProcessHandler.addProcess(hWnd.ToInt32(), new WordApp(ref pObj, hWnd, title, tObj.updateGridView2));
            }
            else
            {
                ProcessHandler.addProcess(hWnd.ToInt32(), new ProcessInterface(ref pObj, hWnd, title, tObj.updateGridView2));
            }
            tObj.updateGridView2();



            // Return true to indicate that we
            // should continue enumerating windows.
            return true;
        }


        [DllImport("user32.dll", EntryPoint = "GetWindowLong")]
        static extern IntPtr GetWindowLongPtr(IntPtr hWnd, int nIndex);

        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        public static extern IntPtr GetParent(IntPtr hWnd);

        enum DWMWINDOWATTRIBUTE : uint
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

        [DllImport("dwmapi.dll")]
        static extern int DwmGetWindowAttribute(IntPtr hwnd, DWMWINDOWATTRIBUTE dwAttribute, out int pvAttribute, int cbAttribute);

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
         * Used to check for visibility in windows
         * function itself is not enough to determine visibility to user
         */
        [DllImport("user32.dll")]
        [return: MarshalAs(UnmanagedType.Bool)]
        static extern bool IsWindowVisible(IntPtr hWnd);

        /*
         * @param: window handle, handle ot shell desktop
         * determines if window handle represents top level application window 
         * 4 checks: visibility (and invisibility), whether last popup is hWnd, and whether window has title
         */
        private bool isTopLevel(IntPtr hWnd, IntPtr lShellWindow, string title)
        {
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

        [DllImport("user32.dll")]
        static extern IntPtr GetShellWindow();

        [DllImport("user32.dll")]
        static extern IntPtr GetLastActivePopup(IntPtr hWnd);

        public enum GetAncestorFlags
        {
            GA_PARENT = 1,
            GA_ROOT = 2,
            GA_GETROOTOWNER = 3
        }
    }
}