using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;
using AudioSwitcher.AudioApi.CoreAudio;

namespace CompCompanionApp
{
    public static class Utility
    {
        private const byte VK_VOLUME_MUTE = 0xAD;
        private const byte VK_VOLUME_DOWN = 0xAE;
        private const byte VK_VOLUME_UP = 0xAF;
        private const byte VK_LCONTROL = 0xA2;
        private const byte VK_KEY_S = 0x53;
        private const byte VK_RWIN = 0x5C;
        private const byte VK_ESCAPE = 0x1B;
        private const byte VK_KEY_E = 0x45;
        private const byte VK_KEY_I = 0x49;
        private static int vol;

        private const UInt32 KEYEVENTF_EXTENDEDKEY = 0x0001;
        private const UInt32 KEYEVENTF_KEYUP = 0x0002;

        static CoreAudioDevice defaultPlaybackDevice = new CoreAudioController().DefaultPlaybackDevice;

        [DllImport("user32.dll")]
        static extern void keybd_event(byte bVk, byte bScan, UInt32 dwFlags, UInt32 dwExtraInfo);
        [DllImport("user32.dll")]
        static extern Byte MapVirtualKey(UInt32 uCode, UInt32 uMapType);



        public static void VolumeUp()
        {
            keybd_event(VK_VOLUME_UP, MapVirtualKey(VK_VOLUME_UP, 0), KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_VOLUME_UP, MapVirtualKey(VK_VOLUME_UP, 0), KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP, 0);
        }

        public static void VolumeDown()
        {
            keybd_event(VK_VOLUME_DOWN, MapVirtualKey(VK_VOLUME_DOWN, 0), KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_VOLUME_DOWN, MapVirtualKey(VK_VOLUME_DOWN, 0), KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP, 0);
        }

        public static void Mute()
        {
            keybd_event(VK_VOLUME_MUTE, MapVirtualKey(VK_VOLUME_MUTE, 0), KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_VOLUME_MUTE, MapVirtualKey(VK_VOLUME_MUTE, 0), KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP, 0);
        }

        public static void Cortana()
        {
            keybd_event(VK_RWIN, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_S, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_S, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_RWIN, 0, KEYEVENTF_KEYUP, 0);
        }

        public static void setVolume(int volume)
        {
            defaultPlaybackDevice.Volume = volume;
        }

        public static double getVolume()
        {
            return defaultPlaybackDevice.Volume;
        }

        public static void Start()
        {
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_ESCAPE, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_ESCAPE, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, 0);
        }

        public static void FileExplorer()
        {
            keybd_event(VK_RWIN, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_E, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_E, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_RWIN, 0, KEYEVENTF_KEYUP, 0);
        }

        public static void Settings()
        {
            keybd_event(VK_RWIN, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_I, 0, KEYEVENTF_EXTENDEDKEY, 0);
            keybd_event(VK_KEY_I, 0, KEYEVENTF_KEYUP, 0);
            keybd_event(VK_RWIN, 0, KEYEVENTF_KEYUP, 0);
        }

        public static void ControlPanel()
        {
            Process p = new Process();
            p.StartInfo.FileName = "cmd.exe";
            p.StartInfo.RedirectStandardInput = true;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.UseShellExecute = false;
            p.Start();

            p.StandardInput.WriteLine("start control");
            p.StandardInput.Flush();
            p.StandardInput.Close();
            Console.WriteLine(p.StandardOutput.ReadToEnd());
        }
    }
}
