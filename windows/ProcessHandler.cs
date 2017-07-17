/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

using System;
using System.Collections.Generic;

namespace Networking
{
    public static class ProcessHandler
    {
        /**
         * Attribute List
        **/ 
        private static IDictionary<int,ProcessInterface> processDict;
        private static List<string> processListNames;
        private static ProcessInterface currentProcess; //Process in focus
        
        /**
         * Constructor
         * initializes processList and currentProcess
         * @return: all Processes in List format
        **/
        static ProcessHandler()
        {
            //processList = new List<ProcessInterface>();
            processDict = new Dictionary<int, ProcessInterface>();
            processListNames = new List<string>();
            updateProcessListNames();
            currentProcess = null;
            determineForegroundWindow();
        }


        [System.Runtime.InteropServices.DllImport("user32.dll")]
        private static extern IntPtr GetForegroundWindow();

        [System.Runtime.InteropServices.DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, IntPtr Processid);


        /**
         * determines the foreground window on computer
         **/
        private static void determineForegroundWindow()
        {
            IntPtr currentForeground = GetForegroundWindow();
            Console.WriteLine("{0}",currentForeground.ToString());
            foreach(int key in processDict.Keys)
            {
                //determine foreground process
                if (processDict[key].ProcessProp.MainWindowHandle == currentForeground)
                {
                    currentProcess = processDict[key];
                    break;
                    //Console.WriteLine("Current window is {0}", pInterface.ProcessProp.MainWindowTitle);
                }
            }
        }

        /**
         * updates the list of process names 
         **/
        public static void updateProcessListNames()
        {
            processListNames.Clear();
            foreach(int key in processDict.Keys)
            {
                processListNames.Add(processDict[key].getTitle());
            }

        }

        public static void addProcess(int key, ProcessInterface pInt)
        {
            processDict.Add(key, pInt);
            //send phone update on process
        }

        public static void removeProcess(int key)
        {
            processDict.Remove(key);
        }

        /**
         * FOR TESTING PURPOSES ONLY
         * calls killProcess method of process specified by user
         **/
        public static void killProcess(int key)
        {
            if (!processDict[key].killApp())
            {
                removeProcess(key);
                //Console.WriteLine("Could not kill process");
            }
        }

        /**
         * main method to be called upon recieving instructions
         * will direct instruction to the appropriate process object
         **/
        public static void performAction()
        {

        }

        private static void setForeGroundApp(int key)
        {
            currentProcess = processDict[key];
            currentProcess.setForegroundApp();
        }

        /**
         * Displays to console list of currently running processes in processList variable
        **/ 
        public static void printProcessList()
        {
            //Console.WriteLine("Current Processes on {0}", System.Environment.MachineName);
            foreach (int key in processDict.Keys)
            {
                processDict[key].printProcessInfo();
            }
            Console.WriteLine("###########################################\n\n");
        }

        public static List<string> getProcessListNames()
        {
            return processListNames;
        }

        public static IDictionary<int, ProcessInterface> getProcessDict()
        {
            return processDict;
        }
    }
}
