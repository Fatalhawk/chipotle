using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Networkings
{
    class firefoxApp
    {
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

        [DllImport("user32.dll", SetLastError = true)]
        static extern void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);

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

        public void favorites()
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
    }
}
