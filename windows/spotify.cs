using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Threading.Tasks;
using System.Runtime.InteropServices;

namespace chrometest
{
    class spotifyApp
    {
        private const int APPCOMMAND_MEDIA_PLAY_PAUSE = 0xE0000;
        private const int WM_APPCOMMAND = 0x319;
        private const byte VK_LCONTROL = 0xA2;
        private const byte VK_LSHIFT = 0xA0;
        private const byte VK_UP = 0x26;
        private const byte VK_DOWN = 0x28;
        private const byte VK_LEFT = 0x25;
        private const byte VK_RIGHT = 0x27;
        private const UInt32 KEYEVENTF_EXTENDEDKEY = 0x0001;
        private const UInt32 KEYEVENTF_KEYUP = 0x0002;
        private const int APPCOMMAND_VOLUME_MUTE = 0x80000;
        private const int APPCOMMAND_VOLUME_UP = 0xA0000;
        private const int APPCOMMAND_VOLUME_DOWN = 0x90000;
        private const int APPCOMMAND_MEDIA_NEXTTRACK = 0xB0000;
        private const int APPCOMMAND_MEDIA_PREVIOUSTRACK = 0xC0000;
        private const int VK_KEY_R = 0x52;
        private const int VK_KEY_S = 0x53;

        private static IntPtr handle;

        [DllImport("user32.dll")]
        public static extern IntPtr SendMessageW(IntPtr hWnd, int Msg,
        IntPtr wParam, IntPtr lParam);
        [DllImport("user32.dll", EntryPoint = "FindWindow", SetLastError = true)]
        public static extern IntPtr FindWindowByCaption(IntPtr zeroOnly, string lpWindowName);
        [DllImport("user32.dll")]
        static extern void keybd_event(byte bVk, byte bScan, UInt32 dwFlags, UInt32 dwExtraInfo);
        [DllImport("user32.dll")]
        static extern Byte MapVirtualKey(UInt32 uCode, UInt32 uMapType);

        private static void playPause()
        {
            handle = FindWindowByCaption(IntPtr.Zero, Console.Title);
            SendMessageW(handle, WM_APPCOMMAND, handle,
                (IntPtr)APPCOMMAND_MEDIA_PLAY_PAUSE);
        }

        private static void nextTrack()
        {
            handle = FindWindowByCaption(IntPtr.Zero, Console.Title);
            SendMessageW(handle, WM_APPCOMMAND, handle,
                (IntPtr)APPCOMMAND_MEDIA_NEXTTRACK);
        }

        private static void prevTrack()
        {
            handle = FindWindowByCaption(IntPtr.Zero, Console.Title);
            SendMessageW(handle, WM_APPCOMMAND, handle,
                (IntPtr)APPCOMMAND_MEDIA_PREVIOUSTRACK);
        }

        private static void volUp()
        {
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_UP, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_UP, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, 0);
        }

        private static void volDown()
        {
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_DOWN, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_DOWN, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, 0);
        }

        private static void shuffle()
        {
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_S, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_S, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, 0);
        }

        private static void repeat()
        {
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_R, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_R, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, 0);
        }

        private static void forward()
        {
            keybd_event(VK_LSHIFT, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_RIGHT, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_RIGHT, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_LSHIFT, 0, KEYEVENTF_KEYUP, 0);
        }

        private static void backward()
        {
            keybd_event(VK_LSHIFT, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_LEFT, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_LEFT, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_LSHIFT, 0, KEYEVENTF_KEYUP, 0);
        }


        static void Main(string[] args)
        {
            if (Console.ReadLine() == "f")
            {
                System.Threading.Thread.Sleep(5000);
                forward();
            }
        } 
    }
}
