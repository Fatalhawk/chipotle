/**
 * Author(s): Takahiro Tow
 * Last updated: July 6, 2017
 **/

//UPDATE TO DICTIONARIES

using System.Collections.Generic;
using System.Timers;
using System.Diagnostics;
using System;
using System.Linq;

namespace Networking
{
    /**
     * Process Listener class to monitor open processes by polling
     * Notes
     * -currently seperated into own class in case more efficient way is found
     * -uses ElapsedEventHandlers
    **/
    public class ProcessListener
    {
        //Timer to be used to control flow of polling
        private Timer pollTimer;
        TestGUI tObj;
        /**
         * Constructor
         * initializes timer and starts it, triggering event sequence
         **/
        public ProcessListener(TestGUI tgui)
        {
            tObj = tgui;
            pollTimer = new Timer(1000); //set polling rate to .5 Hz (every 2 seconds)
            pollTimer.Enabled = true;
            pollTimer.Elapsed += new ElapsedEventHandler(pollProcessList); //add invoked (to-be) method to event handler
            pollTimer.Start(); //start timer
        }

        /**
         * Dictionary Implementation
         * 
         **/
        public Dictionary<int, ProcessInterface> updateProcessDict()
        {
            ProcessInterface pInterface = null;
            Dictionary<int, ProcessInterface> newDict = new Dictionary<int, ProcessInterface>();
            foreach (Process process in Process.GetProcesses())
            {
                if (process.MainWindowHandle.ToInt32() != 0)
                {
                    pInterface = new ProcessInterface(process);
                    newDict.Add(process.Id, pInterface);
                }
            }
            return newDict;
        }

        public List<int> findNewProcesses(IDictionary<int, ProcessInterface> currentDict, IDictionary<int, ProcessInterface> newDict)
        {
            List<int> newProcessesIds = new List<int>();
            foreach (int key in newDict.Keys)
            {
                if (!currentDict.ContainsKey(key))
                {
                    newProcessesIds.Add(key);
                }
            }
            return newProcessesIds;
        }

        public List<int> findKilledProcesses(IDictionary<int, ProcessInterface> currentDict, IDictionary<int, ProcessInterface> newDict)
        {
            List<int> killedProcessIds = new List<int>();
            foreach (int key in currentDict.Keys)
            {
                if (!newDict.ContainsKey(key))
                {
                    killedProcessIds.Add(key);
                }
            }
            return killedProcessIds;
        }

        private void pollProcessList(object sender, EventArgs e)
        {
            Dictionary<int, ProcessInterface> newDict = updateProcessDict();
            if (!dictEquals(newDict.Keys, ProcessHandler.getProcessDict().Keys))
            {
                List<int> newProcesses = findNewProcesses(ProcessHandler.getProcessDict(), newDict);
                foreach (int newProcess in newProcesses)
                {
                    ProcessHandler.addProcess(newProcess, newDict[newProcess]);
                }
                List<int> killedProcesses = findKilledProcesses(ProcessHandler.getProcessDict(), newDict);
                foreach (int killedID in killedProcesses)
                {
                    ProcessHandler.removeProcess(killedID);
                }
                tObj.updateGridView2();
            }
        }

        private bool dictEquals(ICollection<int> dictOneKeys, ICollection<int> dictTwoKeys)
        {
            if (dictOneKeys.Count != dictTwoKeys.Count)
            {
                return false;
            }
            var newDictSet = new HashSet<int>(dictTwoKeys);
            return newDictSet.SetEquals(dictOneKeys);
        }
    }
}
