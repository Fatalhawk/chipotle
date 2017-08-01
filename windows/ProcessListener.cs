/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

//UPDATE TO DICTIONARIES

using System.Collections.Generic;
using System.Timers;
using System.Diagnostics;
using System;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

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
            pollTimer = new Timer(5000); //set polling rate to .5 Hz (every 2 seconds)
            pollTimer.Enabled = true;
            pollTimer.Elapsed += new ElapsedEventHandler(pollProcessList); //add invoked (to-be) method to event handler
            pollTimer.Start(); //start timer
            dele = new WinEventDelegate(WinEventProc);
            m_openclosehook = SetWinEventHook(EVENT_SYSTEM_FOREGROUND, EVENT_SYSTEM_FOREGROUND, IntPtr.Zero, dele, 0, 0, WINEVENT_OUTOFCONTEXT);
        }

        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool SetForegroundWindow(IntPtr hWnd);

        [DllImport("user32.dll")]
        [return: MarshalAs(UnmanagedType.Bool)]
        static extern bool IsWindowVisible(IntPtr hWnd);

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern int GetWindowThreadProcessId(IntPtr handle, out int processId);

        public List<int> findNewProcesses(IDictionary<int, ProcessInterface> currentDict, IDictionary<int, ProcessInterface> newDict)
        {
            List<int> newProcessesIds = new List<int>();
            foreach (int key in newDict.Keys)
            {
                if (!currentDict.ContainsKey(key))
                {
                    newProcessesIds.Add(key);
                }
            }
            return newProcessesIds;
        }

        public List<int> findKilledProcesses(IDictionary<int, ProcessInterface> currentDict, IDictionary<int, ProcessInterface> newDict)
        {
            List<int> killedProcessIds = new List<int>();
            foreach (int key in currentDict.Keys)
            {
                if (!newDict.ContainsKey(key))
                {
                    killedProcessIds.Add(key);
                }
            }
            return killedProcessIds;
        }

        private bool dictEquals(ICollection<int> dictOneKeys, ICollection<int> dictTwoKeys)
        {
            if (dictOneKeys.Count != dictTwoKeys.Count)
            {
                return false;
            }
            var newDictSet = new HashSet<int>(dictTwoKeys);
            return newDictSet.SetEquals(dictOneKeys);
        }

        delegate void WinEventDelegate(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime);

        [DllImport("user32.dll")]
        static extern IntPtr SetWinEventHook(uint eventMin, uint eventMax, IntPtr hmodWinEventProc, WinEventDelegate lpfnWinEventProc, uint idProcess, uint idThread, uint dwFlags);

        private const uint WINEVENT_OUTOFCONTEXT = 0;
        private const uint EVENT_SYSTEM_FOREGROUND = 3;
        private const uint WM_CLOSE = 16;
        private const uint WM_CREATE = 1;


        [DllImport("user32.dll")]
        static extern IntPtr GetForegroundWindow();

        [DllImport("user32.dll")]
        static extern int GetWindowText(IntPtr hWnd, System.Text.StringBuilder text, int count);

        /**
         * event handler for new foreground window occurence
         * raises event for things such as start menu (ALL WINDOWS), etc. 
         **/
        public void WinEventProc(IntPtr hWinEventHook, uint eventType, IntPtr hwnd, int idObject, int idChild, uint dwEventThread, uint dwmsEventTime)
        {
            if (ProcessHandler.getProcessDict().Keys.Contains(GetForegroundWindow().ToInt32()))
            {
                tObj.updateTextbox("New window focus");
            }
        }


        private void pollProcessList(object sender, EventArgs e)
        {
            pollingCall();
        }

        private void pollingCall()
        {
            //Dictionary<int, ProcessInterface> newDict = updateProcessDict();
            Dictionary<int, ProcessInterface> newDict;
            GetDesktopWindowHandlesAndTitles(out newDict);
            if (!dictEquals(newDict.Keys, ProcessHandler.getProcessDict().Keys))
            {
                List<int> newProcesses = findNewProcesses(ProcessHandler.getProcessDict(), newDict);
                foreach (int newProcess in newProcesses)
                {
                    tObj.updateTextbox("Process " + newProcess + " initiated");
                    ProcessHandler.addProcess(newProcess, newDict[newProcess]);
                }
                List<int> killedProcesses = findKilledProcesses(ProcessHandler.getProcessDict(), newDict);
                foreach (int killedID in killedProcesses)
                {
                    tObj.updateTextbox("Process " + killedID + " terminated.");
                    ProcessHandler.removeProcess(killedID);
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

        // Save window titles and handles in these lists
        private Dictionary<int, ProcessInterface> newPDict;
        [DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);

        // Return a list of the desktop windows' handles and titles.
        public void GetDesktopWindowHandlesAndTitles(out Dictionary<int, ProcessInterface> pDict)//, out List<string> titles)
        {
            newPDict = new Dictionary<int, ProcessInterface>();

            if(!EnumDesktopWindows(IntPtr.Zero, FilterCallback,
                IntPtr.Zero))
            {
                pDict = null;
            }
            else
            {
                pDict = newPDict;
            }
        }

        [DllImport("user32.dll", ExactSpelling = true)]
        static extern IntPtr GetAncestor(IntPtr hwnd, GetAncestorFlags flags);

        private const long WS_EX_APPWINDOW = 0x00040000L;

        // We use this function to filter windows.
        // This version selects visible windows that have titles.
        private bool FilterCallback(IntPtr hWnd, int lParam)
        {
            // Get the window's title.
            StringBuilder sb_title = new StringBuilder(256);
            int length = GetWindowText(hWnd, sb_title, sb_title.Capacity);
            string title = sb_title.ToString();
            var lShellWindow = GetShellWindow();
            Process pObj;
            uint processId;

            if (!isTopLevel(hWnd, lShellWindow))
            {
                return true;
            }

            //If the window is visible and has a title, save it(and it's not Program Manager).            
            //tObj.updateTextbox(GetParent(hWnd).ToString());
            GetWindowThreadProcessId(hWnd, out processId);
            pObj = Process.GetProcessById(checked((int)processId));
            //ApplicationFrameHost allows stock windows app to be interacted with through GUI
            //but causes additional problems such as creating hidden top-level windows to Mail, Photos, and Groove Music open
            //while checking for updates to those software in WinStoreApp
                
            newPDict.Add(checked((int)hWnd), new ProcessInterface(ref pObj, hWnd, title));



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

        static bool IsInvisibleWin10BackgroundAppWindow(IntPtr hWnd)
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


        private bool isTopLevel(IntPtr hWnd, IntPtr lShellWindow)
        {
            if(hWnd == lShellWindow)
            {
                return false;
            }

            if (IsInvisibleWin10BackgroundAppWindow(hWnd))
            {
                return false;
            }

            if (!IsWindowVisible(hWnd))
            {
                return false;
            }

            IntPtr root = GetAncestor(hWnd, GetAncestorFlags.GA_GETROOTOWNER);

            if (GetLastVisibleActivePopupOfWindow(root) != hWnd)
            {
                return false;
            }

            StringBuilder sb_title = new StringBuilder(256);
            int length = GetWindowText(hWnd, sb_title, sb_title.Capacity);
            string title = sb_title.ToString();

            if (string.IsNullOrEmpty(title))
            {
                return false;
            }
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


        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        static extern int GetClassName(IntPtr hWnd, StringBuilder lpClassName, int nMaxCount);

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