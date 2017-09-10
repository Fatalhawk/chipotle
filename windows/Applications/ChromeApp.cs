using System;
using System.Diagnostics;
using System.Runtime.InteropServices;

namespace App.Program
{
    public class ChromeApp : ProgramBase
    {
        public override string ProgramType
        {
            get
            {
                return "CHROME";
            }
        }

        #region Keyboard Constants
        private const int J = 0x4A;
        private const int H = 0x48;
        private const int T = 0x54;
        private const int N = 0x4E;
        private const int D = 0x44;
        private const int F = 0x46;
        private const int F5 = 0x74;
        private const int R = 0x52;
        private const int LEFT = 0x25;
        private const int RIGHT = 0x27;
        private const int ALT = 0x12;
        private const int FOR = 0xA7;
        private const int BACK = 0xA6;
        private const int HOME = 0xAC;
        private const int HOMEB = 0x24;
        private const int END = 0x23;
        private const int FAV = 0xAB;
        private const int REFRESH = 0xA8;
        private const int SEARCH = 0xAA;
        private const int SHIFT = 0x10;
        private const int TAB = 0x09;
        public const int KEYEVENTF_KEYUP = 0x0002;
        public const int VK_CONTROL = 0x11;

        public ChromeApp(ref Process pObj, IntPtr hWnd, string title) : base(ref pObj, hWnd, title)
        {
        }
        #endregion

        #region Interop Functions
        [DllImport("user32.dll", SetLastError = true)]
        static extern void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
        #endregion

        public override void performAction(string cmd)
        {
            Command cmdEnum;
            if (Enum.TryParse(cmd, out cmdEnum))
            {
                switch (cmdEnum)
                {
                    case Command.FORWARD:
                        forward();
                        break;
                    case Command.BACK:
                        backward();
                        break;
                    case Command.TOP:
                        top();
                        break;
                    case Command.BOTTOM:
                        bottom();
                        break;
                    case Command.HOME:
                        home();
                        break;
                    case Command.REFRESH:
                        refresh();
                        break;
                    case Command.SEARCH:
                        search();
                        break;
                    case Command.BOOKMARKS:
                        bookmarks();
                        break;
                    case Command.PREV_TAB:
                        prevTab();
                        break;
                    case Command.NEXT_TAB:
                        nextTab();
                        break;
                    case Command.FIND:
                        find();
                        break;
                    case Command.HISTORY:
                        history();
                        break;
                    case Command.DOWNLOADS:
                        downloads();
                        break;
                    case Command.NEW_TAB:
                        newTab();
                        break;
                    case Command.NEW_WINDOW:
                        newWindow();
                        break;
                    case Command.BOOKMARK_PAGE:
                        bookmarkThisPage();
                        break;
                }
            }
        }

        private enum Command
        {
            FORWARD,
            BACK,
            TOP,
            BOTTOM,
            HOME,
            REFRESH,
            SEARCH,
            BOOKMARKS,
            PREV_TAB,
            NEXT_TAB,
            FIND,
            HISTORY,
            DOWNLOADS,
            NEW_TAB,
            NEW_WINDOW,
            BOOKMARK_PAGE
        }

        #region Action Commands
        public void forward()
        {
            keybd_event(FOR, 0x9e, 0, 0);
            keybd_event(FOR, 0x9e, KEYEVENTF_KEYUP, 0);
        }

        public void backward()
        {
            keybd_event(BACK, 0x9e, 0, 0);
            keybd_event(BACK, 0x9e, KEYEVENTF_KEYUP, 0);
        }

        public void top()
        {
            keybd_event(HOMEB, 0x9e, 0, 0);
            keybd_event(HOMEB, 0x9e, KEYEVENTF_KEYUP, 0);
        }

        public void bottom()
        {
            keybd_event(END, 0x9e, 0, 0);
            keybd_event(END, 0x9e, KEYEVENTF_KEYUP, 0);
        }

        public void home()
        {
            keybd_event(HOME, 0x9e, 0, 0);
            keybd_event(HOME, 0x9e, KEYEVENTF_KEYUP, 0);
        }

        public void refresh()
        {
            keybd_event(REFRESH, 0x9e, 0, 0);
            keybd_event(REFRESH, 0x9e, KEYEVENTF_KEYUP, 0);
        }

        public static void search()
        {
            keybd_event(SEARCH, 0x9e, 0, 0);
            keybd_event(SEARCH, 0x9e, KEYEVENTF_KEYUP, 0);
        }

        public void bookmarks()
        {
            keybd_event(FAV, 0x9e, 0, 0);
            keybd_event(FAV, 0x9e, KEYEVENTF_KEYUP, 0);
        }

        public void prevTab()
        {
            keybd_event(VK_CONTROL, 0x9d, 0, 0);
            keybd_event(SHIFT, 0x9d, 0, 0);
            keybd_event(TAB, 0x9e, 0, 0);
            keybd_event(TAB, 0x9e, KEYEVENTF_KEYUP, 0);
            keybd_event(SHIFT, 0x9d, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_CONTROL, 0x9d, KEYEVENTF_KEYUP, 0);
        }

        public void nextTab()
        {
            keybd_event(VK_CONTROL, 0x9d, 0, 0);
            keybd_event(TAB, 0x9e, 0, 0);
            keybd_event(TAB, 0x9e, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_CONTROL, 0x9d, KEYEVENTF_KEYUP, 0);
        }

        public void find()
        {
            keybd_event(VK_CONTROL, 0x9d, 0, 0);
            keybd_event(F, 0x9e, 0, 0);
            keybd_event(F, 0x9e, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_CONTROL, 0x9d, KEYEVENTF_KEYUP, 0);
        }

        public void history()
        {
            keybd_event(VK_CONTROL, 0x9d, 0, 0);
            keybd_event(H, 0x9e, 0, 0);
            keybd_event(H, 0x9e, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_CONTROL, 0x9d, KEYEVENTF_KEYUP, 0);
        }

        public void downloads()
        {
            keybd_event(VK_CONTROL, 0x9d, 0, 0);
            keybd_event(J, 0x9e, 0, 0);
            keybd_event(J, 0x9e, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_CONTROL, 0, KEYEVENTF_KEYUP, 0);
        }
        public void newTab()
        {
            keybd_event(VK_CONTROL, 0x9d, 0, 0);
            keybd_event(T, 0x9e, 0, 0);
            keybd_event(T, 0x9e, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_CONTROL, 0, KEYEVENTF_KEYUP, 0);
        }

        public void newWindow()
        {
            keybd_event(VK_CONTROL, 0x9d, 0, 0);
            keybd_event(N, 0x9e, 0, 0);
            keybd_event(N, 0x9e, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_CONTROL, 0, KEYEVENTF_KEYUP, 0);
        }

        public void bookmarkThisPage()
        {
            keybd_event(VK_CONTROL, 0x9d, 0, 0);
            keybd_event(D, 0x9e, 0, 0);
            keybd_event(D, 0x9e, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_CONTROL, 0, KEYEVENTF_KEYUP, 0);
        }
        #endregion
    }
}
