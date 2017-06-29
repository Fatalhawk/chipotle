using System;
using System.Collections.Generic;
using System.Linq;
using System.Diagnostics;
using System.Timers;

namespace Networking
{
    public class ProcessHandler
    {
        /**
         * Attribute List
        **/ 
        private static List<Process> processList; //main list to store currrently-running processes
        private static Process currentProcess; //Process in focus
        
        /**
         * Constructor
         * initializes processList and currentProcess
         * @return: all Processes in List format
        **/
        public ProcessHandler()
        {
            processList = updateProcessList();
            currentProcess = null;
        }

        /**
         * updates process list 
         * @return: list of all processes currently running on local machine
        **/
        public List<Process> updateProcessList()
        {
            List<Process> processList = new List<Process>();
            foreach (Process process in Process.GetProcesses())
            {
                //process.EnableRaisingEvents = true;                
                //process.Exited += new EventHandler(ProcessExited);
                if (process.MainWindowTitle != "")
                {
                    processList.Add(process);
                }
            }
            return processList;
        }

        /**
         * function to be called if event triggered
         * called every two seconds by timer elapsed event in ProcessListener class
        **/ 
        public void pollProcessList(object sender, EventArgs e)
        {
            List<Process> newList = updateProcessList();
            if (!newList.SequenceEqual(processList))
            {
                processList.Clear();
                processList = newList;
                Console.Clear();
                printProcessList();
            }
        }

        /**
         * Displays to console list of currently running processes in processList variable
        **/ 
        public void printProcessList()
        {
            foreach (Process process in processList)
            {
                Console.WriteLine("Process: {0}\tID: {1}", process.MainWindowTitle, process.Id);
            }
        }
    }
}
