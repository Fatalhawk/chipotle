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
        private static List<ProcessInterface> processList; //main list to store currrently-running processes
        private static ProcessInterface currentProcess; //Process in focus
        
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

        [System.Runtime.InteropServices.DllImport("user32.dll")]
        private static extern IntPtr GetForegroundWindow();

        [System.Runtime.InteropServices.DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, IntPtr Processid);

        /**
         * updates process list 
         * @return: list of all processes currently running on local machine
        **/
        public List<ProcessInterface> updateProcessList()
        {

            IntPtr currentForeground = GetForegroundWindow();
            IntPtr currentPID = IntPtr.Zero;
            ProcessInterface pInterface = null;
            GetWindowThreadProcessId(currentForeground, currentPID);
            List<ProcessInterface> processList = new List<ProcessInterface>();
            foreach (Process process in Process.GetProcesses())
            {
                //process.EnableRaisingEvents = true;                
                //process.Exited += new EventHandler(ProcessExited);
                if (process.MainWindowHandle.ToInt32() != 0) //!String.IsNullOrEmpty(process.MainWindowTitle)
                {
                    pInterface = new ProcessInterface(process);
                    pInterface.findWindow();
                    processList.Add(pInterface);

                    //determine foreground process
                    if (process.Id == currentPID.ToInt32())
                    {
                        currentProcess = pInterface;
                        Console.WriteLine("Current window is {0}", process.MainWindowTitle);
                    }
                }
            }
            if (currentPID.ToInt32() == 0)
            {
                currentProcess = processList[0];
            }
            return processList;
        }

        /**
         * function to be called if event triggered
         * called every two seconds by timer elapsed event in ProcessListener class
        **/ 
        public void pollProcessList(object sender, EventArgs e)
        {
            List<ProcessInterface> newList = updateProcessList();
            if (!newList.SequenceEqual(processList))
            {
                processList.Clear();
                processList = newList;
                Console.Clear();
                printProcessList();
            }
        }

        public void killProcess(int x)
        {
            processList[x].killApp();
            try
            {
                currentProcess.setForegroundApp();
            }
            catch(NullReferenceException e)
            {

            }
        }

        public void setForeGroundApp(int x)
        {
            currentProcess = processList[x];
            currentProcess.setForegroundApp();
        }

        /**
         * Displays to console list of currently running processes in processList variable
        **/ 
        public void printProcessList()
        {
            Console.WriteLine("Current Processes on {0}", System.Environment.MachineName);
            foreach (ProcessInterface process in processList)
            {
                process.printProcessInfo();
            }
        }
    }
}
