using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Networking
{
    class chromeApp
    {
        private const int WM_APPCOMMAND = 0x319;
        private const int APPCOMMAND_BROWSER_BACKWARD = 1 << 16;
        private const int APPCOMMAND_BROWSER_FORWARD = 2 << 16;
        private const int APPCOMMAND_BROWSER_FAVORITES = 6 << 16;
        private const int APPCOMMAND_BROWSER_HOME = 7 << 16;
        private const int APPCOMMAND_BROWSER_REFRESH = 3 << 16;
        private const int APPCOMMAND_BROWSER_SEARCH = 5 << 16;
        private const int APPCOMMAND_BROWSER_STOP = 4 << 16;
        private const int APPCOMMAND_CLOSE = 31 << 16;
        private const int APPCOMMAND_FIND = 28 << 16;
        private const int J = 0x4A;
        private const int H = 0x48;
        private const int T = 0x54;
        private const int N = 0x4E;
        private const int D = 0x44;
        private const int F = 0x46;
        private const int SHIFT = 0x10;
        private const int TAB = 0x09;
        public const int KEYEVENTF_KEYUP = 0x0002;
        public const int VK_CONTROL = 0x11;

        [DllImport("user32.dll", SetLastError = true)]
        static extern void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);

        [DllImport("user32.dll")]
        public static extern IntPtr SendMessageW(IntPtr hWnd, int Msg,
        IntPtr wParam, IntPtr lParam);

        [DllImport("user32.dll")]
        private static extern IntPtr GetForegroundWindow();

        public void forward()
        {
            IntPtr handle = GetForegroundWindow();
            SendMessageW(handle, WM_APPCOMMAND, handle,
                    (IntPtr)APPCOMMAND_BROWSER_FORWARD);
        }

        public void backward()
        {
            IntPtr handle = GetForegroundWindow();
            SendMessageW(handle, WM_APPCOMMAND, handle,
                    (IntPtr)APPCOMMAND_BROWSER_BACKWARD);
        }

        public void home()
        {
            IntPtr handle = GetForegroundWindow();
            SendMessageW(handle, WM_APPCOMMAND, handle,
                    (IntPtr)APPCOMMAND_BROWSER_HOME);
        }

        public void refresh()
        {
            IntPtr handle = GetForegroundWindow();
            SendMessageW(handle, WM_APPCOMMAND, handle,
                    (IntPtr)APPCOMMAND_BROWSER_REFRESH);
        }

        public void search()
        {
            IntPtr handle = GetForegroundWindow();
            SendMessageW(handle, WM_APPCOMMAND, handle,
                    (IntPtr)APPCOMMAND_BROWSER_SEARCH);
        }

        public void favorites()
        {
            IntPtr handle = GetForegroundWindow();
            SendMessageW(handle, WM_APPCOMMAND, handle,
                    (IntPtr)APPCOMMAND_BROWSER_FAVORITES);
        }

        public void find()
        {
            IntPtr handle = GetForegroundWindow();
            SendMessageW(handle, WM_APPCOMMAND, handle,
                    (IntPtr)APPCOMMAND_FIND);
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


    }
}
