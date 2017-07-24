using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Threading.Tasks;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using System.Diagnostics;
using System.Threading;
using SpotifyAPI.Local; //Base Namespace
using SpotifyAPI.Local.Enums; //Enums
using SpotifyAPI.Local.Models; //Models for the JSON-responses
using System.Drawing;

namespace chrometest
{
    class spotifyApp
    {
        private static SpotifyLocalAPI _spotify = new SpotifyLocalAPI();

        public static void Main()
        {
            spotifyApp s = new spotifyApp();
            _spotify.Connect();
            Console.WriteLine(s.TrackName() + " " + s.PlayingPosition().ToString() + " " + s.ArtistName());
            Console.WriteLine(s.IsAd().ToString());
        }
        

        public bool isSpotifyRunning()
        {
            return SpotifyLocalAPI.IsSpotifyRunning();
        }

        public bool isFunctional()
        {
            return (SpotifyLocalAPI.IsSpotifyWebHelperRunning() && _spotify.Connect() && SpotifyLocalAPI.IsSpotifyWebHelperRunning());
        }

        public int Version() { return _spotify.GetStatus().Version; }

        public string ClientVersion() { return _spotify.GetStatus().ClientVersion; }

        public bool Playing() { return _spotify.GetStatus().Playing; }

        public bool PlayEnabled() { return _spotify.GetStatus().PlayEnabled; }

        public bool PrevEnabled() { return _spotify.GetStatus().PrevEnabled; }

        public bool NextEnabled() { return _spotify.GetStatus().NextEnabled; }

        public Track Track() { return _spotify.GetStatus().Track; }

        public double PlayingPosition() { return _spotify.GetStatus().PlayingPosition; }

        public int ServerTime() { return _spotify.GetStatus().ServerTime; }

        public double Volume() { return _spotify.GetStatus().Volume; }

        public bool Online() { return _spotify.GetStatus().Online; }

        public bool Running() { return _spotify.GetStatus().Running; }

        public void Mute() { _spotify.Mute(); }

        public void UnMute() { _spotify.UnMute(); }

        public bool IsSpotifyMuted() { return _spotify.IsSpotifyMuted(); }

        public void SetSpotifyVolume(float volume = 100) { _spotify.SetSpotifyVolume(); }

        public float GetSpotifyVolume() { return _spotify.GetSpotifyVolume(); }

        public void Pause() { _spotify.Pause(); }

        public void Play() { _spotify.Play(); }

        public void PlayURL(string uri, string context = "") { _spotify.PlayURL(uri, context); }

        public void Skip() { _spotify.Skip(); }

        public void Previous() { _spotify.Previous(); }

        public void RunSpotify() { SpotifyLocalAPI.RunSpotify(); }

        public void RunSpotifyWebHelper() { SpotifyLocalAPI.RunSpotifyWebHelper(); }

        public string TrackName()
        {
            return _spotify.GetStatus().Track.TrackResource.Name;
        }

        public string AlbumName()
        {
            return _spotify.GetStatus().Track.AlbumResource.Name;
        }

        public string ArtistName()
        {
            return _spotify.GetStatus().Track.ArtistResource.Name;
        }

        public bool IsAd()
        {
            return _spotify.GetStatus().Track.IsAd();
        }

        public Bitmap AlbumArt(AlbumArtSize size)
        {
            return _spotify.GetStatus().Track.GetAlbumArt(size);
        }

        public int trackLength()
        {
            return _spotify.GetStatus().Track.Length;
        }

        /*public void OnPlayStateChange();
        public void OnTrackChange 
        public void OnTrackTimeChange 
        public void OnVolumeChange*/


        [DllImport("user32.dll")]
        static extern void keybd_event(byte bVk, byte bScan, UInt32 dwFlags, IntPtr dwExtraInfo);
        [DllImport("user32.dll")]
        static extern Byte MapVirtualKey(UInt32 uCode, UInt32 uMapType);
        [DllImport("user32.dll", EntryPoint = "FindWindow", SetLastError = true)]
        public static extern IntPtr FindWindowByCaption(IntPtr zeroOnly, string lpWindowName);


        private const byte VK_LCONTROL = 0xA2;
        private const byte VK_LSHIFT = 0xA0;
        private const byte VK_UP = 0x26;
        private const byte VK_DOWN = 0x28;
        private const byte VK_LEFT = 0x25;
        private const byte VK_RIGHT = 0x27;
        private const UInt32 KEYEVENTF_EXTENDEDKEY = 0x0001;
        private const UInt32 KEYEVENTF_KEYUP = 0x0002;
        private const int VK_KEY_R = 0x52;
        private const int VK_KEY_S = 0x53;

        private static void volUp()
        {
            IntPtr handle = FindWindowByCaption(IntPtr.Zero, "spotify");
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, handle);
            keybd_event(VK_UP, 0, KEYEVENTF_EXTENDEDKEY, handle);
            keybd_event(VK_UP, 0, KEYEVENTF_KEYUP, handle);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, handle);
        }

        private static void volDown()
        {
            IntPtr handle = FindWindowByCaption(IntPtr.Zero, "spotify");
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, handle);
            keybd_event(VK_DOWN, 0, KEYEVENTF_EXTENDEDKEY, handle);
            keybd_event(VK_DOWN, 0, KEYEVENTF_KEYUP, handle);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, handle);
        }

        private static void shuffle()
        {
            IntPtr handle = FindWindowByCaption(IntPtr.Zero, "spotify");
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, handle);
            keybd_event(VK_KEY_S, 0, KEYEVENTF_EXTENDEDKEY, handle);
            keybd_event(VK_KEY_S, 0, KEYEVENTF_KEYUP, handle);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, handle);
        }

        private static void repeat()
        {
            IntPtr handle = FindWindowByCaption(IntPtr.Zero, "spotify");
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, handle);
            keybd_event(VK_KEY_R, 0, KEYEVENTF_EXTENDEDKEY, handle);
            keybd_event(VK_KEY_R, 0, KEYEVENTF_KEYUP, handle);
            keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, handle);
        }













        /*private const int APPCOMMAND_MEDIA_PLAY_PAUSE = 0xE0000;
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
        private static IntPtr SpotifyHandle;
        private static string lastTrack = "";

        public string TitleCache { get; private set; }

        [DllImport("user32.dll")]
        public static extern IntPtr SendMessageW(IntPtr hWnd, int Msg,
        IntPtr wParam, IntPtr lParam);
        [DllImport("user32.dll", EntryPoint = "FindWindow", SetLastError = true)]
        public static extern IntPtr FindWindowByCaption(IntPtr zeroOnly, string lpWindowName);
        [DllImport("user32.dll")]
        static extern void keybd_event(byte bVk, byte bScan, UInt32 dwFlags, IntPtr dwExtraInfo);
        [DllImport("user32.dll")]
        static extern Byte MapVirtualKey(UInt32 uCode, UInt32 uMapType);
        [DllImport("user32.dll")]
        private static extern int SendMessage(IntPtr hwnd, int msg, int wParam, int lParam);
        [DllImport("user32.dll")]
        public static extern int PostMessage(IntPtr hWnd, int Msg, uint wParam, int lParam);

        [System.Runtime.InteropServices.DllImport("user32.dll")]
        [return: System.Runtime.InteropServices.MarshalAs(System.Runtime.InteropServices.UnmanagedType.Bool)]
        private static extern bool ShowWindow(IntPtr hWnd, int flags);

        [System.Runtime.InteropServices.DllImport("user32.dll")]
        private static extern int SetForegroundWindow(IntPtr hwnd);

        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern IntPtr FindWindow(string lpClassName, string lpWindowName);

        [DllImport("user32.dll")]
        public static extern bool SetFocus(IntPtr hWnd);

        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        static extern int GetWindowText(IntPtr hWnd, String text, int count);
        [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        static extern int GetWindowTextLength(IntPtr hWnd);

        public static string GetSpotifyTrackInfo()
        {
            var proc = Process.GetProcessesByName("Spotify").FirstOrDefault(p => !string.IsNullOrWhiteSpace(p.MainWindowTitle));

            if (proc == null)
            {
                return "Spotify is not running!";
            }

            if (string.Equals(proc.MainWindowTitle, "Spotify", StringComparison.InvariantCultureIgnoreCase))
            {
                return lastTrack; //paused
            }
            lastTrack = proc.MainWindowTitle;
            return proc.MainWindowTitle;
        }

        private static void playPause()
        {
            handle = FindWindowByCaption(IntPtr.Zero, "spotify");
            /*Process[] temp = Process.GetProcessesByName("spotify");
            if(temp.Length > 0)
            {
                SpotifyProcess = temp[0];
                handle = SpotifyProcess.MainWindowHandle;
            }*/
        /*    SendMessageW(handle, WM_APPCOMMAND, handle,
                (IntPtr)APPCOMMAND_MEDIA_PLAY_PAUSE);
        }

        public static string GetNowplaying()
        {
            SpotifyHandle = FindWindowByCaption(IntPtr.Zero, "spotify");
            string lpText = new string('a', 100);
            int intLength = GetWindowText(SpotifyHandle, lpText, lpText.Length);
            if ((intLength <= 0) || (intLength > lpText.Length))
            {
                string TitleCache = "Spotify Closed";
                /*if (SpotifyProcess != null)
                {
                    SpotifyProcess = null;
                    SpotifyHandle = null;
                    this.SpotifyState = IController.StateType.Closed;
                }*/
        /*       return TitleCache;
           }
           string strTitle = lpText.Substring(0, intLength);
           //strTitle = Strings.Mid(strTitle, 11);
           strTitle = strTitle.Substring(10, strTitle.Length-11);
           if (strTitle.Length > 0)
           {
               Console.WriteLine(strTitle);
               /*if (strTitle != TitleCache)
               {
                   // the song has changed
                   TitleCache = strTitle;
                   if (TrackStateChanged != null)
                   {
                       TrackStateChanged(TitleCache, _SpotifyState);
                   }
               }*/
        /*        return strTitle;
            }
            else
            {
                Console.WriteLine(strTitle);
                //this.SpotifyState = IController.StateType.Paused;
                /*if ((string.IsNullOrEmpty(TitleCache)))
                {
                    TitleCache = "Nothing Playing";
                }*/
        /*              return strTitle;
                  }
              }


              private static string GetTrackTitle()
              {
                  // this function returns the track title of the NotPlaying Song
                  try
                  {
                      string ArtistTrack = GetNowplaying();
                      string Track = null;
                      Console.WriteLine(ArtistTrack);
                      //Track = ArtistTrack.Substring(Strings.InStr(ArtistTrack, " – ") + 2, ArtistTrack.Count - Strings.InStr(ArtistTrack, " – ") - 2);
                      return ArtistTrack;
                  }
                  catch (System.ArgumentOutOfRangeException ex)
                  {
                      return "";
                  }
                  catch (Exception ex)
                  {
                      return "";
                  }
              }


              private static void nextTrack()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  SendMessageW(handle, WM_APPCOMMAND, handle,
                      (IntPtr)APPCOMMAND_MEDIA_NEXTTRACK);
              }

              private static void prevTrack()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  SendMessageW(handle, WM_APPCOMMAND, handle,
                      (IntPtr)APPCOMMAND_MEDIA_PREVIOUSTRACK);
              }

              private static void volUp()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_UP, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_UP, 0, KEYEVENTF_KEYUP, handle);
                  keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, handle);
              }

              private static void volDown()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_DOWN, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_DOWN, 0, KEYEVENTF_KEYUP, handle);
                  keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, handle);
              }

              private static void shuffle()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_KEY_S, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_KEY_S, 0, KEYEVENTF_KEYUP, handle);
                  keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, handle);
              }

              private static void repeat()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  keybd_event(VK_LCONTROL, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_KEY_R, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_KEY_R, 0, KEYEVENTF_KEYUP, handle);
                  keybd_event(VK_LCONTROL, 0, KEYEVENTF_KEYUP, handle);
              }

              private static void forward()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  keybd_event(VK_LSHIFT, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_RIGHT, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_RIGHT, 0, KEYEVENTF_KEYUP, handle);
                  keybd_event(VK_LSHIFT, 0, KEYEVENTF_KEYUP, handle);
              }

              private static void backward()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  keybd_event(VK_LSHIFT, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_LEFT, 0, KEYEVENTF_EXTENDEDKEY, handle);
                  keybd_event(VK_LEFT, 0, KEYEVENTF_KEYUP, handle);
                  keybd_event(VK_LSHIFT, 0, KEYEVENTF_KEYUP, handle);
              }

              private static void close()
              {
                  handle = FindWindowByCaption(IntPtr.Zero, "spotify");
                  SendMessage(handle, 0x0010, 0, 0);
              }
              */



    }
}
