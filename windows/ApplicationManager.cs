/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using App.Program;
using AppCommunication;
using System.Text;

namespace Networking
{
    public static class ProgramManager
    {
        /**
         * Attribute List
        **/
        private static IDictionary<int,ProgramBase> processDict;
        public static IDictionary<int, ProgramBase> ProcessDict
        {
            get { return processDict; }
            set { processDict = value; }
        }
        //private static List<string> processListNames;
        private static ProgramBase currentProcess; //Process in focus
        private static ConnectionListener cListener;

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
            processDict = new Dictionary<int, ProgramBase>();
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
            if (processDict.Keys.Contains(currentForeground.ToInt32()))
            {
                currentProcess = processDict[currentForeground.ToInt32()];
            }
        }


        #region System Actions
        public static void addProgram(int key, ProgramBase pInt)
        {
            processDict.Add(key, pInt);
            currentProcess = pInt;
            try
            {
                byte[] message = WireProtocol.serializeMessage(WireProtocol.createInfoMessage(Guid.NewGuid().ToString(),key, pInt.WindowTitle));
                cListener.phoneCommChannel.startThread(message);
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
            if (processDict[key].killApp())
            {
                processDict.Remove(key);
                determineForegroundWindow();
            }
        }

        public static void changeProgramTitle(int key, string newTitle)
        {
            processDict[key].WindowTitle = newTitle;
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

        public static void onConnected(object sender, EventArgs e)
        {
            cListener.phoneCommChannel.processCommand += new CommunicationChannel.onDataReceivedEvent(processCommand);
        }

        private static void processCommand(int msgTarget, string msgCommand, byte[] msgInfo)
        {
            if (msgTarget == 0)
            {
                currentProcess.performAction(msgCommand);
            }
            else if (msgTarget == 1)
            {
                performAction(msgCommand,msgInfo);
            }
        }

        private static void performAction(string msgCommand)
        {
            Command cmd;
            if (Enum.TryParse(msgCommand,out cmd))
            {

            }
        }

        private enum Command
        {
            OPEN,
            FOCUS,
            CLOSE
        }

        public static ProgramBase getPInterface(int key)
        {
            return processDict[key];
        }
    }
}
