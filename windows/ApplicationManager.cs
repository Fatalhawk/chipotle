/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using App.Program;
using AppCommunication;
using System.Drawing;
using Google.Protobuf.AppCommunication.Proto;

namespace Networking
{
    public static class ProgramManager
    {
        /**
         * Attribute List
        **/
        private static IDictionary<int,ProgramBase> _processDict;
        public static IDictionary<int, ProgramBase> ProcessDict
        {
            get { return _processDict; }
        }
        //private static List<string> processListNames;
        private static ProgramBase currentProcess; //Process in focus
        private static ConnectionListener cListener;
        private static ImageConverter converter;

        /**
         * Constructor
         * initializes processList and currentProcess
         * @return: all Processes in List format
        **/
        static ProgramManager()
        {
            //cListener = new ConnectionListener();
            //cListener.onConnect += new ConnectionListener.onConnectEvent(onConnected);
            //cListener.sendInitialBroadcast();
            //cListener.listenForClient();
            converter = new ImageConverter();
            _processDict = new Dictionary<int, ProgramBase>();
            currentProcess = null;
            determineForegroundWindow();
        }

        [System.Runtime.InteropServices.DllImport("user32.dll")]
        private static extern IntPtr GetForegroundWindow();
        
        /**
         * determines the foreground window on computer
         **/
        private static void determineForegroundWindow()
        {
            IntPtr currentForeground = GetForegroundWindow();
            if (_processDict.Keys.Contains(currentForeground.ToInt32()))
            {
                currentProcess = _processDict[currentForeground.ToInt32()];
            }
        }

        public static void updateWindowCap(int hWnd, Bitmap cap)
        {
            try
            {
                _processDict[hWnd].WindowCap = cap;
                CompRequest updateCapRequest = WireProtocol.createProgramMessage(Guid.NewGuid().ToString(), hWnd, (byte[])converter.ConvertTo(cap, typeof(byte[])));
                //cListener.phoneCommChannel.sendMessage(WireProtocol.serializeMessage(updateCapRequest));
            }
            catch (KeyNotFoundException e)
            {
                Console.WriteLine(e.ToString());
            }
        }


        #region System Actions
        public static void addProgram(int key, ProgramBase pInt)
        {
            _processDict.Add(key, pInt);
            currentProcess = pInt;
            try
            {
                CompRequest message = WireProtocol.createProgramMessage(Guid.NewGuid().ToString(),key,pInt.WindowTitle, (byte[])converter.ConvertTo(pInt.WindowCap, typeof(byte[])), pInt.ProgramType);
                //cListener.phoneCommChannel.startThread(WireProtocol.serializeMessage(message));
            }
            catch (NullReferenceException e){
                Console.WriteLine(e.ToString());

            }
            //send phone update on process
        }

        /**
         * FOR TESTING PURPOSES ONLY
         * calls killProcess method of process specified by user
         **/
        public static void removeProgram(int key)
        {
            if (_processDict[key].killApp())
            {
                _processDict.Remove(key);
                determineForegroundWindow();

            }
        }

        public static void changeProgramTitle(int key, string newTitle)
        {
            _processDict[key].WindowTitle = newTitle;
        }

        //used for retrieving window handle
        [DllImport("user32.dll", ExactSpelling = true, CharSet = CharSet.Auto)]
        [return: MarshalAs(UnmanagedType.Bool)]
        private static extern bool SetForegroundWindow(IntPtr hWnd);

        public static void focusProgram(int key)
        {
            SetForegroundWindow(new IntPtr(key));
        }
        #endregion

        #region Request Handlers
        private static void processCommand(int msgTarget, string msgCommand, byte[] msgInfo)
        {
            if (msgTarget == 0)
            {
                currentProcess.performAction(msgCommand);
            }
            else if (msgTarget == 1)
            {
                performAction(msgCommand, msgInfo);
            }
        }

        /**
         * main method to be called upon recieving instructions
         * will direct instruction to the appropriate process object
         **/
        public static void performAction(string cmd, byte[] msgInfo)
        {
            Command newAction;
            if (Enum.TryParse(cmd, out newAction))
            {
                int programID = 0;
                switch (newAction)
                {
                    case Command.OPEN:
                        Console.WriteLine("OPEN Program with ID: {0}", programID);
                        if (BitConverter.IsLittleEndian)
                        {
                            Array.Reverse(msgInfo);
                        }
                        string num = BitConverter.ToString(msgInfo);
                        programID = Int32.Parse(num);//BitConverter.ToInt32(msgInfo, 0);
                        focusProgram(programID);
                        break;
                    case Command.CLOSE:
                        if (BitConverter.IsLittleEndian)
                        {
                            Array.Reverse(msgInfo);
                        }
                        programID = BitConverter.ToInt32(msgInfo, 0);
                        removeProgram(programID);
                        break;
                }
            }
        }

        private static void performAction(string msgCommand)
        {
            Command cmd;
            if (Enum.TryParse(msgCommand, out cmd))
            {

            }
        }
        #endregion

        public static void onConnected(object sender, EventArgs e)
        {
            cListener.PhoneCommChannel.processCommand += new CommunicationChannel.onDataReceivedEvent(processCommand);
        }

        private enum Command
        {
            OPEN,
            FOCUS,
            CLOSE
        }
    }
}
